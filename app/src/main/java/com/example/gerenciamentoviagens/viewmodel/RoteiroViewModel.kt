package com.example.gerenciamentoviagens.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.AtividadeViagem
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.data.repository.RoteiroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoteiroViewModel(private val repository: RoteiroRepository) : ViewModel() {

    private val _atividades = MutableStateFlow<List<AtividadeViagem>>(emptyList())
    val atividades: StateFlow<List<AtividadeViagem>> = _atividades.asStateFlow()

    var isGenerating by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun carregarRoteiro(viagemId: Int) {
        viewModelScope.launch {
            repository.getAtividades(viagemId).collect {
                _atividades.value = it
            }
        }
    }

    fun gerarERoteiro(apiKey: String, viagem: Viagem) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicio = dateFormat.format(Date(viagem.dataInicio))
        val fim = dateFormat.format(Date(viagem.dataFim))
        val orcamento = "R$ ${String.format("%.2f", viagem.orcamento)}"

        isGenerating = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val roteiro = repository.gerarRoteiro(
                    apiKey = apiKey,
                    cidade = viagem.destino,
                    inicio = inicio,
                    fim = fim,
                    orcamento = orcamento
                )

                if (roteiro != null) {
                    repository.salvarRoteiro(viagem.id, roteiro)
                } else {
                    errorMessage = "Não foi possível gerar o roteiro. Verifique sua chave de API ou conexão."
                }
            } catch (e: Exception) {
                errorMessage = "Erro: ${e.message}"
            } finally {
                isGenerating = false
            }
        }
    }
}
