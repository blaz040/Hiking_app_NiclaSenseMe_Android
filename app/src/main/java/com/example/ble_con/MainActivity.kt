package com.example.ble_con

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.emptyLongSet
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ble_con.Presentation.MainScreen
import com.example.ble_con.Presentation.Navigation
import com.example.ble_con.Presentation.Routes
import com.example.ble_con.ble.BLE_manager
import com.example.ble_con.presentation.ShowBLEResults
import com.example.ble_con.ui.theme.BLE_conTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import java.util.logging.Handler
import javax.inject.Inject

//@AndroidEntryPoint
@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    //@Inject lateinit var bluetoothAdapter: BluetoothAdapter
    //private lateinit var manager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    //private var ScanResults = mutableStateListOf<ScanResult>()
    //lateinit var bleManager: BLE_manager

    //private val bluetoothManager by lazy { BLE_manager(bluetoothAdapter, this) }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val manager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = manager.adapter
        val ble_api = BLE_manager(bluetoothAdapter,this)

        //bluetoothManager = getSystemService(BluetoothManager::class.java)
        //bluetoothAdapter = bluetoothManager.adapter
        setContent {
            //val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            //bluetoothAdapter = manager.adapter

            //ShowBLEResults(bluetoothManager)

            Navigation(ble_api)
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothDialog()
    }

    private fun showBluetoothDialog()
    {
        if(bluetoothAdapter?.isEnabled == false)
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
