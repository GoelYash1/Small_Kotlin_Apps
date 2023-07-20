package com.example.expensetracker.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.repo.ExpenseTrackerRepository

class ExpenseTrackerViewModelProviderFactory(private val expenseTrackerRepository: ExpenseTrackerRepository): ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExpenseTrackerViewModel(expenseTrackerRepository) as T
    }
}