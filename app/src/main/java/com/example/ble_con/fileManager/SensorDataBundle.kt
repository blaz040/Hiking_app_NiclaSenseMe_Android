package com.example.ble_con.fileManager

import co.yml.charts.common.model.Point
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
@Serializable
data class SensorDataBundle(
    val temperature:List<@Contextual Point>,
    val humidity:List<@Contextual Point>,
    val pressure:List<@Contextual Point>,
    val iaq:List<@Contextual Point>,
    val voc:List<@Contextual Point>,
    val co2:List<@Contextual Point>,
    val steps:List<@Contextual Point>,
    val altitude:List<@Contextual Point>,
    val location:List<@Contextual LatLng>
)

