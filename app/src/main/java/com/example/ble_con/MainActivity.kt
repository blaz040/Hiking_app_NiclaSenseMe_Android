package com.example.ble_con

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ble_con.presentation.Navigation

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),0)

        val bluetoothmanager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothmanager.adapter

        setContent {
            Navigation()
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothDialog()
    }
    private fun showBluetoothDialog()
    {
        if(bluetoothAdapter.isEnabled == false)
        {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startBluetoothIntentForResult.launch(enableBluetoothIntent)
        }
    }
    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if(result.resultCode != Activity.RESULT_OK)
        {
            showBluetoothDialog()
        }
    }
}
