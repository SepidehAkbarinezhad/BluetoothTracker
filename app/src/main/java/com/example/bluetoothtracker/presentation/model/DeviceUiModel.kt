package com.example.bluetoothtracker.presentation.model

data class DeviceUiModel(
    val name: String,
    val macAddress: String,
    val lastSeenFormatted: String,
    val rssi: String
)
