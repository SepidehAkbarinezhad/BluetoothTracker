package com.example.bluetoothtracker.data.mapper

import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import com.example.bluetoothtracker.data.model.BluetoothScanResult


fun ScannedDeviceEntity.toDomain(): BluetoothScanResult = with(this) {
    BluetoothScanResult(
        macAddress = macAddress,
        name = name ?: "",
        rssi = rssi,
        lastSeen = lastSeen
    )
}

fun List<ScannedDeviceEntity>.toDomainList(): List<BluetoothScanResult> = this.map {result->result.toDomain()}

