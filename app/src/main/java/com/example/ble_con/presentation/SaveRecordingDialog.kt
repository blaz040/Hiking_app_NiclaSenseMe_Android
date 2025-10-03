package com.example.ble_con.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ble_con.ViewModel


@Composable
fun SaveRecordingDialog(vm: ViewModel,title: String,onDismissRequest: () -> Unit = {}) {
    Dialog(onDismissRequest = { onDismissRequest() }) {

        vm.pauseRecording()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val error = remember { mutableStateOf(false) }
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                val fileName = remember { mutableStateOf("") }
                TextField(
                    fileName.value,
                    { fileName.value = it },
                    Modifier.fillMaxWidth().padding(20.dp, 0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                )
                if (error.value) {
                    Text("Can't Create ${fileName.value}, try other name", color = Color.Red)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    TextButton({
                        vm.resumeRecording(); onDismissRequest()
                    }) { Text("Cancel") }

                    TextButton({
                        vm.saveRecording("", { success->
                            check(success, error, onDismissRequest)
                        })
                    }) { Text("No save it by date") }

                    TextButton({
                        vm.saveRecording(fileName.value, { success->
                            check(success, error, onDismissRequest)
                        })
                    }) { Text("Confirm") }
                }
            }
        }
    }
}

fun check(success: Boolean,error: MutableState<Boolean>, callback: ()->Unit){
    if(success){
        callback()
        error.value = false
    }
    else{
        error.value = true
    }
}