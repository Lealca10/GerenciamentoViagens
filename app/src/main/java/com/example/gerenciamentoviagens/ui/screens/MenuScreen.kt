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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.utils.LocationHelper
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(nav: NavHostController, email: String, user: Usuario?, viagemVm: ViagemViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val locationHelper = remember { LocationHelper(context) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted && user != null) {
            scope.launch {
                locationHelper.getCidadeAtualFlow().collectLatest { cidade ->
                    if (cidade != null) {
                        viagemVm.buscarViagemPelaCidade(user.id, cidade)
                    }
                }
            }
            scope.launch {
                locationHelper.getLocalizacaoAtualFlow().collectLatest { location ->
                    viagemVm.latitudeAtual = location?.latitude
                    viagemVm.longitudeAtual = location?.longitude
                }
            }
        }
    }

    LaunchedEffect(user) {
        if (user == null) return@LaunchedEffect

        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            scope.launch {
                locationHelper.getCidadeAtualFlow().collectLatest { cidade ->
                    if (cidade != null) {
                        viagemVm.buscarViagemPelaCidade(user.id, cidade)
                    }
                }
            }
            scope.launch {
                locationHelper.getLocalizacaoAtualFlow().collectLatest { location ->
                    viagemVm.latitudeAtual = location?.latitude
                    viagemVm.longitudeAtual = location?.longitude
                }
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
                    title = { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Menu Principal", style = MaterialTheme.typography.titleMedium)
                            if (user != null) {
                                Text(
                                    user.nome, 
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                if (viagemVm.viagemAtual != null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Map, contentDescription = null) },
                            label = { Text("Roteiro") },
                            selected = false,
                            onClick = { /* Futuro */ }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                            label = { Text("Fotos") },
                            selected = false,
                            onClick = {
                                nav.navigate("photos/${viagemVm.viagemAtual!!.id}")
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header compacto
                viagemVm.cidadeAtual?.let { cidade ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Você está em: $cidade",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    val viagem = viagemVm.viagemAtual
                    if (viagem != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Card de Viagem mais compacto
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Viagem em andamento",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        viagem.destino,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                AssistChip(
                                    onClick = { /* Detalhes */ },
                                    label = { Text(viagem.tipo) },
                                    leadingIcon = {
                                        Icon(
                                            if (viagem.tipo == "Lazer") Icons.Default.BeachAccess else Icons.Default.Work,
                                            contentDescription = null,
                                            Modifier.size(AssistChipDefaults.IconSize)
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mapa ocupando o restante do espaço útil
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    MapView(ctx).apply {
                                        setTileSource(TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(true)
                                        controller.setZoom(15.0)
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                                update = { mapView ->
                                    val lat = viagemVm.latitudeAtual
                                    val lon = viagemVm.longitudeAtual
                                    if (lat != null && lon != null) {
                                        val point = GeoPoint(lat, lon)
                                        mapView.controller.animateTo(point)
                                        
                                        mapView.overlays.clear()
                                        val marker = Marker(mapView)
                                        marker.position = point
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        marker.title = "Sua localização"
                                        mapView.overlays.add(marker)
                                        mapView.invalidate()
                                    }
                                }
                            )
                        }

                    } else {
                        Spacer(modifier = Modifier.height(24.dp))
                        Icon(
                            Icons.Default.TravelExplore, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Nenhuma viagem em andamento\npara esta cidade hoje.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Buscando sua localização...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
