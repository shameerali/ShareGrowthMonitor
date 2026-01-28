package com.irothink.sharegrowthmonitor.domain.repository

import com.irothink.sharegrowthmonitor.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsForStock(symbol: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
}
