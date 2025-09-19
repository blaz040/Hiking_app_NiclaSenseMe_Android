package com.example.ble_con.presentation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.ble_con.dataManager.repo.SensorData
import kotlin.math.abs
import kotlin.math.log10

fun Number.length() =  when(this) {
    0 -> 1
    else -> log10(abs(this.toFloat())).toInt() + 1
}
fun to_int_or_float(num: Float): Number {
    if(num.toInt()-num == 0f)
        return num.toInt()
    else return num
}
fun round(value:Number, dec:Int): Float {
    var offset:Int = 1
    for (i in 1..dec) {
        offset *= 10
    }
    return ((value.toFloat()*offset).toInt()).toFloat()/offset
}
fun<T: Number> List<T>.getMin(): Float {
    val list = this.toList<T>()
    if(list.isEmpty()) return -100f
    var min:T? = null
    for(x in list)
    {
        if(min == null || x.toFloat() < min.toFloat())
        {
            min = x
        }
    }
    return min!!.toFloat();
}
fun<T: Number> List<T>.getMax(): Float {
    val list = this.toList<T>()
    if(list.isEmpty()) return -100f
    var max:T? = null
    for(x in list)
    {
        if(max == null || x.toFloat() > max.toFloat())
        {
            max = x
        }
    }
    return max!!.toFloat();
}
fun List<Point>.max(): Float{
    val list = this.toList()
    var max:Point? = null
    for(x in list) {
        if(max == null || x.y > max.y){
            max = x
        }
    }
    return max!!.y
}
fun List<Point>.min(): Float{
    val list = this.toList()
    var min:Point? = null
    for(x in list) {
        if(min == null || x.y <min.y){
            min = x
        }
    }
    return min!!.y
}

fun formatPopUp(x:Float, y:Float): String {
    var str:String = "time: ${formatTime(x.toInt())}, y: $y"
    return str
}
@Composable
fun <T:Number>Graph_old(list: List<T>, maxValue: Float, minValue: Float, strokeColor: Color, strokeWidth: Float = 1f, lineSpace: Float = 10f)
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
                    val path = generatePath(list,size,maxValue,minValue)
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
fun <T:Number>Flexible_Graph(list: List<T>,bonus_path_offset:Float = 0.1f, strokeColor: Color, strokeWidth: Float = 1f, lineSpace: Float = 10f)
{
    val maxValue:Float = list.getMax()
    val minValue:Float = list.getMin()

    val max_min_diff = abs(maxValue - minValue)

    val dp_graph_y_offset = 0f-minValue // graph offset to above the x-os
    val top_down_padding = 20f

    Log.d("GRAPH_FLX","->[${minValue},${maxValue}]")

    val graphColor = Color.Black

    val textMeasurer = rememberTextMeasurer()

    var Graph_X_start_Offset = (maxValue.length()*20).toFloat()
    val Graph_Y_end_Offset = 30f

    val numberOfHorizontalLines = 5

    Card(Modifier.padding(15.dp).sizeIn(300.dp,200.dp,500.dp,350.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp + Graph_Y_end_Offset.dp)
                .padding(20.dp)
                .drawWithCache {
                    val path_size = Size(size.width,size.height - top_down_padding)
                    val path = generatePath(list,path_size,maxValue,minValue)
                    onDrawBehind {
                        drawPath(path, strokeColor, style = Stroke(width = 4f))
                    }
                }
        )
        {
            val graphSize = Size(size.width,size.height-Graph_Y_end_Offset)
            //val numberOfHorizontalLines = (max_min_diff / lineSpace).toInt()
            val line_spacing_dp = graphSize.height / numberOfHorizontalLines
            val number_spacing = (max_min_diff)/numberOfHorizontalLines.toFloat()

            // adds more line Offset in X for a text place if the line spacing has any decimals

            if(number_spacing - number_spacing.toInt() > 0) {
                val arg:Float = number_spacing
                var temp = 0
                // +10 because of the dot

                //if(arg != 0f) temp = arg.length()*20 + 10
                //Graph_X_start_Offset += temp.toFloat()
                Graph_X_start_Offset += 40 + 10

            }

            repeat(numberOfHorizontalLines+1){ i->
                val line_Y = line_spacing_dp * i
                val num = to_int_or_float(round(minValue + number_spacing * (numberOfHorizontalLines-i),2))

                drawText(textMeasurer,text = "$num", topLeft = Offset(0f,line_Y - 20f))
                drawLine(graphColor,start = Offset(Graph_X_start_Offset,line_Y),end = Offset(graphSize.width-10,line_Y), strokeWidth = strokeWidth)
            }

        }
    }
}

