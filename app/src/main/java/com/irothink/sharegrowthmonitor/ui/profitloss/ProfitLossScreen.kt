package com.irothink.sharegrowthmonitor.ui.profitloss

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.ProfitLossData
import com.irothink.sharegrowthmonitor.domain.model.StockPL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitLossScreen(
    onNavigateUp: () -> Unit,
    viewModel: ProfitLossViewModel = hiltViewModel()
) {
    val profitLossData by viewModel.profitLossData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profit & Loss") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        profitLossData?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfitLossSummaryCard(data)
                }

                item {
                    Text(
                        text = "Stock-wise P&L",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(data.stockPLList) { stockPL ->
                    StockPLItem(stockPL)
                }

                if (data.stockPLList.isEmpty()) {
                    item {
                        Text(
                            text = "No transactions yet.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading P&L data...")
            }
        }
    }
}

@Composable
fun ProfitLossSummaryCard(data: ProfitLossData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Overall Profit/Loss",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))

            val overallColor = if (data.overallPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
            val prefix = if (data.overallPL >= 0) "+" else ""

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$prefix$${String.format("%.2f", data.overallPL)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = overallColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "($prefix${String.format("%.2f", data.overallPLPercentage)}%)",
                    style = MaterialTheme.typography.titleLarge,
                    color = overallColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Realized P&L",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    val realizedColor = if (data.totalRealizedPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val realizedPrefix = if (data.totalRealizedPL >= 0) "+" else ""
                    Text(
                        text = "$realizedPrefix$${String.format("%.2f", data.totalRealizedPL)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = realizedColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Unrealized P&L",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    val unrealizedColor = if (data.totalUnrealizedPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val unrealizedPrefix = if (data.totalUnrealizedPL >= 0) "+" else ""
                    Text(
                        text = "$unrealizedPrefix$${String.format("%.2f", data.totalUnrealizedPL)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = unrealizedColor
                    )
                }
            }
        }
    }
}

@Composable
fun StockPLItem(stockPL: StockPL) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Symbol and Total P&L
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stockPL.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stockPL.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val totalColor = if (stockPL.totalPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                val totalPrefix = if (stockPL.totalPL >= 0) "+" else ""

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$totalPrefix$${String.format("%.2f", stockPL.totalPL)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = totalColor
                    )
                    Text(
                        text = "($totalPrefix${String.format("%.2f", stockPL.totalPLPercentage)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = totalColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Realized and Unrealized P&L
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Realized",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val realizedColor = if (stockPL.realizedPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val realizedPrefix = if (stockPL.realizedPL >= 0) "+" else ""
                    Text(
                        text = "$realizedPrefix$${String.format("%.2f", stockPL.realizedPL)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = realizedColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Unrealized",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val unrealizedColor = if (stockPL.unrealizedPL >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    val unrealizedPrefix = if (stockPL.unrealizedPL >= 0) "+" else ""
                    Text(
                        text = "$unrealizedPrefix$${String.format("%.2f", stockPL.unrealizedPL)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = unrealizedColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Share counts
            Text(
                text = "Bought: ${stockPL.sharesBought.toInt()}  |  Sold: ${stockPL.sharesSold.toInt()}  |  Held: ${stockPL.sharesHeld.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
