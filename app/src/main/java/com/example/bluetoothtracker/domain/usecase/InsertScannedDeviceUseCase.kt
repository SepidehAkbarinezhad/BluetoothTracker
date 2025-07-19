package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.mapper.toEntityList
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.presentation.utils.printLog

import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    private val dao: ScannedDeviceDao
) {
   suspend  operator fun invoke() {
        printLog("startCollectingScansAndInsertToDb")
       bluetoothRepository.scannedDevicesFlow().collect { deviceList ->
            printLog("collect: $deviceList")
            dao.insertAll(deviceList.toEntityList())
        }
    }
}
