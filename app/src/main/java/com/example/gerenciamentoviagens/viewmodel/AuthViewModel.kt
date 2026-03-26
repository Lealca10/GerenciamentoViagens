package com.example.gerenciamentoviagens.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var errorMessage by mutableStateOf("")

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Preencha todos os campos"
        } else {
            errorMessage = ""
            onSuccess()
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() ||
            password.isBlank() || confirmPassword.isBlank()
        ) {
            errorMessage = "Todos os campos são obrigatórios"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "As senhas não coincidem"
            return
        }

        errorMessage = ""
        onSuccess()
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