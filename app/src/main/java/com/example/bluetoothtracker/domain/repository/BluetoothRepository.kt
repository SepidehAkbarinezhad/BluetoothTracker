package com.example.bluetoothtracker.domain.repository

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.data.Device
import kotlinx.coroutines.flow.Flow

/*
* A clean, platform-independent interface for accessing Bluetooth-related use cases.
* This repository abstracts the logic for discovering and interacting with nearby Bluetooth devices
* without exposing any platform(android) framework dependencies
* */
interface BluetoothRepository {
    fun isBluetoothEnabled(): Boolean
    fun startScan()
    fun stopScan()
    fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>>
}