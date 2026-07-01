package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TripsListScreen(nav: NavHostController, vm: ViagemViewModel, userId: Int) {
    val blue = Color(0xFF3D5A99)
    val blueDark = Color(0xFF2C4175)
    val blueLight = Color(0xFFEEF2FF)

    val viagens by vm.viagens.collectAsState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var viagemParaExcluir by remember { mutableStateOf<Viagem?>(null) }

    LaunchedEffect(userId) {
        vm.carregarViagens(userId)
    }

    viagemParaExcluir?.let { viagem ->
        AlertDialog(
            onDismissRequest = { viagemParaExcluir = null },
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Excluir viagem", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            },
            text = {
                Text(
                    "Deseja excluir a viagem para ${viagem.destino}? Esta ação não pode ser desfeita.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.excluirViagem(viagem)
                        viagemParaExcluir = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Excluir") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viagemParaExcluir = null },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = blue)
                ) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Minhas Viagens",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = blue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        if (viagens.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.TravelExplore,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = blue.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nenhuma viagem cadastrada.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Crie sua primeira viagem no menu principal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(viagens) { viagem ->
                    val isLazer = viagem.tipo == "Lazer"
                    val gradientColors = if (isLazer)
                        listOf(Color(0xFF3D5A99), Color(0xFF5B7EC9))
                    else
                        listOf(Color(0xFF2C4175), Color(0xFF3D5A99))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(gradientColors))
                            .combinedClickable(
                                onClick = { },
                                onLongClick = {
                                    vm.prepararEdicao(viagem)
                                    nav.navigate("new_trip")
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ícone do tipo
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isLazer) Icons.Default.BeachAccess else Icons.Default.BusinessCenter,
                                        contentDescription = viagem.tipo,
                                        modifier = Modifier.size(28.dp),
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = viagem.destino,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${dateFormatter.format(Date(viagem.dataInicio))} → ${dateFormatter.format(Date(viagem.dataFim))}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "R$ ${String.format("%.2f", viagem.orcamento)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Segure para editar",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.45f)
                                )
                            }

                            IconButton(onClick = { viagemParaExcluir = viagem }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}