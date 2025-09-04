package com.example.ble_con

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import com.example.ble_con.presentation.Navigation
import com.example.ui.theme.AppTheme

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    val TAG = "MainActivity_Log"
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate....")
        val bluetoothmanager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothmanager.adapter

        setContent {
            Navigation()
        }
    }
    override fun onDestroy(){
        Log.d(TAG,"onDestroy.....")
        super.onDestroy()

    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart....")
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
    private fun showBluetoothDialog() {
        if(bluetoothAdapter.isEnabled == false) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startBluetoothIntentForResult.launch(enableBluetoothIntent)
        }
    }
    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if(result.resultCode != Activity.RESULT_OK) {
            showBluetoothDialog()
        }
    }
}
