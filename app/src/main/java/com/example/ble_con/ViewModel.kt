package com.example.ble_con

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble_con.ble.BLE_manager
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ViewModel(application: Application) : AndroidViewModel(application) {
    /* bluetooth */
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val ble_api: BLE_manager by lazy{ BLE_manager(bluetoothAdapter, application) }


    /* LiveData */
    val maxListSize = 80

    private val _selectedDevice: MutableLiveData<ScanResult?> = MutableLiveData<ScanResult?>(null)
    val selectedDevice: LiveData<ScanResult?> = _selectedDevice

    private val _conStatus = MutableLiveData<String>("Disconnected")
    val conStatus: LiveData<String> = _conStatus

    private val _tempValue: MutableLiveData<MutableList<Float>> = MutableLiveData( mutableListOf(0f) )
    val tempValue: LiveData<MutableList<Float>> = _tempValue

    private val _humidityValue: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val humidityValue: LiveData<MutableList<Int>> = _humidityValue

    private val _IAQValue: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val IAQValue: LiveData<MutableList<Int>> = _IAQValue

    private val _bVOCValue: MutableLiveData<MutableList<Float>> = MutableLiveData( mutableListOf(0f) )
    val bVOCValue: LiveData<MutableList<Float>> = _bVOCValue

    private val _CO2Value: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val CO2Value: LiveData<MutableList<Int>> = _CO2Value
    /* */

    fun scanBLE()
    {
        ble_api.closeConnection()
        _conStatus.value = "Disconnected"
        _selectedDevice.value = null
        ble_api.scanLeDevice()
    }
    fun getScanResults(): SnapshotStateList<ScanResult>
    {
        return ble_api.getbleScanResults()
    }
    fun connect(result: ScanResult)
    {
        _conStatus.value = "Disconnected"
        _selectedDevice.value = result
        ble_api.connectToDevice(result.device,::setConnectionStatus, ::onDataReceived)
    }
    fun <T> updateList (list: MutableLiveData<MutableList<T>>, value :T)
    {
        val newList = list.value.toMutableList().apply { add(value) }

        if(list.value.size >= maxListSize )
            newList.removeAt(0)

        list.postValue(newList)
    }
    fun setConnectionStatus(status: String)
    {
        _conStatus.postValue(status)
    }
    fun onDataReceived(char: BluetoothGattCharacteristic)
    {
        val intFormat = BluetoothGattCharacteristic.FORMAT_UINT16
        when (char.uuid) {
            ble_api.temp_UUID -> {
                val rawData = char.value
                if (rawData != null && rawData.size == 4) {
                    val buffer = ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN)
                    val floatValue = buffer.float

                    updateList(_tempValue,floatValue)

                    Log.d("GATT_NOTIFY", "Characteristic temp: $floatValue")
                } else {
                    Log.e("GATT_NOTIFY", "Received invalid data for temp characteristic.")
                }
                //Log.d("GATT_NOTIFY","Characteristic temp ${ sData.value.temp_value }")
            }

            ble_api.humidity_UUID -> {
                updateList(_humidityValue,char.getIntValue(intFormat,0))
               // Log.d("GATT_NOTIFY", "Characteristic humidity ")
            }
            ble_api.IAQ_UUID -> {
                updateList(_IAQValue,char.getIntValue(intFormat,0))
                Log.d("GATT_NOTIFY", "Characteristic IAQ")
            }
            ble_api.bVOC_UUID -> {
                val rawData = char.value
                if (rawData != null && rawData.size == 4) {
                    val buffer = ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN)
                    val floatValue = buffer.float

                    updateList(_bVOCValue,floatValue)

                    Log.d("GATT_NOTIFY", "Characteristic bVOC: $floatValue")
                } else {
                    Log.e("GATT_NOTIFY", "Received invalid data for bVOC characteristic.")
                }
               // Log.d("GATT_NOTIFY", "Characteristic bVOC")
            }

            ble_api.CO2_UUID -> {
                updateList(_CO2Value,char.getIntValue(intFormat,0))

                Log.d("GATT_NOTIFY", "Characteristic CO2")
            }
            else -> {
                Log.d("GATT_NOTIFY", "Characteristic Unknown")
            }
        }
    }
}