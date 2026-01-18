package com.irothink.sharegrowthmonitor.domain.repository

import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun getAllCompanies(): Flow<List<CompanyInfoEntity>>
    suspend fun insertCompany(company: CompanyInfoEntity)
    suspend fun updateCompany(company: CompanyInfoEntity)
    suspend fun deleteCompany(company: CompanyInfoEntity)
}
