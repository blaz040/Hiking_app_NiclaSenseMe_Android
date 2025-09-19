package com.example.ble_con.repository

import androidx.lifecycle.LiveData
import co.yml.charts.common.model.Point
import com.example.ble_con.R
import com.example.ble_con.dataManager.repo.SensorData

// TODO CLEAN file
data class DataInfo(val name:String,val icon:Int,val postFix:String)

//data class DataList<T>(val info: DataInfo,val list: T)

val nothing = DataInfo("null",-1,"")

val humidityInfo = DataInfo("Humidity", R.drawable.humidity_icon," %")
val temperatureInfo = DataInfo("Temperature", R.drawable.temp_icon," C")
val pressureInfo = DataInfo("Pressure", R.drawable.pressure_icon," hPa")
val stepsInfo = DataInfo("Steps", R.drawable.steps_icon,"")
val airQualityInfo = DataInfo("Air Quality", R.drawable.air_quiality_icon,"")
val vocInfo = DataInfo("VOC", R.drawable.voc_icon,"")
val co2Info = DataInfo("CO2", R.drawable.co2_icon,"")
val altitudeInfo = DataInfo("Altitude", R.drawable.co2_icon," m")

//val humidity = DataList(humidityInfo, mutableListOf<Point>())
//val temperature = DataList(humidityInfo, mutableListOf<Point>())