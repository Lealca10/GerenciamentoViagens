package com.example.gerenciamentoviagens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gerenciamentoviagens.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun login(email: String, senha: String): Usuario?
}