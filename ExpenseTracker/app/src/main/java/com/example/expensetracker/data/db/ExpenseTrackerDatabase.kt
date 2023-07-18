package com.example.expensetracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.data.daos.AccountDao
import com.example.expensetracker.data.daos.MonthlyBudgetDao
import com.example.expensetracker.data.daos.TransactionDao
import com.example.expensetracker.data.models.Account
import com.example.expensetracker.data.models.MonthlyBudget
import com.example.expensetracker.data.models.Transaction

@Database(entities = [Transaction::class, Account::class, MonthlyBudget::class], version = 1)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun monthlyBudgetDao(): MonthlyBudgetDao

    companion object {
        private const val DATABASE_NAME = "expense_tracker_database"

        @Volatile
        private var instance: ExpenseTrackerDatabase? = null

        fun getInstance(context: Context): ExpenseTrackerDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ExpenseTrackerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ExpenseTrackerDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
