package com.example.ble_con.dataManager.ble

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
import com.example.ble_con.R
import com.example.ble_con.dataManager.repo.BluetoothBroadcastAction


@SuppressLint("MissingPermission")
class BLEService: Service(){

    private val ble_api = BLEManager(this)

    private var connected = false

    private val TAG = "BLE_SERVICE"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"got Intent")
        when(intent?.action)
        {
            CONNECT -> connect(intent)
            SEND -> { send(intent) }
            DISCONNECT -> disconnect()
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
        val disconnectIntent = Intent(this, BLEService::class.java).apply {
            setAction(DISCONNECT)
        }

        val result = intent.getParcelableExtra("ScanResult") as ScanResult?

        Log.d(TAG,"${result?.device?.name}")
        result?.let {
            ble_api.connectToDevice(result.device)
        }

        // val buttonPendingIntent: PendingIntent = PendingIntent.getBroadcast(this,0,buttonIntent,PendingIntent.FLAG_IMMUTABLE)
        val disconnectPendingIntent: PendingIntent = PendingIntent.getService(this,0,disconnectIntent,PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this,"BLE_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Connected....")
            .setContentText("${result?.device?.name}")
            .addAction(0,"Disconnect",disconnectPendingIntent)

        startForeground(1,builder.build())

        //mNotificationManager = NotificationManager(this,1,builder)

    }
    private fun disconnect() {
        connected = false
        ble_api.disconnect()
        Log.d(TAG,"DISCONNECTED")
    }

    private fun send(intent: Intent)
    {
        val command: Int = intent.getIntExtra("command",0)
        ble_api.send(command)
    }

    override fun onBind(intent: Intent?): IBinder? = null

     val myReceiver =  object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"Received Action ${intent?.action}")
            when(intent?.action){
                BluetoothBroadcastAction.CONNECTED -> {
                //    ViewModelData.setConnectionStatus(ConnectionStatus.CONNECTED)
                }
                BluetoothBroadcastAction.DISCONNECTED -> {
                 //   ViewModelData.setConnectionStatus(ConnectionStatus.DISCONNECTED)
                    disconnect()
                    stopSelf()
                }
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Registered Receiver")
        val filter = IntentFilter().apply {
            addAction(BluetoothBroadcastAction.CONNECTED)
            addAction(BluetoothBroadcastAction.DISCONNECTED)
        }

        registerReceiver(myReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"Unregistered receiver")
        unregisterReceiver(myReceiver)
    }
    companion object {
        val START = "start"
        val STOP = "stop"
        val CONNECT = "connect"
        val DISCONNECT = "disconnect"
        val SCAN = "scan"
        val START_CONN = "start_connect"
        val SEND = "send"
    }

}