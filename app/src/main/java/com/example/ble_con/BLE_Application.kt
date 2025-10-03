package com.example.ble_con

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import kotlin.concurrent.timer

class BLE_Application :Application() {
    //val testService: Intent = Intent(this,TestService::class.java)
    override fun onCreate() {
        super.onCreate()
        application = this
        val ble_channel = NotificationChannel("BLE_channel","Running Notifications",NotificationManager.IMPORTANCE_LOW)
        val timer_channel = NotificationChannel("timer_channel","Running Notifications",NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(ble_channel)
        notificationManager.createNotificationChannel(timer_channel)
   }
    companion object{
        lateinit var application: Application
    }
}