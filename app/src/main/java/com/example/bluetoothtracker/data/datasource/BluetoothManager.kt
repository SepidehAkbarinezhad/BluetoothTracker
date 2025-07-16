package com.example.bluetoothtracker.data.datasource

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

/*
* This manager wraps BluetoothAdapter, BroadcastReceivers, and platform-specific discovery behavior.
* */
interface BluetoothManager {
    fun isBluetoothEnabled(): Boolean
    fun startDiscovery()
    //Stops the Bluetooth discovery process and unregisters any receivers
    fun stopDiscovery()
    fun observeFoundDevices(): Flow<List<BluetoothDevice>>
}