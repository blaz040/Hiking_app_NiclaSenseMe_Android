package com.example.ble_con

import android.app.Application
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.ble_con.Snackbar.SnackbarManager
import com.example.ble_con.fileManager.FileManager
import com.example.ble_con.dataManager.SensorDataManagerService
import com.example.ble_con.dataManager.ble.BLEManager
import com.example.ble_con.repository.ViewModelData
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "ViewModel"
    /* bluetooth */
    private val ble_api: BLEManager by lazy{ BLEManager(application) }

    val file_api: FileManager by lazy{ FileManager(application) }

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
    fun saveRecording() {
        SDMS_send(SensorDataManagerService.RECORDING_SAVE)
    }
    fun stopService(){
        SDMS_send(SensorDataManagerService.STOP_SELF)
    }
    fun saveRecording(fileName: String, callback: (Boolean)->Unit){
        pauseRecording()
        if(file_api.save(fileName) == true) {
            callback(true)
            stopRecording()
        }
        else{
            callback(false)
        }
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
        ble_api.scanBleDevice()
    }
    fun connect(result: ScanResult) {
        SDMS_send(SensorDataManagerService.DEVICE_CONNECT, result = result)
    }
    fun disconnect() {
        SDMS_send(SensorDataManagerService.DEVICE_DISCONNECT)
    }

    fun deleteFile(fileName:String) = file_api.delete(fileName)
    // TODO add this to coroutine
    fun loadFile(fileName:String){
        SnackbarManager.send("Loading Files....")
        file_api.loadFile(fileName)
    }
    fun loadFileList() = file_api.notifyViewModelData()
    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}