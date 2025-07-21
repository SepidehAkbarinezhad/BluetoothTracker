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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.R
import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun DeviceListItem(
    modifier: Modifier = Modifier,
    device: Device,
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
                AppText(text = stringResource(R.string.mac_address_label,macAddress) , textType = TextType.SubTitle)
                AppText(
                    text = stringResource(R.string.name_label,name?:"Unknown"),
                    textType = TextType.Body

                )
                AppText(
                    text = stringResource(R.string.rssi_label,rssi),
                    textType = TextType.Body
                )

            }

        }
    }

}

@Preview
@Composable
private fun DeviceListItemPrev() {
    DeviceListItem(device = Device(name = "gav", macAddress = "gav", rssi = 1, lastSeen = 1))
}