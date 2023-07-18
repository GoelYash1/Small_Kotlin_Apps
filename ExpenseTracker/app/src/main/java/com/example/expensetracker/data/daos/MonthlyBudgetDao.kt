package com.example.expensetracker.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.models.MonthlyBudget

@Dao
interface MonthlyBudgetDao {
    @Insert
    suspend fun insert(monthlyBudget: MonthlyBudget)

    @Update
    suspend fun update(monthlyBudget: MonthlyBudget)

    @Delete
    suspend fun delete(monthlyBudget: MonthlyBudget)

    @Query("SELECT * FROM monthly_budget WHERE month = :month AND year = :year")
    suspend fun getMonthlyBudget(month: Int, year: Int): MonthlyBudget?
}
