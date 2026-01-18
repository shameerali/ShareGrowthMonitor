package com.irothink.sharegrowthmonitor.ui.transactions.add

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    
    val state by viewModel.uiState.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Navigation event
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateUp()
        }
    }

    // Material3 DatePicker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.date
    )

    // Show Material3 DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onEvent(AddTransactionEvent.DateChanged(millis))
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
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ---------------- Transaction Type ---------------- */

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Type:", modifier = Modifier.padding(end = 16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.type == TransactionType.BUY,
                        onClick = {
                            viewModel.onEvent(
                                AddTransactionEvent.TypeChanged(TransactionType.BUY)
                            )
                        }
                    )
                    Text(
                        "Buy",
                        modifier = Modifier.clickable {
                            viewModel.onEvent(
                                AddTransactionEvent.TypeChanged(TransactionType.BUY)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.type == TransactionType.SELL,
                        onClick = {
                            viewModel.onEvent(
                                AddTransactionEvent.TypeChanged(TransactionType.SELL)
                            )
                        }
                    )
                    Text(
                        "Sell",
                        modifier = Modifier.clickable {
                            viewModel.onEvent(
                                AddTransactionEvent.TypeChanged(TransactionType.SELL)
                            )
                        }
                    )
                }
            }

            /* ---------------- Company Dropdown ---------------- */

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.selectedCompany?.let {
                        "${it.name} (${it.symbol})"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Company") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    companies.forEach { company ->
                        DropdownMenuItem(
                            text = {
                                Text("${company.name} (${company.symbol})")
                            },
                            onClick = {
                                viewModel.onEvent(
                                    AddTransactionEvent.CompanySelected(company)
                                )
                                expanded = false
                            }
                        )
                    }
                }
            }

            /* ---------------- Quantity ---------------- */

            OutlinedTextField(
                value = state.quantity,
                onValueChange = {
                    viewModel.onEvent(
                        AddTransactionEvent.QuantityChanged(it)
                    )
                },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            /* ---------------- Price ---------------- */

            OutlinedTextField(
                value = state.price,
                onValueChange = {
                    viewModel.onEvent(
                        AddTransactionEvent.PriceChanged(it)
                    )
                },
                label = { Text("Price per Share") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            /* ---------------- Brokerage Fee ---------------- */

            OutlinedTextField(
                value = state.brokerageFee,
                onValueChange = {
                    viewModel.onEvent(
                        AddTransactionEvent.BrokerageFeeChanged(it)
                    )
                },
                label = { Text("Brokerage Fee") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            /* ---------------- Taxes ---------------- */

            OutlinedTextField(
                value = state.taxAmount,
                onValueChange = {
                    viewModel.onEvent(
                        AddTransactionEvent.TaxAmountChanged(it)
                    )
                },
                label = { Text("Taxes") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            /* ---------------- Date ---------------- */

            OutlinedTextField(
                value = SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(state.date),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )

            /* ---------------- Notes ---------------- */

            OutlinedTextField(
                value = state.notes,
                onValueChange = {
                    viewModel.onEvent(
                        AddTransactionEvent.NotesChanged(it)
                    )
                },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            /* ---------------- Save ---------------- */

            Button(
                onClick = {
                    viewModel.onEvent(AddTransactionEvent.SaveClicked)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }
        }
    }
}
