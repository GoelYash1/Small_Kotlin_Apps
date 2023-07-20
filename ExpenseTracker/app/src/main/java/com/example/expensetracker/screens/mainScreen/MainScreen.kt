package com.example.expensetracker.screens.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.Home
import com.example.expensetracker.MainBottomNavigation
import com.example.expensetracker.MainTopBar
import com.example.expensetracker.Notification
import com.example.expensetracker.Transactions
import com.example.expensetracker.screens.mainScreen.homeScreen.HomeScreen
import com.example.expensetracker.screens.mainScreen.transactions.TransactionScreen
import com.example.expensetracker.screens.notifications.NotificationScreen
import com.example.expensetracker.viewModels.ExpenseTrackerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(expenseTrackerViewModel: ExpenseTrackerViewModel) {
    expenseTrackerViewModel.readAndStoreSMS()
    val homeNavController  = rememberNavController()
    Scaffold(
        topBar = {
            MainTopBar(homeNavController)
        },
        bottomBar = {
            MainBottomNavigation(navController = homeNavController)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeNavigation(homeNavController,expenseTrackerViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavigation(
    navController: NavHostController,
    expenseTrackerViewModel: ExpenseTrackerViewModel
) {
    NavHost(navController = navController, startDestination = Home.route){
        composable(Home.route){
            HomeScreen(expenseTrackerViewModel)
        }
        composable(Transactions.route){
            TransactionScreen(expenseTrackerViewModel)
        }
        composable(Notification.route){
            NotificationScreen(expenseTrackerViewModel)
        }
    }
}
