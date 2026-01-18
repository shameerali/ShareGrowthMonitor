package com.irothink.sharegrowthmonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class CompanyInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val industry: String = "",
    val symbol: String,
    val currentPrice: Double = 0.0,
    val createdAt: String
)
