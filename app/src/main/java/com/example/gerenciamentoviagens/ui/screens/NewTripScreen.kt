package com.example.gerenciamentoviagens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gerenciamentoviagens.viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(nav: NavHostController, vm: ViagemViewModel, userId: Int) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Viagem") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = vm.destino,
                onValueChange = { vm.destino = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tipo de Viagem", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = vm.tipo == "Lazer",
                    onClick = { vm.tipo = "Lazer" }
                )
                Text("Lazer")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = vm.tipo == "Negócios",
                    onClick = { vm.tipo = "Negócios" }
                )
                Text("Negócios")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data Início
            OutlinedButton(
                onClick = { showStartPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (vm.dataInicio == null) "Data de Início" 
                           else "Início: ${dateFormatter.format(Date(vm.dataInicio!!))}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Data Fim
            OutlinedButton(
                onClick = { showEndPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (vm.dataFim == null) "Data de Fim" 
                           else "Fim: ${dateFormatter.format(Date(vm.dataFim!!))}"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = vm.orcamento,
                onValueChange = { vm.orcamento = it },
                label = { Text("Orçamento (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    vm.salvarViagem(userId) {
                        nav.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Salvar Viagem")
            }

            if (vm.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(vm.errorMessage, color = Color.Red)
            }
        }
    }

    if (showStartPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.dataInicio = datePickerState.selectedDateMillis
                    showStartPicker = false
                }) { Text("Confirmar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.dataFim = datePickerState.selectedDateMillis
                    showEndPicker = false
                }) { Text("Confirmar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
