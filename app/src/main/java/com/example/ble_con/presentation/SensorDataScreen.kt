package com.example.ble_con.presentation
import android.Manifest
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.common.model.Point
import com.example.ble_con.R
import com.example.ble_con.ViewModel
import com.example.ble_con.dataManager.repo.ConStatus
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.repository.ViewModelData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.selects.select

@Composable
fun SensorDataScreen(
    vm: ViewModel = viewModel()
) {
    // Box used for whitespace
    Spacer(modifier = Modifier.height(100.dp))
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()
        .padding(10.dp)
        .verticalScroll(scrollState)
    ){
        val selectedData = remember { mutableStateOf(ViewModelData.nothing)}
        Column(Modifier.fillMaxWidth()){

            ControlButtons(vm)

            val time = ViewModelData.time.observeAsState(0).value
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = formatTime(time))
            }
        }
        Row(Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)){
            ShowBlock(ViewModelData.humidity,selected = selectedData)
            ShowBlock(ViewModelData.pressure,selected = selectedData)
        }
        Row(Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)){
            ShowBlock(ViewModelData.temperature,selected = selectedData)
            ShowBlock(ViewModelData.steps,selected = selectedData)

        }
        Row(Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)){
            ShowBlock(ViewModelData.airQuality,selected = selectedData)
            ShowBlock(ViewModelData.voc,selected = selectedData)
            ShowBlock(ViewModelData.co2,selected = selectedData)
        }
        ShowBlock(ViewModelData.altitude,selected = selectedData)
        ShowGraph(selectedData.value)
        ShowMap()
    }
}

@Composable
fun ShowMap() {
    val locationList = SensorData.location.liveData.observeAsState().value
    var startLocation = when(locationList!!.isEmpty()) {
        true -> LatLng(46.05,14.50)// Ljubljana
        false -> locationList.first()
    }
    val cameraPositionState = when(locationList!!.isEmpty()) {
        false -> rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(startLocation, 20f)
        }

        true -> CameraPositionState(CameraPosition.fromLatLngZoom(startLocation, 20f))
    }
    GoogleMap(
    modifier = Modifier.height(300.dp).fillMaxWidth(),
        cameraPositionState = cameraPositionState,
        uiSettings = com.google.maps.android.compose.MapUiSettings(
            zoomControlsEnabled = false,  // hide + / - buttons
            mapToolbarEnabled = false     // hide the navigation toolbar
        )
    ) {
        if(locationList.isNotEmpty()){
            startLocation = locationList.first()
            val markerState = rememberMarkerState(null,startLocation)
            Marker(state = markerState)
            Polyline(
                points = locationList.toList(),
                color = Color.Blue, // Blue line
                width = 8f
            )
        }
    }
}
@Composable
fun ShowBlock(data: ViewModelData.DataInfo,selected: MutableState<ViewModelData.DataInfo>) {

    Card(Modifier.padding(10.dp).sizeIn(80.dp,70.dp,200.dp,80.dp).clickable{ selected.value = data} , elevation = CardDefaults.elevatedCardElevation(10.dp), border = BorderStroke(1.dp,color = Color.Black)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(data.icon), "icon", Modifier.size(40.dp).padding(5.dp))
            Text(text = data.name, Modifier.padding(5.dp))
        }
        val value = data.list.observeAsState().value
        var txt = "null"
        if (value?.isNotEmpty() == true) txt = value.last().y.toString() + data.postFix
        Text(text = txt, Modifier.padding(5.dp))
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
    Row(Modifier.fillMaxWidth().wrapContentHeight(Alignment.CenterVertically)){
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

        Button({vm.saveRecording()}) {
            Text(text = "Save", color = Color.White)
        }
    }
}
@Composable
fun ShowGraph(data: ViewModelData.DataInfo)
{
    if(data.name == "null") return
    Column {
        Text(text = data.name,Modifier.padding(10.dp))
        Graph(data.list)
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