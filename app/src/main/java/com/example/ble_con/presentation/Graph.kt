package com.example.ble_con.presentation

import android.util.Log
import androidx.annotation.Nullable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.traceEventStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.round

fun Number.length() =  when(this) {
    0 -> 1
    else -> log10(abs(this.toFloat())).toInt() + 1
}
fun to_int_or_float(num: Float): Number
{
    if(num.toInt()-num == 0f)
        return num.toInt()
    else return num
}
@Composable
fun <T:Number>Graph(list: MutableList<T>, maxValue: Int, minValue: Int, strokeColor: Color, strokeWidth: Float = 1f, lineSpace: Number = 10f)
{
    val graphColor = Color.Black
    val lineSpace_F = lineSpace.toFloat()
    val textMeasurer = rememberTextMeasurer()
    var Graph_X_start_Offset = (maxValue.length()*20).toFloat()
    val Graph_Y_end_Offset = 30
    Card(Modifier.padding(15.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp + Graph_Y_end_Offset.dp)
                .padding(20.dp)
                .drawWithCache {
                    val path = generatePath(list,size,maxValue)
                    onDrawBehind {
                        drawPath(path, strokeColor, style = Stroke(width = 4f))
                    }
                }
        )
        {
            val graphSize = Size(size.width,size.height-Graph_Y_end_Offset)
            val horizontalLines = (maxValue / lineSpace_F).toInt()
            val line_spacing = graphSize.height / horizontalLines
            val numberSpacing = maxValue/horizontalLines.toFloat()

            // adds more line Offset in X for a text place if the line spacing has any decimals
            if(numberSpacing < 1) {
                val arg:Float = numberSpacing
                var temp = 0
                // +10 because of the dot
                if(arg != 0f) temp = arg.length()*20 + 10
                Graph_X_start_Offset += temp.toFloat()
            }
            repeat(horizontalLines+1){ i->
                val Y = line_spacing * i
                val num = to_int_or_float(minValue + numberSpacing * (horizontalLines-i))
                drawText(textMeasurer,text = "$num", topLeft = Offset(0f,Y-20f))
                drawLine(graphColor,start = Offset(Graph_X_start_Offset,Y),end = Offset(graphSize.width-10,Y), strokeWidth = strokeWidth)
            }
        }
    }
}
@Composable
fun <T:Number>Flexible_Graph(list: MutableList<T>,bonus_path_offset:Float = 0.1f, strokeColor: Color, strokeWidth: Float = 1f, lineSpace: Number = 10f)
{
    Log.d("GRAPH_FLX","->[${list.minByOrNull { it.toFloat() as Float }},${list.maxByOrNull { it.toFloat() as Float }}]")

    var maxValue:Float = when(val arg:Float? = list.maxByOrNull { it.toFloat() }?.toFloat())
    {
        null->100f
        else->arg
    }
    var minValue:Float = when(val arg:Float?= list.minByOrNull { it.toFloat() }?.toFloat())
    {
        null->100f
        else->arg
    }
    Log.d("GRAPH_FLX","->[${minValue},${maxValue}]")

    val graphColor = Color.Black
    val lineSpace_F = lineSpace.toFloat()
    val textMeasurer = rememberTextMeasurer()
    var Graph_X_start_Offset = (maxValue.length()*20).toFloat()
    val Graph_Y_end_Offset = 30

    Card(Modifier.padding(15.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp + Graph_Y_end_Offset.dp)
                .padding(20.dp)
                .drawWithCache {
                    val path = generatePath(list,size,maxValue.toInt())
                    onDrawBehind {
                        drawPath(path, strokeColor, style = Stroke(width = 4f))
                    }
                }
        )
        {
            val graphSize = Size(size.width,size.height-Graph_Y_end_Offset)
            val horizontalLines = (maxValue / lineSpace_F).toInt()
            val line_spacing = graphSize.height / horizontalLines
            val numberSpacing = maxValue/horizontalLines.toFloat()

            // adds more line Offset in X for a text place if the line spacing has any decimals
            if(numberSpacing < 1) {
                val arg:Float = numberSpacing
                var temp = 0
                // +10 because of the dot
                if(arg != 0f) temp = arg.length()*20 + 10
                Graph_X_start_Offset += temp.toFloat()
            }
            repeat(horizontalLines+1){ i->
                val Y = line_spacing * i
                val num = to_int_or_float(minValue + numberSpacing * (horizontalLines-i))
                drawText(textMeasurer,text = "$num", topLeft = Offset(0f,Y-20f))
                drawLine(graphColor,start = Offset(Graph_X_start_Offset,Y),end = Offset(graphSize.width-10,Y), strokeWidth = strokeWidth)
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
    Flexible_Graph(humList, strokeColor = Color.Blue, lineSpace = 20)

    //Graph(humList,100,0, Color.Blue, lineSpace = 20)
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