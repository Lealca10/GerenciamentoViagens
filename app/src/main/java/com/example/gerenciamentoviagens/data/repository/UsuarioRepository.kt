package com.example.gerenciamentoviagens.data.repository

import com.example.gerenciamentoviagens.data.local.dao.UsuarioDao
import com.example.gerenciamentoviagens.data.local.entity.Usuario

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun salvar(usuario: Usuario) {
        dao.inserir(usuario)
    }
}