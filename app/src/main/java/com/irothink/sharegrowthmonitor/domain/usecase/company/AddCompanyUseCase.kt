package com.irothink.sharegrowthmonitor.domain.usecase.company

import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import javax.inject.Inject

class AddCompanyUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(company: CompanyInfoEntity) {
        repository.insertCompany(company)
    }
}
