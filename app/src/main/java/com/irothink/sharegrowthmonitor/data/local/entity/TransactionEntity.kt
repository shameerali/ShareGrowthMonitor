package com.irothink.sharegrowthmonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // "BUY" or "SELL"
    val mainCompanyName: String,
    val subCompanyName: String,
    val symbol: String,
    val quantity: Double,
    val pricePerShare: Double,
    val totalAmount: Double,
    val date: String, // YYYY-MM-DD
    val brokerageFee: Double,
    val notes: String,
    val createdAt: String // ISO 8601
)

enum class TransactionType {
    BUY, SELL
}
