package com.example.ble_con.presentation

import android.annotation.SuppressLint
import android.net.http.UrlRequest.Status
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ble_con.ViewModel
import com.example.ble_con.repository.Routes
import com.example.ble_con.repository.SendCommand
import com.example.ble_con.repository.ViewModelData

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    vm: ViewModel = viewModel()
) {
    Column {
        // Box used for whitespace
        Box(modifier = Modifier.fillMaxWidth().height(100.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButtons(navController,vm)
                GenScanResults()
            }
            StatusInfo()
        }
    }
}
@Composable
fun ControlButtons(
    navController: NavController,
   vm:ViewModel = viewModel()
) {
    Column {
        Button(
            onClick = {
                navController.navigate(Routes.DataScren)
                vm.send(SendCommand.START)
            }
        ) {
            Text(text = "Start", color = Color.White)
        }
        Button(onClick = { vm.disconnect() })
        {
            Text(text = "Disconnect", color = Color.White)
        }
        Button(onClick = { vm.scanBLE() })
        {
            Text(text = "Scan", color = Color.White)
        }
    }
}
@SuppressLint("MissingPermission")
@Composable
fun GenScanResults(vm: ViewModel = viewModel())
{
    Column {
        val scanResults = ViewModelData.scanResultMap.observeAsState().value
        if(scanResults != null) for (result in scanResults.values) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { vm.connect(result) }
            )
            {
                val txt = "Device Name: ${result.device.name ?: "Unknown"} - Address: ${result.device.address}"
                Text( txt, modifier = Modifier.padding(16.dp) )
            }
        }
    }
}
@SuppressLint("MissingPermission")
@Composable
fun StatusInfo()
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        val connectionStatus by ViewModelData.conStatus.observeAsState()
        val selectedDevice by ViewModelData.selectedDevice.observeAsState()
        selectedDevice?.let {
            Text(text = "Selected: ${it.device.name}")
        }
        Text(text = "Connection: ${connectionStatus}")

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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