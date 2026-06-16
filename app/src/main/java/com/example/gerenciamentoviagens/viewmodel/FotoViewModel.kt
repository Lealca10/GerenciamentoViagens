package com.example.gerenciamentoviagens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.Foto
import com.example.gerenciamentoviagens.data.repository.FotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FotoViewModel(private val repository: FotoRepository) : ViewModel() {

    private val _fotos = MutableStateFlow<List<Foto>>(emptyList())
    val fotos: StateFlow<List<Foto>> = _fotos.asStateFlow()

    fun carregarFotos(viagemId: Int) {
        viewModelScope.launch {
            repository.getFotosByViagem(viagemId).collect {
                _fotos.value = it
            }
        }
    }

    fun adicionarFoto(viagemId: Int, uri: String) {
        viewModelScope.launch {
            repository.insert(Foto(viagemId = viagemId, uri = uri))
        }
    }
}
