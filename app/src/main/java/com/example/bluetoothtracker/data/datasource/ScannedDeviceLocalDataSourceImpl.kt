package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannedDeviceLocalDataSourceImpl @Inject constructor(
    private val dao: ScannedDeviceDao
) : ScannedDeviceLocalDataSource {

    override suspend fun insert(device: ScannedDeviceEntity) {
        dao.insert(device)
    }

    override suspend fun insertAll(devices: List<ScannedDeviceEntity>) {
        dao.insertAll(devices)
    }

    override fun getAll(): Flow<List<ScannedDeviceEntity>> = dao.getAll()

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
