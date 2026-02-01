package com.irothink.sharegrowthmonitor.domain.repository

import com.irothink.sharegrowthmonitor.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TrialPortfolioRepository {
    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun getTransactionById(id: String): Transaction?
}
