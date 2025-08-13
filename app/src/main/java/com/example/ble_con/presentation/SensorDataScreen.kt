package com.example.ble_con.presentation

import android.util.Log
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import com.example.ble_con.presentation.CO2Graph
import com.example.ble_con.presentation.HumidityGraph
import com.example.ble_con.presentation.IAQGraph
import com.example.ble_con.presentation.TempGraph
import com.example.ble_con.presentation.bVOCGraph

@Composable
fun SensorDataScreen(
    vm: ViewModel = viewModel()
) {
    // Box used for whitespace
    Box(modifier = Modifier.fillMaxWidth().height(100.dp))
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()
        .padding(10.dp)
        .verticalScroll(scrollState)
    ){
        ShowData(vm = vm, name = "Temperature")
        ShowData(vm = vm,name = "Humidity")
        ShowData(vm = vm,name = "IAQ")
        ShowData(vm = vm,name = "bVOC")
        ShowData(vm = vm,name = "CO2")
    }

}
@Composable
fun ShowData(vm: ViewModel = viewModel(),name: String = "null")
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
            "Temperature"-> TempGraph(vm)
            "Humidity"-> HumidityGraph(vm)
            "IAQ"-> IAQGraph(vm)
            "bVOC"-> bVOCGraph(vm)
            "CO2"-> CO2Graph(vm)
            else-> Log.e("GRAPH_DATA","Wrong name ")
        }
    }
}
