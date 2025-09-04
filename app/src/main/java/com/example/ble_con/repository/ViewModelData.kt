package com.example.ble_con.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SensorData

@SuppressLint("MissingPermission")
object ViewModelData {

    private val _scanResultMap: MutableLiveData<MutableMap<String,ScanResult>> = MutableLiveData<MutableMap<String,ScanResult>>(mutableMapOf<String,ScanResult>())
    val scanResultMap: MutableLiveData<MutableMap<String,ScanResult>> = _scanResultMap

    val _selectedDevice: MutableLiveData<ScanResult?> = MutableLiveData<ScanResult?>(null)
    val selectedDevice: LiveData<ScanResult?> = _selectedDevice

    val _conStatus = MutableLiveData<String>("Disconnected")
    val conStatus: LiveData<String> = _conStatus

    val _recordingStatus = MutableLiveData<RecordingStatus>(RecordingStatus.STOPPED)
    val recordingStatus: LiveData<RecordingStatus> = _recordingStatus

    val time : LiveData<Int> = SensorData._time

    fun addScanResult(result: ScanResult) {
        if(_scanResultMap.value != null) {
            if (!_scanResultMap.value.containsKey(result.device.name)) {
                val map = _scanResultMap.value.toMutableMap().apply { put(result.device.name, result) }
                _scanResultMap.value = map
            }
        }
        else {
            val map = mutableMapOf<String,ScanResult>().apply { put(result.device.name,result) }
            _scanResultMap.value = (map)
        }

    }
    fun clearScanResult(){
        val map = _scanResultMap.value.toMutableMap().apply{ clear() }
        _scanResultMap.value = map
    }
    fun setSelectedDevice(result: ScanResult?) = _selectedDevice.postValue(result)
    fun setConnectionStatus(status: String) = _conStatus.postValue(status)
}