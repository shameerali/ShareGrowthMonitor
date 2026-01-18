package com.irothink.sharegrowthmonitor.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.domain.model.ImportStats
import com.irothink.sharegrowthmonitor.domain.usecase.excel.ExportDataToExcelUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.excel.ImportDataFromExcelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val exportDataToExcelUseCase: ExportDataToExcelUseCase,
    private val importDataFromExcelUseCase: ImportDataFromExcelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            exportDataToExcelUseCase(uri)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Data exported successfully!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Export failed: ${error.message}"
                    )
                }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            importDataFromExcelUseCase(uri)
                .onSuccess { stats ->
                    val message = buildString {
                        append("Import completed!\n")
                        append("Transactions: ${stats.transactionsImported}\n")
                        append("Companies: ${stats.companiesImported}\n")
                        append("Budget entries: ${stats.budgetsImported}")
                        if (stats.hasErrors) {
                            append("\n\nWarnings:\n")
                            stats.errors.take(3).forEach { append("• $it\n") }
                            if (stats.errors.size > 3) {
                                append("...and ${stats.errors.size - 3} more")
                            }
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = message,
                        importStats = stats
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Import failed: ${error.message}"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val importStats: ImportStats? = null
)
