package com.irothink.sharegrowthmonitor.domain.usecase.company

import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    operator fun invoke(): Flow<List<CompanyInfoEntity>> {
        return repository.getAllCompanies()
    }
}
