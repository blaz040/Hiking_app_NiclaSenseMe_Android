package com.example.ble_con

import android.app.Application
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.ble_con.dataManager.SensorDataManagerService
import com.example.ble_con.dataManager.ble.BLE_Manager
import com.example.ble_con.repository.ViewModelData

class ViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "ViewModel"
    /* bluetooth */
    private val ble_api: BLE_Manager by lazy{ BLE_Manager(application) }

    fun startRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_START)
    }
    fun stopRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_STOP)
    }
    fun toggleRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_TOGGLE)
    }
    fun pauseRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_PAUSE)
    }
    fun resumeRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_RESUME)
    }
    fun SDMS_send(action: String,value:Int = -1, result:ScanResult? = null) {
        Intent(application, SensorDataManagerService::class.java).also{
            it.action = action
            if(value != -1)
                it.putExtra("command",value)
            if(result != null)
                it.putExtra("ScanResult",result)
            application.startService(it)
        }
    }
    fun scanBLE() {
        ble_api.scanLeDevice()
    }
    fun connect(result: ScanResult) {
        SDMS_send(SensorDataManagerService.DEVICE_CONNECT, result = result)
    }
    fun disconnect() {
        SDMS_send(SensorDataManagerService.DEVICE_DISCONNECT)
    }
    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}