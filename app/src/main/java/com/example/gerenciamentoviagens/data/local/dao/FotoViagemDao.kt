package com.example.gerenciamentoviagens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gerenciamentoviagens.data.local.entity.FotoViagem
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoViagemDao {
    @Insert
    suspend fun insert(foto: FotoViagem)

    @Query("SELECT * FROM fotos_viagem WHERE viagemId = :viagemId")
    fun getFotosByViagem(viagemId: Int): Flow<List<FotoViagem>>
}
