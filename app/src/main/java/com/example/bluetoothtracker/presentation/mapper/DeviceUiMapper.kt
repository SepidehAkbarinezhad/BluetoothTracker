package com.example.bluetoothtracker.presentation.mapper

import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.presentation.model.DeviceUiModel
import com.example.bluetoothtracker.presentation.utils.toReadableDate

fun Device.toDeviceUiModel(): DeviceUiModel {
    return DeviceUiModel(
        name = name.ifEmpty { "Known" },
        macAddress = macAddress,
        lastSeenFormatted = lastSeen.toReadableDate(),
        rssi = rssi.toString()
    )
}

fun List<Device>.toDeviceUiList(): List<DeviceUiModel> {
    return this.map { device -> device.toDeviceUiModel() }
}