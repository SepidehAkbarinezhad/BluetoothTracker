package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.datasource.room.BScannedDeviceDao
import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import kotlinx.coroutines.flow.Flow

class ScannedDeviceLocalDataSourceImpl(
    private val dao: BScannedDeviceDao
) : ScannedDeviceLocalDataSource {

    override suspend fun insert(device: ScannedDeviceEntity) {
        dao.insert(device)
    }

    override fun getAll(): Flow<List<ScannedDeviceEntity>> = dao.getAll()

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
