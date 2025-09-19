package com.example.ble_con.dataManager.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.util.Log
import com.example.ble_con.dataManager.repo.BluetoothBroadcastAction
import com.example.ble_con.dataManager.repo.ConStatus
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.dataManager.repo.add
import com.example.ble_con.repository.ViewModelData
import java.util.UUID
import kotlin.math.pow
import kotlin.math.round

@SuppressLint("MissingPermission")
class BLEManager(
    private val context: Context
){
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val envService_UUID = UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB")
    private val temp_UUID = UUID.fromString("00002A6E-0000-1000-8000-00805F9B34FB")
    private val humidity_UUID = UUID.fromString("00002A6F-0000-1000-8000-00805F9B34FB")
    private val pressure_UUID = UUID.fromString("00002780-0000-1000-8000-00805F9B34FB")
    private val IAQ_UUID = UUID.fromString("00002AF2-0000-1000-8000-00805F9B34FB")
    private val CO2_UUID = UUID.fromString("00002B8C-0000-1000-8000-00805F9B34FB")
    private val bVOC_UUID = UUID.fromString("00002BE7-0000-1000-8000-00805F9B34FB")

    private val env_characteristics_list = listOf(temp_UUID,humidity_UUID,pressure_UUID,IAQ_UUID,bVOC_UUID,CO2_UUID)

    private val otherService_UUID = UUID.fromString("00001000-0000-1000-8000-00805F9B34FB")
    private val step_UUID = UUID.fromString("000027BA-0000-1000-8000-00805F9B34FB")
    private val messageReceiver_UUID  = UUID.fromString("00001001-0000-1000-8000-00805F9B34FB")

    private val other_characteristics_list = listOf(step_UUID)

    private var bluetoothGatt: BluetoothGatt? = null

    private val bluetoothManager by lazy {context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter}
    private val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    private var scanning = false
    private val handler = android.os.Handler()

    private val connectionStatus = ViewModelData._conStatus

    // Stops scanning after 10 seconds.
     private val SCAN_PERIOD: Long = 10000


    fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            ViewModelData.clearScanResult()
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }
    // Device scan callback.
     private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(result.device.name != null)
                ViewModelData.addScanResult(result)
        }
    }
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }
    fun closeConnection() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
    fun broadcastUpdate(action:String) {
        val intent = Intent(action)
            .setPackage(context.getPackageName());
        context.sendBroadcast(intent)
    }
    fun connectToDevice(device: BluetoothDevice) {
        // Disconnect from any previously connected device
        disconnect()

        val gattQueue = mutableListOf<() ->Unit>()
        var gattQueueBusy = false

        fun nextGattOperation() {
            if(gattQueue.isNotEmpty() && !gattQueueBusy)
            {
                gattQueueBusy = true
                gattQueue.removeAt(0).invoke()
            }
        }
        // Attempt to connect to the GATT server
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            // Callback for connection state changes
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Device connected
                    Log.d("GATT_CONN", "Connected to GATT server.")
                    gatt.discoverServices()

                    broadcastUpdate(BluetoothBroadcastAction.CONNECTED)
                    connectionStatus.postValue(ConStatus.CONNECTED)

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Device disconnected
                    Log.d("GATT_CONN", "Disconnected from GATT server.")

                    broadcastUpdate(BluetoothBroadcastAction.DISCONNECTED)
                    connectionStatus.postValue(ConStatus.DISCONNECTED)

                    closeConnection()
                }
            }
            // Callback for services discovered
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("GATT_CONN", "Services discovered successfully")
                    //gatt.printGattTable()
                    gatt.services.forEach {
                        Log.d("GATT_CONN","service ${it.type}: ${it.uuid} discovered")
                    }

                    //Enable notifications for each characteristic in list
                    env_characteristics_list.forEach {
                        val tmpChar = gatt.getService(envService_UUID)?.getCharacteristic(it)
                        if(tmpChar != null) {
                            gattQueue.add { enableCharacteristicNotification(gatt, tmpChar) }
                        }
                    }
                    other_characteristics_list.forEach {
                        val tmpChar = gatt.getService(otherService_UUID)?.getCharacteristic(it)
                        if(tmpChar != null) {
                            gattQueue.add { enableCharacteristicNotification(gatt, tmpChar) }
                        }
                    }

                    nextGattOperation()
                } else {
                    Log.e("GATT_CONN", "Service discovery failed with status: $status")
                }
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
                super.onDescriptorWrite(gatt, descriptor, status)
                gattQueueBusy = false
                nextGattOperation()
            }

            private fun enableCharacteristicNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic.getDescriptor(CCCD_UUID)
                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    val writeSuccess = gatt.writeDescriptor(descriptor)
                    Log.d("GATT_CONN", "Attempting to write CCCD for ${characteristic.uuid}: $writeSuccess")
                } else {
                    Log.e("GATT_CONN", "CCCD descriptor is NULL for characteristic: ${characteristic.uuid}. Continuing with next op.")
                    gattQueueBusy = false
                    nextGattOperation()
                }
            }
            // Callback for characteristic read operations
            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: android.bluetooth.BluetoothGattCharacteristic, status: Int) {
                //super.onCharacteristicRead(gatt, characteristic, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0) // Get the byte array value
                    Log.d("GATT_CON", "Characteristic ${characteristic.uuid} read: ${value}")
                    // Process the read value here
                } else {
                    Log.d("GATT_CON", "Characteristic ${characteristic.uuid} read failed with status: $status")
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                characteristic?.let { char -> onDataReceived(char) }
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                Log.d("GATT_WRITE","Writing to characteristic : $status")
            }
        })
    }

    fun calcAltitude(pressure: Float): Float {
        val sea_press = SensorData.seaLevelPressure
        val temp = SensorData.seaLevelTemperature
        return round((((sea_press / pressure).pow(1 / 5.257f) - 1.0f) * (temp + 273.15f)) / 0.0065f)
    }
    fun onDataReceived(char: BluetoothGattCharacteristic) {
        val shortFormat = BluetoothGattCharacteristic.FORMAT_UINT16
        val intFormat = BluetoothGattCharacteristic.FORMAT_UINT32
        when (char.uuid) {
            temp_UUID -> {
                val floatValue = char.getIntValue(shortFormat,0).toFloat()/100
                SensorData.temperature.add(floatValue)
                Log.d("GATT_NOTIFY", "Characteristic temp: $floatValue")
            }
            humidity_UUID -> {
                val value = char.getIntValue(shortFormat,0)
                SensorData.humidity.add(value)
                Log.d("GATT_NOTIFY", "Characteristic humidity: $value ")
            }
            pressure_UUID ->{
                val pressure = char.getIntValue(intFormat,0).toFloat()/100
                SensorData.pressure.add(pressure)
                SensorData.altitude.add(calcAltitude(pressure))

                Log.d("GATT_NOTIFY", "Characteristic pressure: $pressure ")
            }
            IAQ_UUID -> {
                val value = char.getIntValue(shortFormat,0)
                SensorData.iaq.add(value)

                Log.d("GATT_NOTIFY", "Characteristic IAQ: $value")
            }
            bVOC_UUID -> {
                val floatValue = char.getIntValue(shortFormat,0).toFloat()/100
                SensorData.voc.add(floatValue)
                Log.d("GATT_NOTIFY", "Characteristic bVOC: $floatValue")
            }
            CO2_UUID -> {
                val value = char.getIntValue(shortFormat,0)
                SensorData.co2.add(value)
                Log.d("GATT_NOTIFY", "Characteristic CO2: $value ")
            }
            step_UUID -> {
                val value = char.getIntValue(shortFormat,0)
                SensorData.steps.add(value)
                Log.d("GATT_NOTIFY","Characteristic Steps: $value")
            }
            else -> {
                Log.d("GATT_NOTIFY", "Characteristic Unknown")
            }
        }
    }
    fun send(value: Int)
    {
        val service = bluetoothGatt?.getService(otherService_UUID)
        val commandChar = service?.getCharacteristic(messageReceiver_UUID)

        if(commandChar == null) {bluetoothGatt?.printGattTable(); return}

        commandChar?.setValue(value,BluetoothGattCharacteristic.FORMAT_UINT16,0)
        val writeSuccess = bluetoothGatt?.writeCharacteristic(commandChar)
        Log.d("BlE_WRITE","Attempting to write command: $value. Success: $writeSuccess")
    }

}