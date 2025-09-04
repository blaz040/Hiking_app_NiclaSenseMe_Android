package com.example.ble_con.dataManager

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecordingManager(
    val onTimeUpdate: (Int)->Unit
) {

    private var time: Int = 0

    private var run = false

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    private val TAG = "RecordingManager"

    fun start() {
        Log.d(TAG,"Starting...")
        startTimer()
    }
    private fun startTimer() {
        timerJob?.cancel()

        run = true;

        timerJob = serviceScope.launch {
            while(true) {
                if (run) {
                    time++
                    onTimeUpdate(time)
               }
                delay(1000)
            }
        }
    }
    fun stop() {
        Log.d(TAG,"Stopping")
        time = 0
        onTimeUpdate(time)
        run = false

        timerJob?.cancel()
    }
    fun toggleRun() {
        run = !run
        Log.d(TAG,"Toggled run=$run")
    }
}