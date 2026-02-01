package com.irothink.sharegrowthmonitor.ui.trial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.usecase.AddTrialTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TrialAddTransactionViewModel @Inject constructor(
    private val addTrialTransactionUseCase: AddTrialTransactionUseCase,
    private val companyRepository: com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrialAddTransactionUiState())
    val uiState: StateFlow<TrialAddTransactionUiState> = _uiState.asStateFlow()

    val companies: StateFlow<List<com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity>> = companyRepository.getAllCompanies()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSymbolChanged(symbol: String) {
        _uiState.value = _uiState.value.copy(symbol = symbol)
    }

    fun onCompanySelected(company: com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity) {
        _uiState.value = _uiState.value.copy(
            companyName = company.name,
            symbol = company.symbol
        )
    }

    fun onCompanyNameChanged(companyName: String) {
        _uiState.value = _uiState.value.copy(companyName = companyName)
    }

    fun onTypeChanged(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
        calculateTotals()
    }

    fun onQuantityChanged(quantity: String) {
        _uiState.value = _uiState.value.copy(quantity = quantity)
        calculateTotals()
    }

    fun onPriceChanged(price: String) {
        _uiState.value = _uiState.value.copy(pricePerShare = price)
        calculateTotals()
    }

    fun onBrokerageFeeChanged(fee: String) {
        _uiState.value = _uiState.value.copy(brokerageFee = fee)
        calculateTotals()
    }

    fun onTaxAmountChanged(tax: String) {
        _uiState.value = _uiState.value.copy(taxAmount = tax)
        calculateTotals()
    }

    fun onDateChanged(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    private fun calculateTotals() {
        val qty = _uiState.value.quantity.toDoubleOrNull() ?: 0.0
        val price = _uiState.value.pricePerShare.toDoubleOrNull() ?: 0.0
        val fee = _uiState.value.brokerageFee.toDoubleOrNull() ?: 0.0
        val tax = _uiState.value.taxAmount.toDoubleOrNull() ?: 0.0
        
        val gross = qty * price

        val net = if (_uiState.value.type == TransactionType.BUY) {
            gross + fee + tax
        } else {
            gross - fee - tax
        }

        _uiState.value = _uiState.value.copy(
            grossAmount = gross,
            netAmount = net
        )
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                type = currentState.type,
                mainCompanyName = currentState.companyName.ifBlank { "Trial Company" },
                subCompanyName = "",
                symbol = currentState.symbol.uppercase(),
                quantity = currentState.quantity.toDoubleOrNull() ?: 0.0,
                pricePerShare = currentState.pricePerShare.toDoubleOrNull() ?: 0.0,
                taxAmount = currentState.taxAmount.toDoubleOrNull() ?: 0.0,
                grossAmount = currentState.grossAmount,
                netAmount = currentState.netAmount,
                date = currentState.date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) },
                brokerageFee = currentState.brokerageFee.toDoubleOrNull() ?: 0.0,
                notes = currentState.notes.ifBlank { "Trial Transaction" },
                createdAt = Date().toString()
            )
            addTrialTransactionUseCase(transaction)
            onSuccess()
        }
    }
}

data class TrialAddTransactionUiState(
    val symbol: String = "",
    val companyName: String = "",
    val type: TransactionType = TransactionType.BUY,
    val quantity: String = "",
    val pricePerShare: String = "",
    val brokerageFee: String = "",
    val taxAmount: String = "",
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val notes: String = "",
    val grossAmount: Double = 0.0,
    val netAmount: Double = 0.0
)
