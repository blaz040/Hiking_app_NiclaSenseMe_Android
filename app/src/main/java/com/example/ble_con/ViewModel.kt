package com.example.ble_con

import android.app.Application
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.ble_con.ble.BLE_Manager
import com.example.ble_con.ble.BLE_Service
import com.example.ble_con.repository.SendCommand
import com.example.ble_con.repository.SensorData
import com.example.ble_con.repository.ViewModelData

class ViewModel(application: Application) : AndroidViewModel(application) {


    /* bluetooth */
    private val ble_api: BLE_Manager by lazy{ BLE_Manager(application) }

    fun send(value: SendCommand)
    {
        Intent(application,BLE_Service::class.java).also {
            it.action = BLE_Service.Actions.SEND.toString()
            it.putExtra("command",value.ordinal)
            application.startService(it)
        }
    }
    fun scanBLE()
    {
        ble_api.scanLeDevice()
    }
    fun getScanResults(): SnapshotStateList<ScanResult>
    {
        return ble_api.getbleScanResults()
    }
    fun clearData()
    {
        SensorData._tempList.value.clear()
        SensorData._CO2List.value.clear()
        SensorData._bVOCList.value.clear()
        SensorData._IAQList.value.clear()
        SensorData._humidityList.value.clear()
    }
    fun connect(result: ScanResult)
    {
        ViewModelData.setSelectedDevice(result)
        val context = getApplication() as Context
        val intent = Intent(context,BLE_Service::class.java)

        intent.action = BLE_Service.Actions.CONNECT.toString()
        intent.putExtra("ScanResult",result)

        context.startService(intent)
    }
    fun disconnect()
    {
        val context = getApplication() as Context
        Intent(context,BLE_Service::class.java).also {
            it.action = BLE_Service.Actions.DISCONNECT.toString()
            context.startService(it)
        }
    }
    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}