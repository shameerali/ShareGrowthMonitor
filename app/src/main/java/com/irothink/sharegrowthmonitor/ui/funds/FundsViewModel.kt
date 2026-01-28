package com.irothink.sharegrowthmonitor.ui.funds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FundsViewModel @Inject constructor(
    private val repository: PortfolioRepository
) : ViewModel() {

    val fundsTransactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .map { transactions ->
            transactions.filter { 
                it.type == TransactionType.DEPOSIT || it.type == TransactionType.WITHDRAW 
            }.sortedByDescending { it.date }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
