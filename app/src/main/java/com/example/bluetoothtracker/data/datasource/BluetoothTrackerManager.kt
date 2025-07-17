package com.example.bluetoothtracker.data.datasource

/*
* This manager wraps BluetoothAdapter, BroadcastReceivers, and platform-specific discovery behavior.
* */
interface BluetoothTrackerManager {
    fun isBluetoothEnabled(): Boolean
    fun startScan()
    //Stops the Bluetooth discovery process and unregisters any receivers
    fun stopScan()
}