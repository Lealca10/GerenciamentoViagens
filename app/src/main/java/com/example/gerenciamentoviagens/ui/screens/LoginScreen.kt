package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.R
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.ui.components.PasswordField
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel

@Composable
fun LoginScreen(nav: NavHostController, vm: AuthViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

//        Image(
//            painter = painterResource(id = R.drawable.travel.png),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(140.dp)
//                .padding(bottom = 16.dp)
//        )

        Text(
            text = "Bem-vindo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 📧 EMAIL
        TextField(
            value = vm.email,
            onValueChange = { vm.email = it },
            label = { Text("Email") },
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

        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 BOTÃO LOGIN
        Button(
            onClick = {
                vm.login {
                    nav.navigate("menu")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔗 LINKS LADO A LADO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { nav.navigate("register") }) {
                Text("Novo usuário")
            }

            TextButton(onClick = { nav.navigate("forgot") }) {
                Text("Esqueci a senha")
            }
        }

        // ❌ ERRO
        if (vm.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(vm.errorMessage, color = Color.Red)
        }
    }
}