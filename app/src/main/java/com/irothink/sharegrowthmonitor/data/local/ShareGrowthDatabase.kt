package com.irothink.sharegrowthmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.irothink.sharegrowthmonitor.data.local.dao.TransactionDao
import com.irothink.sharegrowthmonitor.data.local.entity.TransactionEntity

import com.irothink.sharegrowthmonitor.data.local.dao.BudgetDao
import com.irothink.sharegrowthmonitor.data.local.entity.BudgetEntity
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity

@Database(
    entities = [TransactionEntity::class, CompanyInfoEntity::class, BudgetEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ShareGrowthDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun companyInfoDao(): com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao
    abstract fun budgetDao(): BudgetDao
}
