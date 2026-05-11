package com.example.gerenciamentoviagens.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.utils.LocationHelper
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(nav: NavHostController, email: String, user: Usuario?, viagemVm: ViagemViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val locationHelper = remember { LocationHelper(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted && user != null) {
            scope.launch {
                val cidade = locationHelper.getCidadeAtual()
                if (cidade != null) {
                    viagemVm.buscarViagemPelaCidade(user.id, cidade)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission && user != null) {
            val cidade = locationHelper.getCidadeAtual()
            if (cidade != null) {
                viagemVm.buscarViagemPelaCidade(user.id, cidade)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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
                verticalArrangement = Arrangement.Top
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

                // Informações da Viagem Atual baseada na Localização
                viagemVm.cidadeAtual?.let { cidade ->
                    Text(
                        text = "Você está em: $cidade",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    val viagem = viagemVm.viagemAtual
                    if (viagem != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Viagem em andamento:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Destino: ${viagem.destino}")
                                Text("Início: ${dateFormatter.format(Date(viagem.dataInicio))}")
                                Text("Fim: ${dateFormatter.format(Date(viagem.dataFim))}")
                                Text("Tipo: ${viagem.tipo}")
                                Text("Orçamento: R$ ${String.format("%.2f", viagem.orcamento)}")
                                Text("Total de Gastos: R$ ${String.format("%.2f", viagem.gastos)}")
                            }
                        }
                    } else {
                        Text(
                            "Nenhuma viagem programada para esta cidade hoje.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } ?: run {
                    Text("Obtendo localização...", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Use o menu lateral para navegar", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
