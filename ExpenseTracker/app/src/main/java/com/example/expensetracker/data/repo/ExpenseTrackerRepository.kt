package com.example.expensetracker.data.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.expensetracker.api.SMSReadAPI
import com.example.expensetracker.data.db.ExpenseTrackerDatabase
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.helper.TransactionSMSFilter

class ExpenseTrackerRepository(
    private val db: ExpenseTrackerDatabase,
    private val smsReadAPI: SMSReadAPI
) {
    private val transactionDao = db.transactionDao()
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun readAndStoreSMS() {
        val smsMessages = smsReadAPI.getGroupedSMSMessagesByDate()

        val transactions = smsMessages.flatMap { (_, messages) ->
            messages.map { sms ->
                val amountSpent = TransactionSMSFilter().getAmountSpent(sms.body) ?: 0.0
                val type = if (TransactionSMSFilter().isExpense(sms.body)) "Expense" else "Income"

                Transaction(
                    title = null,
                    amount = amountSpent,
                    timestamp = sms.time,
                    type = type,
                    categoryName = null, // Set as per user's choice or null if not selected
                    accountId = null // Set as per user's choice or null if not selected
                )
            }
        }

        transactionDao.insert(transactions)
    }
}

