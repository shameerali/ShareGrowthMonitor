package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.ProfitLossData
import com.irothink.sharegrowthmonitor.domain.model.StockPL
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetProfitLossUseCase @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val companyRepository: CompanyRepository,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) {
    operator fun invoke(): Flow<ProfitLossData> {
        return combine(
            portfolioRepository.getAllTransactions(),
            companyRepository.getAllCompanies(),
            getPortfolioSummaryUseCase()
        ) { transactions, companies, portfolioSummary ->
            
            val companiesMap = companies.associateBy { it.symbol }
            val stockPLMap = mutableMapOf<String, StockPLCalculation>()
            
            // Calculate realized P&L by processing transactions chronologically
            transactions.sortedBy { it.date }.forEach { transaction ->
                when (transaction.type) {
                    TransactionType.BUY -> {
                        val calc = stockPLMap.getOrPut(transaction.symbol) {
                            StockPLCalculation(
                                symbol = transaction.symbol,
                                companyName = transaction.mainCompanyName
                            )
                        }
                        
                        // Update average cost using weighted average
                        val totalCost = (calc.avgCost * calc.quantity) + transaction.netAmount
                        calc.quantity += transaction.quantity
                        calc.avgCost = if (calc.quantity > 0) totalCost / calc.quantity else 0.0
                        calc.sharesBought += transaction.quantity
                        calc.totalInvested += transaction.netAmount
                    }
                    
                    TransactionType.SELL -> {
                        val calc = stockPLMap.getOrPut(transaction.symbol) {
                            StockPLCalculation(
                                symbol = transaction.symbol,
                                companyName = transaction.mainCompanyName
                            )
                        }
                        
                        // Calculate realized P&L for this sale
                        val costBasis = calc.avgCost * transaction.quantity
                        val proceeds = transaction.netAmount
                        calc.realizedPL += (proceeds - costBasis)
                        calc.quantity -= transaction.quantity
                        calc.sharesSold += transaction.quantity
                        calc.totalInvested -= costBasis
                    }
                    
                    else -> {} // Ignore DEPOSIT/WITHDRAW
                }
            }
            
            // Get unrealized P&L from portfolio summary
            val holdingsMap = portfolioSummary.holdings.associateBy { it.stockSymbol }
            
            // Build stock P&L list
            val allSymbols = (stockPLMap.keys + holdingsMap.keys).distinct()
            val stockPLList = allSymbols.mapNotNull { symbol ->
                val calc = stockPLMap[symbol]
                val holding = holdingsMap[symbol]
                val companyName = companiesMap[symbol]?.name ?: calc?.companyName ?: symbol
                
                val realizedPL = calc?.realizedPL ?: 0.0
                val unrealizedPL = holding?.profitLoss ?: 0.0
                val totalPL = realizedPL + unrealizedPL
                
                val sharesBought = calc?.sharesBought ?: 0.0
                val sharesSold = calc?.sharesSold ?: 0.0
                val sharesHeld = holding?.quantity ?: 0.0
                val totalInvested = calc?.totalInvested ?: 0.0
                
                val totalPLPercentage = if (totalInvested > 0) {
                    (totalPL / totalInvested) * 100
                } else 0.0
                
                // Only include stocks that have had transactions
                if (sharesBought > 0 || sharesSold > 0 || sharesHeld > 0) {
                    StockPL(
                        symbol = symbol,
                        companyName = companyName,
                        realizedPL = realizedPL,
                        unrealizedPL = unrealizedPL,
                        totalPL = totalPL,
                        totalPLPercentage = totalPLPercentage,
                        sharesBought = sharesBought,
                        sharesSold = sharesSold,
                        sharesHeld = sharesHeld,
                        totalInvested = totalInvested
                    )
                } else null
            }.sortedByDescending { it.totalPL }
            
            // Calculate totals
            val totalRealizedPL = stockPLList.sumOf { it.realizedPL }
            val totalUnrealizedPL = portfolioSummary.totalProfitLoss
            val overallPL = totalRealizedPL + totalUnrealizedPL
            val totalInvestedEver = stockPLList.sumOf { it.totalInvested } + portfolioSummary.totalInvested
            val overallPLPercentage = if (totalInvestedEver > 0) {
                (overallPL / totalInvestedEver) * 100
            } else 0.0
            
            ProfitLossData(
                totalRealizedPL = totalRealizedPL,
                totalUnrealizedPL = totalUnrealizedPL,
                overallPL = overallPL,
                overallPLPercentage = overallPLPercentage,
                stockPLList = stockPLList
            )
        }
    }
    
    private data class StockPLCalculation(
        val symbol: String,
        val companyName: String,
        var quantity: Double = 0.0,
        var avgCost: Double = 0.0,
        var realizedPL: Double = 0.0,
        var sharesBought: Double = 0.0,
        var sharesSold: Double = 0.0,
        var totalInvested: Double = 0.0
    )
}
