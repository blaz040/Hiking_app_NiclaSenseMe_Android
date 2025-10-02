package com.example.ble_con.fileManager

import android.content.Context
import android.util.Log
import co.yml.charts.common.model.Point
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.dataManager.repo.add
import com.example.ble_con.repository.ViewModelData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FileManager(
    val context:Context
) {
    private val TAG = "FileManager"
    private val recordingsFolderName = "/recordings/"
    private val recordingsPath = context.dataDir.path + recordingsFolderName
    private val recordingsFolder = File(context.dataDir,recordingsFolderName)
    private val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm", Locale.getDefault())
    private val saveDateFormat = SimpleDateFormat("MM-DD-YYYY_HH:mm", Locale.getDefault())
    private val scope = CoroutineScope(Dispatchers.IO)

    val module = SerializersModule {
        contextual(LatLng::class, LatLngSerializer)
        contextual(Point::class, PointSerializer)
    }
    val json = Json {
        serializersModule = module
    }

    init{
        Log.d(TAG,"init...")

        if(!recordingsFolder.exists())
            Log.d(TAG,"creating folder${recordingsFolder} is ${recordingsFolder.mkdir()}")
    }
    fun getFileFromName(fileName: String): File{
        return File(context.dataDir.toString()+ recordingsFolderName,fileName)
    }
    fun getFileDataFromName(fileName: String): FileData{
        val file = getFileFromName(fileName)

        val curDate = Calendar.getInstance()
        curDate.timeInMillis = file.lastModified()
        val str = dateFormat.format(curDate.time)

       return FileData(fileName,str)
    }
    fun delete(fileName:String){
        val file = getFileFromName(fileName)
        Log.d(TAG,"deleting file $fileName ${file.delete()}")
        notifyViewModelData()
    }
    fun readAll(file: File): String{
        //val outFile = File(context.dataDir.toString()+ recordingsFolderName,fileName)
        if (file.isDirectory || !file.canRead()) {
            Log.e(TAG,"Cant read ${file.name} is a nonReadable or a directory")
            return "Error"
        }
        var content = file.reader().use{it.readText()}
        /*
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            content += lines.fold("") { some, text ->
                "$some\n$text"
            }
        }
        */
        return content
    }
    private fun write(file:File,str:String){
        file.writeBytes(str.toByteArray())
    }
    fun save(str: String): Boolean{

        var fileName = str
        if(fileName == "") {
            val curDate = Calendar.getInstance()
            fileName = saveDateFormat.format(curDate.time)
        }
        val file = getFileFromName(fileName)
        if(!file.exists()){
            val ch = file.createNewFile()
            if(ch == false) {
                Log.d(TAG,"can't create file $fileName")
                return false
            }
            Log.d(TAG,"Created new File $fileName")
        }
        else{
            Log.d(TAG,"file $fileName already exists")
            return false
        }
        scope.launch {
            val dataBundle = SensorDataBundle(
                temperature = SensorData.temperature.getList(),
                humidity = SensorData.humidity.getList(),
                pressure = SensorData.pressure.getList(),
                iaq = SensorData.iaq.getList(),
                voc = SensorData.voc.getList(),
                co2 = SensorData.co2.getList(),
                steps = SensorData.steps.getList(),
                altitude = SensorData.altitude.getList(),
                location = SensorData.location.getList()
            )
            val json = json.encodeToJsonElement(dataBundle).toString()
            write(file, json)
            notifyViewModelData()

        }
        return true
    }

    fun getFileList(): List<FileData>{
        val list = mutableListOf<FileData>()
        val folder = recordingsFolder.list()

        folder?.forEach{fileName->
            list.add(getFileDataFromName(fileName))
        }

        return list.toList()
    }

    fun loadFile(fileName: String){
        val file = getFileFromName(fileName)

        ViewModelData.fileData = getFileDataFromName(fileName)

        val out = json.decodeFromString<SensorDataBundle>(readAll(file))
        Log.d(TAG,"$out")


        SensorData.clearData()
        out.temperature.forEach { SensorData.temperature.add(it) }
        out.humidity.forEach { SensorData.humidity.add(it) }
        out.pressure.forEach { SensorData.pressure.add(it) }
        out.steps.forEach { SensorData.steps.add(it) }
        out.iaq.forEach { SensorData.iaq.add(it) }
        out.voc.forEach { SensorData.voc.add(it) }
        out.co2.forEach { SensorData.co2.add(it) }
        out.altitude.forEach { SensorData.altitude.add(it) }
        out.location.forEach { SensorData.location.add(it) }
    }
    fun notifyViewModelData(){
        ViewModelData.updateFileList(getFileList())
    }
}