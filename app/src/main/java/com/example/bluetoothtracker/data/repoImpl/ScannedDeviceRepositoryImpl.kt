package com.example.bluetoothtracker.data.repoImpl

import com.example.bluetoothtracker.data.datasource.ScannedDeviceLocalDataSource
import com.example.bluetoothtracker.data.mapper.toDomainList
import com.example.bluetoothtracker.data.mapper.toEntity
import com.example.bluetoothtracker.data.mapper.toEntityList
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannedDeviceRepositoryImpl @Inject constructor(
    private val localDataSource : ScannedDeviceLocalDataSource
) : ScannedDeviceRepository {

    override suspend fun insertDevice(device: BluetoothScanResult) {
        localDataSource.insert(device.toEntity())
    }

    override suspend fun insertDeviceList(deviceList: List<BluetoothScanResult>) {
        localDataSource.insertAll(deviceList.toEntityList())
    }

    override fun getAllDevices(): Flow<List<Device>> {
        return localDataSource.getAll().map {list->list.toDomainList() }
    }

    override suspend fun deleteAllDevices() {
        localDataSource.deleteAll()
    }
}