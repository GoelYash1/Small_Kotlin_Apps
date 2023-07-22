package com.example.expensetracker.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.repo.ExpenseTrackerRepository
import com.example.expensetracker.helper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class ExpenseTrackerViewModel(
    private val repository: ExpenseTrackerRepository
) : ViewModel() {
    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading)
    val transactions: StateFlow<Resource<List<Transaction>>>
        get() = _transactions

    // Set the default polling interval to 30 minutes
    private val defaultPollingInterval: Long = 30 * 60 * 1000 // 30 minutes in milliseconds

    // MutableStateFlow to hold the polling interval set by the user
    private val _pollingInterval = MutableStateFlow(defaultPollingInterval)
    val pollingInterval: StateFlow<Long>
        get() = _pollingInterval

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    init {
        // Fetch and store all transactions when the ViewModel is initialized
        fetchAndStoreTransactions()
        // Start the periodic polling
        startPeriodicPolling(defaultPollingInterval)
    }

    private fun fetchAndStoreTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _transactions.value = Resource.Loading
                repository.readAndStoreSMS()
                repository.getAllTransactions().collect {
                    _transactions.value = Resource.Success(it)
                }
            } catch (e: Exception) {
                _transactions.value = Resource.Error(e)
            }
        }
    }

    private fun startPeriodicPolling(interval: Long) {
        viewModelScope.launch {
            while (true) {
                delay(interval)
                fetchAndStoreTransactions()
            }
        }
    }

    // Function to manually trigger a refresh
    fun refreshTransactions() {
        _refreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            fetchAndStoreTransactions()
            _refreshing.postValue(false)
        }
    }

    // Function to set the polling interval as per user input
    fun setPollingInterval(interval: Long) {
        _pollingInterval.value = interval
        // Restart the periodic polling with the new interval
        startPeriodicPolling(interval)
    }

    fun getTransactionsForMonthAndYear(year: Int, month: Month) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
                val startTimestamp = getStartOfMonthTimestamp(year, month)
                val endTimestamp = getEndOfMonthTimestamp(year, month)
                repository.getTransactionsForTimePeriod(startTimestamp, endTimestamp).collect {
                    // Emit success state with data
                    _transactions.value = Resource.Success(it)
                }
            } catch (e: Exception) {
                // Handle error state
                _transactions.value = Resource.Error(e)
            }
        }
    }

    fun getTransactionsForDate(date: Int, month: Month, year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
                val startTimestamp = getStartOfDayTimestamp(year, month, date)
                val endTimestamp = getEndOfDayTimestamp(year, month, date)
                repository.getTransactionsForTimePeriod(startTimestamp, endTimestamp).collect {
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

    fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start with loading state
                _transactions.value = Resource.Loading
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

    // Utility functions to calculate timestamps
    private fun getStartOfMonthTimestamp(year: Int, month: Month): Long {
        val startOfMonth = YearMonth.of(year, month).atDay(1)
        return startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun getEndOfMonthTimestamp(year: Int, month: Month): Long {
        val endOfMonth = YearMonth.of(year, month).atEndOfMonth()
        return endOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun getStartOfDayTimestamp(year: Int, month: Month, date: Int): Long {
        val startOfDay = LocalDate.of(year, month, date)
        return startOfDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun getEndOfDayTimestamp(year: Int, month: Month, date: Int): Long {
        val endOfDay = LocalDate.of(year, month, date)
        return endOfDay.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}


