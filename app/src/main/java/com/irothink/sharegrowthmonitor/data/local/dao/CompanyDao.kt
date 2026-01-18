package com.irothink.sharegrowthmonitor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyInfoEntity)

    @Query("SELECT * FROM company_info WHERE symbol = :symbol")
    fun getCompany(symbol: String): Flow<CompanyInfoEntity?>

    @Query("SELECT * FROM company_info")
    fun getAllCompanies(): Flow<List<CompanyInfoEntity>>
}
