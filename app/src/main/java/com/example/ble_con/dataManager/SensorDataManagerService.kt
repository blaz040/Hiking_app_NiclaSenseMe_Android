package com.example.ble_con.dataManager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ble_con.R
import com.example.ble_con.dataManager.ble.BLEManager
import com.example.ble_con.dataManager.repo.BroadcastAction
import com.example.ble_con.dataManager.repo.ConStatus
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SendCommand
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.repository.ViewModelData
import com.google.android.gms.maps.model.LatLng


@SuppressLint("MissingPermission")
class SensorDataManagerService: Service(){
    private val TAG = "SDMS"

    private val ble_api = BLEManager(this)

    private val recording_api = RecordingManager(::onTimerUpdateCallback)

    private val location_api = LocationManager(this,::onLocationUpdateCallback,delay_ms = 3000)

    private var mNotificationManager: NotificationManager? = null
    private val ble_notificationID = 1
    private val recording_notificationID = 2

    private val connectionStatus = ViewModelData._conStatus
    private val recordingStatus = ViewModelData._recordingStatus
    private val connected_device = ViewModelData._selectedDevice

    private val binder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"got Intent")
        when(intent?.action)
        {
            DEVICE_CONNECT    -> connect(intent)
            DEVICE_SEND       -> send(intent)
            DEVICE_DISCONNECT -> disconnect()
            RECORDING_START   -> startRecording()
            RECORDING_STOP    -> stopRecording()
            RECORDING_PAUSE, RECORDING_RESUME, RECORDING_TOGGLE   -> toggleRecording()
            else -> Log.e(TAG,"Wrong action for SensorDataManagerService")
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun clearData(){
        SensorData._tempList.value?.clear()
        SensorData._CO2List.value?.clear()
        SensorData._bVOCList.value?.clear()
        SensorData._IAQList.value?.clear()
        SensorData._humidityList.value?.clear()
        SensorData._pressureList.value?.clear()
        SensorData._stepsList.value?.clear()
    }

    private fun connect(intent: Intent) {
        if(connectionStatus.value == ConStatus.CONNECTED) {
            disconnect()
        }

        Log.d(TAG,"CONNECTING...")
        val result = intent.getParcelableExtra("ScanResult") as ScanResult?

        connected_device.postValue(result)
        result?.let { ble_api.connectToDevice(result.device) }

        val disconnectIntent = Intent(this, SensorDataManagerService::class.java).setAction(DEVICE_DISCONNECT)
        val disconnectPendingIntent: PendingIntent = PendingIntent.getService(this,0,disconnectIntent,PendingIntent.FLAG_IMMUTABLE)

        val ble_builder = NotificationCompat.Builder(this,"BLE_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Connected....")
            .setContentText("${result?.device?.name}")
            .addAction(0,"Disconnect",disconnectPendingIntent)
            .build()
        startForeground(ble_notificationID,ble_builder)
    }
    private fun disconnect() {
        stopRecording()
        connected_device.postValue(null)
        ble_api.disconnect()
        Log.d(TAG,"DISCONNECTED")
    }

    private fun send(intent: Intent) {
        val command: Int = intent.getIntExtra("command",0)
        ble_api.send(command)
    }
    private fun startRecording() {
        clearData()
        val timer_notify_builder = NotificationCompat.Builder(this,"timer_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recording....")

        mNotificationManager = NotificationManager(this,recording_notificationID,timer_notify_builder)

        recordingStatus.postValue(RecordingStatus.RUNNING)

        ble_api.send(SendCommand.START)
        location_api.start()
        recording_api.start()
    }
    private fun stopRecording() {
        recordingStatus.postValue(RecordingStatus.STOPPED)

        mNotificationManager?.closeNotification()
        mNotificationManager = null

        ble_api.send(SendCommand.STOP)
        location_api.stop()
        recording_api.stop()
    }
    private fun toggleRecording() {
        when(recordingStatus.value) { // PAUSING recording
            RecordingStatus.RUNNING -> {
                ble_api.send(SendCommand.STOP)
                recordingStatus.postValue(RecordingStatus.PAUSED)
            }
            RecordingStatus.PAUSED -> {
                ble_api.send(SendCommand.START)
                recordingStatus.postValue(RecordingStatus.RUNNING)
            }
            else -> { Log.e(TAG,"ERROR toggleRecording ") }
        }
        location_api.toggleRun()
        recording_api.toggleRun()
    }

    fun translate(value: Int): String {
        var str = value.toString()
        if(value <10) str ="0${value.toString()}"
        return str
    }
    fun formatTime(time: Int):String {
        //format 00:00:00
        val seconds = translate(time%60)
        val minutes = translate((time/60)%60)
        val hours   = translate((time/3600))
        return "$hours:$minutes:$seconds"
    }

    private fun onTimerUpdateCallback(value:Int) {
        mNotificationManager?.updateNotification(formatTime(value))
        SensorData.updateTime(value)
    }
    private fun onLocationUpdateCallback(location: LatLng) {
        SensorData.updateList(SensorData._location,location)
    }
    val myReceiver =  object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"Received Action ${intent?.action}")
            when(intent?.action){
                BroadcastAction.CONNECTED -> { }
                BroadcastAction.DISCONNECTED -> {
                    disconnect()
                    stopSelf()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    inner class LocalBinder: Binder(){
        fun getService(): SensorDataManagerService = this@SensorDataManagerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Registered Receiver")
        val filter = IntentFilter().apply {
            addAction(BroadcastAction.CONNECTED)
            addAction(BroadcastAction.DISCONNECTED)
        }
        registerReceiver(myReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"Unregistered receiver")
        unregisterReceiver(myReceiver)
    }
    companion object {
        val RECORDING_START = "start"
        val RECORDING_STOP = "stop"
        val DEVICE_CONNECT = "connect"
        val DEVICE_DISCONNECT = "disconnect"
        val BLE_SCAN = "scan"
        val DEVICE_SEND = "send"
        val RECORDING_PAUSE = "pause"
        val RECORDING_RESUME = "resume"
        val RECORDING_RESTART = "restart"
        val RECORDING_TOGGLE = "toggle"
    }

}