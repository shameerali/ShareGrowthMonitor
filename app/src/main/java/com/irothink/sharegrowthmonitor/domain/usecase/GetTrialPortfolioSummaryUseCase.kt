package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.Holding
import com.irothink.sharegrowthmonitor.domain.model.PortfolioSummary
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import com.irothink.sharegrowthmonitor.domain.repository.TrialPortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrialPortfolioSummaryUseCase @Inject constructor(
    private val trialRepository: TrialPortfolioRepository,
    private val companyRepository: CompanyRepository
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return combine(
            trialRepository.getAllTransactions(),
            companyRepository.getAllCompanies()
        ) { transactions, companies ->
            val companiesMap = companies.associateBy { it.symbol }
            val holdingsMap = mutableMapOf<String, CalculatedHolding>()

            var availableFunds = 0.0

            transactions.sortedBy { it.date }.forEach { transaction ->
                when (transaction.type) {
                    TransactionType.BUY -> {
                        val current = holdingsMap.getOrPut(transaction.symbol) {
                             CalculatedHolding(transaction.symbol)
                        }
                        val totalCost = current.quantity * current.averagePrice
                        val newCost = transaction.quantity * transaction.pricePerShare
                        val totalQuantity = current.quantity + transaction.quantity
                        // Weighted Average Price
                        if (totalQuantity > 0) {
                            current.averagePrice = (totalCost + newCost) / totalQuantity
                        }
                        current.quantity += transaction.quantity
                        current.investedAmount += transaction.netAmount
                        current.totalGross += transaction.grossAmount
                        // Deduct from available funds
                        availableFunds -= transaction.netAmount
                    }
                    TransactionType.SELL -> {
                         val current = holdingsMap.getOrPut(transaction.symbol) {
                             CalculatedHolding(transaction.symbol)
                        }
                        
                        // proportional reduction
                        if (current.quantity > 0) {
                            val ratio = transaction.quantity / current.quantity
                            current.totalGross -= (current.totalGross * ratio)
                            current.investedAmount -= (current.investedAmount * ratio)
                        }

                        current.quantity -= transaction.quantity
                        if (current.quantity < 0) current.quantity = 0.0
                        
                        // Add to available funds
                        availableFunds += transaction.netAmount
                    }
                    TransactionType.DEPOSIT -> {
                        availableFunds += transaction.netAmount
                    }
                    TransactionType.WITHDRAW -> {
                        availableFunds -= transaction.netAmount
                    }
                }
                
                if (transaction.type == TransactionType.BUY || transaction.type == TransactionType.SELL) {
                     val current = holdingsMap[transaction.symbol]
                     current?.lastTransactionPrice = transaction.pricePerShare
                }
            }

            val holdings = holdingsMap.values.filter { it.quantity > 0 }.map {
                val dbPrice = companiesMap[it.symbol]?.currentPrice
                val currentPrice = dbPrice ?: it.lastTransactionPrice
                val pl = (it.quantity * currentPrice) - it.investedAmount
                val plPercentage = if (it.investedAmount != 0.0) (pl / it.investedAmount) * 100 else 0.0
                
                Holding(
                    stockSymbol = it.symbol,
                    quantity = it.quantity,
                    averagePrice = it.averagePrice,
                    currentPrice = currentPrice,
                    totalValue = it.quantity * currentPrice,
                    profitLoss = pl,
                    profitLossPercentage = plPercentage,
                    netAmountInvested = it.investedAmount,
                    totalGrossInvested = it.totalGross,
                    totalCharges = 0.0 // Simplified for trial mode
                )
            }

            val totalInvested = holdings.sumOf { it.quantity * it.averagePrice }
            val currentValue = holdings.sumOf { it.totalValue }
            
            PortfolioSummary(
                totalInvested = totalInvested,
                currentValue = currentValue,
                totalProfitLoss = currentValue - totalInvested,
                availableFunds = availableFunds,
                holdings = holdings
            )
        }
    }

    private data class CalculatedHolding(
        val symbol: String,
        var quantity: Double = 0.0,
        var averagePrice: Double = 0.0,
        var lastTransactionPrice: Double = 0.0,
        var investedAmount: Double = 0.0,
        var totalGross: Double = 0.0
    )
}
