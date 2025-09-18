package com.example.ble_con.dataManager.repo

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
import com.google.android.gms.maps.model.LatLng

object SensorData {

    val maxListSize = 60*60

    val _tempList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val tempList: LiveData<MutableList<Point>> = _tempList

    val _humidityList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val humidityList: LiveData<MutableList<Point>> = _humidityList

    val _IAQList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val IAQList: LiveData<MutableList<Point>> = _IAQList

    val _bVOCList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val bVOCList: LiveData<MutableList<Point>> = _bVOCList

    val _CO2List: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val CO2List: LiveData<MutableList<Point>> = _CO2List

    val _pressureList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val pressureList: LiveData<MutableList<Point>> = _pressureList

    val _stepsList: MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val stepsList: LiveData<MutableList<Point>> = _stepsList

    val _location:MutableLiveData<MutableList<LatLng>> = MutableLiveData(mutableListOf())
    val location: LiveData<MutableList<LatLng>> = _location

    val _altitude:MutableLiveData<MutableList<Point>> = MutableLiveData(mutableListOf())
    val altitude: LiveData<MutableList<Point>> = _altitude

    val _time: MutableLiveData<Int> = MutableLiveData(0)

    var seaLevelPressure = 1013.25f // default value
    var seaLevelTemperature = 20.0f // default value

    /* */
    fun <T> MutableLiveData<MutableList<T>>.add(value: T){

    }
    fun<T: Number> updateList(list: MutableLiveData<MutableList<Point>>, value :T) {
        val currentTime = _time.value.toFloat()

        if(list.value.size >= maxListSize)
            list.value?.removeAt(0)

        val newList = list.value.toMutableList().apply { add(Point(currentTime, value.toFloat())) }

        list.postValue(newList)
    }
    fun updateList (list: MutableLiveData<MutableList<LatLng>>, value : LatLng) {
        if(list.value.size >= maxListSize)
            list.value?.removeAt(0)

        val newList = list.value.toMutableList().apply { add(value) }

        list.postValue(newList)
    }
    fun updateTime(value:Int) {
        _time.postValue(value)
    }
}