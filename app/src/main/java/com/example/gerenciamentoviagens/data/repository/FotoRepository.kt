package com.example.gerenciamentoviagens.data.repository

import com.example.gerenciamentoviagens.data.local.dao.FotoDao
import com.example.gerenciamentoviagens.data.local.entity.Foto
import kotlinx.coroutines.flow.Flow

class FotoRepository(private val fotoDao: FotoDao) {
    fun getFotosByViagem(viagemId: Int): Flow<List<Foto>> = fotoDao.getFotosByViagem(viagemId)
    suspend fun insert(foto: Foto) = fotoDao.insert(foto)
}
