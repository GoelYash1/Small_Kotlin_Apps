package com.example.expensetracker.screens.mainScreen.transactions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.models.TransactionCategories
import com.example.expensetracker.helper.Resource
import com.example.expensetracker.utils.Calendar
import com.example.expensetracker.utils.DropDownMenuUI
import com.example.expensetracker.viewModels.ExpenseTrackerViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(expenseTrackerViewModel: ExpenseTrackerViewModel) {
    val hasSmsPermission = ContextCompat.checkSelfPermission(
        LocalContext.current,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED
    val resource by expenseTrackerViewModel.transactions.collectAsState()
    val isRefreshing by expenseTrackerViewModel.refreshing.observeAsState()

    val currentMonth = Month.values()[LocalDate.now().month.value - 1].name.toLowerCase().capitalize()

    var selectedMonth by remember {
        mutableStateOf("All")
    }
    val isRefreshAllowed = selectedMonth == "All" || selectedMonth.equals(currentMonth, true)

    val swipeToRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing ?: false,
        onRefresh = {
            if (isRefreshAllowed) {
                expenseTrackerViewModel.refreshTransactions(hasSmsPermission,Year.now().value,selectedMonth)
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.inversePrimary)
                .padding(horizontal = 20.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.44f)
            ) {
                val categories = TransactionCategories.categories.map { it.name }
                Text(text = "Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                DropDownMenuUI(categories,"Category"){

                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
            ) {
                val months = listOf<String>("All","January","February","March","April","May","June","July","August","September","October","November","December")
                Text(text = "Months", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                DropDownMenuUI(months,"Months"){
                    month->
                    selectedMonth = month
                    if (month!= "All"){
                        val selectedMonthEnum = Month.valueOf(month.toUpperCase())
                        expenseTrackerViewModel.getTransactionsForMonthAndYear(month = selectedMonthEnum)
                    }
                    else{
                        expenseTrackerViewModel.fetchAndStoreTransactions(hasSmsPermission)
                    }
                }
            }
        }
        Calendar()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(swipeToRefreshState)

        ) {

            when (resource) {

                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Resource.Success -> {
                    val transactions = (resource as Resource.Success<List<Transaction>>).data

                    val transactionsByDates = transactions.groupBy { transaction ->
                        val localDateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(transaction.timestamp),
                            ZoneId.systemDefault()
                        )
                        localDateTime.toLocalDate()
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
            if (selectedMonth == "All" || selectedMonth.equals(currentMonth,true)){

                PullRefreshIndicator(
                    refreshing = isRefreshing ?: false,
                    state = swipeToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}


