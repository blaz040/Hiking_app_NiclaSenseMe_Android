package com.example.ble_con.dataManager.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.yml.charts.common.model.Point
import com.example.ble_con.dataManager.repo.SensorData

import com.google.android.gms.maps.model.LatLng

class DataList<T>{
    val maxListSize = 60*60*5 // 5h vredno podatkov

    val mutableList: MutableList<T> = mutableListOf()
    val mutableLiveDataList = MutableLiveData<MutableList<T>>(mutableListOf())
    val liveData: LiveData<MutableList<T>> = mutableLiveDataList

    fun clear(){
        mutableList.clear()
    }
    fun getList(): List<T> {
        return mutableList.toList()
        /*
        var list = mutableListOf<T>()
        mutableList.value?.let{
            list = it
        }
        return list.toList()
        */
    }

}

fun DataList<Point>.add(value: Point){
    mutableList.add(value)
    mutableLiveDataList.postValue( mutableList.toMutableList() )
}
fun <T:Number>DataList<Point>.add(value: T){
    val list = this.mutableList
    val currentTime = SensorData.time.toFloat()

    if(list.size >= maxListSize)
        list?.removeAt(0)

    list.add(Point(currentTime, value.toFloat()))

    this.mutableLiveDataList.postValue( list.toMutableList() )
}
fun DataList<LatLng>.add(value: LatLng){
    val list = this.mutableList
    if(list.size >= maxListSize)
        list?.removeAt(0)

    list.add(value)

    this.mutableLiveDataList.postValue(list.toMutableList())
}
