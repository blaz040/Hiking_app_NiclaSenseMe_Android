package com.example.ble_con.dataManager.repo

import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
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

    val _time: MutableLiveData<Int> = MutableLiveData(0)

    var seaLevelPressure = 1013.25f // default value
    var seaLevelTemperature = 20.0f // default value

    fun updateTime(value:Int) {
        _time.postValue(value)
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