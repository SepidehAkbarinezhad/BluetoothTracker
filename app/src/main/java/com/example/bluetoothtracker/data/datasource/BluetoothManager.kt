package com.example.bluetoothtracker.data.datasource

/*
* This manager wraps BluetoothAdapter, BroadcastReceivers, and platform-specific discovery behavior.
* */
interface BluetoothManager {
    fun isBluetoothEnabled(): Boolean
    fun startDiscovery()
    //Stops the Bluetooth discovery process and unregisters any receivers
    fun stopDiscovery()
}