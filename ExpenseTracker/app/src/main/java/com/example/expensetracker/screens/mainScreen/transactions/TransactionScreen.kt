package com.example.expensetracker.screens.mainScreen.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.models.TransactionCategories
import com.example.expensetracker.helper.Resource
import com.example.expensetracker.viewModels.ExpenseTrackerViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(expenseTrackerViewModel: ExpenseTrackerViewModel) {
    val resource by expenseTrackerViewModel.transactions.collectAsState()
    val isRefreshing by expenseTrackerViewModel.refreshing.observeAsState()

    val swipeToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing ?: false,
        onRefresh = { expenseTrackerViewModel.refreshTransactions() }
    )

    Box(
        modifier = Modifier
            .pullRefresh(swipeToRefreshState)
            .fillMaxSize()
    ) {
        // Display UI based on resource state
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

                // Group transactions by dates
                val transactionsByDates = transactions.groupBy { transaction ->
                    val localDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(transaction.timestamp),
                        ZoneId.systemDefault()
                    )
                    localDateTime.toLocalDate() // Extract only the date part
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    transactionsByDates.forEach { (date, transactionsForDate) ->
                        stickyHeader {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        items(transactionsForDate) { transaction ->
                            val transactionTypeColor = if(transaction.type == "Expense") Color.Red else Color.Green
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.TopEnd
                            ){
                                Text(
                                    text = transaction.type,
                                    modifier = Modifier
                                        .background(
                                            transactionTypeColor,
                                            RoundedCornerShape(topStart = 25.dp)
                                        )
                                        .padding(8.dp)
                                    ,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                border = BorderStroke(0.2.dp, Color.Black)
                            ){
                                TransactionItemUI(transaction = transaction)
                            }
                            Spacer(modifier = Modifier.padding(6.dp))
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

        // Overlay the refresh indicator on top of everything
        PullRefreshIndicator(
            refreshing = isRefreshing ?: false,
            state = swipeToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}


