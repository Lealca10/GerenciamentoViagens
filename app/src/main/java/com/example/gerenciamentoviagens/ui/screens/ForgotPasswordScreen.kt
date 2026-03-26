package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(nav: NavHostController, vm: AuthViewModel) {

    Column {

        TextField(
            value = vm.email,
            onValueChange = { vm.email = it },
            label = { Text("Email") }
        )

        Button(onClick = {
            vm.resetPassword {
                nav.popBackStack()
            }
        }) {
            Text("Enviar")
        }

        Text(vm.errorMessage)
    }
}