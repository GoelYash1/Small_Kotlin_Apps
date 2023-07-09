package com.example.expensetracker.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String,
    val from: String?,
    val to: String?,
    @ColumnInfo(name = "category_name")
    val categoryName: Long,
    @ColumnInfo(name = "account_id")
    val accountId: Long?,
    val timestamp: Long
)
