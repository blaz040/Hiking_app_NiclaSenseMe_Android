package com.example.ble_con.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble_con.ViewModel
import com.example.ble_con.presentation.MainScreen
import com.example.ble_con.repository.Routes

@Composable
fun Navigation(
   vm: ViewModel = viewModel()
) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.screenA)
    {
        composable(Routes.screenA)
        {
            MainScreen(navController = navController,vm)
        }
        composable(Routes.screenB)
        {
            SensorDataScreen(vm)
        }
    }
}