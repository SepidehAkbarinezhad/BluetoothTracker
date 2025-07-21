package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.R
import com.example.bluetoothtracker.presentation.components.AppRowItem
import com.example.bluetoothtracker.presentation.model.DeviceUiModel

@Composable
fun DeviceListItem(
    modifier: Modifier = Modifier,
    device: DeviceUiModel,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, )
    ) {
        with(device) {
            Column(
                Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppRowItem(title = R.string.mac_address_label, value = macAddress)
                AppRowItem(title = R.string.name_label, value = name)
                AppRowItem(title = R.string.last_seen_label, value = lastSeenFormatted)
                AppRowItem(title = R.string.rssi_label, value = rssi)
            }

        }
    }

}
