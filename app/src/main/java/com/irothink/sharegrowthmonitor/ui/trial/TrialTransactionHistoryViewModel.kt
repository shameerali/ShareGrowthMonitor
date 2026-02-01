package com.irothink.sharegrowthmonitor.ui.trial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.repository.TrialPortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrialTransactionHistoryViewModel @Inject constructor(
    private val trialRepository: TrialPortfolioRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = trialRepository.getAllTransactions()
        .map { it.sortedByDescending { t -> t.date } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            trialRepository.deleteTransaction(transaction)
        }
    }
}
