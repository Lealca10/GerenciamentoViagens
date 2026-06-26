package com.example.gerenciamentoviagens.data.repository

import android.util.Log
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
            Log.d("Gemini", "Chamando API para $cidade...")
            val response = geminiService.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            Log.d("Gemini", "Resposta Bruta: $rawText")

            if (!rawText.isNullOrEmpty()) {
                // Extração inteligente: localiza o JSON real ignorando markdown ou avisos
                val startIndex = rawText.indexOf("{")
                val endIndex = rawText.lastIndexOf("}")
                
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    val jsonClean = rawText.substring(startIndex, endIndex + 1)
                    Log.d("Gemini", "JSON Extraído: $jsonClean")
                    Gson().fromJson(jsonClean, ItineraryResponse::class.java)
                } else {
                    Log.e("Gemini", "A resposta não contém um JSON válido")
                    null
                }
            } else {
                Log.e("Gemini", "A IA retornou uma resposta vazia")
                null
            }
        } catch (e: Exception) {
            Log.e("Gemini", "Erro na requisição: ${e.message}", e)
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
        Log.d("Gemini", "Roteiro salvo no banco com ${atividades.size} atividades.")
    }
}
