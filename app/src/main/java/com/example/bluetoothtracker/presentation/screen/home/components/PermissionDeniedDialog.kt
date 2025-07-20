package com.example.bluetoothtracker.presentation.screen.home.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.bluetoothtracker.R
import com.example.bluetoothtracker.presentation.components.AppButton
import com.example.bluetoothtracker.presentation.components.AppDialog
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.components.DialogModel
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun PermissionDeniedDialog(onConfirm: () -> Unit) {
    AppDialog(
        dialogModel = DialogModel(
            content = {
                PermissionDeniedDialogContent(
                    onConfirm = onConfirm,
                )
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    )
}

@Composable
fun PermissionDeniedDialogContent(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppText(
            text = R.string.permission_denied_dialog,
            textType = TextType.Body,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppButton(text = R.string.ok_btn_label, onClick = onConfirm)
    }
}

