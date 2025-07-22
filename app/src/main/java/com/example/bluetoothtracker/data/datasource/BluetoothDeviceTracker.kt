package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import kotlinx.coroutines.flow.Flow


interface BluetoothDeviceTracker {
    fun messageFlow(): Flow<String>
    fun isBluetoothEnabled(): Boolean
    fun startScan()
    fun stopScan()
    fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>>
}