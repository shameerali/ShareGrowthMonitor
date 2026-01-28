package com.irothink.sharegrowthmonitor.ui.transactions.add

import androidx.lifecycle.SavedStateHandle
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import com.irothink.sharegrowthmonitor.domain.usecase.AddTransactionUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.GetTransactionByIdUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.UpdateTransactionUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.GetCompaniesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AddTransactionViewModelTest {

    private lateinit var viewModel: AddTransactionViewModel
    private lateinit var fakePortfolioRepository: FakePortfolioRepository
    private lateinit var fakeCompanyRepository: FakeCompanyRepository

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        
        fakePortfolioRepository = FakePortfolioRepository()
        fakeCompanyRepository = FakeCompanyRepository()
        
        val addTransactionUseCase = AddTransactionUseCase(fakePortfolioRepository)
        val updateTransactionUseCase = UpdateTransactionUseCase(fakePortfolioRepository)
        val getTransactionByIdUseCase = GetTransactionByIdUseCase(fakePortfolioRepository)
        val getCompaniesUseCase = GetCompaniesUseCase(fakeCompanyRepository)
        
        viewModel = AddTransactionViewModel(
            addTransactionUseCase,
            updateTransactionUseCase,
            getTransactionByIdUseCase,
            getCompaniesUseCase,
            SavedStateHandle()
        )
    }

    @Test
    fun `saveTransaction calculates netAmount correctly for BUY`() = runBlocking {
        // Given
        val company = CompanyInfoEntity(1, "Apple", "Tech", "AAPL", 100.0, "")
        viewModel.onEvent(AddTransactionEvent.CompanySelected(company))
        viewModel.onEvent(AddTransactionEvent.QuantityChanged("10"))
        viewModel.onEvent(AddTransactionEvent.PriceChanged("100"))
        viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.BUY))
        viewModel.onEvent(AddTransactionEvent.BrokerageFeeChanged("5"))
        viewModel.onEvent(AddTransactionEvent.TaxAmountChanged("5"))

        // When
        viewModel.onEvent(AddTransactionEvent.SaveClicked)

        // Then
        // Allow some time for coroutines
        Thread.sleep(100)
        
        val transaction = fakePortfolioRepository.lastInsertedTransaction!!
        assertEquals(1010.0, transaction.netAmount, 0.0) // 1000 + 5 + 5
    }

    @Test
    fun `saveTransaction calculates netAmount correctly for SELL`() = runBlocking {
        // Given
        val company = CompanyInfoEntity(1, "Apple", "Tech", "AAPL", 100.0, "")
        viewModel.onEvent(AddTransactionEvent.CompanySelected(company))
        viewModel.onEvent(AddTransactionEvent.QuantityChanged("10"))
        viewModel.onEvent(AddTransactionEvent.PriceChanged("100"))
        viewModel.onEvent(AddTransactionEvent.TypeChanged(TransactionType.SELL))
        viewModel.onEvent(AddTransactionEvent.BrokerageFeeChanged("5"))
        viewModel.onEvent(AddTransactionEvent.TaxAmountChanged("5"))

        // When
        viewModel.onEvent(AddTransactionEvent.SaveClicked)

        // Then
        Thread.sleep(100)
        
        val transaction = fakePortfolioRepository.lastInsertedTransaction!!
        // Expected: 1000 - 5 - 5 = 990
        assertEquals(990.0, transaction.netAmount, 0.0) 
    }
}

class FakePortfolioRepository : PortfolioRepository {
    var lastInsertedTransaction: Transaction? = null
    
    override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(emptyList())
    
    // Adjust signature based on real interface if needed. 
    // Assuming simple insert for now.
    override suspend fun insertTransaction(transaction: Transaction) {
        lastInsertedTransaction = transaction
    }
    
    override suspend fun updateTransaction(transaction: Transaction) {
        lastInsertedTransaction = transaction
    }
    
    override suspend fun deleteTransaction(transaction: Transaction) {}
    
    override fun getTransactionsForStock(symbol: String): Flow<List<Transaction>> = flowOf(emptyList())

    override suspend fun getTransactionById(id: String): Transaction? = null
}

class FakeCompanyRepository : CompanyRepository {
    override fun getAllCompanies(): Flow<List<CompanyInfoEntity>> = flowOf(emptyList())
    override suspend fun insertCompany(company: CompanyInfoEntity) {}
    override suspend fun updateCompany(company: CompanyInfoEntity) {}
    override suspend fun deleteCompany(company: CompanyInfoEntity) {}
}
