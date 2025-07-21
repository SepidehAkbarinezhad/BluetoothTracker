package com.example.bluetoothtracker.domain.repository

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.data.Device
import kotlinx.coroutines.flow.Flow

interface ScannedDeviceRepository {
    suspend fun insertDevice(device: BluetoothScanResult)
    suspend fun insertDeviceList(deviceList: List<BluetoothScanResult>)
    fun getAllDevices(): Flow<List<Device>>
    suspend fun deleteAllDevices()
}