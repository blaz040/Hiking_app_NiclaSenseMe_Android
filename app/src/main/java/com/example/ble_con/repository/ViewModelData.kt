package com.example.ble_con.repository

import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ViewModelData {

    private val _selectedDevice: MutableLiveData<ScanResult?> = MutableLiveData<ScanResult?>(null)
    val selectedDevice: LiveData<ScanResult?> = _selectedDevice

    private val _conStatus = MutableLiveData<String>("Disconnected")
    val conStatus: LiveData<String> = _conStatus

    fun setConnectionStatus(status: String)
    {
        _conStatus.postValue(status)
    }
    fun setSelectedDevice(result: ScanResult?)
    {
        _selectedDevice.postValue(result)
    }
}