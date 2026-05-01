package com.example.gerenciamentoviagens.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.data.repository.ViagemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViagemViewModel(private val repository: ViagemRepository) : ViewModel() {

    var destino by mutableStateOf("")
    var tipo by mutableStateOf("Lazer")
    var dataInicio by mutableStateOf<Long?>(null)
    var dataFim by mutableStateOf<Long?>(null)
    var orcamento by mutableStateOf("")

    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    private val _viagens = MutableStateFlow<List<Viagem>>(emptyList())
    val viagens: StateFlow<List<Viagem>> = _viagens.asStateFlow()

    fun carregarViagens(userId: Int) {
        viewModelScope.launch {
            repository.getViagensByUser(userId).collect {
                _viagens.value = it
            }
        }
    }

    fun salvarViagem(userId: Int, onSuccess: () -> Unit) {
        if (destino.isBlank() || orcamento.isBlank() || dataInicio == null || dataFim == null) {
            errorMessage = "Todos os campos são obrigatórios"
            return
        }

        val orcamentoDouble = orcamento.toDoubleOrNull()
        if (orcamentoDouble == null) {
            errorMessage = "Orçamento inválido"
            return
        }

        viewModelScope.launch {
            try {
                val viagem = Viagem(
                    destino = destino,
                    tipo = tipo,
                    dataInicio = dataInicio!!,
                    dataFim = dataFim!!,
                    orcamento = orcamentoDouble,
                    userId = userId
                )
                repository.insert(viagem)
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
        destino = ""
        tipo = "Lazer"
        dataInicio = null
        dataFim = null
        orcamento = ""
        errorMessage = ""
    }
}
