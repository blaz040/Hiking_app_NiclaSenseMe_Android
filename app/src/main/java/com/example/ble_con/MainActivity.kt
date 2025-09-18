package com.example.ble_con

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ble_con.presentation.Navigation

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    val TAG = "MainActivity_Log"
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var interacted = true // used so that there arent multiple BLE_notification windows

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate....")

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mReceiver,filter)

        val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        setContent {
            Navigation()
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG,"onDestroy.....")
        unregisterReceiver(mReceiver)
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart....")
        requestBLEPermission()
        showBluetoothDialog()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause....")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume....")
    }
    private fun showLocationDialog() {
        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
    }
    fun requestPermission(permission:String){
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }.launch(permission)
    }
    fun requestBLEPermission() = requestBluetoothPermission.launch(
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH
        )
    )

    val requestBluetoothPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            showLocationDialog()
    }

    val requestBluetoothIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
            interacted = true
            if(result.resultCode != RESULT_OK) {
                showBluetoothDialog()
            }
        }
    private fun showBluetoothDialog() {
        if(bluetoothAdapter.isEnabled == false && interacted) {
            interacted = false
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetoothIntent.launch(enableBluetoothIntent)
        }
    }

    private val mReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"received BLE intent ${bluetoothAdapter.isEnabled}")
            when(intent?.action) {
               BluetoothAdapter.ACTION_STATE_CHANGED -> showBluetoothDialog()
            }
        }
    }
}
