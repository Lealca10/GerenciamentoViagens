package com.example.gerenciamentoviagens.navigation

import android.content.Context
import com.example.gerenciamentoviagens.ui.screens.LoginScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gerenciamentoviagens.data.local.AppDatabase
import com.example.gerenciamentoviagens.data.remote.RetrofitClient
import com.example.gerenciamentoviagens.data.repository.FotoRepository
import com.example.gerenciamentoviagens.data.repository.RoteiroRepository
import com.example.gerenciamentoviagens.data.repository.UsuarioRepository
import com.example.gerenciamentoviagens.data.repository.ViagemRepository
import com.example.gerenciamentoviagens.ui.screens.*
import com.example.gerenciamentoviagens.utils.EnvReader
import com.example.gerenciamentoviagens.viewmodel.AuthViewModel
import com.example.gerenciamentoviagens.viewmodel.FotoViewModel
import com.example.gerenciamentoviagens.viewmodel.RoteiroViewModel
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel

@Composable
fun NavGraph(context: Context) {

    val navController = rememberNavController()

    val db = AppDatabase.getDatabase(context)
    val usuarioDao = db.usuarioDao()
    val viagemDao = db.viagemDao()
    val fotoDao = db.fotoDao()
    val atividadeViagemDao = db.atividadeViagemDao()

    val usuarioRepository = UsuarioRepository(usuarioDao)
    val viagemRepository = ViagemRepository(viagemDao)
    val fotoRepository = FotoRepository(fotoDao)
    val roteiroRepository = RoteiroRepository(RetrofitClient.geminiService, atividadeViagemDao)

    val authVm: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(usuarioRepository) as T
            }
        }
    )

    val viagemVm: ViagemViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViagemViewModel(viagemRepository) as T
            }
        }
    )

    val fotoVm: FotoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FotoViewModel(fotoRepository) as T
            }
        }
    )

    val roteiroVm: RoteiroViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RoteiroViewModel(roteiroRepository) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, authVm)
        }

        composable("register") {
            RegisterScreen(navController, authVm)
        }

        composable("forgot") {
            ForgotPasswordScreen(navController, authVm)
        }

        composable(
            route = "menu/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MenuScreen(navController, email, authVm.loggedUser, viagemVm)
        }

        composable(
            route = "photos/{viagemId}",
            arguments = listOf(navArgument("viagemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val viagemId = backStackEntry.arguments?.getInt("viagemId") ?: 0
            PhotosScreen(navController, fotoVm, viagemId)
        }

        composable(
            route = "roteiro/{viagemId}",
            arguments = listOf(navArgument("viagemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val viagemId = backStackEntry.arguments?.getInt("viagemId") ?: 0
            val apiKey = EnvReader.getApiKey(context)
            RoteiroScreen(navController, roteiroVm, viagemId, viagemVm.viagemAtual, apiKey)
        }

        composable("new_trip") {
            val user = authVm.loggedUser
            if (user != null) {
                NewTripScreen(navController, viagemVm, user.id)
            }
        }

        composable("trips_list") {
            val user = authVm.loggedUser
            if (user != null) {
                TripsListScreen(navController, viagemVm, user.id)
            }
        }

        composable("about") {
            AboutScreen(navController)
        }
    }
}
