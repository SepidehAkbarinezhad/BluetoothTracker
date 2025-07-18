package com.example.bluetoothtracker.data.repoImpl

import com.example.bluetoothtracker.data.datasource.ScannedDeviceLocalDataSource
import com.example.bluetoothtracker.data.mapper.toDomain
import com.example.bluetoothtracker.data.mapper.toEntity
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScannedDeviceRepositoryImpl(
    private val localDataSource : ScannedDeviceLocalDataSource
) : ScannedDeviceRepository {

    override suspend fun insertDevice(device: BluetoothScanResult) {
        localDataSource.insert(device.toEntity())
    }

    override fun getAllDevices(): Flow<List<BluetoothScanResult>> {
        return localDataSource.getAll().map { list -> list.map { device -> device.toDomain() } }
    }

    override suspend fun deleteAllDevices() {
        localDataSource.deleteAll()
    }
}