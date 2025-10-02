package com.example.ble_con.presentation.SavedRecordingsScreen

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ble_con.fileManager.FileData
import com.example.ble_con.Snackbar.SnackbarManager
import com.example.ble_con.ViewModel
import com.example.ble_con.repository.Routes
import com.example.ble_con.repository.ViewModelData

private val TAG =" SavedRecordingScreen"
@Composable
fun SavedRecordingsScreen(
    navController: NavController,
    vm:ViewModel = viewModel()
){
    Column(Modifier.fillMaxSize()) {
        DisplayTitle("Saved Recordings")
        val fileList = ViewModelData.fileList.observeAsState().value
        val selectedFile = remember{ mutableStateOf(FileData("null", "null")) }

        var first = true
        fileList?.forEach{ file_data->
            if(!first){
                MyHorizontalDivider()
            }
            first = false

            ShowFile(
                Modifier.clickable { selectedFile.value = file_data },
                file_data,
                (selectedFile.value.name == file_data.name),
                onLoadClick = {
                    SnackbarManager.send("Loading Files....")
                    vm.loadFile(file_data.name);
                    navController.navigate(Routes.AnalyzeScreen)
                },
                onDeleteClick = {
                    SnackbarManager.send("Deleted file ${file_data.name}")
                    vm.deleteFile(file_data.name)
                },
            )
        }
    }
}
@Composable
fun ShowFile(modifier: Modifier = Modifier, fileData: FileData, clicked:Boolean, onLoadClick: ()->Unit, onDeleteClick: ()->Unit){
    val txt_modifier = Modifier.padding(5.dp)
    Column(modifier.sizeIn(500.dp,50.dp,500.dp,200.dp)) {
        Text(fileData.name,txt_modifier)
        if(clicked){
            Column(){
                Text("Date: ${fileData.date}",color = Color.Gray, modifier = Modifier.fillMaxWidth().height(25.dp).wrapContentHeight(Alignment.CenterVertically))
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(400.dp, 50.dp)
                ) {
                    Row(Modifier.padding(5.dp, 0.dp)) {
                        Button(onDeleteClick) { Text("Delete") }
                        Button(onLoadClick) { Text("Load") }
                    }
                }
            }

        }

    }
}
@Composable
fun DisplayTitle(title:String = "Saved Recordings"){
    Box(
        Modifier.fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(title, fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
    MyHorizontalDivider()
}
@Composable
fun MyHorizontalDivider(){
    HorizontalDivider(thickness = 5.dp,color = MaterialTheme.colorScheme.inversePrimary)
}