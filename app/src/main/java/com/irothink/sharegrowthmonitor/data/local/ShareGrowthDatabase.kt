package com.irothink.sharegrowthmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.irothink.sharegrowthmonitor.data.local.dao.TransactionDao
import com.irothink.sharegrowthmonitor.data.local.entity.TransactionEntity

import com.irothink.sharegrowthmonitor.data.local.dao.BudgetDao
import com.irothink.sharegrowthmonitor.data.local.entity.BudgetEntity
import com.irothink.sharegrowthmonitor.data.local.entity.CompanyInfoEntity

import com.irothink.sharegrowthmonitor.data.local.dao.TrialTransactionDao
import com.irothink.sharegrowthmonitor.data.local.entity.TrialTransactionEntity

@Database(
    entities = [TransactionEntity::class, CompanyInfoEntity::class, BudgetEntity::class, TrialTransactionEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ShareGrowthDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun trialTransactionDao(): TrialTransactionDao
    abstract fun companyInfoDao(): com.irothink.sharegrowthmonitor.data.local.dao.CompanyInfoDao
    abstract fun budgetDao(): BudgetDao
}
