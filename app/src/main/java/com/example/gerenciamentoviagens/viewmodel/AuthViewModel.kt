package com.example.gerenciamentoviagens.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Preencha todos os campos"
        } else {
            errorMessage = ""
            onSuccess()
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || phone.isBlank()
            || password.isBlank() || confirmPassword.isBlank()
        ) {
            errorMessage = "Todos os campos são obrigatórios"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Senhas não coincidem"
            return
        }
        viewModelScope.launch {
            try {
                repository.salvar(
                    Usuario(
                        nome = name,
                        email = email,
                        telefone = phone,
                        senha = password
                    )
                )
                successMessage = "Usuário cadastrado com sucesso!"
                errorMessage = ""
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Erro ao salvar usuário"
            }
        }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        if (email.isBlank()) {
            errorMessage = "Informe o email"
        } else {
            errorMessage = ""
            onSuccess()
        }
    }
}