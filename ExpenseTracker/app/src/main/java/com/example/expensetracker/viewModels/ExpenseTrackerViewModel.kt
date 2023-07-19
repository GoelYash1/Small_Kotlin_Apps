package com.example.expensetracker.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.repo.ExpenseTrackerRepository
import kotlinx.coroutines.launch
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
class ExpenseTrackerViewModel(
    private val repository: ExpenseTrackerRepository
):ViewModel() {
    init {
        viewModelScope.launch {
            repository.readAndStoreSMS()
        }
    }
    fun readAndStoreSMS(year:Int?=null, month:Month?=null, date:Int?=null){
        viewModelScope.launch {
            repository.readAndStoreSMS(year,month,date)
        }
    }
    suspend fun getAllTransactions(): List<Transaction> {
        return repository.getAllTransactions()
    }

    suspend fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long): List<Transaction> {
        return repository.getTransactionsForMonth(startTimestamp, endTimestamp)
    }

    suspend fun getTransactionsForCategory(categoryName: String): List<Transaction> {
        return repository.getTransactionsForCategory(categoryName)
    }
    override fun onCleared() {
        // Clean up any resources here
        super.onCleared()
    }
}