fun <T: Number>generatePath(list: List<T>, size: androidx.compose.ui.geometry.Size, maxValue :Float,minValue: Float) : Path
{

    val path = Path()

    var max_min_diff = abs(maxValue - minValue)
    var y_offset = 0 - minValue

    if(max_min_diff == 0f){ max_min_diff = maxValue*2 ;y_offset = 0f}
    val multiplier = size.height / max_min_diff

    if(list.isEmpty()) return path

    path.moveTo(0f,size.height - (list.first().toFloat() + y_offset) * multiplier)

    list.forEachIndexed { index, value ->
        path.lineTo(index.toFloat()*10,size.height - (value.toFloat() + y_offset) * multiplier)
    }

    return path
}
@Composable
fun Line_Graph(pointList: List<Point>){
    val maxValue = pointList.max()
    val minValue = pointList.min()
    val diff = maxValue - minValue

    val firstTime = pointList.first().x.toInt()
    val latestTime = pointList.last().x.toInt()
    val yLinePeriod = 10

    val Xsteps = 5
    val Ysteps = 5

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(Xsteps)
        .labelData { i -> formatTime(firstTime+i) }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(Ysteps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i->
            val yScale = diff / Ysteps
            (minValue + i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointList,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.Straight()
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary, radius = 0.dp
                    ),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.tertiary,),
                    ShadowUnderLine(
                        alpha = 0.8f,
                        brush = Brush.verticalGradient(
                            colors = listOf( MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent )
                        )
                    ),
                    SelectionHighlightPopUp(popUpLabel = ::formatPopUp)
                )
            ),
        ),
        yAxisData = yAxisData,
        xAxisData = xAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.tertiary),
        backgroundColor = MaterialTheme.colorScheme.surface
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}
@Composable
fun Graph(list: LiveData<MutableList<Point>>)
{
    val points = list.observeAsState(mutableListOf(Point(0f,0f))).value

    if(points.isEmpty()) points.add(Point(0f,0f))
    Line_Graph(points)
}
/*
@Composable
fun TempGraph()
{
    val tempList = SensorData.tempList.observeAsState(mutableListOf(Point(0f,0f))).value
    //Graph(tempList,40,0, Color.Blue)
    Line_Graph(tempList)
    // Flexible_Graph(tempList, strokeColor = Color.Blue, lineSpace = 5f)

}
@Composable
fun HumidityGraph() {
    val humList = SensorData.humidityList.observeAsState(mutableListOf(Point(0f,0f))).value
    Line_Graph(humList)
    //Flexible_Graph(humList, strokeColor = Color.Blue, lineSpace = 5f)
    //Graph(humList,100,0, Color.Blue, lineSpace = 20)
}
@Composable
fun IAQGraph()
{
    val iaq = SensorData.IAQList.observeAsState(mutableListOf(Point(0f,0f))).value

    //Graph(iaq,500,0, Color.Red, lineSpace = 100f)
    Line_Graph(iaq)
    //Flexible_Graph(iaq, strokeColor = Color.Blue, lineSpace = 5f)
}
@Composable
fun bVOCGraph()
{
    val bVOC = SensorData.bVOCList.observeAsState(mutableListOf(Point(0f,0f))).value

    //Graph(bVOC,1,0, Color.Red, lineSpace = 0.2f)
    //Flexible_Graph(bVOC, strokeColor = Color.Blue, lineSpace = 5f)
    Line_Graph(bVOC)
}
@Composable
fun CO2Graph()
{
    val CO2 = SensorData.CO2List.observeAsState(mutableListOf(Point(0f,0f))).value

    //Graph(CO2,1000,0, Color.Red, lineSpace = 100f)
    //Flexible_Graph(CO2, strokeColor = Color.Blue, lineSpace = 5f)
    Line_Graph(CO2)
}
@Composable
fun PressureGraph()
{
    val pressure = SensorData.pressureList.observeAsState(mutableListOf(Point(0f,0f))).value

    //Graph(pressure,1000,0, Color.Red, lineSpace = 100f)
    //Flexible_Graph(pressure, strokeColor = Color.Blue, lineSpace = 5f)
    Line_Graph(pressure)
}
@Composable
fun StepsGraph()
{
    val steps = SensorData.stepsList.observeAsState(mutableListOf(Point(0f,0f))).value
    //Graph(steps,1000,0, Color.Red, lineSpace = 100f)
   // Flexible_Graph(steps, strokeColor = Color.Blue, lineSpace = 5f)
    if(steps.isEmpty()) steps.add(Point(0f,0f))
    Line_Graph(steps)
}
*/