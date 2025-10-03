package com.example.ble_con.dataManager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.ble_con.repository.ViewModelData
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
    private var run = false
    private var locationClient: FusedLocationProviderClient? = null
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
                // checks location avaliability and updates it to ViewModelData / UI
                locationClient?.locationAvailability?.addOnCompleteListener {location ->
                    val ch = location.result.isLocationAvailable()
                    ViewModelData.locationEnabled.postValue(ch)
                    Log.d(TAG,"location is: $ch")
                }
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