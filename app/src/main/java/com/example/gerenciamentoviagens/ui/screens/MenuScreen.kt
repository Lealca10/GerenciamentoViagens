package com.example.gerenciamentoviagens.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val blue = Color(0xFF3D5A99)
    val blueLight = Color(0xFFEEF2FF)

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
                    if (cidade != null) viagemVm.buscarViagemPelaCidade(user.id, cidade)
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
                    if (cidade != null) viagemVm.buscarViagemPelaCidade(user.id, cidade)
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
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                // Cabeçalho do drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Default.Flight,
                            contentDescription = null,
                            tint = blue,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Gerenciamento\nde Viagens",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                        if (user != null) {
                            Text(
                                user.nome,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Nova Viagem", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viagemVm.prepararNovaViagem()
                        nav.navigate("new_trip")
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = blue) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Minhas Viagens", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("trips_list")
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = blue) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFFEEEEEE)
                )

                NavigationDrawerItem(
                    label = { Text("Sobre", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("about")
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(color = Color(0xFFEEEEEE))
                NavigationDrawerItem(
                    label = { Text("Sair", color = Color.Red, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("login") {
                            popUpTo("menu/$email") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
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
                            Text(
                                "Menu Principal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E)
                            )
                            if (user != null) {
                                Text(
                                    user.nome,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = blue)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                if (viagemVm.viagemAtual != null) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Map, contentDescription = null) },
                            label = { Text("Roteiro", fontSize = 12.sp) },
                            selected = false,
                            onClick = { nav.navigate("roteiro/${viagemVm.viagemAtual!!.id}") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = blue,
                                indicatorColor = blueLight
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                            label = { Text("Fotos", fontSize = 12.sp) },
                            selected = false,
                            onClick = { nav.navigate("photos/${viagemVm.viagemAtual!!.id}") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = blue,
                                indicatorColor = blueLight
                            )
                        )
                    }
                }
            },
            containerColor = Color(0xFFF5F7FA)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viagemVm.cidadeAtual?.let { cidade ->

                    // Card de localização
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = blueLight),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = blue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Você está em: ${cidade.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = blue
                            )
                        }
                    }

                    val viagem = viagemVm.viagemAtual
                    if (viagem != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Card da viagem em andamento
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = blue),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Viagem em andamento",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        viagem.destino.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (viagem.tipo == "Lazer") Icons.Default.BeachAccess else Icons.Default.Work,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            viagem.tipo,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mapa
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
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
                        Spacer(modifier = Modifier.height(60.dp))
                        Icon(
                            Icons.Default.TravelExplore,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = blue.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Nenhuma viagem em andamento\npara esta cidade hoje.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = {
                                viagemVm.prepararNovaViagem()
                                nav.navigate("new_trip")
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, blue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = blue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nova Viagem", color = blue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = blue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Buscando sua localização...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}