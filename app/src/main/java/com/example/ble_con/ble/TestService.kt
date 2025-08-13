package com.example.ble_con.ble

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ble_con.R
import com.example.ble_con.repository.SensorData
import kotlin.concurrent.thread

class TestService(): Service() {

    val TAG = "SERVICE_TEST"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       when(intent?.action)
       {
           Actions.START.toString() -> start()
           //Actions.CONNECT.toString() -> connect(intent)
           //Actions.SCAN.toString() -> scan(intent)
           Actions.STOP.toString()  -> stop()
           else -> Log.e(TAG,"Wrong action for service")
       }
        return super.onStartCommand(intent, flags, startId)
    }
    fun start()
    {
        Log.d(TAG,"Created")
        val notification = NotificationCompat.Builder(this,"runnable_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Service running")
            .setContentText("Running....")
            .build()
        startForeground(1,notification)
        thread(true){
           /*
            while(true) {
                Log.d(TAG, "Incrementing num ${SensorData._incNumber.value}")
                SensorData.incNumber()
                Thread.sleep(1000)
            }
            */
        }

    }

    fun stop(){
        Log.d(TAG,"STOPED")
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SERVICE_TEST","Ended....")
    }
    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    enum class Actions{
        START,STOP,CONNECT,SCAN
    }
}