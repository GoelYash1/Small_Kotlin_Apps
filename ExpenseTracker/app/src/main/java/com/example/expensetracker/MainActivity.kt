package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.mainScreen.MainScreen
import com.example.expensetracker.presentation.onBoardingScreen.OnBoardingScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var mainNavController = rememberNavController()
            ExpenseTrackerTheme {
                MainNavigation(mainNavController = mainNavController)
            }
        }
    }
}

@Composable
fun MainNavigation(
    mainNavController: NavHostController
){
    NavHost(
        navController = mainNavController,
        startDestination = OnBoarding.route
    ){
        composable(OnBoarding.route){
            OnBoardingScreen(mainNavController)
        }
        composable(Home.route){
            MainScreen()
        }
    }
}