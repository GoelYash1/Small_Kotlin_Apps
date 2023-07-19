package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.expensetracker.data.models.Account

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    suspend fun getAccountByAccountId(accountId: String): Account?
}