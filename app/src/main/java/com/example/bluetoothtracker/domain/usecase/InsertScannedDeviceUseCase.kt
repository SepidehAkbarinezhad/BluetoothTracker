package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTracker
import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.mapper.toEntityList
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
     val tracker: BluetoothDeviceTracker,
     val dao: ScannedDeviceDao
) {
    operator fun invoke(): Job = CoroutineScope(Dispatchers.IO).launch {
        printLog("startCollectingScansAndInsertToDb")
        tracker.scannedDevicesFlow().collect { deviceList ->
            dao.insertAll(deviceList.toEntityList())
        }
    }
}
