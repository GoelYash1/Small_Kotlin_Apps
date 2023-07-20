package com.example.expensetracker.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.api.SMSReadAPI
import com.example.expensetracker.data.db.ExpenseTrackerDatabase
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.repo.ExpenseTrackerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
class ExpenseTrackerViewModel(
    private val repository: ExpenseTrackerRepository
):ViewModel() {
    fun readAndStoreSMS(year:Int?=null, month:Month?=null, date:Int?=null){
        viewModelScope.launch(Dispatchers.IO)  {
            repository.readAndStoreSMS(year,month,date)
        }
    }
    fun getAllTransactions(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }

    fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long): Flow<List<Transaction>> {
        return repository.getTransactionsForMonth(startTimestamp, endTimestamp)
    }

    fun getTransactionsForCategory(categoryName: String): Flow<List<Transaction>> {
        return repository.getTransactionsForCategory(categoryName)
    }
    override fun onCleared() {
        // Clean up any resources here
        super.onCleared()
    }
}