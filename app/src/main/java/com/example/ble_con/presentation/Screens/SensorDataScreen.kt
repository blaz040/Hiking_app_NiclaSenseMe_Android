package com.example.ble_con.presentation.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.R
import com.example.ble_con.ViewModel
import com.example.ble_con.dataManager.repo.ConnectionStatus
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.presentation.MyHorizontalDivider
import com.example.ble_con.presentation.SaveRecordingDialog
import com.example.ble_con.presentation.ShowDataBlock
import com.example.ble_con.presentation.ShowGraph
import com.example.ble_con.presentation.ShowMap
import com.example.ble_con.repository.ViewModelData

@Composable
fun SensorDataScreen(
    vm: ViewModel = viewModel()
) {
    // whitespace
    Spacer(modifier = Modifier.height(100.dp))
    val scrollState = rememberScrollState()

    Column(Modifier
        .fillMaxSize()
        //.padding(10.dp)
        .verticalScroll(scrollState)
    ){
        val selectedData = remember { mutableStateOf(ViewModelData.nothing)}
        Column(Modifier.fillMaxWidth().padding(15.dp).wrapContentWidth(Alignment.CenterHorizontally)){

            ControlButtons(vm)

            Column(Modifier.fillMaxWidth(), horizontalAlignment =Alignment.CenterHorizontally) {
                val time = ViewModelData.time.observeAsState(0).value
                val recordingStatus = ViewModelData.recordingStatus.observeAsState().value

                data class pair(val txt:String, val color:Color)
                val tmpPair = when(recordingStatus){
                    RecordingStatus.PAUSED -> pair("Paused", MaterialTheme.colorScheme.inversePrimary)
                    RecordingStatus.STOPPED -> pair("Stopped", MaterialTheme.colorScheme.error)
                    RecordingStatus.RECORDING -> pair("Recording", MaterialTheme.colorScheme.primary)
                    else -> pair("Unknown",MaterialTheme.colorScheme.error)
                }
                Text(text = tmpPair.txt, color = tmpPair.color)
                Text(text = formatTime(time), fontSize = 30.sp)
            }
        }
        MyHorizontalDivider()
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ShowDataBlock(
                ViewModelData.altitude,
                showDataValue = true,
                modifier = Modifier.clickable {
                    selectedData?.value = ViewModelData.altitude
                })
        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(
                ViewModelData.humidity,
                showDataValue = true,
                modifier = Modifier.clickable {
                    selectedData?.value = ViewModelData.humidity
                })
            ShowDataBlock(
                ViewModelData.pressure,
                showDataValue = true,
                modifier = Modifier.clickable {
                    selectedData?.value = ViewModelData.pressure
                })
        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(
                ViewModelData.temperature,
                showDataValue = true,
                modifier = Modifier.clickable {
                    selectedData?.value = ViewModelData.temperature
                })
            ShowDataBlock(ViewModelData.steps, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.steps
            })

        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(
                ViewModelData.airQuality,
                showDataValue = true,
                modifier = Modifier.clickable {
                    selectedData?.value = ViewModelData.airQuality
                })
            ShowDataBlock(ViewModelData.voc, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.voc
            })
            ShowDataBlock(ViewModelData.co2, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.co2
            })
        }
        ShowDataBlock(selectedData.value) {
            ShowGraph(selectedData.value)
        }
        val locationEnabled = ViewModelData.locationEnabled.observeAsState(false).value
        val color = when(locationEnabled){
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.error
        }
        ShowMap(Modifier.border(2.dp, color))
    }
}
fun translate(value: Int): String {
    var str = value.toString()
    if(value <10) str ="0${value}"
    return str
}
fun formatTime(time: Int):String {
    //format 00:00:00
    val seconds = translate(time%60)
    val minutes = translate((time/60)%60)
    val hours   = translate((time/3600))
    return "$hours:$minutes:$seconds"
}
@Composable
fun ControlButtons(vm:ViewModel = viewModel()) {
    Row(Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally))
    {
        val recordingStatus = ViewModelData.recordingStatus.observeAsState().value
        val conStatus = ViewModelData.conStatus.observeAsState().value

        val btn1_enabled = when(conStatus){
            ConnectionStatus.CONNECTED -> true
            else -> false
        }
        var resume_pause = R.drawable.resume_icon

        var fun1 = {vm.toggleRecording()}

        var btn2_3_enabled = true

        when(recordingStatus){
            RecordingStatus.RECORDING -> {
                resume_pause = R.drawable.pause_icon
            }
            RecordingStatus.PAUSED ->{
                resume_pause = R.drawable.resume_icon
            }
            RecordingStatus.STOPPED->{
                btn2_3_enabled = false
                resume_pause = R.drawable.resume_icon
                fun1 = {vm.startRecording()}
            }
            else ->{}
        }
        TextButton(fun1,enabled = btn1_enabled) {
            Image(
                painterResource(resume_pause),
                "Start/Resume/Pause",
                Modifier
                    .size(20.dp)
            )
        }
        TextButton({vm.stopRecording()},enabled = btn2_3_enabled) {
            Text(text = "Stop",color = MaterialTheme.colorScheme.primary,fontSize = 20.sp)
        }

        TextButton({vm.saveRecording()},enabled = btn2_3_enabled) {
            Text(text = "Save", color = MaterialTheme.colorScheme.primary,fontSize = 20.sp)
        }
    }
}