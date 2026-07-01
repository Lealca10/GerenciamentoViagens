package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val blue = Color(0xFF3D5A99)
    val blueDark = Color(0xFF2C4175)
    val blueLight = Color(0xFFEEF2FF)

    val atividades by vm.atividades.collectAsState()

    LaunchedEffect(viagemId) {
        vm.carregarRoteiro(viagemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Roteiro IA",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Estado: gerando
                vm.isGenerating -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = blue, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "O Gemini está montando\nseu roteiro...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Estado: sem roteiro
                atividades.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = blue.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Nenhum roteiro gerado\npara esta viagem.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { if (viagem != null) vm.gerarERoteiro(apiKey, viagem) },
                                enabled = viagem != null,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = blue),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Gerar Roteiro com Gemini",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Estado: com roteiro
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        val grouped = atividades.groupBy { it.dia }

                        grouped.forEach { (dia, lista) ->

                            // Header do dia com gradiente
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.horizontalGradient(listOf(blue, Color(0xFF5B7EC9))))
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Dia $dia",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Atividades do dia
                            items(lista) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Linha do tempo
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(blue)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(80.dp)
                                                .background(blue.copy(alpha = 0.2f))
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Card da atividade
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(y = (-4).dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Horário
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = blueLight
                                                ) {
                                                    Text(
                                                        text = item.horario,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = blue,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }

                                                // Custo
                                                val isGratis = item.custoEstimado.contains("grátis", ignoreCase = true)
                                                        || item.custoEstimado == "R$ 0,00"
                                                        || item.custoEstimado == "R$ 0.00"
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (isGratis) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                                ) {
                                                    Text(
                                                        text = item.custoEstimado,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 12.sp,
                                                        color = if (isGratis) Color(0xFF2E7D32) else Color(0xFFE65100),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = item.descricao,
                                                fontSize = 13.sp,
                                                color = Color(0xFF444444),
                                                lineHeight = 19.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Botão regerar
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { if (viagem != null) vm.gerarERoteiro(apiKey, viagem) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = blue),
                                border = androidx.compose.foundation.BorderStroke(1.dp, blue)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Regerar com IA", fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}