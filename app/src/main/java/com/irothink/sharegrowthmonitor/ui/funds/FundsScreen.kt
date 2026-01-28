package com.irothink.sharegrowthmonitor.ui.funds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundsScreen(
    onNavigateUp: () -> Unit,
    viewModel: FundsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Funds") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            item {
                FundsSummaryCard(
                    availableFunds = uiState.availableFunds,
                    totalDeposit = uiState.totalDeposit,
                    totalWithdrawal = uiState.totalWithdrawal,
                    netDeposit = uiState.netDeposit
                )
            }

            // Transactions List
            if (uiState.listItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No deposit or withdraw transactions yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.listItems) { item ->
                    when (item) {
                        is FundsListItem.Header -> {
                            Text(
                                text = item.date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        is FundsListItem.Item -> {
                            FundsTransactionItem(item.transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FundsSummaryCard(
    availableFunds: Double,
    totalDeposit: Double,
    totalWithdrawal: Double,
    netDeposit: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Available Trade & Net Deposit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Available Trade",
                    amount = availableFunds,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    isLarge = true
                )
                StatItem(
                    label = "Net Deposit",
                    amount = netDeposit,
                    color = if (netDeposit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828), // Darker Green/Red
                    alignment = Alignment.End
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row: Total Deposit & Total Withdraw
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Deposit",
                    amount = totalDeposit,
                    color = Color(0xFF2E7D32) // Darker Green
                )
                StatItem(
                    label = "Total Withdraw",
                    amount = totalWithdrawal,
                    color = Color(0xFFC62828), // Darker Red
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String, 
    amount: Double, 
    color: Color, 
    alignment: Alignment.Horizontal = Alignment.Start,
    isLarge: Boolean = false
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = "$${String.format("%.2f", amount)}",
            style = if (isLarge) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun FundsTransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon background
                val bgColor = if (transaction.type == TransactionType.DEPOSIT) 
                    Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                val iconColor = if (transaction.type == TransactionType.DEPOSIT) 
                    Color(0xFF4CAF50) else Color(0xFFE53935)
                
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .background(bgColor, shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (transaction.type == TransactionType.DEPOSIT) 
                            Icons.Default.ArrowBack else Icons.Default.ArrowBack, // Placeholder, usually would allow different icons
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier
                            .rotate(
                                if (transaction.type == TransactionType.DEPOSIT) -90f else 90f
                            )
                    )
                }

                Column {
                    Text(
                        text = if (transaction.type == TransactionType.DEPOSIT) "Deposit" else "Withdraw",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                            val date = inputFormat.parse(transaction.date)
                            if (date != null) outputFormat.format(date) else transaction.date
                        } catch (e: Exception) {
                            transaction.date
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val amountColor = if (transaction.type == TransactionType.DEPOSIT) 
                Color(0xFF4CAF50) else Color(0xFFE53935)
            val amountPrefix = if (transaction.type == TransactionType.DEPOSIT) "+" else "-"
            
            Text(
                text = "$amountPrefix$${String.format("%.2f", transaction.netAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
