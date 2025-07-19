package com.example.bluetoothtracker.data.mapper

import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.data.Device


fun ScannedDeviceEntity.toDomain(): Device = with(this) {
    Device(
        macAddress = macAddress,
        name = name ?: "",
        rssi = rssi,
        lastSeen = lastSeen
    )
}

fun List<ScannedDeviceEntity>.toDomainList(): List<Device> = this.map {result->result.toDomain()}

