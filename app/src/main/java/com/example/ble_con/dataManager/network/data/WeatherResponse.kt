package com.example.ble_con.dataManager.network.data

data class WeatherResponse(
    val current: CurrentData
)
data class CurrentData(
    val pressure_msl: Float,
    val temperature_2m: Float
)
