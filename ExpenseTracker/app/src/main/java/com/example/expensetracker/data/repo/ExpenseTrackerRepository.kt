package com.example.expensetracker.data.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.expensetracker.api.SMSReadAPI
import com.example.expensetracker.data.db.ExpenseTrackerDatabase
import com.example.expensetracker.data.models.Account
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.helper.TransactionSMSFilter
import java.time.Month

class ExpenseTrackerRepository(
    private val db: ExpenseTrackerDatabase,
    private val smsReadAPI: SMSReadAPI
) {
    private val transactionDao = db.transactionDao()
    private val accountDao = db.accountDao()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun readAndStoreSMS(year: Int? = null, month: Month? = null, date: Int? = null) {
        val smsMessages = smsReadAPI.getGroupedSMSMessagesByDateMonthYear(year, month, date)

        val transactions = smsMessages.flatMap { (_, messages) ->
            messages.map { sms ->
                val amountSpent = TransactionSMSFilter().getAmountSpent(sms.body) ?: 0.0
                val type = if (TransactionSMSFilter().isExpense(sms.body)) "Expense" else "Income"
                val extractedAccount = TransactionSMSFilter().extractAccount(sms.body)

                val account = extractedAccount?.let { accountDao.getAccountByAccountId(it) }

                if (account == null) {
                    // Account is not present, ask the user to store the account and its details
                    // and then create a new account entry in the database
                    // You can prompt the user for default title and category selection here
                    val newAccount = extractedAccount?.let {
                        Account(
                            accountId = it,
                            name = "", // Prompt the user for account name
                            defaultTitle = "", // Prompt the user for default title
                            defaultCategoryName = "" // Prompt the user for default category
                        )
                    }
                    if (newAccount != null) {
                        accountDao.insert(newAccount)
                    }
                }

                val defaultTitle = account?.defaultTitle
                val defaultCategoryName = account?.defaultCategoryName

                Transaction(
                    title = defaultTitle ?: "What is the Transaction For",
                    amount = amountSpent,
                    timestamp = sms.time,
                    type = type,
                    categoryName = defaultCategoryName,
                    accountId = extractedAccount
                )
            }
        }

        transactionDao.insert(transactions)
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }

    suspend fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long): List<Transaction> {
        return transactionDao.getTransactionsForMonth(startTimestamp, endTimestamp)
    }

    suspend fun getTransactionsForCategory(categoryName: String): List<Transaction> {
        return transactionDao.getTransactionsForCategory(categoryName)
    }
}


