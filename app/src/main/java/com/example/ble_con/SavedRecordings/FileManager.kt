package com.example.ble_con.SavedRecordings

import android.content.Context
import android.util.Log
import co.yml.charts.common.model.Point
import com.example.ble_con.dataManager.repo.SensorData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

class FileManager(
    val context:Context
) {
    private val TAG = "FileManager"
    private val recordingsFolderName = "/recordings/"
    private val recordingsPath = context.dataDir.path + recordingsFolderName
    private val recordingsFolder = File(context.dataDir,recordingsFolderName)

    init{
        if(!recordingsFolder.exists())
            Log.d(TAG,"${recordingsFolder.mkdir()}")

    }
    fun delete(fileName:String){
        //TODO add this fun to viewModel
        context.deleteFile(recordingsFolderName+fileName)
    }
    fun readAll(fileName:String): String{
        val outFile = File(context.dataDir,recordingsFolderName+fileName)
        if (outFile.isDirectory || !outFile.canRead()) {
            Log.e(TAG,"Cant read $fileName is a nonReadable or a directory")
            return "Error"
        }
        var content = ""
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            content += lines.fold("") { some, text ->
                "$some\n$text"
            }
        }
        return content
    }
    private fun write(file:File,str:String){
        file.writeBytes(str.toByteArray())
    }
    fun save(){
    //TODO add identification fo the files by date
        val file = File(context.dataDir.toString()+ recordingsFolderName,"firstSavedFile")
        if(!file.exists()) Log.d(TAG,"${file.createNewFile()}")

        val json = Json.encodeToJsonElement(dataList).toString()
        write(file,json)
    }
    fun getFolder():File{
        //TODO chech if this actually works opens the folder file
        return File(context.dataDir,recordingsFolderName)
    }
    fun loadFile(){

    }
}