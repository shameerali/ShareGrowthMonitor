package com.irothink.sharegrowthmonitor.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.irothink.sharegrowthmonitor.data.local.entity.TrialTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrialTransactionDao {
    @Query("SELECT * FROM trial_transactions")
    fun getAllTransactions(): Flow<List<TrialTransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TrialTransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TrialTransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TrialTransactionEntity)

    @Query("SELECT * FROM trial_transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TrialTransactionEntity?
}
