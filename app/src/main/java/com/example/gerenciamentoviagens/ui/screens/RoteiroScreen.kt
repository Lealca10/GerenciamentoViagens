package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.data.local.entity.Viagem
import com.example.gerenciamentoviagens.viewmodel.RoteiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoteiroScreen(
    nav: NavHostController,
    vm: RoteiroViewModel,
    viagemId: Int,
    viagem: Viagem?,
    apiKey: String
) {
    val atividades by vm.atividades.collectAsState()

    LaunchedEffect(viagemId) {
        vm.carregarRoteiro(viagemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roteiro IA") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (vm.isGenerating) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("O Gemini está montando seu roteiro...")
                    }
                }
            } else if (atividades.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Nenhum roteiro gerado para esta viagem.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { if (viagem != null) vm.gerarERoteiro(apiKey, viagem) },
                            enabled = viagem != null
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gerar Roteiro com Gemini")
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val grouped = atividades.groupBy { it.dia }
                    grouped.forEach { (dia, lista) ->
                        item {
                            Text("Dia $dia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                        }
                        items(lista) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(item.horario, fontWeight = FontWeight.Bold)
                                        Text(item.custoEstimado, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Text(item.descricao)
                                }
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = { if (viagem != null) vm.gerarERoteiro(apiKey, viagem) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        ) {
                            Text("Regerar com IA")
                        }
                    }
                }
            }
        }
    }
}
