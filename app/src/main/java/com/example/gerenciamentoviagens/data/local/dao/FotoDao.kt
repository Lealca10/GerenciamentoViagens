package com.example.gerenciamentoviagens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gerenciamentoviagens.data.local.entity.Foto
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoDao {
    @Query("SELECT * FROM fotos WHERE viagemId = :viagemId")
    fun getFotosByViagem(viagemId: Int): Flow<List<Foto>>

    @Insert
    suspend fun insert(foto: Foto)
}
