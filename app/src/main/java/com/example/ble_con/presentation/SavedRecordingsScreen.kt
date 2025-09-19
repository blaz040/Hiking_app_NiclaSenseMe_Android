package com.example.ble_con.presentation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import java.io.File

private val TAG =" SavedRecordingScreen"
@Composable
fun SavedRecordingsScreen(
    vm:ViewModel = viewModel()
){
    //TODO move the logic of this code to FileManager
    val context = vm.getAppContext()
    val folder = vm.file_api.getFolder()
    Column(Modifier.fillMaxSize()) {
        val selectedFile = remember{ mutableStateOf("null") }
        val list = folder.list()

        var first = true
        list.forEach{ fileName->
            if(!first){
                HorizontalDivider(thickness = 5.dp)
            }
            first = false

            ShowFile(
                Modifier.clickable { selectedFile.value = fileName },
                fileName,
                (selectedFile.value == fileName),
                {vm.file_api.toString()},
                {},
            )
            //Box(Modifier.background(color = Color(0xc2c2c2c2)).sizeIn(500.dp,50.dp,1000.dp,100.dp)){
            //    Text(file,Modifier.padding(5.dp))
            //}
        }
    }
}
@Composable
fun ShowFile(modifier: Modifier = Modifier,fileName:String, clicked:Boolean,onLoadClick: ()->Unit,onDeleteClick: ()->Unit){
    val txt_modifier = Modifier.padding(5.dp)
    Column(modifier.sizeIn(500.dp,50.dp,1000.dp,100.dp)) {
        Text(fileName,txt_modifier)
        if(clicked){
            Box(contentAlignment = Alignment.BottomEnd,modifier = Modifier.size(400.dp,50.dp)){
                Row(Modifier.padding(5.dp,0.dp)){
                    Button(onDeleteClick) { Text("Delete") }
                    Button(onLoadClick) { Text("Load") }
                }
            }
        }

    }

}