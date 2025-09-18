package com.example.ble_con.dataManager.network
import com.example.ble_con.dataManager.network.data.WeatherResponse
import retrofit2.http.GET


interface WeatherApiInterface {
    @GET("forecast?latitude=45.5469&longitude=13.7294&current=temperature_2m,pressure_msl&forecast_days=1")
    suspend fun getWeather(): WeatherResponse
}