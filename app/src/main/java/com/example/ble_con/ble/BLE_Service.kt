package com.example.ble_con.ble

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ble_con.R

@SuppressLint("MissingPermission")
class BLE_Service: Service(){
    val ble_api by lazy { BLE_Manager(applicationContext) }

    var connected = false

    val TAG = "BLE_SERVICE"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action)
        {
            //Actions.START.toString() -> start()
            Actions.CONNECT.toString() -> connect(intent)
            //Actions.START_CONN.toString() -> {start(); connect(intent)}
            Actions.SEND.toString() -> { send(intent) }
            Actions.DISCONNECT.toString() -> disconnect()
            //Actions.STOP.toString()  -> stop()
            else -> Log.e(TAG,"Wrong action for service")
        }
        return super.onStartCommand(intent, flags, startId)
    }
    fun connect(intent: Intent)
    {
        if(connected)
        {
           connected = false
           disconnect()
        }

        Log.d(TAG,"CONNECTING...")
        val notification = NotificationCompat.Builder(this,"runnable_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Service running")
            .setContentText("Running....")
            .build()
        startForeground(1,notification)

        val result = intent.getParcelableExtra("ScanResult") as ScanResult?
        //val result = ViewModelData.selectedDevice.value
        Log.d(TAG,"${result?.device?.name}")
        result?.let {
            ble_api.connectToDevice(result.device)
        }
    }
    fun disconnect()
    {
        stopSelf()
        connected = false
        ble_api.disconnect()
        Log.d(TAG,"DISCONNECTED")
    }
    fun send(intent: Intent)
    {
        val command = intent.getIntExtra("command",0)
        ble_api.send(command)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class Actions{
        START,STOP,CONNECT,DISCONNECT,SCAN,START_CONN,SEND
    }
}