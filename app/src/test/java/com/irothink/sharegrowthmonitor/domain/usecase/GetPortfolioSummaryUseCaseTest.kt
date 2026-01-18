package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPortfolioSummaryUseCaseTest {

    @Test
    fun `calculate portfolio summary correctly`() = runBlocking {
        // Given
        val transactions = listOf(
            Transaction(1, "AAPL", 1000L, 10.0, 150.0, TransactionType.BUY),
            Transaction(2, "AAPL", 2000L, 5.0, 160.0, TransactionType.BUY),
            Transaction(3, "GOOG", 3000L, 2.0, 2000.0, TransactionType.BUY),
            Transaction(4, "AAPL", 4000L, 5.0, 170.0, TransactionType.SELL)
        )
        val fakeRepository = FakePortfolioRepository(transactions)
        val useCase = GetPortfolioSummaryUseCase(fakeRepository)

        // When
        val summary = useCase().first()

        // Then
        // AAPL:
        // Buy 10 @ 150 = 1500
        // Buy 5 @ 160 = 800
        // Total Cost: 2300, Total Qty: 15, Avg Price: 153.333
        // Sell 5. Qty becomes 10. Avg Price stays 153.333.
        // Current Price (from last transaction 170? No, last transaction for AAPL was SELL at 170. So current price 170).
        // AAPL Value: 10 * 170 = 1700.
        // AAPL P/L: 1700 - (10 * 153.333) = 1700 - 1533.33 = 166.67
        
        // GOOG:
        // Buy 2 @ 2000 = 4000.
        // Current Price 2000.
        // Value: 4000.
        // P/L: 0.

        // Total Value: 1700 + 4000 = 5700.
        // Total Invested: (10 * 153.333) + (2 * 2000) = 1533.33 + 4000 = 5533.33
        
        assertEquals(2, summary.holdings.size)
        
        val aapl = summary.holdings.find { it.stockSymbol == "AAPL" }!!
        assertEquals(10.0, aapl.quantity, 0.01)
        assertEquals(153.33, aapl.averagePrice, 0.01)
        assertEquals(1700.0, aapl.totalValue, 0.01)

        assertEquals(5700.0, summary.currentValue, 0.01)
    }
}

class FakePortfolioRepository(private val transactions: List<Transaction>) : PortfolioRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(transactions)
    override fun getTransactionsForStock(symbol: String): Flow<List<Transaction>> = flowOf(transactions.filter { it.stockSymbol == symbol })
    override suspend fun insertTransaction(transaction: Transaction) {}
    override suspend fun updateTransaction(transaction: Transaction) {}
    override suspend fun deleteTransaction(transaction: Transaction) {}
}
