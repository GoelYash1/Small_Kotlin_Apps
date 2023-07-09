package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

import com.example.expensetracker.data.models.Account

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Delete
    suspend fun delete(account: Account)
}