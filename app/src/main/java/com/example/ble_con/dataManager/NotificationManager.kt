package com.example.ble_con.dataManager

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationManager(
    val context: Context,
    val id:Int,
    val mBuilder:NotificationCompat.Builder,
) {
    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun updateNotification(message:String) {
        mBuilder.setContentText(message)
        mNotificationManager.notify(id,mBuilder.build())
    }
    fun closeNotification() {
        mNotificationManager.cancel(id)
    }
 }