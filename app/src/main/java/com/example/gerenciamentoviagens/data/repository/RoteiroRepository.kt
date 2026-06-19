package com.example.gerenciamentoviagens.data.repository

import com.example.gerenciamentoviagens.data.local.dao.AtividadeViagemDao
import com.example.gerenciamentoviagens.data.local.entity.AtividadeViagem
import com.example.gerenciamentoviagens.data.remote.Content
import com.example.gerenciamentoviagens.data.remote.GeminiRequest
import com.example.gerenciamentoviagens.data.remote.GeminiService
import com.example.gerenciamentoviagens.data.remote.ItineraryResponse
import com.example.gerenciamentoviagens.data.remote.Part
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class RoteiroRepository(
    private val geminiService: GeminiService,
    private val atividadeViagemDao: AtividadeViagemDao
) {
    suspend fun gerarRoteiro(
        apiKey: String,
        cidade: String,
        inicio: String,
        fim: String,
        orcamento: String
    ): ItineraryResponse? {
        val prompt = """
            Atue como um guia turístico especializado. Crie um roteiro de viagem detalhado para a cidade de $cidade, do dia $inicio ao dia $fim, com um orçamento total de $orcamento. 
            Retorne a resposta exclusivamente em formato JSON, com a seguinte estrutura: 
            { 
              "destino": "string", 
              "dias": [ 
                { 
                  "dia": 1, 
                  "atividades": [ 
                    { "horario": "string", "descricao": "string", "custo_estimado": "string" } 
                  ] 
                } 
              ] 
            }. 
            Não adicione textos explicativos fora do JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        return try {
            val response = geminiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?.replace("```json", "")
                ?.replace("```", "")
                ?.trim()

            if (!jsonText.isNullOrEmpty()) {
                Gson().fromJson(jsonText, ItineraryResponse::class.java)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAtividades(viagemId: Int): Flow<List<AtividadeViagem>> =
        atividadeViagemDao.getAtividadesByViagem(viagemId)

    suspend fun salvarRoteiro(viagemId: Int, roteiro: ItineraryResponse) {
        val atividades = roteiro.dias.flatMap { diaPlan ->
            diaPlan.atividades.map { activityPlan ->
                AtividadeViagem(
                    viagemId = viagemId,
                    dia = diaPlan.dia,
                    horario = activityPlan.horario,
                    descricao = activityPlan.descricao,
                    custoEstimado = activityPlan.custoEstimado
                )
            }
        }
        atividadeViagemDao.replaceAtividades(viagemId, atividades)
    }
}
