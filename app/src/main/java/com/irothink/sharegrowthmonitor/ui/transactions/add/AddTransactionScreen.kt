package com.irothink.sharegrowthmonitor.ui.transactions.add

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateUp()
        }
    }

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = state.date

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            viewModel.onEvent(AddTransactionEvent.DateChanged(newCalendar.timeInMillis))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type (Buy/Sell)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Type:", modifier = Modifier.padding(end = 16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.type == TransactionType.BUY,
                        onClick = { viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.BUY)) }
                    )
                    Text("Buy", modifier = Modifier.clickable { viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.BUY)) })
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.type == TransactionType.SELL,
                        onClick = { viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.SELL)) }
                    )
                    Text("Sell", modifier = Modifier.clickable { viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.SELL)) })
                }
            }

            // Company Dropdown
            OutlinedTextField(
                value = state.selectedCompany?.let { "${it.name} (${it.symbol})" } ?: "",
                onValueChange = {},
                label = { Text("Select Company") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                companies.forEach { company ->
                    DropdownMenuItem(
                        text = { Text("${company.name} (${company.symbol})") },
                        onClick = {
                            viewModel.onEvent(AddTransactionEvent.CompanySelected(company))
                            expanded = false
                        }
                    )
                }
            }

            // Company Name Display (Read-only)
            OutlinedTextField(
                value = state.companyName,
                onValueChange = {},
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )

            // Stock Symbol Display (Read-only)
            OutlinedTextField(
                value = state.symbol,
                onValueChange = {},
                label = { Text("Stock Symbol") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )

            OutlinedTextField(
                value = state.quantity,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.QuantityChanged(it)) },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.onEvent(AddTransactionEvent.PriceChanged(it)) },
                label = { Text("Price per Share") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(state.date),
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            )

            Button(
                onClick = { viewModel.onEvent(AddTransactionEvent.SaveClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }
        }
    }
}
