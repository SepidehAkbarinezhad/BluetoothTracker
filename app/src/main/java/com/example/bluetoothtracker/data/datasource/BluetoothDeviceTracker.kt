package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import kotlinx.coroutines.flow.Flow

/*
* This manager wraps BluetoothAdapter, BroadcastReceivers, and platform-specific discovery behavior.
* */
interface BluetoothDeviceTracker {
    fun isBluetoothEnabled(): Boolean
    fun startScan()
    //Stops the Bluetooth discovery process and unregisters any receivers
    fun stopScan()
    fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>>
}