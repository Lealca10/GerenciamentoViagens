package com.example.gerenciamentoviagens.data.repository

import com.example.gerenciamentoviagens.data.local.dao.FotoViagemDao
import com.example.gerenciamentoviagens.data.local.entity.FotoViagem
import kotlinx.coroutines.flow.Flow

class FotoViagemRepository(private val fotoViagemDao: FotoViagemDao) {
    fun getFotosByViagem(viagemId: Int): Flow<List<FotoViagem>> = fotoViagemDao.getFotosByViagem(viagemId)
    suspend fun insert(foto: FotoViagem) = fotoViagemDao.insert(foto)
}
