package com.irothink.sharegrowthmonitor.domain.model

data class PortfolioSummary(
    val totalInvested: Double,
    val currentValue: Double, // This would require current price, for now maybe just from last transaction?
    val totalProfitLoss: Double,
    val availableFunds: Double,
    val holdings: List<Holding>
)

data class Holding(
    val stockSymbol: String,
    val quantity: Double,
    val averagePrice: Double,
    val currentPrice: Double, // Placeholder for now
    val totalValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double,
    val netAmountInvested: Double,
    val totalGrossInvested: Double,
    val totalCharges: Double
)
