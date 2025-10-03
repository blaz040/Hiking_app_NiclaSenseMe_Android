package com.example.ble_con.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.presentation.Screens.ControlButtons
import com.example.ble_con.presentation.Screens.GenScanResults
import com.example.ble_con.presentation.Screens.StatusInfo
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
import kotlin.collections.first
import kotlin.collections.isNotEmpty
import kotlin.collections.last
import kotlin.collections.toList


@Composable
fun ShowMap(modifier: Modifier = Modifier) {
    val locationList = SensorData.location.liveData.observeAsState().value
    var startLocation = when(locationList!!.isEmpty()) {
        true -> LatLng(46.05,14.50)// Ljubljana
        false -> locationList.first()
    }
    val cameraPositionState = when(locationList.isEmpty()) {
        false -> rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(startLocation, 20f)
        }

        true -> CameraPositionState(CameraPosition.fromLatLngZoom(startLocation, 20f))
    }
    GoogleMap(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
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
fun ShowDataBlock(data: ViewModelData.DataInfo, modifier: Modifier = Modifier, showDataValue: Boolean = false, content:@Composable (()->Unit) = {} ) {
    if(data == ViewModelData.nothing) return

    Card(modifier = modifier
        .padding(10.dp)
        .sizeIn(80.dp, 70.dp, 700.dp, 700.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        border = BorderStroke(1.dp,color = Color.Black)
    ){
        Column(Modifier.padding(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(data.icon), "icon", Modifier
                        .size(40.dp)
                        .padding(5.dp)
                )
                Text(text = data.name, Modifier.padding(5.dp))
            }
            val value = data.list.observeAsState().value
            var txt = "null"
            if (value?.isNotEmpty() == true) txt = value.last().y.toString() + data.postFix
            if (showDataValue) Text(text = txt, Modifier.padding(5.dp))
            content()
        }
    }
}
@Composable
fun ShowGraph(data: ViewModelData.DataInfo) {
    if(data.name == ViewModelData.nothing.name) return
    Graph(data.list)
}
@Composable
fun MyHorizontalDivider(){
    HorizontalDivider(thickness = 5.dp,color = MaterialTheme.colorScheme.inversePrimary)
}