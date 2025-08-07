package com.example.ble_con.Presentation

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
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
import dagger.hilt.android.HiltAndroidApp

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    ble_api: BLE_manager
) {
    var selectedIndex = remember { mutableStateOf<ScanResult?>(null) };
    var temp_value = remember { mutableStateOf<Float>(0f) }
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
            Text(text = "Selected: ${selectedIndex.value?.device?.name}")
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
                                    selectedIndex.value = scanResult

                                    ble_api.connectToDevice(scanResult.device, connectionState,
                                        { temp->
                                           temp_value.value = temp.toFloat()
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
            if(temp_value.value != 0f)
            {
                Text(text = "Temp: ${temp_value.value/100} C")
            }
        }
    }

}