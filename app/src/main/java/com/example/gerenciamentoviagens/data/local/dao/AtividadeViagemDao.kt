package com.example.gerenciamentoviagens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.gerenciamentoviagens.data.local.entity.AtividadeViagem
import kotlinx.coroutines.flow.Flow

@Dao
interface AtividadeViagemDao {
    @Insert
    suspend fun insertAll(atividades: List<AtividadeViagem>)

    @Query("DELETE FROM atividades_viagem WHERE viagemId = :viagemId")
    suspend fun deleteByViagem(viagemId: Int)

    @Query("SELECT * FROM atividades_viagem WHERE viagemId = :viagemId ORDER BY dia, horario")
    fun getAtividadesByViagem(viagemId: Int): Flow<List<AtividadeViagem>>

    @Transaction
    suspend fun replaceAtividades(viagemId: Int, atividades: List<AtividadeViagem>) {
        deleteByViagem(viagemId)
        insertAll(atividades)
    }
}
