package com.irothink.sharegrowthmonitor.ui.profitloss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.ProfitLossData
import com.irothink.sharegrowthmonitor.domain.usecase.GetProfitLossUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfitLossViewModel @Inject constructor(
    getProfitLossUseCase: GetProfitLossUseCase
) : ViewModel() {

    val profitLossData: StateFlow<ProfitLossData?> = getProfitLossUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
