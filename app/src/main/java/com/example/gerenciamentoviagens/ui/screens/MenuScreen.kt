package com.example.gerenciamentoviagens.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(nav: NavHostController, email: String, user: Usuario?, viagemVm: ViagemViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Handle back button to exit app
    BackHandler(enabled = true) {
        (context as? Activity)?.finish()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Gerenciamento de Viagens",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                
                NavigationDrawerItem(
                    label = { Text("Nova Viagem") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viagemVm.prepararNovaViagem()
                        nav.navigate("new_trip")
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Minhas Viagens") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("trips_list")
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    label = { Text("Sobre") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("about")
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("login") {
                            popUpTo("menu/$email") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Menu Principal") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bem-vindo,",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = user?.nome ?: email,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("Use o menu lateral para navegar", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
