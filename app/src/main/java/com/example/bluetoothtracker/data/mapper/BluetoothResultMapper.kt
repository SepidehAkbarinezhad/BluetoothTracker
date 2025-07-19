package com.example.bluetoothtracker.data.mapper

import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import com.example.bluetoothtracker.data.model.BluetoothScanResult

fun BluetoothScanResult.toEntity(): ScannedDeviceEntity = with(this) {
    ScannedDeviceEntity(
        macAddress = macAddress,
        name = name ?: "",
        rssi = rssi,
        lastSeen = lastSeen
    )
}

fun List<BluetoothScanResult>.toEntityList(): List<ScannedDeviceEntity> = this.map {result->result.toEntity()}


