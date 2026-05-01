package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TripsListScreen(nav: NavHostController, vm: ViagemViewModel, userId: Int) {
    val viagens by vm.viagens.collectAsState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(userId) {
        vm.carregarViagens(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (viagens.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhuma viagem cadastrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viagens) { viagem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (viagem.tipo == "Lazer") Icons.Default.BeachAccess else Icons.Default.BusinessCenter,
                                contentDescription = viagem.tipo,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = viagem.destino, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    text = "${dateFormatter.format(Date(viagem.dataInicio))} - ${dateFormatter.format(Date(viagem.dataFim))}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Orçamento: R$ ${String.format("%.2f", viagem.orcamento)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            
                            Row {
                                IconButton(onClick = { 
                                    vm.prepararEdicao(viagem)
                                    nav.navigate("new_trip")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { vm.excluirViagem(viagem) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
