package com.irothink.sharegrowthmonitor.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.PortfolioSummary
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import com.irothink.sharegrowthmonitor.domain.usecase.GetPortfolioSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: PortfolioRepository,
    getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) : ViewModel() {

    val portfolioSummary: StateFlow<PortfolioSummary?> = getPortfolioSummaryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun addFunds(amount: Double) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.DEPOSIT,
                mainCompanyName = "System",
                subCompanyName = "Deposit",
                symbol = "CASH",
                quantity = 1.0,
                pricePerShare = 1.0,
                taxAmount = 0.0,
                grossAmount = amount,
                netAmount = amount,
                date = LocalDateTime.now().toString(),
                brokerageFee = 0.0,
                notes = "Deposit",
                createdAt = LocalDateTime.now().toString()
            )
            repository.insertTransaction(transaction)
        }
    }

    fun withdrawFunds(amount: Double) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.WITHDRAW,
                mainCompanyName = "System",
                subCompanyName = "Withdrawal",
                symbol = "CASH",
                quantity = 1.0,
                pricePerShare = 1.0,
                taxAmount = 0.0,
                grossAmount = amount,
                netAmount = amount,
                date = LocalDateTime.now().toString(),
                brokerageFee = 0.0,
                notes = "Withdrawal",
                createdAt = LocalDateTime.now().toString()
            )
            repository.insertTransaction(transaction)
        }
    }
}
