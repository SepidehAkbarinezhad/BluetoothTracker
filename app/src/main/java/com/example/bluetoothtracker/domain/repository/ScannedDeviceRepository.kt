package com.example.bluetoothtracker.domain.repository

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import kotlinx.coroutines.flow.Flow

interface ScannedDeviceRepository {
    suspend fun insertDevice(device: BluetoothScanResult)
    fun getAllDevices(): Flow<List<BluetoothScanResult>>
    suspend fun deleteAllDevices()
}