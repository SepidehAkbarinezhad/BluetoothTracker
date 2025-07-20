package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.di.ApplicationScope
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val bluetoothRepository: BluetoothRepository,
    private val scannedDeviceRepository: ScannedDeviceRepository
) {
    operator fun invoke() {
        printLog("startCollectingScansAndInsertToDb1", "checkDebug")
        appScope.launch(Dispatchers.IO) {
            printLog("startCollectingScansAndInsertToDb2 ${appScope.isActive}", "checkDebug")
            bluetoothRepository.scannedDevicesFlow().collect { deviceList ->
                printLog("collect: $deviceList", "checkDebug")
                scannedDeviceRepository.insertDeviceList(deviceList)
            }

        }.invokeOnCompletion { handler-> printLog("handler ${handler==null}  ${handler?.message}") }

    }
}
