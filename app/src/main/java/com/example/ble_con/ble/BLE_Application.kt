package com.example.ble_con.ble

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import androidx.lifecycle.MutableLiveData
import com.example.ble_con.presentation.SensorDataScreen
import com.example.ble_con.repository.SensorData
import com.example.ble_con.repository.SensorData.add

class BLE_Application :Application() {
    //val testService: Intent = Intent(this,TestService::class.java)
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("runnable_channel","Running Notifications",NotificationManager.IMPORTANCE_HIGH)
        val notificationmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationmanager.createNotificationChannel(channel)

        SensorData._tempValue.add(20f)
        SensorData.updateList(SensorData._tempValue,20f)

        Intent(applicationContext,TestService::class.java).also {
            it.action = TestService.Actions.START.toString()
            startService(it)
        }

    }
}