package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.ui.components.PasswordField
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(nav: NavHostController, vm: AuthViewModel) {
    val blue = Color(0xFF3D5A99)

    var showSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var nameLocal by remember { mutableStateOf(vm.name) }
    var emailLocal by remember { mutableStateOf(vm.email) }
    var phoneLocal by remember { mutableStateOf(vm.phone) }
    var passwordLocal by remember { mutableStateOf(vm.password) }
    var confirmPasswordLocal by remember { mutableStateOf(vm.confirmPassword) }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            snackbarHostState.showSnackbar("Usuário cadastrado com sucesso!")
            nav.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Criar Conta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Preencha seus dados para começar",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nome
            OutlinedTextField(
                value = nameLocal,
                onValueChange = {
                    nameLocal = it
                    vm.name = it
                },
                label = { Text("Nome completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = blue)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blue,
                    focusedLabelColor = blue,
                    cursorColor = blue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = emailLocal,
                onValueChange = {
                    emailLocal = it
                    vm.email = it
                },
                label = { Text("E-mail") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = blue)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blue,
                    focusedLabelColor = blue,
                    cursorColor = blue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Telefone
            OutlinedTextField(
                value = phoneLocal,
                onValueChange = {
                    phoneLocal = it
                    vm.phone = it
                },
                label = { Text("Telefone") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = blue)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blue,
                    focusedLabelColor = blue,
                    cursorColor = blue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Senha
            PasswordField(
                value = passwordLocal,
                onValueChange = {
                    passwordLocal = it
                    vm.password = it
                },
                label = "Senha"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirmar Senha
            PasswordField(
                value = confirmPasswordLocal,
                onValueChange = {
                    confirmPasswordLocal = it
                    vm.confirmPassword = it
                },
                label = "Confirmar senha"
            )

            if (vm.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = vm.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botão Registrar
            Button(
                onClick = {
                    vm.name = nameLocal
                    vm.email = emailLocal
                    vm.phone = phoneLocal
                    vm.password = passwordLocal
                    vm.confirmPassword = confirmPasswordLocal
                    vm.register { showSuccess = true }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) {
                Text("Registrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Já tem uma conta?", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = { nav.popBackStack() }) {
                    Text(
                        "Fazer login",
                        color = blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}