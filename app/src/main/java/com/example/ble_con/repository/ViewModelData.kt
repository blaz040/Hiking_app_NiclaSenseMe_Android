package com.example.ble_con.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.common.model.Point
import com.example.ble_con.R
import com.example.ble_con.ViewModel
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.presentation.HumidityGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
object ViewModelData {
    data class DataInfo(val name:String,val icon:Int,val postFix:String, val list:LiveData<MutableList<Point>>)
    val nothing = DataInfo("null",-1,"",SensorData.humidityList)

    val humidity = DataInfo("Humidity",R.drawable.humidity_icon," %",SensorData.humidityList)
    val temperature = DataInfo("Temperature",R.drawable.temp_icon," C",SensorData.tempList)
    val pressure = DataInfo("Pressure",R.drawable.pressure_icon," hPa",SensorData.pressureList)
    val steps = DataInfo("Steps",R.drawable.steps_icon,"",SensorData.stepsList)
    val airQuality = DataInfo("Air Quality",R.drawable.air_quiality_icon,"",SensorData.IAQList)
    val voc = DataInfo("VOC",R.drawable.voc_icon,"",SensorData.bVOCList)
    val co2 = DataInfo("CO2",R.drawable.co2_icon,"",SensorData.CO2List)
    val altitude = DataInfo("Altitude",R.drawable.co2_icon," m",SensorData.altitude)

    val _scanResultMap = MutableLiveData<MutableMap<String,ScanResult>>(mutableMapOf<String,ScanResult>())
    val scanResultMap: MutableLiveData<MutableMap<String,ScanResult>> = _scanResultMap

    val _selectedDevice: MutableLiveData<ScanResult?> = MutableLiveData<ScanResult?>(null)
    val selectedDevice: LiveData<ScanResult?> = _selectedDevice

    val _conStatus = MutableLiveData<String>("Disconnected")
    val conStatus: LiveData<String> = _conStatus

    val _recordingStatus = MutableLiveData<RecordingStatus>(RecordingStatus.STOPPED)
    val recordingStatus: LiveData<RecordingStatus> = _recordingStatus

    val _currentStatus = MutableLiveData<String>("null")
    val currentStatus:LiveData<String> = _currentStatus

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