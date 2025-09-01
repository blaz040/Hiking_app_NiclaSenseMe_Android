package com.example.ble_con.ble

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.application
import com.example.ble_con.R

@SuppressLint("MissingPermission")
class BLE_Service: Service(){
    private val ble_api by lazy { BLE_Manager(applicationContext) }

    private var connected = false

    private val TAG = "BLE_SERVICE"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"got Intent")
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
    private fun connect(intent: Intent)
    {
        if(connected)
        {
           connected = false
           disconnect()
        }

        Log.d(TAG,"CONNECTING...")
        val disconnectIntent = Intent(this,BLE_Service::class.java).apply {
            setAction(Actions.DISCONNECT.toString())
        }

        // val buttonPendingIntent: PendingIntent = PendingIntent.getBroadcast(this,0,buttonIntent,PendingIntent.FLAG_IMMUTABLE)
        val disconnectPendingIntent: PendingIntent = PendingIntent.getService(this,0,disconnectIntent,PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this,"runnable_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Connected....")
            .setContentText("Text")
            .addAction(0,"Disconnect",disconnectPendingIntent)

            .build()
        startForeground(1,notification)

        val result = intent.getParcelableExtra("ScanResult") as ScanResult?
        //val result = ViewModelData.selectedDevice.value
        Log.d(TAG,"${result?.device?.name}")
        result?.let {
            ble_api.connectToDevice(result.device)
        }
    }
    private fun disconnect()
    {
        connected = false
        ble_api.disconnect()
        Log.d(TAG,"DISCONNECTED")
        stopSelf()
    }
    private fun send(intent: Intent)
    {
        val command: Int = intent.getIntExtra("command",0)
        ble_api.send(command)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    enum class Actions{
        START,STOP,CONNECT,DISCONNECT,SCAN,START_CONN,SEND
    }

}