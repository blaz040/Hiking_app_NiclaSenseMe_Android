package com.example.ble_con.dataManager.repo

import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
import com.example.ble_con.R
import com.example.ble_con.repository.ViewModelData
import com.example.ble_con.repository.ViewModelData.DataInfo
import com.google.android.gms.maps.model.LatLng

object SensorData {

    val temperature = DataList<Point>()
    val humidity = DataList<Point>()
    val pressure = DataList<Point>()
    val altitude = DataList<Point>()
    val steps = DataList<Point>()
    val iaq = DataList<Point>()
    val voc = DataList<Point>()
    val co2 = DataList<Point>()

    val location = DataList<LatLng>()

    val data:Map<String,DataList<out Any>> = mapOf(
        "Temperature"   to temperature,
        "Humidity"      to humidity,
        "Pressure"      to pressure,
        "Altitude"      to altitude,
        "Steps"         to steps,
        "Iaq"           to iaq,
        "Voc"           to voc,
        "Co2"           to co2,
        "Location"      to location,
    )
    
    var time:Int = 0

    var seaLevelPressure = 1013.25f // default value
    var seaLevelTemperature = 20.0f // default value

    fun updateTime(value:Int) {
        time = value
        ViewModelData.time.postValue(time)
    }
    fun clearData(){
        temperature.clear()
        co2.clear()
        voc.clear()
        iaq.clear()
        humidity.clear()
        pressure.clear()
        steps.clear()
        altitude.clear()
        location.clear()
    }

}