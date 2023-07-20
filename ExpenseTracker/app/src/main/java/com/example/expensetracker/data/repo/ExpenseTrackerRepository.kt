package com.example.expensetracker.data.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.expensetracker.api.SMSReadAPI
import com.example.expensetracker.data.db.ExpenseTrackerDatabase
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.models.TransactionCategories
import com.example.expensetracker.helper.TransactionSMSFilter
import kotlinx.coroutines.flow.Flow
import java.time.Month

class ExpenseTrackerRepository(
    private val db: ExpenseTrackerDatabase,
    private val context: Context
) {
    private val transactionDao = db.transactionDao()
    private val accountDao = db.accountDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun readAndStoreSMS(year: Int? = null, month: Month? = null, date: Int? = null) {
        val smsReadAPI = if (checkSMSPermission()) SMSReadAPI(context.contentResolver) else null

        if (smsReadAPI != null) {
            val smsMessages = smsReadAPI.getGroupedSMSMessagesByDateMonthYear(year, month, date)

            smsMessages.flatMap { (_, messages) ->
                messages.map { sms ->
                    val transactionFilter = TransactionSMSFilter()
                    val amountSpent = transactionFilter.getAmountSpent(sms.body) ?: 0.0
                    val isExpense = transactionFilter.isExpense(sms.body)
                    val extractedAccount = transactionFilter.extractAccount(sms.body)

                    val defaultTitle: String?
                    val defaultCategoryName: String?

                    val account = extractedAccount?.let { accountDao.getAccountByAccountId(it) }
                    if (account == null) {
                        // Account is not present, prompt the user to store the account and its details
                        // You can implement the prompt logic here

                        // Set default values
                        defaultTitle = "What was the payment for?"
                        defaultCategoryName = TransactionCategories.OTHERS
                    } else {
                        defaultTitle = account.defaultTitle
                        defaultCategoryName = account.defaultCategoryName
                    }

                    val transaction = Transaction(
                        title = defaultTitle,
                        amount = amountSpent,
                        timestamp = sms.time,
                        type = if (isExpense) "Expense" else "Income",
                        categoryName = defaultCategoryName,
                        accountId = extractedAccount
                    )

                    transactionDao.insert(transaction)
                }
            }
        } else {
            return
        }
    }
    suspend fun addManualTransaction(
        title: String,
        amount: Double,
        timestamp: Long,
        type: String, // "Expense" or "Income"
        categoryName: String,
        accountId: String? = null // Only needed if there's an associated account
    ) {
        val transaction = Transaction(
            title = title,
            amount = amount,
            timestamp = timestamp,
            type = type,
            categoryName = categoryName,
            accountId = accountId
        )

        transactionDao.insert(transaction)
    }

    private fun checkSMSPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForMonth(startTimestamp, endTimestamp)
    }

    fun getTransactionsForCategory(categoryName: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForCategory(categoryName)
    }
}


