package com.example.bluetoothtracker.data.datasource

import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import com.example.bluetoothtracker.presentation.utils.printLog
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

    override suspend fun insertAll(deviceList: List<ScannedDeviceEntity>) {
        printLog("insertDeviceList ScannedDeviceLocalDataSourceImpl")
        dao.insertAll(deviceList)
    }

    override fun getAll(): Flow<List<ScannedDeviceEntity>> = dao.getAll()

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
