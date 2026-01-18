package com.irothink.sharegrowthmonitor.ui.company.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.usecase.company.AddCompanyUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.DeleteCompanyUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.GetCompaniesUseCase
import com.irothink.sharegrowthmonitor.domain.usecase.company.UpdateCompanyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListViewModel @Inject constructor(
    getCompaniesUseCase: GetCompaniesUseCase,
    private val addCompanyUseCase: AddCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val deleteCompanyUseCase: DeleteCompanyUseCase
) : ViewModel() {

    val companies: StateFlow<List<CompanyInfoEntity>> = getCompaniesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCompany(name: String, industry: String, symbol: String, currentPrice: Double) {
        viewModelScope.launch {
            addCompanyUseCase(
                CompanyInfoEntity(
                    name = name,
                    industry = industry,
                    symbol = symbol,
                    currentPrice = currentPrice,
                    createdAt = System.currentTimeMillis().toString()
                )
            )
        }
    }

    fun updateCompany(company: CompanyInfoEntity, name: String, industry: String, symbol: String, currentPrice: Double) {
        viewModelScope.launch {
            updateCompanyUseCase(
                company.copy(
                    name = name,
                    industry = industry,
                    symbol = symbol,
                    currentPrice = currentPrice
                )
            )
        }
    }

    fun deleteCompany(company: CompanyInfoEntity) {
        viewModelScope.launch {
            deleteCompanyUseCase(company)
        }
    }
}
