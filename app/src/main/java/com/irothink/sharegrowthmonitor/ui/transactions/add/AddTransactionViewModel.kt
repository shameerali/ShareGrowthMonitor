package com.irothink.sharegrowthmonitor.ui.transactions.add

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.usecase.AddTransactionUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.GetCompaniesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    getCompaniesUseCase: GetCompaniesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<Unit>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val companies: StateFlow<List<CompanyInfoEntity>> = getCompaniesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.CompanySelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedCompany = event.company,
                    symbol = event.company?.symbol ?: "",
                    companyName = event.company?.name ?: ""
                )
            }
            is AddTransactionEvent.QuantityChanged -> _uiState.value = _uiState.value.copy(quantity = event.quantity)
            is AddTransactionEvent.PriceChanged -> _uiState.value = _uiState.value.copy(price = event.price)
            is AddTransactionEvent.TypeChanged -> _uiState.value = _uiState.value.copy(type = event.type)
            is AddTransactionEvent.DateChanged -> _uiState.value = _uiState.value.copy(date = event.date)
            AddTransactionEvent.SaveClicked -> saveTransaction()
        }
    }

    private fun saveTransaction() {
        val state = _uiState.value
        if (state.selectedCompany == null || state.quantity.isBlank() || state.price.isBlank()) {
            // Show error (simplified for now)
//            Toast.makeText(getco)
            return
        }

        viewModelScope.launch {
            try {
                val quantityVal = state.quantity.toDouble()
                val priceVal = state.price.toDouble()
                val totalVal = quantityVal * priceVal
                val dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date(state.date))
                
                addTransactionUseCase(
                    Transaction(
                        id = java.util.UUID.randomUUID().toString(),
                        symbol = state.selectedCompany.symbol,
                        mainCompanyName = state.selectedCompany.name,
                        subCompanyName = "",
                        date = dateString,
                        quantity = quantityVal,
                        pricePerShare = priceVal,
                        taxAmount = 0.0,
                        grossAmount = totalVal,
                        netAmount = totalVal,
                        type = state.type,
                        brokerageFee = 0.0,
                        notes = "",
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
                _navigationEvent.send(Unit)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class AddTransactionUiState(
    val selectedCompany: CompanyInfoEntity? = null,
    val symbol: String = "",
    val companyName: String = "",
    val quantity: String = "",
    val price: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.BUY
)

sealed class AddTransactionEvent {
    data class CompanySelected(val company: CompanyInfoEntity?) : AddTransactionEvent()
    data class QuantityChanged(val quantity: String) : AddTransactionEvent()
    data class PriceChanged(val price: String) : AddTransactionEvent()
    data class TypeChanged(val type: TransactionType) : AddTransactionEvent()
    data class DateChanged(val date: Long) : AddTransactionEvent()
    object SaveClicked : AddTransactionEvent()
}
