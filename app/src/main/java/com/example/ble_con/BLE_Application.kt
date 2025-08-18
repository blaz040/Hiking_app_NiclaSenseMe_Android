package com.example.ble_con

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class BLE_Application :Application() {
    //val testService: Intent = Intent(this,TestService::class.java)
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("runnable_channel","Running Notifications",NotificationManager.IMPORTANCE_HIGH)
        val notificationmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationmanager.createNotificationChannel(channel)
   }
}