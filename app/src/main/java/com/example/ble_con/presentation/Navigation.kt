package com.example.ble_con.Presentation

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble_con.ble.BLE_manager

@Composable
fun Navigation(
   ble_api :BLE_manager
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.screenA)
    {
        composable(Routes.screenA)
        {
            MainScreen(navController = navController,ble_api)
        }
        composable(Routes.screenB)
        {
            SensorDataScreen()
        }
    }
}