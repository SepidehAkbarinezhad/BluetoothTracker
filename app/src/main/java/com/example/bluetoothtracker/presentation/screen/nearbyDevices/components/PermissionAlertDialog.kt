package com.example.bluetoothtracker.presentation.screen.nearbyDevices.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bluetoothtracker.R
import com.example.bluetoothtracker.presentation.components.AppButton
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun PermissionAlertDialog(onConfirm: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(text = R.string.permission_alert_dialog, textType = TextType.SubTitle)
                Spacer(modifier = Modifier.height(8.dp))
                AppButton(text = R.string.permission_dialog_btn_label, onClick = onConfirm)
            }
        }

    }
}

