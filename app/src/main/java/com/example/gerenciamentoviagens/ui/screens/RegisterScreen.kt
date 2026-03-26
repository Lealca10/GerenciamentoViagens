package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.ui.components.PasswordField
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(nav: NavHostController, vm: AuthViewModel) {

    Column {

        TextField(vm.name, { vm.name = it }, label = { Text("Nome") })
        TextField(vm.email, { vm.email = it }, label = { Text("Email") })
        TextField(vm.phone, { vm.phone = it }, label = { Text("Telefone") })

        PasswordField(vm.password, { vm.password = it }, "Senha")
        PasswordField(vm.confirmPassword, { vm.confirmPassword = it }, "Confirmar Senha")

        Button(onClick = {
            vm.register {
                nav.popBackStack() // volta pro login
            }
        }) {
            Text("Registrar")
        }

        Text(vm.errorMessage)
    }
}