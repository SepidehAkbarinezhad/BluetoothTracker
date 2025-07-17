package com.example.bluetoothtracker.data.model

data class BluetoothScanResult(
    val name: String?,
    val macAddress: String,
    val rssi: Int,
    val timestamp: Long
)
