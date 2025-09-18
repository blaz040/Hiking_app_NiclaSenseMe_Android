package com.example.ble_con.dataManager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ble_con.R
import com.example.ble_con.SnackbarManager
import com.example.ble_con.dataManager.ble.BLEManager
import com.example.ble_con.dataManager.network.WeatherApiManager
import com.example.ble_con.dataManager.network.data.WeatherResponse
import com.example.ble_con.dataManager.repo.BluetoothBroadcastAction
import com.example.ble_con.dataManager.repo.ConStatus
import com.example.ble_con.dataManager.repo.RecordingStatus
import com.example.ble_con.dataManager.repo.SendCommand
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.repository.ViewModelData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.math.round


@SuppressLint("MissingPermission")
class SensorDataManagerService: Service(){
    private val TAG = "SDMS"

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val ble_api = BLEManager(this)

    private val recording_api = RecordingManager(serviceScope,::onTimerUpdateCallback)

    private val location_api = LocationManager(this,serviceScope,::onLocationUpdateCallback,delay_ms = 3000)

    private val weather_api = WeatherApiManager(serviceScope,::onWeatherCallback)

    private var mNotificationManager: NotificationManager? = null
    private val ble_notificationID = 1
    private val recording_notificationID = 2

    private val connectionStatus = ViewModelData._conStatus
    private val recordingStatus = ViewModelData._recordingStatus
    private val connected_device = ViewModelData._selectedDevice

    private val binder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"got Intent")
        when(intent?.action) {
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
        weather_api.getWeatherData()

        clearData()
        val record_timer_notify_builder = NotificationCompat.Builder(this,"timer_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recording....")

        mNotificationManager = NotificationManager(this,recording_notificationID,record_timer_notify_builder)

        recordingStatus.postValue(RecordingStatus.RUNNING)

        ble_api.send(SendCommand.START)
        location_api.start()
        recording_api.start()
        SnackbarManager.send("Recording started")
    }
    private fun stopRecording() {

        recordingStatus.postValue(RecordingStatus.STOPPED)

        mNotificationManager?.closeNotification()
        mNotificationManager = null

        ble_api.send(SendCommand.STOP)
        location_api.stop()
        recording_api.stop()
        SnackbarManager.send("Recording stopped")
    }
    private fun toggleRecording() {
        when(recordingStatus.value) {
            RecordingStatus.RUNNING -> { //PAUSING recording
                ble_api.send(SendCommand.STOP)
                recordingStatus.value = RecordingStatus.PAUSED
            }
            RecordingStatus.PAUSED -> { // RESUMING recording
                ble_api.send(SendCommand.START)
                recordingStatus.value = RecordingStatus.RUNNING
            }
            else -> { Log.e(TAG,"ERROR toggleRecording ") }
        }
        location_api.toggleRun()
        recording_api.toggleRun()

        SnackbarManager.send("Recording toggled: ${recordingStatus.value}")
    }

    private fun translate(value: Int): String {
        var str = value.toString()
        if(value <10) str ="0${value}"
        return str
    }
    private fun formatTime(time: Int):String {
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
    private fun onWeatherCallback(response: WeatherResponse){
        SensorData.seaLevelPressure = response.current.pressure_msl
        SensorData.seaLevelTemperature = response.current.temperature_2m
    }
    val myReceiver =  object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"Received Action ${intent?.action}")
            when(intent?.action){
                BluetoothBroadcastAction.CONNECTED -> {
                    SnackbarManager.send("device Connected")
                }
                BluetoothBroadcastAction.DISCONNECTED -> {
                    disconnect()
                    stopSelf()
                    SnackbarManager.send("device Disconnected")
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
            addAction(BluetoothBroadcastAction.CONNECTED)
            addAction(BluetoothBroadcastAction.DISCONNECTED)
        }
        registerReceiver(myReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"Unregistered receiver")
        unregisterReceiver(myReceiver)
        serviceScope.cancel()

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