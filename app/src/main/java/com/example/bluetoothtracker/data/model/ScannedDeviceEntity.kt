package com.example.bluetoothtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bluetooth_devices")
data class ScannedDeviceEntity(
    @PrimaryKey val macAddress: String,
    val name: String,
    val rssi: Int,
    val lastSeen: Long
)
