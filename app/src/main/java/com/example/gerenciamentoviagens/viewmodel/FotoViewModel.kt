package com.example.gerenciamentoviagens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.Foto
import com.example.gerenciamentoviagens.data.repository.FotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FotoViewModel(private val repository: FotoRepository) : ViewModel() {

    fun getFotos(viagemId: Int): Flow<List<Foto>> {
        return repository.getFotosByViagem(viagemId)
    }

    fun adicionarFoto(viagemId: Int, uri: String) {
        viewModelScope.launch {
            repository.insert(Foto(viagemId = viagemId, uri = uri))
        }
    }
}
