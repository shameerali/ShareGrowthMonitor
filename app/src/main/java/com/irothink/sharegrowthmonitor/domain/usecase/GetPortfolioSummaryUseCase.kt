package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.Holding
import com.irothink.sharegrowthmonitor.domain.model.PortfolioSummary
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPortfolioSummaryUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return repository.getAllTransactions().map { transactions ->
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
                        current.investedAmount += newCost
                        
                        // Deduct from available funds
                        availableFunds -= transaction.netAmount
                    }
                    TransactionType.SELL -> {
                         val current = holdingsMap.getOrPut(transaction.symbol) {
                             CalculatedHolding(transaction.symbol)
                        }
                        // SELL logic
                        // Realized P/L calculation could happen here, but for simple holdings:
                        // Decrement quantity. Average price usually stays same (FIFO/Average Cost)
                        current.quantity -= transaction.quantity
                        // We assume selling reduces invested amount proportionally to avg price?
                        // Or we just track remaining quantity and avg price remains same until next buy
                        // Standard approach: Sell reduces quantity, keeps average price same.
                        if (current.quantity < 0) current.quantity = 0.0 // data issue protection
                        
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
                
                // Track last price as current price proxy if available (naive approach)
                if (transaction.type == TransactionType.BUY || transaction.type == TransactionType.SELL) {
                     val current = holdingsMap[transaction.symbol]
                     current?.currentPrice = transaction.pricePerShare
                }
            }

            val holdings = holdingsMap.values.filter { it.quantity > 0 }.map {
                Holding(
                    stockSymbol = it.symbol,
                    quantity = it.quantity,
                    averagePrice = it.averagePrice,
                    currentPrice = it.currentPrice,
                    totalValue = it.quantity * it.currentPrice,
                    profitLoss = (it.quantity * it.currentPrice) - (it.quantity * it.averagePrice)
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
        var currentPrice: Double = 0.0,
        var investedAmount: Double = 0.0
    )
}
