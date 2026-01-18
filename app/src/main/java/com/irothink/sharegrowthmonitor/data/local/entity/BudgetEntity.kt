package com.irothink.sharegrowthmonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_budget")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: String, // "CREDIT" or "DEBIT"
    val date: Long,
    val notes: String
)
