package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun DeviceListItem(
    modifier: Modifier = Modifier,
    device: Device,
) {

    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        with(device) {
            Column(
                Modifier
                    .weight(.8f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppText(text = macAddress, textType = TextType.SubTitle)
                AppText(
                    text = name,
                    textType = TextType.Body

                )
                AppText(
                    text = "rssi: $rssi",
                    textType = TextType.Body

                )

            }

        }
    }

}