package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.ui.components.PasswordField
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel
import com.example.gerenciamentoviagens.R

@Composable
fun LoginScreen(nav: NavHostController, vm: AuthViewModel) {
    val blue = Color(0xFF3D5A99)

    var emailLocal by remember { mutableStateOf(vm.email) }
    var passwordLocal by remember { mutableStateOf(vm.password) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Logo
        Image(
            painter = painterResource(id = R.drawable.aviao),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bem-vindo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )

        Text(
            text = "Faça login para continuar",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

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

        // Senha
        PasswordField(
            value = passwordLocal,
            onValueChange = {
                passwordLocal = it
                vm.password = it
            },
            label = "Senha",
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blue,
                focusedLabelColor = blue,
                cursorColor = blue
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Esqueci a senha alinhado à direita
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { nav.navigate("forgot") }) {
                Text("Esqueci a senha", color = blue, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Login
        Button(
            onClick = {
                vm.login {
                    nav.navigate("menu/${vm.email}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue)
        ) {
            Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Novo usuário
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Não tem uma conta?", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = { nav.navigate("register") }) {
                Text("Criar conta", color = blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        // Erro
        if (vm.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vm.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }
    }
}