package com.example.ble_con.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SensorData {

    val maxListSize = 80

    val _tempList: MutableLiveData<MutableList<Float>> = MutableLiveData(mutableListOf())
    val tempList: LiveData<MutableList<Float>> = _tempList

    val _humidityList: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val humidityList: LiveData<MutableList<Int>> = _humidityList

    val _IAQList: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val IAQList: LiveData<MutableList<Int>> = _IAQList

    val _bVOCList: MutableLiveData<MutableList<Float>> = MutableLiveData(mutableListOf())
    val bVOCList: LiveData<MutableList<Float>> = _bVOCList

    val _CO2List: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val CO2List: LiveData<MutableList<Int>> = _CO2List

    val _pressureList: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val pressureList: LiveData<MutableList<Int>> = _pressureList

    val _stepsList: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val stepsList: LiveData<MutableList<Int>> = _stepsList

    /* */
    fun <T> MutableLiveData<MutableList<T>>.add(value: T){

    }
    fun <T> updateList (list: MutableLiveData<MutableList<T>>, value :T)
    {
        val newList = list.value.toMutableList().apply { add(value) }

        if(list.value.size >= maxListSize )
            newList.removeAt(0)

        list.postValue(newList)
    }
}