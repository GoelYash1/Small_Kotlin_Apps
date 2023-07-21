package com.example.expensetracker.screens.mainScreen.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.helper.Resource
import com.example.expensetracker.viewModels.ExpenseTrackerViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(expenseTrackerViewModel: ExpenseTrackerViewModel) {
    // Fetch transactions when the screen is first displayed
    LaunchedEffect(Unit) {
        expenseTrackerViewModel.readAndStoreSMS()
    }

    val resource by expenseTrackerViewModel.transactions.collectAsState()

    when (resource) {
        is Resource.Loading -> {
            // Display loading state UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            val transactions = (resource as Resource.Success<List<Transaction>>).data
            // Display success state UI with transactions
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                items(transactions) { transaction ->
                    Box(modifier = Modifier.padding(5.dp)) {
                        Column {
                            // Convert timestamp to readable format
                            val localDateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(transaction.timestamp),
                                ZoneId.systemDefault()
                            )
                            val formattedTime =
                                localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()))

                            Text(
                                text = formattedTime,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = transaction.title ?: "",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = transaction.amount.toString(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        is Resource.Error -> {
            val error = (resource as Resource.Error).throwable
            // Display error state UI with error message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error occurred: ${error.message ?: "Unknown error"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }
        }
    }
}

