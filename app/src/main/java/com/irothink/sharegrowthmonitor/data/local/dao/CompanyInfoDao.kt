package com.irothink.sharegrowthmonitor.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyInfoDao {
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun getAllCompanies(): Flow<List<CompanyInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyInfoEntity)

    @Update
    suspend fun updateCompany(company: CompanyInfoEntity)

    @Delete
    suspend fun deleteCompany(company: CompanyInfoEntity)

    @Query("SELECT * FROM companies")
    suspend fun getAllCompaniesSync(): List<CompanyInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(companies: List<CompanyInfoEntity>)

    @Query("DELETE FROM companies")
    suspend fun deleteAll()
}
