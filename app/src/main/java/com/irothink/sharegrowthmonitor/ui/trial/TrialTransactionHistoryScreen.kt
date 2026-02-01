package com.irothink.sharegrowthmonitor.ui.trial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialTransactionHistoryScreen(
    onNavigateUp: () -> Unit,
    viewModel: TrialTransactionHistoryViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trial Transaction History") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { transaction ->
                TrialTransactionHistoryItem(
                    transaction = transaction,
                    onDelete = { transactionToDelete = transaction }
                )
            }
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "No trial transactions found.", 
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Trial Transaction") },
            text = { Text("Are you sure you want to delete this trial transaction for ${transaction.symbol}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        transactionToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TrialTransactionHistoryItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = transaction.symbol, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val date = inputFormat.parse(transaction.date)
                            if (date != null) outputFormat.format(date) else transaction.date
                        } catch (e: Exception) {
                            transaction.date
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val typeColor = if (transaction.type == TransactionType.BUY) Color(0xFF2E7D32) else Color(0xFF1565C0)
                    Text(
                        text = transaction.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = typeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${transaction.quantity} @ $${String.format("%.2f", transaction.pricePerShare)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Net: $${String.format("%.2f", transaction.netAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
            
            if (!transaction.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transaction.notes ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
