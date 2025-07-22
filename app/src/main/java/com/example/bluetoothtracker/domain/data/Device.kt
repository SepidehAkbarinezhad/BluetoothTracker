package com.example.bluetoothtracker.domain.data

data class Device(
    val name: String,
    val macAddress: String,
    val rssi: Int,
    val lastSeen: Long,
    val isOnline: Boolean=false
)
