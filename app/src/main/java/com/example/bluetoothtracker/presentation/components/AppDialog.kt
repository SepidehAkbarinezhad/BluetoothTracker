package com.example.bluetoothtracker.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppDialog(
    dialogModel: DialogModel,
) {
    with(dialogModel) {
        Dialog(properties = dialogModel.properties,onDismissRequest = onDismissRequest) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    content()
                }
            }
        }
    }

}

data class DialogModel(
    val content: @Composable () -> Unit,
    val onDismissRequest: () -> Unit = {},
    val properties: DialogProperties = DialogProperties()
)