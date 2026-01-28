package com.irothink.sharegrowthmonitor.ui.funds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import com.irothink.sharegrowthmonitor.domain.usecase.GetPortfolioSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FundsViewModel @Inject constructor(
    private val repository: PortfolioRepository,
    getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) : ViewModel() {

    val uiState: StateFlow<FundsUiState> = combine(
        repository.getAllTransactions(),
        getPortfolioSummaryUseCase()
    ) { transactions, portfolioSummary ->
        val fundsTransactions = transactions.filter {
            it.type == TransactionType.DEPOSIT || it.type == TransactionType.WITHDRAW
        }.sortedByDescending { it.date }

        val totalDeposit = fundsTransactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.netAmount }
        val totalWithdrawal = fundsTransactions.filter { it.type == TransactionType.WITHDRAW }.sumOf { it.netAmount }
        val netDeposit = totalDeposit - totalWithdrawal

        // Group transactions by month
        val groupedTransactions = fundsTransactions.groupBy { transaction ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(transaction.date)
                if (date != null) outputFormat.format(date) else "Unknown Date"
            } catch (e: Exception) {
                "Unknown Date"
            }
        }

        val listItems = mutableListOf<FundsListItem>()
        groupedTransactions.forEach { (header, txns) ->
            listItems.add(FundsListItem.Header(header))
            txns.forEach { txn ->
                listItems.add(FundsListItem.Item(txn))
            }
        }

        FundsUiState(
            availableFunds = portfolioSummary.availableFunds,
            totalDeposit = totalDeposit,
            totalWithdrawal = totalWithdrawal,
            netDeposit = netDeposit,
            listItems = listItems
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FundsUiState()
    )
}

data class FundsUiState(
    val availableFunds: Double = 0.0,
    val totalDeposit: Double = 0.0,
    val totalWithdrawal: Double = 0.0,
    val netDeposit: Double = 0.0,
    val listItems: List<FundsListItem> = emptyList()
)

sealed class FundsListItem {
    data class Header(val date: String) : FundsListItem()
    data class Item(val transaction: Transaction) : FundsListItem()
}
