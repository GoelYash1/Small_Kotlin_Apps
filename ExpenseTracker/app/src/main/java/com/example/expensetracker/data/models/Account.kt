package com.example.expensetracker.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: String,
    val name: String,
    val defaultTitle: String,
    @ColumnInfo(name = "default_category_name")
    val defaultCategoryName: String
)


