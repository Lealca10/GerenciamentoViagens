package com.example.gerenciamentoviagens.data.repository

import com.example.gerenciamentoviagens.data.local.dao.ViagemDao
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import kotlinx.coroutines.flow.Flow

class ViagemRepository(private val viagemDao: ViagemDao) {
    fun getViagensByUser(userId: Int): Flow<List<Viagem>> = viagemDao.getViagensByUser(userId)
    
    suspend fun insert(viagem: Viagem) = viagemDao.insert(viagem)
    
    suspend fun update(viagem: Viagem) = viagemDao.update(viagem)
    
    suspend fun delete(viagem: Viagem) = viagemDao.delete(viagem)
    
    suspend fun getViagemById(id: Int) = viagemDao.getViagemById(id)

    suspend fun getViagemAtual(userId: Int, cidade: String, dataAtual: Long) = 
        viagemDao.getViagemAtual(userId, cidade, dataAtual)
}
