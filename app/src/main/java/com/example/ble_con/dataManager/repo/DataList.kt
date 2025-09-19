package com.example.ble_con.dataManager.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
import com.example.ble_con.dataManager.repo.SensorData._time
import com.google.android.gms.maps.model.LatLng

class DataList<T>{
    val maxListSize = 60*60

    val mutableList: MutableLiveData<MutableList<T>> = MutableLiveData(mutableListOf())
    val liveData: LiveData<MutableList<T>> = mutableList

    fun clear(){
        mutableList.value.clear()
    }
    fun getList(): List<T> {
        var list = mutableListOf<T>()
        mutableList.value?.let{
            list = it
        }
        return list.toList()
    }

}
fun <T:Number>DataList<Point>.add(value: T){
    val list = this.mutableList
    val currentTime = _time.value.toFloat()

    if(list.value.size >= maxListSize)
        list.value?.removeAt(0)

    val newList = list.value.toMutableList().apply{ add(Point(currentTime, value.toFloat()))}

    list.postValue(newList)
}
fun DataList<LatLng>.add(value: LatLng){
    val list = this.mutableList
    if(list.value.size >= maxListSize)
        list.value?.removeAt(0)

    val newList = list.value.toMutableList().apply { add(value) }

    list.postValue(newList)
}