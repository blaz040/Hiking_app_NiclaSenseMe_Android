package com.example.ble_con.presentation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import com.example.ble_con.repository.SensorData

@Composable
fun <T:Number>Graph(list: MutableList<T>, maxValue: Int, minValue: Int, strokeColor: Color, strokeWidth: Float = 1f, lineSpace: Number = 10f)
{
    val graphColor = Color.Black
    val lineSpace_F = lineSpace.toFloat()
    val textMeasurer = rememberTextMeasurer()

    Card(Modifier.padding(15.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp)
                .drawWithCache {
                    val path = generatePath(list,size,maxValue)
                    onDrawBehind {
                        drawPath(path, strokeColor, style = Stroke(width = 4f))
                    }
                }
        )
        {
            val horizontalLines = (maxValue / lineSpace_F).toInt()
            val horiSize = size.height / horizontalLines
            repeat(horizontalLines+1){ i->
                val Y = horiSize * i
                val num = minValue + (maxValue/horizontalLines.toFloat()) * (horizontalLines-i)

                drawText(textMeasurer,text = "$num", topLeft = Offset(0f,Y))
                drawLine(graphColor,start = Offset(30f,Y),end = Offset(size.width-10,Y), strokeWidth = strokeWidth)
            }
        }
    }
}
fun <T: Number>generatePath(list: MutableList<T>, size: androidx.compose.ui.geometry.Size, maxValue :Int) : Path
{
    val multiplier = size.height/maxValue
    val path = Path()

    if(list.isEmpty()) return path

    path.moveTo(0f,size.height - list.first().toFloat() * multiplier)
    //list.removeAt(0)
    list.forEachIndexed { index, value ->
        path.lineTo(index.toFloat()*10,size.height - value.toFloat() * multiplier)
    }
    return path
}
@Composable
fun TempGraph(vm: ViewModel = viewModel())
{
    val tempList by SensorData.tempList.observeAsState(mutableListOf(0))

    Graph(tempList,40,0, Color.Blue)
}
@Composable
fun HumidityGraph(vm: ViewModel = viewModel())
{
    val humList by SensorData.humidityList.observeAsState(mutableListOf(0))

    Graph(humList,100,0, Color.Blue, lineSpace = 20)
}
@Composable
fun IAQGraph(vm: ViewModel = viewModel())
{
    val iaq by SensorData.IAQList.observeAsState(mutableListOf(0))

    Graph(iaq,500,0, Color.Red, lineSpace = 100)
}
@Composable
fun bVOCGraph(vm: ViewModel = viewModel())
{
    val bVOC by SensorData.bVOCList.observeAsState(mutableListOf(0))

    Graph(bVOC,1,0, Color.Red, lineSpace = 0.2)
}
@Composable
fun CO2Graph(vm: ViewModel = viewModel())
{
    val CO2 by SensorData.CO2List.observeAsState(mutableListOf(0))

    Graph(CO2,1000,0, Color.Red, lineSpace = 100)
}
@Composable
fun PressureGraph(vm: ViewModel = viewModel())
{
    val pressure by SensorData.pressureList.observeAsState(mutableListOf(0))

    Graph(pressure,1000,0, Color.Red, lineSpace = 100)
}
@Composable
fun StepsGraph(vm: ViewModel = viewModel())
{
    val Steps by SensorData.stepsList.observeAsState(mutableListOf(0))

    Graph(Steps,1000,0, Color.Red, lineSpace = 100)
}