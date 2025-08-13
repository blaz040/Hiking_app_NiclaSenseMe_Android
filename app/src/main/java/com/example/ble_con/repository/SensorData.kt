package com.example.ble_con.repository

import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SensorData {

    val maxListSize = 80

    val _tempValue: MutableLiveData<MutableList<Float>> = MutableLiveData( mutableListOf(0f) )
    val tempValue: LiveData<MutableList<Float>> = _tempValue

    val _humidityValue: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val humidityValue: LiveData<MutableList<Int>> = _humidityValue

    val _IAQValue: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val IAQValue: LiveData<MutableList<Int>> = _IAQValue

    val _bVOCValue: MutableLiveData<MutableList<Float>> = MutableLiveData( mutableListOf(0f) )
    val bVOCValue: LiveData<MutableList<Float>> = _bVOCValue

    val _CO2Value: MutableLiveData<MutableList<Int>> = MutableLiveData( mutableListOf(0) )
    val CO2Value: LiveData<MutableList<Int>> = _CO2Value
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
    val _incNumber: MutableLiveData<Int> = MutableLiveData(0)
    val incNumber: LiveData<Int> = _incNumber

    fun incNumber()
    {
        _incNumber.postValue(incNumber.value +1)
    }

}