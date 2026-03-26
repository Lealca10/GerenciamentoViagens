package com.example.gerenciamentoviagens.navigation

import com.example.gerenciamentoviagens.ui.screens.LoginScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.gerenciamentoviagens.ui.screens.ForgotPasswordScreen
import com.example.gerenciamentoviagens.ui.screens.MenuScreen
import com.example.gerenciamentoviagens.ui.screens.RegisterScreen
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val vm: AuthViewModel = viewModel()

    NavHost(navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, vm)
        }

        composable("register") {
            RegisterScreen(navController, vm)
        }

        composable("forgot") {
            ForgotPasswordScreen(navController, vm)
        }

        composable("menu") {
            MenuScreen()
        }
    }
}