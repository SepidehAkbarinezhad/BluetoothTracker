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
import com.example.bluetoothtracker.presentation.components.AppDialog
import com.example.bluetoothtracker.presentation.components.AppRowButtons
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.components.DialogModel
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun BluetoothStateAlertDialog(onConfirm: () -> Unit, onDismissRequest: () -> Unit) {
    AppDialog(
        dialogModel = DialogModel(
            content = {
                BluetoothStateAlertContent(
                    onConfirm = onConfirm,
                    onDismissRequest = onDismissRequest
                )
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    )
}

@Composable
fun BluetoothStateAlertContent(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppText(
            modifier = Modifier.padding(4.dp),
            text = R.string.Bluetooth_state_title_dialog,
            textType = TextType.SubTitle
        )
        AppText(
            text = R.string.bluetooth_state_alert_dialog,
            textType = TextType.Body,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppRowButtons(
            firstButtonTitle = R.string.turn_on_btn_label,
            onFirstButtonClick =
            onConfirm,
            secondButtonTitle = R.string.dismiss_permission_btn_label,
            onSecondButtonClick = onDismissRequest
        )

    }
}


