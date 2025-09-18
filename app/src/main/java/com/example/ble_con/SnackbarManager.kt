package com.example.ble_con

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableStateFlow

data class SnackbarEvent(
    val message:String,
    val action: SnackbarAction?,
    val duration: SnackbarDuration
)

data class SnackbarAction(
    val label:String,
    val callback:()->Unit
)

object SnackbarManager {
    val defaultDuration:SnackbarDuration = SnackbarDuration.Short
    val snackbarMessage = MutableStateFlow(SnackbarEvent("",null, defaultDuration))

    fun send(message: String,duration: SnackbarDuration = defaultDuration){
        snackbarMessage.tryEmit(SnackbarEvent(message,null,duration))
    }
    fun send(message: String,duration: SnackbarDuration = defaultDuration, label:String, callback: () -> Unit){
        snackbarMessage.tryEmit(SnackbarEvent(message, SnackbarAction(label,callback),duration))
    }
}