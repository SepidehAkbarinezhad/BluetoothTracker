package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    private val scannedDeviceRepository: ScannedDeviceRepository
) {
     operator fun invoke() {
        printLog("startCollectingScansAndInsertToDb","listTag")
       CoroutineScope(Dispatchers.Main).launch{
           bluetoothRepository.scannedDevicesFlow().collect { deviceList ->
               printLog("collect: $deviceList","listTag")
               scannedDeviceRepository.insertDeviceList(deviceList)
           }
       }

    }
}
