package com.example.ble_con.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
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
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BLE_manager(
    private val bluetoothAdapter: BluetoothAdapter,
    val context: Context
){
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val envService_UUID = UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB")
    val temp_UUID = UUID.fromString("00002A6E-0000-1000-8000-00805F9B34FB")
    val humidity_UUID = UUID.fromString("00002A6F-0000-1000-8000-00805F9B34FB")

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
     fun connectToDevice(device: BluetoothDevice, connectionState: MutableState<String>, onDataReceived: (BluetoothGattCharacteristic,Int) -> Unit ) {
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
                    connectionState.value = "Connected"
                    // Discover services after successful connection
                    gatt.discoverServices()
                    Log.d("GATT_CONN", "Discovering services...")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Device disconnected
                    Log.d("GATT_CONN", "Disconnected from GATT server.")
                    connectionState.value = "Disconnected"
                    gatt.close() // Close GATT client
                    bluetoothGatt = null
                }
            }

            // Callback for services discovered
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("GATT_CONN", "Services discovered successfully.")

                    var tempChar = gatt.getService(envService_UUID)?.getCharacteristic(temp_UUID)
                    var humChar = gatt.getService(envService_UUID)?.getCharacteristic(humidity_UUID)

                    if(tempChar != null)
                    {
                        gattQueue.add { enableCharacteristicNotification(gatt,tempChar) }
                    }
                    if(humChar != null)
                    {
                        gattQueue.add { enableCharacteristicNotification(gatt,humChar) }
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

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                characteristic?.let { char ->
                    if (char.uuid == temp_UUID || char.uuid == humidity_UUID) {
                        // Assuming Nicla sends temperature as a 16-bit unsigned integer
                        val value = char.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)
                        Log.d("GATT_NOTIFY", "Characteristic ${char.uuid} changed (notified): ${value}")
                        onDataReceived(char,value)

                    } else {
                        val value = char.value // For other characteristics, get raw bytes
                        Log.d("GATT_NOTIFY", "Characteristic ${char.uuid} changed (notified): ${value?.toHexString()}")
                    }
                }
            }

            // You can add more overrides for onCharacteristicWrite, onCharacteristicChanged, etc.
        })
    }

    // Helper function to convert byte array to hex string for logging
     fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }



    fun getbleScanResults(): SnapshotStateList<ScanResult>
    {
        return ble_scanResults
    }

}