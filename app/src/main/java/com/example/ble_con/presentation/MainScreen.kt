package com.example.ble_con.presentation

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ble_con.ViewModel
import com.example.ble_con.repository.Routes
import com.example.ble_con.repository.SensorData

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    vm: ViewModel = viewModel()
) {
    Column(Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        )
        {
            val connectionStatus by vm.conStatus.observeAsState()
            val selectedDevice  by vm.selectedDevice.observeAsState()
            selectedDevice?.let {
                Text(text = "Selected: ${it.device.name}")
            }
                Text(text = "Connection: ${connectionStatus}")

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5F),
            contentAlignment = Alignment.Center
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Box(
                        modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.Blue, CircleShape)
                        .clickable {
                            //ble_api.send(1)
                            navController.navigate(Routes.screenB)
                        },
                        contentAlignment = Alignment.Center)
                    {
                        Text(text = "Send",color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color.Blue, CircleShape)
                            .clickable {
                                vm.scanBLE()
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
                }
                LazyColumn()
                {
                    val ScanResults = vm.getScanResults()

                    itemsIndexed(
                        items = ScanResults,
                        key = { _, item -> item.device.address }) { index, scanResult ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                                .clickable {
                                    vm.connect(scanResult)
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
            val number = SensorData.incNumber.observeAsState(0)
            Text(text = "${number.value}")
            /*
            val tempValue by vm.tempValue.observeAsState(mutableListOf(0f))

            Log.d("LIVE_DATA","updated temp list")
            if(tempValue.last() != 0f)
            {
                Text(text = "Temp: ${tempValue.last()} C")
            }
            */

            /*
            if(sData.value.humidity_value != -1)
                Text(text = "Humidity ${sData.value.humidity_value} %")
            if(sData.value.IAQ_value != -1)
                Text(text = "IAQ ${sData.value.IAQ_value}")
            if(sData.value.bVOC_value != 0f)
                Text(text = "bVOC ${sData.value.bVOC_value}")
            if(sData.value.CO2_value != -1)
                Text(text = "CO2 ${sData.value.CO2_value}")
        */
        }

    }
}