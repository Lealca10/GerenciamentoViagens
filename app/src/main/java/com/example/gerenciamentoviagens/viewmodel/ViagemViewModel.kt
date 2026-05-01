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

    var viagemId by mutableStateOf<Int?>(null)
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

    fun prepararEdicao(viagem: Viagem) {
        viagemId = viagem.id
        destino = viagem.destino
        tipo = viagem.tipo
        dataInicio = viagem.dataInicio
        dataFim = viagem.dataFim
        orcamento = viagem.orcamento.toString()
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
        errorMessage = ""
    }
}
