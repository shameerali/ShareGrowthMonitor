package com.irothink.sharegrowthmonitor.ui.company.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListScreen(
    onNavigateUp: () -> Unit,
    viewModel: CompanyListViewModel = hiltViewModel()
) {
    val companies by viewModel.companies.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var companyToEdit by remember { mutableStateOf<CompanyInfoEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Companies") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Company")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(companies) { company ->
                CompanyItem(
                    company = company,
                    onEdit = { companyToEdit = company },
                    onDelete = { viewModel.deleteCompany(company) }
                )
            }
        }
    }

    if (showAddDialog) {
        CompanyDialog(
            title = "Add Company",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, industry, symbol, price ->
                viewModel.addCompany(name, industry, symbol, price)
                showAddDialog = false
            }
        )
    }

    companyToEdit?.let { company ->
        CompanyDialog(
            title = "Edit Company",
            initialName = company.name,
            initialIndustry = company.industry,
            initialSymbol = company.symbol,
            initialPrice = company.currentPrice,
            onDismiss = { companyToEdit = null },
            onConfirm = { name, industry, symbol, price ->
                viewModel.updateCompany(company, name, industry, symbol, price)
                companyToEdit = null
            }
        )
    }
}

@Composable
fun CompanyItem(
    company: CompanyInfoEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = company.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Symbol: ${company.symbol}", style = MaterialTheme.typography.bodyMedium)
                if (company.industry.isNotBlank()) {
                    Text(text = company.industry, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = "Price: $${company.currentPrice}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun CompanyDialog(
    title: String,
    initialName: String = "",
    initialIndustry: String = "",
    initialSymbol: String = "",
    initialPrice: Double = 0.0,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var industry by remember { mutableStateOf(initialIndustry) }
    var symbol by remember { mutableStateOf(initialSymbol) }
    var price by remember { mutableStateOf(if (initialPrice > 0) initialPrice.toString() else "") }
    var isNameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        isNameError = false
                    },
                    label = { Text("Name") },
                    isError = isNameError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Symbol") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = { Text("Industry (Optional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Current Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        onConfirm(name, industry, symbol, price.toDoubleOrNull() ?: 0.0)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
