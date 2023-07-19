package com.example.expensetracker.screens.mainScreen.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.expensetracker.viewModels.ExpenseTrackerViewModel

@Composable
fun HomeScreen(expenseTrackerViewModel: ExpenseTrackerViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Home Screen", textAlign = TextAlign.Center)
    }
}