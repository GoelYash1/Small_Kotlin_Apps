package com.example.expensetracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.mainScreen.MainScreen
import com.example.expensetracker.presentation.onBoardingScreen.OnBoardingScreen
import com.example.expensetracker.presentation.transactions.TransactionScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val onBoardingCompleted = remember {
                mutableStateOf(
                    getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                        .getBoolean("onBoardingCompleted", false)
                )
            }
            var mainNavController = rememberNavController()
            if (onBoardingCompleted.value) {
                Scaffold(
                    topBar = {
                        MainTopBar()
                    },
                    bottomBar = {
                        MainBottomNavigation(navController = mainNavController)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        MainNavigation(mainNavController,onBoardingCompleted)
                    }
                }
            }
            else {
                OnBoardingScreen(
                    mainNavController = mainNavController,
                    onBoardingCompleted = onBoardingCompleted
                )
            }
        }
    }
}


@Composable
fun MainNavigation(
    mainNavController: NavHostController,
    onBoardingCompleted: MutableState<Boolean>
){
    NavHost(
        navController = mainNavController,
        startDestination = OnBoarding.route
    ){
        composable(OnBoarding.route){
            OnBoardingScreen(mainNavController,onBoardingCompleted)
        }
        composable(Home.route){
            MainScreen()
        }
        composable(Transactions.route){
            TransactionScreen()
        }
    }
}


