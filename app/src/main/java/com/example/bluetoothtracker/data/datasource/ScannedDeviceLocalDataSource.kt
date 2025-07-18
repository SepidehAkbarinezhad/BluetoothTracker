package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import kotlinx.coroutines.flow.Flow

interface ScannedDeviceLocalDataSource {
    suspend fun insert(device: ScannedDeviceEntity)
    fun getAll(): Flow<List<ScannedDeviceEntity>>
    suspend fun deleteAll()
}