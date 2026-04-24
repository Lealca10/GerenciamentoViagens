package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.ui.components.PasswordField
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(nav: NavHostController, vm: AuthViewModel) {

    // Exibe o Snackbar de sucesso antes de navegar
    var showSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            snackbarHostState.showSnackbar("Usuário cadastrado com sucesso!")
            nav.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Criar Conta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Preencha seus dados",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 👤 NOME
            TextField(
                value = vm.name,
                onValueChange = { vm.name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📧 EMAIL
            TextField(
                value = vm.email,
                onValueChange = { vm.email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📱 TELEFONE
            TextField(
                value = vm.phone,
                onValueChange = { vm.phone = it },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔒 SENHA
            PasswordField(
                value = vm.password,
                onValueChange = { vm.password = it },
                label = "Senha"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔒 CONFIRMAR SENHA
            PasswordField(
                value = vm.confirmPassword,
                onValueChange = { vm.confirmPassword = it },
                label = "Confirmar senha"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔘 BOTÃO
            Button(
                onClick = {
                    vm.register {
                        showSuccess = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔙 VOLTAR
            TextButton(onClick = { nav.popBackStack() }) {
                Text("Já tenho conta")
            }

            // ❌ ERRO
            if (vm.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = vm.errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
