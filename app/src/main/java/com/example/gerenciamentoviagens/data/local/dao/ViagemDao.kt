package com.example.gerenciamentoviagens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import kotlinx.coroutines.flow.Flow

@Dao
interface ViagemDao {
    @Insert
    suspend fun insert(viagem: Viagem)

    @Update
    suspend fun update(viagem: Viagem)

    @Delete
    suspend fun delete(viagem: Viagem)

    @Query("SELECT * FROM viagens WHERE userId = :userId")
    fun getViagensByUser(userId: Int): Flow<List<Viagem>>

    @Query("SELECT * FROM viagens WHERE id = :id")
    suspend fun getViagemById(id: Int): Viagem?

    @Query("SELECT * FROM viagens WHERE userId = :userId AND LOWER(destino) = LOWER(:cidade) AND :dataAtual BETWEEN dataInicio AND dataFim LIMIT 1")
    suspend fun getViagemAtual(userId: Int, cidade: String, dataAtual: Long): Viagem?
}
