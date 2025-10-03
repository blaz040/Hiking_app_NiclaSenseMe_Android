package com.example.ble_con.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
import com.example.ble_con.R
import com.example.ble_con.dataManager.repo.ConnectionStatus
import com.example.ble_con.fileManager.FileData
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SensorData

@SuppressLint("MissingPermission")
object ViewModelData {

    class LiveDataWrapper<T>(
        val init: T
    ){
        val mutableData = MutableLiveData<T>(init)
        val liveData: LiveData<T> = mutableData

        @Composable
        fun observeAsState(value:T): State<T>{
            return liveData.observeAsState(value)
        }
        @Composable
        fun observeAsState(): State<T?>{
            return liveData.observeAsState()
        }
        var value
            get() = mutableData.value ?: init
            set(v) = mutableData.postValue(v)

        fun postValue(value:T){
            mutableData.postValue(value)
        }
    }
    data class DataInfo(val name:String,val icon:Int,val postFix:String, val list:LiveData<MutableList<Point>>)
    val nothing = DataInfo("null",-1,"",SensorData.humidity.liveData)

    val humidity =      DataInfo("Humidity",R.drawable.humidity_icon," %",SensorData.humidity.liveData)
    val temperature =   DataInfo("Temperature",R.drawable.temp_icon," C",SensorData.temperature.liveData)
    val pressure =      DataInfo("Pressure",R.drawable.pressure_icon," hPa",SensorData.pressure.liveData)
    val steps =         DataInfo("Steps",R.drawable.steps_icon,"",SensorData.steps.liveData)
    val airQuality =    DataInfo("Air Quality",R.drawable.air_quiality_icon,"",SensorData.iaq.liveData)
    val voc =           DataInfo("VOC",R.drawable.voc_icon,"",SensorData.voc.liveData)
    val co2 =           DataInfo("CO2",R.drawable.co2_icon,"",SensorData.co2.liveData)
    val altitude =      DataInfo("Altitude",R.drawable.altitude_icon," m",SensorData.altitude.liveData)

    val listOfDataInfo = listOf(humidity,temperature,pressure,steps,airQuality,voc,co2,altitude)

    val scanResultMap = LiveDataWrapper<MutableMap<String,ScanResult>>(mutableMapOf<String,ScanResult>())

    val selectedDevice= LiveDataWrapper<ScanResult?>(null)

    val fileList = LiveDataWrapper<List<FileData>>(listOf())

    //private val _fileList = MutableLiveData<List<FileData>>(listOf())
    //val fileList: LiveData<List<FileData>> = _fileList

    var fileData = FileData("null","null")

    val conStatus = LiveDataWrapper<String>(ConnectionStatus.DISCONNECTED)

    val recordingStatus = LiveDataWrapper<RecordingStatus>(RecordingStatus.STOPPED)

//    val _currentStatus = MutableLiveData<String>("null")
//    val currentStatus:LiveData<String> = _currentStatus

    val time = LiveDataWrapper<Int>(SensorData.time)

    val scanningStatus = LiveDataWrapper<Boolean>(false)

    val locationEnabled = LiveDataWrapper<Boolean>(false)

    fun addScanResult(result: ScanResult) {
        if(scanResultMap.value != null) {
            if (!scanResultMap.value.containsKey(result.device.name)) {
                val map = scanResultMap.value.toMutableMap().apply { put(result.device.name, result) }
                scanResultMap.value = map
            }
        }
        else {
            val map = mutableMapOf<String,ScanResult>().apply { put(result.device.name,result) }
            scanResultMap.value = (map)
        }
    }
    fun clearScanResult(){
        val map = scanResultMap.value.toMutableMap().apply{ clear() }
        scanResultMap.value = map
    }

    fun updateFileList(list:List<FileData>) = fileList.postValue(list)

    fun setSelectedDevice(result: ScanResult?) = selectedDevice.postValue(result)
    fun setConnectionStatus(status: String) = conStatus.postValue(status)
}