package com.example.ble_con.data



data class SensorData(
    var temp_value: Float = 0f,
    var humidity_value: Int = -1,
    var IAQ_value: Int = -1,
    var bVOC_value:Float = 0f,
    var CO2_value: Int = -1
)