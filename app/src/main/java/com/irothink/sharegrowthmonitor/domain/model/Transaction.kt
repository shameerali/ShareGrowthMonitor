package com.irothink.sharegrowthmonitor.domain.model

data class Transaction(
    val id: String,
    val type: TransactionType,
    val mainCompanyName: String,
    val subCompanyName: String,
    val symbol: String,
    val quantity: Double,
    val pricePerShare: Double,
    val totalAmount: Double,
    val date: String,
    val brokerageFee: Double,
    val notes: String,
    val createdAt: String
)

enum class TransactionType {
    BUY, SELL, DEPOSIT, WITHDRAW
}
