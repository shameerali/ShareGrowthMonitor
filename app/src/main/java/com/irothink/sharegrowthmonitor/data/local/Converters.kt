package com.irothink.sharegrowthmonitor.data.local

import androidx.room.TypeConverter
import com.irothink.sharegrowthmonitor.data.local.entity.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
