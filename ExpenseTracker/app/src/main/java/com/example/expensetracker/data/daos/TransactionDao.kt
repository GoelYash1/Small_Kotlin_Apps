package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.models.Transaction

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: List<Transaction>)
    @Delete
    suspend fun delete(transaction: Transaction)
    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>
    @Query("SELECT * FROM transactions WHERE category_name = :categoryName")
    suspend fun getTransactionsForCategory(categoryName: Long): List<Transaction>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp")
    suspend fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long): List<Transaction>
}
