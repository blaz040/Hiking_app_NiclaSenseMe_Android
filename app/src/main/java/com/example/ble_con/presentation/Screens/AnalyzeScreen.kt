package com.example.ble_con.presentation.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ble_con.ViewModel
import com.example.ble_con.presentation.ShowDataBlock
import com.example.ble_con.presentation.ShowGraph
import com.example.ble_con.presentation.ShowMap
import com.example.ble_con.repository.ViewModelData

@Composable
fun AnalyzeScreen(
    vm: ViewModel = viewModel(),
) {
    // Box used for whitespace
    Spacer(modifier = Modifier.Companion.height(100.dp))
    val scrollState = rememberScrollState()
    Column(
        Modifier.Companion
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        DisplayTitle(ViewModelData.fileData.name)
        ViewModelData.listOfDataInfo.forEach { data ->

            val showGraph = remember { mutableStateOf(false) }
            ShowDataBlock(
                data,
                modifier = Modifier.Companion
                    .width(500.dp)
                    .noRippleClickable{
                       showGraph.value = !showGraph.value
                    }
            ) {
                if (showGraph.value == true)
                    ShowGraph(data)

            }
        }
        ShowMap()
    }
}

@Composable
fun Alert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}