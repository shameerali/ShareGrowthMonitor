package com.irothink.sharegrowthmonitor.data.repository

import com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import com.irothink.sharegrowthmonitor.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val dao: CompanyInfoDao
) : CompanyRepository {
    override fun getAllCompanies(): Flow<List<CompanyInfoEntity>> {
        return dao.getAllCompanies()
    }

    override suspend fun insertCompany(company: CompanyInfoEntity) {
        dao.insertCompany(company)
    }

    override suspend fun updateCompany(company: CompanyInfoEntity) {
        dao.updateCompany(company)
    }

    override suspend fun deleteCompany(company: CompanyInfoEntity) {
        dao.deleteCompany(company)
    }
}
