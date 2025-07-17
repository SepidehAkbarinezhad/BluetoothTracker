package com.example.bluetoothtracker.domain.data

data class Device(
    val name: String?,
    val macAddress: String,
    val rssi: Int,
    val lastSeen: Long,
) {
    val isOnline: Boolean
        get() = System.currentTimeMillis() - lastSeen <= 2 * 60 * 1000  // 2 minutes
}
