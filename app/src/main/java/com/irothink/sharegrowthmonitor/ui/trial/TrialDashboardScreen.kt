package com.irothink.sharegrowthmonitor.ui.trial

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.irothink.sharegrowthmonitor.domain.model.Holding
import com.irothink.sharegrowthmonitor.domain.model.PortfolioSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialDashboardScreen(
    onNavigateUp: () -> Unit,
    onNavigateToAddForTrial: () -> Unit,
    viewModel: TrialDashboardViewModel = hiltViewModel()
) {
    val portfolioSummary by viewModel.portfolioSummary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Trial Portfolio", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Practice Mode", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF3E0), // Light Orange/Amber for distinction
                    titleContentColor = Color(0xFFE65100),
                    navigationIconContentColor = Color(0xFFE65100)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddForTrial,
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trial Transaction")
            }
        },
        containerColor = Color(0xFFFFF8E1) // Very light amber background
    ) { padding ->
        portfolioSummary?.let { summary ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                item {
                    TrialPortfolioSummaryCard(summary)
                }

                item {
                    Text(
                        text = "Trial Holdings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }

                if (summary.holdings.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Your trial portfolio is empty.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Add a transaction to start practicing!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(summary.holdings) { holding ->
                        TrialHoldingItem(holding)
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
                Text("Loading trial data...")
            }
        }
    }
}

@Composable
fun TrialPortfolioSummaryCard(summary: PortfolioSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFE0B2) // Light Orange
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Current Value",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE65100)
            )
            Text(
                text = "$${String.format("%.2f", summary.currentValue)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Invested",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE65100).copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$${String.format("%.2f", summary.totalInvested)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Profit/Loss",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE65100).copy(alpha = 0.8f)
                    )
                    
                    val plColor = if (summary.totalProfitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    val prefix = if (summary.totalProfitLoss >= 0) "+" else ""
                    
                    Text(
                        text = "$prefix$${String.format("%.2f", summary.totalProfitLoss)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = plColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE65100).copy(alpha = 0.2f)))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Available Funds",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "$${String.format("%.2f", summary.availableFunds)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun TrialHoldingItem(holding: Holding) {
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
                Text(
                    text = holding.stockSymbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", holding.totalValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${String.format("%.2f", holding.quantity)} @ $${String.format("%.2f", holding.averagePrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                val plColor = if (holding.profitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                val prefix = if (holding.profitLoss >= 0) "+" else ""
                
                Text(
                    text = "$prefix$${String.format("%.2f", holding.profitLoss)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = plColor
                )
            }
        }
    }
}
