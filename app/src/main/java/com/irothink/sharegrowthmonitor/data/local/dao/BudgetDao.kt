package com.irothink.sharegrowthmonitor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.irothink.sharegrowthmonitor.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(budget: BudgetEntity)

    @Query("SELECT * FROM user_budget ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<BudgetEntity>>

    @Query("SELECT SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END) FROM user_budget")
    fun getTotalBalance(): Flow<Double?>

    @Query("SELECT * FROM user_budget")
    suspend fun getAllBudgetsSync(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<BudgetEntity>)

    @Query("DELETE FROM user_budget")
    suspend fun deleteAll()
}
