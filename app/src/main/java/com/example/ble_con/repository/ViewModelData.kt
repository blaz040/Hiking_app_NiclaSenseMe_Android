package com.example.ble_con.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@SuppressLint("MissingPermission")
object ViewModelData {

    private val _scanResultMap: MutableLiveData<MutableMap<String,ScanResult>> = MutableLiveData<MutableMap<String,ScanResult>>(mutableMapOf<String,ScanResult>())
    val scanResultMap: MutableLiveData<MutableMap<String,ScanResult>> = _scanResultMap

    private val _selectedDevice: MutableLiveData<ScanResult?> = MutableLiveData<ScanResult?>(null)
    val selectedDevice: LiveData<ScanResult?> = _selectedDevice

    private val _conStatus = MutableLiveData<String>("Disconnected")
    val conStatus: LiveData<String> = _conStatus

    private val _transferringData = MutableLiveData<Boolean>(false)
    val transferringData: LiveData<Boolean> = _transferringData

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
    fun setTransferringData(status: Boolean) = _transferringData.postValue(status)

}