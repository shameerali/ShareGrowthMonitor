package com.irothink.sharegrowthmonitor.domain.usecase

import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
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
            Transaction("1", TransactionType.BUY, "Apple", "", "AAPL", 10.0, 150.0, 0.0, 1500.0, 1500.0, "2023-01-01", 0.0, "", ""),
            Transaction("2", TransactionType.BUY, "Apple", "", "AAPL", 5.0, 160.0, 0.0, 800.0, 800.0, "2023-01-02", 0.0, "", ""),
            Transaction("3", TransactionType.BUY, "Google", "", "GOOG", 2.0, 2000.0, 0.0, 4000.0, 4000.0, "2023-01-03", 0.0, "", ""),
            Transaction("4", TransactionType.SELL, "Apple", "", "AAPL", 5.0, 170.0, 0.0, 850.0, 850.0, "2023-01-04", 0.0, "", "")
        )
        val companies = listOf(
            CompanyInfoEntity(id = 1, name = "Apple", symbol = "AAPL", currentPrice = 180.0, createdAt = ""),
            CompanyInfoEntity(id = 2, name = "Google", symbol = "GOOG", currentPrice = 2100.0, createdAt = "")
        )
        val fakePortfolioRepository = FakePortfolioRepository(transactions)
        val fakeCompanyRepository = FakeCompanyRepository(companies)
        val useCase = GetPortfolioSummaryUseCase(fakePortfolioRepository, fakeCompanyRepository)

        // When
        val summary = useCase().first()

        // Then
        // AAPL:
        // Buy 10 @ 150 = 1500
        // Buy 5 @ 160 = 800
        // Total Cost: 2300, Total Qty: 15, Avg Price: 153.333
        // Sell 5. Qty becomes 10. Avg Price stays 153.333.
        // Current Price (from companies table is 180.0).
        // AAPL Value: 10 * 180 = 1800.
        // AAPL P/L: 1800 - (10 * 153.333) = 1800 - 1533.33 = 266.67
        
        // GOOG:
        // Buy 2 @ 2000 = 4000.
        // Current Price (from companies table is 2100.0).
        // Value: 2 * 2100 = 4200.
        // P/L: 4200 - 4000 = 200.

        // Total Value: 1800 + 4200 = 6000.
        // Total Invested: (10 * 153.333) + (2 * 2000) = 1533.33 + 4000 = 5533.33
        
        assertEquals(2, summary.holdings.size)
        
        val aapl = summary.holdings.find { it.stockSymbol == "AAPL" }!!
        assertEquals(10.0, aapl.quantity, 0.01)
        assertEquals(153.33, aapl.averagePrice, 0.01)
        assertEquals(1800.0, aapl.totalValue, 0.01)
        assertEquals(180.0, aapl.currentPrice, 0.01)

        val goog = summary.holdings.find { it.stockSymbol == "GOOG" }!!
        assertEquals(2100.0, goog.currentPrice, 0.01)

        assertEquals(6000.0, summary.currentValue, 0.01)
    }
}

class FakePortfolioRepository(private val transactions: List<Transaction>) : PortfolioRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(transactions)
    override fun getTransactionsForStock(symbol: String): Flow<List<Transaction>> = flowOf(transactions.filter { it.symbol == symbol })
    override suspend fun getTransactionById(id: String): Transaction? = transactions.find { it.id == id }
    override suspend fun insertTransaction(transaction: Transaction) {}
    override suspend fun updateTransaction(transaction: Transaction) {}
    override suspend fun deleteTransaction(transaction: Transaction) {}
}

class FakeCompanyRepository(private val companies: List<CompanyInfoEntity>) : CompanyRepository {
    override fun getAllCompanies(): Flow<List<CompanyInfoEntity>> = flowOf(companies)
    override suspend fun insertCompany(company: CompanyInfoEntity) {}
    override suspend fun updateCompany(company: CompanyInfoEntity) {}
    override suspend fun deleteCompany(company: CompanyInfoEntity) {}
}
