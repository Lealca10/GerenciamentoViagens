package com.example.gerenciamentoviagens.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.data.repository.ViagemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import java.text.Normalizer

class ViagemViewModel(private val repository: ViagemRepository) : ViewModel() {

    var viagemId by mutableStateOf<Int?>(null)
    var destino by mutableStateOf("")
    var tipo by mutableStateOf("Lazer")
    var dataInicio by mutableStateOf<Long?>(null)
    var dataFim by mutableStateOf<Long?>(null)
    var orcamento by mutableStateOf("")
    var gastosBase by mutableStateOf(0.0)

    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    private val _viagens = MutableStateFlow<List<Viagem>>(emptyList())
    val viagens: StateFlow<List<Viagem>> = _viagens.asStateFlow()

    // Funcionalidade: Viagem Atual
    var viagemAtual by mutableStateOf<Viagem?>(null)
    var cidadeAtual by mutableStateOf<String?>(null)
    var latitudeAtual by mutableStateOf<Double?>(null)
    var longitudeAtual by mutableStateOf<Double?>(null)

    fun carregarViagens(userId: Int) {
        viewModelScope.launch {
            repository.getViagensByUser(userId).collect {
                _viagens.value = it
            }
        }
    }

    fun buscarViagemPelaCidade(userId: Int, cidade: String) {
        val cidadeLimpa = cidade.trim()
        cidadeAtual = cidadeLimpa

        viewModelScope.launch {
            try {
                val localCalendar = Calendar.getInstance()
                val day = localCalendar.get(Calendar.DAY_OF_MONTH)
                val month = localCalendar.get(Calendar.MONTH)
                val year = localCalendar.get(Calendar.YEAR)

                val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    clear()
                    set(year, month, day)
                }
                val dataAtualNormalizada = utcCalendar.timeInMillis

                val encontrada = repository.getViagemAtual(userId, cidadeLimpa.normalizar(), dataAtualNormalizada)
                viagemAtual = encontrada

                Log.d("ViagemViewModel", "Busca em $cidadeLimpa (Data: $dataAtualNormalizada): ${if (encontrada != null) "Sucesso" else "Vazio"}")
            } catch (e: Exception) {
                Log.e("ViagemViewModel", "Erro na busca reativa", e)
            }
        }
    }

    private fun String.normalizar(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .trim()

    fun prepararEdicao(viagem: Viagem) {
        viagemId = viagem.id
        destino = viagem.destino
        tipo = viagem.tipo
        dataInicio = viagem.dataInicio
        dataFim = viagem.dataFim
        orcamento = viagem.orcamento.toString()
        gastosBase = viagem.gastos
        errorMessage = ""
    }

    fun prepararNovaViagem() {
        limparCampos()
    }

    fun salvarViagem(userId: Int, onSuccess: () -> Unit) {
        if (destino.isBlank() || orcamento.isBlank() || dataInicio == null || dataFim == null) {
            errorMessage = "Todos os campos são obrigatórios"
            return
        }

        val orcamentoDouble = orcamento.replace(",", ".").toDoubleOrNull()
        if (orcamentoDouble == null) {
            errorMessage = "Orçamento inválido"
            return
        }

        viewModelScope.launch {
            try {
                val viagem = Viagem(
                    id = viagemId ?: 0,
                    destino = destino,
                    tipo = tipo,
                    dataInicio = dataInicio!!,
                    dataFim = dataFim!!,
                    orcamento = orcamentoDouble,
                    gastos = if (viagemId != null) gastosBase else 0.0,
                    userId = userId
                )
                
                if (viagemId == null) {
                    repository.insert(viagem)
                } else {
                    repository.update(viagem)
                }
                
                successMessage = "Viagem salva com sucesso!"
                limparCampos()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Erro ao salvar viagem"
            }
        }
    }

    fun excluirViagem(viagem: Viagem) {
        viewModelScope.launch {
            repository.delete(viagem)
        }
    }

    private fun limparCampos() {
        viagemId = null
        destino = ""
        tipo = "Lazer"
        dataInicio = null
        dataFim = null
        orcamento = ""
        gastosBase = 0.0
        errorMessage = ""
    }
}
