package com.irothink.sharegrowthmonitor.ui.trial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.PortfolioSummary
import com.irothink.sharegrowthmonitor.domain.usecase.GetTrialPortfolioSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrialDashboardViewModel @Inject constructor(
    getTrialPortfolioSummaryUseCase: GetTrialPortfolioSummaryUseCase
) : ViewModel() {

    val portfolioSummary: StateFlow<PortfolioSummary?> = getTrialPortfolioSummaryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
