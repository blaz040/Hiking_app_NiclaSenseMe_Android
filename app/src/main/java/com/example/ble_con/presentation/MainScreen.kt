package com.example.ble_con.Presentation

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ble_con.ble.BLE_manager
import com.example.ble_con.data.SensorData
import dagger.hilt.android.HiltAndroidApp
import java.nio.ByteBuffer
import java.nio.ByteOrder

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    ble_api: BLE_manager
) {
    var selectedDevice = remember { mutableStateOf<ScanResult?>(null) };

    var sData =  remember { mutableStateOf<SensorData>(SensorData()) }
    /*
    var temp_value = remember { mutableStateOf<Float>(0f) }
    var humidity_value = remember { mutableStateOf<Int>(-1) }
    */
    var connectionState = remember { mutableStateOf<String>("Disconnected") }

    Column(Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =   Arrangement.Center

        )
        {
            Text(text = "Selected: ${selectedDevice.value?.device?.name}")
            Text(text = "Connection: ${connectionState.value}")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5F),
            contentAlignment = Alignment.Center
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.Blue, CircleShape)
                        .clickable {
                            ble_api.closeConnection()
                            connectionState.value = "Disconnected"
                            selectedDevice.value = null
                            ble_api.scanLeDevice()
                        },
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = "Start",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                LazyColumn()
                {
                    var ScanResults = ble_api.getbleScanResults()

                    itemsIndexed(
                        items = ScanResults,
                        key = { _, item -> item.device.address }) { index, scanResult ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .clickable {
                                    selectedDevice.value = scanResult

                                    ble_api.connectToDevice(scanResult.device, connectionState,
                                        { char->
                                            when(char.uuid)
                                            {
                                                ble_api.temp_UUID ->{
                                                    val rawData = char.value
                                                    if (rawData != null && rawData.size == 4) {
                                                        val buffer = ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN)
                                                        val floatValue = buffer.float

                                                        sData.value = sData.value.copy(temp_value = floatValue)
                                                        Log.d("GATT_NOTIFY", "Characteristic temp: $floatValue")
                                                    } else {
                                                        Log.e("GATT_NOTIFY", "Received invalid data for temp characteristic.")
                                                    }
                                                    //Log.d("GATT_NOTIFY","Characteristic temp ${ sData.value.temp_value }")
                                                }
                                                ble_api.humidity_UUID->{
                                                    sData.value =sData.value.copy(humidity_value = char.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0))

                                                    Log.d("GATT_NOTIFY","Characteristic humidity ${sData.value.humidity_value}")
                                                }
                                                ble_api.IAQ_UUID->{
                                                    Log.d("GATT_NOTIFY","Characteristic IAQ")
                                                }
                                                ble_api.bVOC_UUID->{
                                                    Log.d("GATT_NOTIFY","Characteristic bVOC")
                                                }
                                                ble_api.CO2_UUID->{
                                                    Log.d("GATT_NOTIFY","Characteristic CO2")
                                                }
                                                else->{
                                                    Log.d("GATT_NOTIFY","Characteristic Unknown")
                                                }
                                            }

                                        }
                                    )

                                }
                        )
                        {
                            Text(
                                text = "Device Name: ${scanResult.device.name ?: "Unknown"} - " +
                                        "Address: ${scanResult.device.address}",
                                modifier = Modifier.padding(16.dp)

                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =   Arrangement.Center
        ) {
            if(sData.value.temp_value != 0f)
            {
                Text(text = "Temp: ${sData.value.temp_value} C")
            }
            if(sData.value.humidity_value != -1)
                Text(text = "Humidity ${sData.value.humidity_value} %")
        }
    }

}