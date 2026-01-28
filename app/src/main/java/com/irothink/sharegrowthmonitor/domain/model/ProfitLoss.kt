package com.irothink.sharegrowthmonitor.domain.model

data class ProfitLossData(
    val totalRealizedPL: Double,
    val totalUnrealizedPL: Double,
    val overallPL: Double,
    val overallPLPercentage: Double,
    val stockPLList: List<StockPL>
)

data class StockPL(
    val symbol: String,
    val companyName: String,
    val realizedPL: Double,
    val unrealizedPL: Double,
    val totalPL: Double,
    val totalPLPercentage: Double,
    val sharesBought: Double,
    val sharesSold: Double,
    val sharesHeld: Double,
    val totalInvested: Double
)
