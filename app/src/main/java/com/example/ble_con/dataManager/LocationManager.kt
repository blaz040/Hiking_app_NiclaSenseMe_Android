package com.example.ble_con.dataManager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class LocationManager(
    val context:Context,
    val serviceScope: CoroutineScope,
    val onLocationReceived: (LatLng) ->Unit,
    val delay_ms:Long = 5000
) {
    private val TAG = "LocationManager"

    private var run = false
    private var timerJob: Job? = null
    private var locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun start() {
        Log.d(TAG,"Starting...")
        startTracking()
    }

    private fun startTracking() {
        timerJob?.cancel()
        run = true
        timerJob = serviceScope.launch {
            while(true) {
                if (run) {
                    locationClient?.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null)?.addOnSuccessListener { location->
                        if(location != null){
                            val lat = location.latitude
                            val lng = location.longitude
                            val loc = LatLng(lat,lng)
                            //ViewModelData._altitude.postValue(location.altitude.toInt())
                            //val s = location.verticalAccuracyMeters
                            //val loc = LatLng(lat,lng)
                            onLocationReceived(loc)
                            Log.d(TAG,"LOCATION: $location")
                        }
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