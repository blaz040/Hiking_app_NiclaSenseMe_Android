package com.example.ble_con.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble_con.SnackbarManager
import com.example.ble_con.ViewModel
import com.example.ble_con.presentation.MainScreen
import com.example.ble_con.repository.Routes

@Composable
fun Navigation(
   vm: ViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        SnackbarManager.snackbarMessage.collect { event->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true,
                actionLabel = event.action?.label,
                duration = event.duration
            )
            if(result == SnackbarResult.ActionPerformed){
                event.action!!.callback()
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.MainScreen,
            modifier = Modifier.padding(contentPadding))
        {
            composable(Routes.MainScreen) {
                MainScreen(navController = navController, vm)
            }
            composable(Routes.RecordingScren) {
                SensorDataScreen(vm)
            }
            composable(Routes.SavedRecordingsScreen){
                SavedRecordingsScreen(vm)
            }
        }
    }
}