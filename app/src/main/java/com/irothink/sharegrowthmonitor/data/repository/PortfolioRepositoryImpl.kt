package com.irothink.sharegrowthmonitor.data.repository

import com.irothink.sharegrowthmonitor.data.local.dao.TransactionDao
import com.irothink.sharegrowthmonitor.data.local.entity.TransactionEntity
import com.irothink.sharegrowthmonitor.domain.model.Transaction
import com.irothink.sharegrowthmonitor.domain.model.TransactionType
import com.irothink.sharegrowthmonitor.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : PortfolioRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsForStock(symbol: String): Flow<List<Transaction>> {
        return dao.getTransactionsForStock(symbol).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }
}

private fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        mainCompanyName = mainCompanyName,
        subCompanyName = subCompanyName,
        symbol = symbol,
        quantity = quantity,
        pricePerShare = pricePerShare,
        taxAmount = taxAmount,
        grossAmount = grossAmount,
        netAmount = netAmount,
        date = date,
        brokerageFee = brokerageFee,
        notes = notes,
        createdAt = createdAt
    )
}

private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        type = type.name,
        mainCompanyName = mainCompanyName,
        subCompanyName = subCompanyName,
        symbol = symbol,
        quantity = quantity,
        pricePerShare = pricePerShare,
        taxAmount = taxAmount,
        grossAmount = grossAmount,
        netAmount = netAmount,
        date = date,
        brokerageFee = brokerageFee,
        notes = notes,
        createdAt = createdAt
    )
}
