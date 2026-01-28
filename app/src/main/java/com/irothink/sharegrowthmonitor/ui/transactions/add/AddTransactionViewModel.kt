package com.irothink.sharegrowthmonitor.ui.transactions.add

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.usecase.AddTransactionUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.GetTransactionByIdUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.UpdateTransactionUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.GetCompaniesUseCase
import androidx.lifecycle.SavedStateHandle
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
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    getCompaniesUseCase: GetCompaniesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: String? = savedStateHandle["transactionId"]

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

    init {
        transactionId?.let { id ->
            viewModelScope.launch {
                getTransactionByIdUseCase(id)?.let { transaction ->
                    _uiState.value = _uiState.value.copy(
                        symbol = transaction.symbol,
                        companyName = transaction.mainCompanyName,
                        quantity = transaction.quantity.toString(),
                        price = transaction.pricePerShare.toString(),
                        date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(transaction.date)?.time ?: System.currentTimeMillis(),
                        type = transaction.type,
                        brokerageFee = transaction.brokerageFee.toString(),
                        taxAmount = transaction.taxAmount.toString(),
                        notes = transaction.notes,
                        selectedCompany = companies.value.find { it.symbol == transaction.symbol }
                    )
                    
                    // If companies list wasn't loaded yet, we might need to wait or rely on the name/symbol from transaction
                }
            }
        }
        
        // Ensure selectedCompany is updated when companies list loads (for editing)
        viewModelScope.launch {
            companies.collect { list ->
                val state = _uiState.value
                if (list.isNotEmpty() && state.selectedCompany == null && state.symbol.isNotEmpty()) {
                    list.find { it.symbol == state.symbol }?.let { company ->
                        _uiState.value = _uiState.value.copy(selectedCompany = company)
                    }
                }
            }
        }
    }

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
            is AddTransactionEvent.BrokerageFeeChanged -> _uiState.value = _uiState.value.copy(brokerageFee = event.brokerageFee)
            is AddTransactionEvent.TaxAmountChanged -> _uiState.value = _uiState.value.copy(taxAmount = event.taxAmount)
            is AddTransactionEvent.NotesChanged -> _uiState.value = _uiState.value.copy(notes = event.notes)
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
                val brokerageFeeVal = state.brokerageFee.toDoubleOrNull() ?: 0.0
                val taxAmountVal = state.taxAmount.toDoubleOrNull() ?: 0.0
                
                val grossAmount = quantityVal * priceVal
                val netAmount = grossAmount + brokerageFeeVal + taxAmountVal
                
                val dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date(state.date))
                
                val transaction = Transaction(
                    id = transactionId ?: java.util.UUID.randomUUID().toString(),
                    symbol = state.selectedCompany?.symbol ?: state.symbol,
                    mainCompanyName = state.selectedCompany?.name ?: state.companyName,
                    subCompanyName = "",
                    date = dateString,
                    quantity = quantityVal,
                    pricePerShare = priceVal,
                    taxAmount = taxAmountVal,
                    grossAmount = grossAmount,
                    netAmount = netAmount,
                    type = state.type,
                    brokerageFee = brokerageFeeVal,
                    notes = state.notes,
                    createdAt = System.currentTimeMillis().toString()
                )

                if (transactionId != null) {
                    updateTransactionUseCase(transaction)
                } else {
                    addTransactionUseCase(transaction)
                }
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
    val type: TransactionType = TransactionType.BUY,
    val brokerageFee: String = "",
    val taxAmount: String = "",
    val notes: String = ""
)

sealed class AddTransactionEvent {
    data class CompanySelected(val company: CompanyInfoEntity?) : AddTransactionEvent()
    data class QuantityChanged(val quantity: String) : AddTransactionEvent()
    data class PriceChanged(val price: String) : AddTransactionEvent()
    data class TypeChanged(val type: TransactionType) : AddTransactionEvent()
    data class DateChanged(val date: Long) : AddTransactionEvent()
    data class BrokerageFeeChanged(val brokerageFee: String) : AddTransactionEvent()
    data class TaxAmountChanged(val taxAmount: String) : AddTransactionEvent()
    data class NotesChanged(val notes: String) : AddTransactionEvent()
    object SaveClicked : AddTransactionEvent()
}
