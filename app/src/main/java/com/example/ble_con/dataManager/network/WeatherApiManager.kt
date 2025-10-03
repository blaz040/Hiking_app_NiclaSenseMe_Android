package com.example.ble_con.dataManager.network

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import com.example.ble_con.Snackbar.SnackbarManager
import com.example.ble_con.dataManager.network.data.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiManager(
    val coroutineScope: CoroutineScope,
    val callback: (WeatherResponse)->Unit
) {
    private val base_URL = "https://api.open-meteo.com/v1/"

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(base_URL)
        .build()

    private val retrofitService: WeatherApiInterface by lazy {
        retrofit.create(WeatherApiInterface::class.java)
    }
    private val delay_ms:Long = 5000
    fun getWeatherData(){
        var stop = false
        coroutineScope.launch {
            try {
                val response = retrofitService.getWeather()
                callback(response)
                Log.d("WEATHER",response.toString())
                SnackbarManager.send("got weather data :${response.current.pressure_msl} hPa, ${response.current.temperature_2m} C", duration = SnackbarDuration.Long)
            }catch (e: Exception){
                Log.e("WEATHER","Trying again in ${delay_ms/1000}s : "+e.toString())
                SnackbarManager.send(
                    "Failed to get weather data Trying again in ${delay_ms/1000} s",
                    duration = SnackbarDuration.Long,
                    label = "Stop",
                    {
                    stop = true
                    })
                delay(delay_ms)
                if(!stop) getWeatherData()
            }
        }
    }
}