package com.example.expensetracker.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.repo.ExpenseTrackerRepository
import com.example.expensetracker.helper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
class ExpenseTrackerViewModel(
    private val repository: ExpenseTrackerRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading)
    val transactions: StateFlow<Resource<List<Transaction>>>
        get() = _transactions

    fun readAndStoreSMS(year: Int? = null, month: Month? = null, date: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
                repository.readAndStoreSMS(year, month, date)
                // Fetch new transactions after storing SMS data
                fetchAllTransactions()
            } catch (e: Exception) {
                // Handle error state
                _transactions.value = Resource.Error(e)
            }
        }
    }

    private fun fetchAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllTransactions().collect {
                    // Emit success state with data
                    _transactions.value = Resource.Success(it)
                }
            } catch (e: Exception) {
                // Handle error state
                _transactions.value = Resource.Error(e)
            }
        }
    }

    fun getTransactionsForMonth(startTimestamp: Long, endTimestamp: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
                repository.getTransactionsForMonth(startTimestamp, endTimestamp).collect {
                    // Emit success state with data
                    _transactions.value = Resource.Success(it)
                }
            } catch (e: Exception) {
                // Handle error state
                _transactions.value = Resource.Error(e)
            }
        }
    }

    fun getTransactionsForCategory(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
                repository.getTransactionsForCategory(categoryName).collect {
                    // Emit success state with data
                    _transactions.value = Resource.Success(it)
                }
            } catch (e: Exception) {
                // Handle error state
                _transactions.value = Resource.Error(e)
            }
        }
    }
}


