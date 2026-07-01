package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(nav: NavHostController, vm: ViagemViewModel, userId: Int) {
    val blue = Color(0xFF3D5A99)
    val blueLight = Color(0xFFEEF2FF)
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val isEditing = vm.viagemId != null

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var destinoLocal by remember { mutableStateOf(vm.destino) }
    var orcamentoLocal by remember { mutableStateOf(vm.orcamento) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Viagem" else "Nova Viagem",
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Campo Destino
            OutlinedTextField(
                value = destinoLocal,
                onValueChange = { destinoLocal = it },
                label = { Text("Destino") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = blue)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blue,
                    focusedLabelColor = blue,
                    cursorColor = blue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Tipo de Viagem
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        "Tipo de Viagem",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Opção Lazer
                        val lazerSelected = vm.tipo == "Lazer"
                        Card(
                            onClick = { vm.tipo = "Lazer" },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (lazerSelected) blue else blueLight
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.BeachAccess,
                                    contentDescription = null,
                                    tint = if (lazerSelected) Color.White else blue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Lazer",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (lazerSelected) Color.White else blue
                                )
                            }
                        }

                        // Opção Negócios
                        val negociosSelected = vm.tipo == "Negócios"
                        Card(
                            onClick = { vm.tipo = "Negócios" },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (negociosSelected) blue else blueLight
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    tint = if (negociosSelected) Color.White else blue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Negócios",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (negociosSelected) Color.White else blue
                                )
                            }
                        }
                    }
                }
            }

            // Datas
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        "Período da Viagem",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Data Início
                    OutlinedButton(
                        onClick = { showStartPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = blue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (vm.dataInicio != null) blue else Color(0xFFCCCCCC))
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (vm.dataInicio == null) "Data de Início"
                            else "Início: ${dateFormatter.format(Date(vm.dataInicio!!))}"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Data Fim
                    OutlinedButton(
                        onClick = { showEndPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = blue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (vm.dataFim != null) blue else Color(0xFFCCCCCC))
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (vm.dataFim == null) "Data de Fim"
                            else "Fim: ${dateFormatter.format(Date(vm.dataFim!!))}"
                        )
                    }
                }
            }

            // Orçamento
            OutlinedTextField(
                value = orcamentoLocal,
                onValueChange = { orcamentoLocal = it },
                label = { Text("Orçamento (R$)") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = blue)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blue,
                    focusedLabelColor = blue,
                    cursorColor = blue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Salvar
            Button(
                onClick = {
                    vm.destino = destinoLocal
                    vm.orcamento = orcamentoLocal
                    vm.salvarViagem(userId) {
                        nav.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue)
            ) {
                Text(
                    if (isEditing) "Salvar Alterações" else "Salvar Viagem",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (vm.errorMessage.isNotEmpty()) {
                Text(
                    vm.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }
        }
    }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = vm.dataInicio)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.dataInicio = datePickerState.selectedDateMillis
                    showStartPicker = false
                }) { Text("Confirmar", color = blue) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = vm.dataFim)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.dataFim = datePickerState.selectedDateMillis
                    showEndPicker = false
                }) { Text("Confirmar", color = blue) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}