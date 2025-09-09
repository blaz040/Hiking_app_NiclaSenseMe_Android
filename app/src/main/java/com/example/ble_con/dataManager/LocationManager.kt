package com.example.ble_con.dataManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class LocationManager(
    val context:Context,
    val onLocationReceived: (LatLng) ->Unit,
    val delay_ms:Long = 5000
) {
    private var run = false
    private var locationClient: FusedLocationProviderClient? = null

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    private val TAG = "LocationManager"

    fun start() {
        Log.d(TAG,"Starting...")
        startTracking()
    }

    private fun startTracking() {
        timerJob?.cancel()

        locationClient = LocationServices.getFusedLocationProviderClient(context)
        run = true

        timerJob = serviceScope.launch {
            while(true) {
                if (run) {
                    locationClient?.lastLocation?.addOnSuccessListener {location ->
                        val lat = location.latitude
                        val lng = location.longitude
                        val loc = LatLng(lat,lng)
                        if(location != null) onLocationReceived(loc)
                        Log.d(TAG,"LOCATION: $location")
                    }
                }
                delay(delay_ms)
            }
        }
    }
    fun stop() {
        Log.d(TAG,"Stopping")
        run = false

        timerJob?.cancel()
    }
    fun toggleRun() {
        run = !run
        Log.d(TAG,"Toggled run=$run")
    }
}