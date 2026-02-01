package com.irothink.sharegrowthmonitor.ui.trial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialAddTransactionScreen(
    onNavigateUp: () -> Unit,
    viewModel: TrialAddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Date Picker Dialog Logic
    val datePickerState = rememberDatePickerState()
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val selectedDate = sdf.format(Date(millis))
                            viewModel.onDateChanged(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Trial Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0),
                    titleContentColor = Color(0xFFE65100),
                    navigationIconContentColor = Color(0xFFE65100)
                )
            )
        },
        containerColor = Color(0xFFFFF8E1)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Transaction Type Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.type == TransactionType.BUY,
                    onClick = { viewModel.onTypeChanged(TransactionType.BUY) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100))
                )
                Text("Buy")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = uiState.type == TransactionType.SELL,
                    onClick = { viewModel.onTypeChanged(TransactionType.SELL) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100))
                )
                Text("Sell")
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Company Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.companyName,
                    onValueChange = { viewModel.onCompanyNameChanged(it) },
                    label = { Text("Company Name") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                
                if (companies.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        companies.forEach { company ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = company.name, style = MaterialTheme.typography.bodyLarge)
                                        Text(text = company.symbol, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    viewModel.onCompanySelected(company)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.symbol,
                onValueChange = viewModel::onSymbolChanged,
                label = { Text("Symbol") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Derived from company selection
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.quantity,
                onValueChange = viewModel::onQuantityChanged,
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.pricePerShare,
                onValueChange = viewModel::onPriceChanged,
                label = { Text("Price per Share") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.brokerageFee,
                    onValueChange = viewModel::onBrokerageFeeChanged,
                    label = { Text("Broker Fees") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = uiState.taxAmount,
                    onValueChange = viewModel::onTaxAmountChanged,
                    label = { Text("Tax") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection with Picker Dialog
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.date,
                    onValueChange = { },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE65100),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                // Click overlay to trigger picker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100).copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gross Amount:", style = MaterialTheme.typography.bodyMedium)
                        Text("$${String.format("%.2f", uiState.grossAmount)}", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Amount:", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE65100))
                        Text(
                            "$${String.format("%.2f", uiState.netAmount)}", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveTransaction(onSuccess = onNavigateUp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Text("Save Trial Transaction")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
