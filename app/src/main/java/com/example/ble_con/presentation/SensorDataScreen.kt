package com.example.ble_con.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import com.example.ble_con.repository.SendCommand

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
        Row(){
            Button({ vm.send(SendCommand.STOP) })
            {
                Text(text = "Stop",color = Color.White)
            }
            Button({ vm.clearData();vm.send(SendCommand.RESTART) })
            {
                Text(text = "Restart",color = Color.White)
            }
        }
        ShowData(vm = vm,name = "Temperature")
        ShowData(vm = vm,name = "Humidity")
        ShowData(vm = vm,name = "IAQ")
        ShowData(vm = vm,name = "bVOC")
        ShowData(vm = vm,name = "CO2")
        ShowData(vm = vm,name = "Pressure")
        ShowData(vm = vm,name = "Steps")
    }

}
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
            "Temperature"-> TempGraph(vm)
            "Humidity"-> HumidityGraph(vm)
            "IAQ"-> IAQGraph(vm)
            "bVOC"-> bVOCGraph(vm)
            "CO2"-> CO2Graph(vm)
            "Pressure" -> PressureGraph(vm)
            "Steps" -> StepsGraph(vm)
            else-> Log.e("GRAPH_DATA","Wrong name ")
        }
    }
}
