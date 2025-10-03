package com.example.ble_con.presentation.MainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import com.example.ble_con.presentation.MyHorizontalDivider
import com.example.ble_con.repository.Routes
import com.example.ble_con.repository.ViewModelData

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    vm: ViewModel = viewModel()
) {
    // Box used for whitespace
    Spacer(modifier = Modifier.height(100.dp))

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
        Button(
            onClick = {
                navController.navigate(Routes.SavedRecordingsScreen)
            }
        ) {
            Text(text = "See recorded Hikes", color = Color.White)
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
                navController.navigate(Routes.RecordingScren)
            }
        ) {
            Text(text = "Start", color = Color.White)
        }
        Button(onClick = { vm.disconnect() }) {
            Text(text = "Disconnect", color = Color.White)
        }
        Button(onClick = { vm.scanBLE() }) {
            Text(text = "Scan", color = Color.White)
        }
        val scanStatus = ViewModelData.scanningStatus.observeAsState().value
        Text("Scanning: $scanStatus", Modifier.padding(5.dp))
    }
}
@SuppressLint("MissingPermission")
@Composable
fun GenScanResults(vm: ViewModel = viewModel())
{
    Column(Modifier.padding(5.dp).sizeIn(300.dp,200.dp,300.dp,300.dp).border(2.dp,color = Color.Black,shape = RoundedCornerShape(10.dp))) {
        var first = true
        val scanResults = ViewModelData.scanResultMap.observeAsState().value
        if(scanResults != null) for (result in scanResults.values) {
            if(!first){
                MyHorizontalDivider()
            }
            first = false
            Box(
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

}