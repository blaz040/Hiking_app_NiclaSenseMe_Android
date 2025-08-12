package com.example.ble_con.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BLE_manager(
    private val bluetoothAdapter: BluetoothAdapter,
    val context: Context
) {
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val envService_UUID = UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB")

    val temp_UUID = UUID.fromString("00002A6E-0000-1000-8000-00805F9B34FB")
    val humidity_UUID = UUID.fromString("00002A6F-0000-1000-8000-00805F9B34FB")
    val IAQ_UUID = UUID.fromString("00002AF2-0000-1000-8000-00805F9B34FB")
    val bVOC_UUID = UUID.fromString("00002BE7-0000-1000-8000-00805F9B34FB")
    val CO2_UUID = UUID.fromString("00002B8C-0000-1000-8000-00805F9B34FB")

    val send_UUID  = UUID.fromString("00000001-0000-1000-8000-00805F9B34FB")

    val characteristics_list = listOf(temp_UUID,humidity_UUID,IAQ_UUID,bVOC_UUID,CO2_UUID)

    var bluetoothGatt: BluetoothGatt? = null

    var ble_scanResults = mutableStateListOf<ScanResult>()

     val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
     var scanning = false
     val handler = android.os.Handler()

    // Stops scanning after 10 seconds.
     val SCAN_PERIOD: Long = 10000


    fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            ble_scanResults.clear()
            scanning = true
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    // Device scan callback.
     val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val index = ble_scanResults.indexOfFirst { it.device.address == result.device.address}
            if(index != -1)
            {
                ble_scanResults[index] = result
            }
            else{
                if(result.device.name != null)
                    ble_scanResults.add(result)
            }
        }
    }

    fun closeConnection()
    {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission") // BLUETOOTH_CONNECT permission needed here
     fun connectToDevice(device: BluetoothDevice, setConnectionStatus: (String) -> Unit, onDataReceived: (BluetoothGattCharacteristic) -> Unit ) {
        // Disconnect from any previously connected device
       closeConnection()

        var gattQueue = mutableListOf<() ->Unit>()
        var gattQueueBusy = false

        fun nextGattOperation()
        {
            if(!gattQueue.isEmpty() && !gattQueueBusy)
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
                    setConnectionStatus("Connected")

                    // Discover services after successful connection
                    gatt.discoverServices()
                    Log.d("GATT_CONN", "Discovering services...")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Device disconnected
                    Log.d("GATT_CONN", "Disconnected from GATT server.")
                    setConnectionStatus("Disconnected")
                    gatt.close() // Close GATT client
                    bluetoothGatt = null
                }
            }

            // Callback for services discovered
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("GATT_CONN", "Services discovered successfully.")

                    characteristics_list.forEach {
                        var tmpChar = gatt.getService(envService_UUID)?.getCharacteristic(it)
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
                    // The onDescriptorWrite callback will handle the next operation
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
                Log.d("GATT_WRITE","Writting to characteristic : $status")
            }
            // You can add more overrides for onCharacteristicWrite, onCharacteristicChanged, etc.
        })
    }

    // Helper function to convert byte array to hex string for logging
    fun getbleScanResults(): SnapshotStateList<ScanResult>
    {
        return ble_scanResults
    }

    fun send(value: Int)
    {
        val service = bluetoothGatt?.getService(envService_UUID)
        val commandChar = service?.getCharacteristic(send_UUID)

        commandChar?.setValue(value,BluetoothGattCharacteristic.FORMAT_UINT16,0)
        val writeSuccess = bluetoothGatt?.writeCharacteristic(commandChar)
        Log.d("BlE_WRITE","Attempting to write command: $value. Success: $writeSuccess")
    }

}