package com.example.ble_con.presentation.SensorDataScreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import com.example.ble_con.dataManager.repo.ConStatus
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.presentation.Graph
import com.example.ble_con.presentation.ShowDataBlock
import com.example.ble_con.presentation.ShowGraph
import com.example.ble_con.presentation.ShowMap
import com.example.ble_con.repository.ViewModelData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun SensorDataScreen(
    vm: ViewModel = viewModel()
) {
    // whitespace
    Spacer(modifier = Modifier.height(100.dp))
    val scrollState = rememberScrollState()

    Column(Modifier
        .fillMaxSize()
        .padding(10.dp)
        .verticalScroll(scrollState)
    ){
        val selectedData = remember { mutableStateOf(ViewModelData.nothing)}
        Column(Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)){

            ControlButtons(vm)

            val time = ViewModelData.time.observeAsState(0).value
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = formatTime(time))
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ShowDataBlock(ViewModelData.altitude, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.altitude
            })
        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(ViewModelData.humidity, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.humidity
            })
            ShowDataBlock(ViewModelData.pressure, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.pressure
            })
        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(ViewModelData.temperature, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.temperature
            })
            ShowDataBlock(ViewModelData.steps, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.steps
            })

        }
        Row(Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)){
            ShowDataBlock(ViewModelData.airQuality, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.steps
            })
            ShowDataBlock(ViewModelData.voc, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.voc
            })
            ShowDataBlock(ViewModelData.co2, showDataValue = true, modifier = Modifier.clickable {
                selectedData?.value = ViewModelData.co2
            })
        }
        ShowDataBlock(selectedData.value){
            ShowGraph(selectedData.value)
        }
        ShowMap()
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
    var showDialog = remember { mutableStateOf(false) }

    Row(Modifier
        .fillMaxWidth()
        .wrapContentHeight(Alignment.CenterVertically)){
        val recordingStatus = ViewModelData.recordingStatus.observeAsState(0).value
        val conStatus = ViewModelData.conStatus.observeAsState(ConStatus.DISCONNECTED).value

        val btn1_enabled = when(conStatus){
            ConStatus.CONNECTED -> true
            else -> false
        }
        var text_start_stop = "Start"
        var fun1 = {vm.startRecording()}

        var btn2_enabled = false
        var resume_pause = "Pause"

        when(recordingStatus){
            RecordingStatus.RUNNING -> {
                text_start_stop = "Stop"
                fun1 = {vm.stopRecording()}

                btn2_enabled = true
                resume_pause = "Pause"
            }
            RecordingStatus.PAUSED ->{
                text_start_stop = "Stop"
                fun1 = {vm.stopRecording()}

                btn2_enabled = true
                resume_pause = "Resume"
            }
            RecordingStatus.STOPPED->{
                text_start_stop = "Start"
                fun1 = {vm.startRecording()}

                btn2_enabled = false
                resume_pause = "Resume"
            }
        }
        Button(fun1,enabled = btn1_enabled) {
            Text(text = text_start_stop, color = Color.White)
        }
        Button({vm.toggleRecording()},enabled = btn2_enabled) {
            Text(text = resume_pause, color = Color.White)
        }
        Text(text = "Recording : ${recordingStatus}",color = Color.Black)

        Button({showDialog.value = true}) {
            Text(text = "Save", color = Color.White)
        }
    }
    if(showDialog.value == true){
        Log.d("dialog","showed dialog")

        vm.pauseRecording()
        InputDialog(vm,{
            Log.d("dialog","called dismiss")
            showDialog.value = false
       })
    }
}
/*
@Composable
fun ShowData(vm: ViewModel = viewModel(),name: String = "null",graph: Boolean = true)
{
    val visible = remember {mutableStateOf<Boolean>(false)}

    Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically){
        Text(text = name, Modifier.widthIn(20.dp,300.dp),fontSize = 20.sp)
        Box(Modifier.fillMaxWidth()) {
            Button(
                onClick = { visible.value = !visible.value },
                Modifier.size(50.dp)
                    .align(Alignment.CenterEnd,
                    )
            ){

            }
        }
    }
    if(visible.value) {
        when(name)
        {
            "Temperature"-> TempGraph()
            "Humidity"-> HumidityGraph()
            "IAQ"-> IAQGraph()
            "bVOC"-> bVOCGraph()
            "CO2"-> CO2Graph()
            "Pressure" -> PressureGraph()
            "Steps" -> StepsGraph()
            else-> Log.e("GRAPH_DATA","Wrong name ")
        }
    }
}
*/
@Composable
fun InputDialog(vm: ViewModel,onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
       Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
       ) {
           Column(
               modifier = Modifier
                   .fillMaxSize(),
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally,
           ) {
                val error = remember { mutableStateOf(false) }
                Text(
                    text = "Add a name for this recording",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                val fileName = remember { mutableStateOf("") }
                TextField(
                    fileName.value,
                    { fileName.value = it },
                    Modifier.fillMaxWidth().padding(20.dp, 0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                )
                if (error.value) {
                    Text("Can't Create ${fileName.value}, try other name", color = Color.Red)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    TextButton({
                        vm.resumeRecording(); onDismissRequest()
                    }) { Text("Cancel") }

                    TextButton({
                        vm.saveRecording("", { success->
                            check(success, error, onDismissRequest)
                        })
                    }) { Text("No save it by date") }

                    TextButton({
                        vm.saveRecording(fileName.value, { success->
                            check(success, error, onDismissRequest)
                        })
                    }) { Text("Confirm") }
                }
           }
       }
    }
}
fun check(success: Boolean,error: MutableState<Boolean>, callback: ()->Unit){
    if(success){
        callback()
        error.value = false
    }
    else{
        error.value = true
    }
}