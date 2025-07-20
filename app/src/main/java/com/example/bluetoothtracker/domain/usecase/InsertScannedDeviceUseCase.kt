package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.di.ApplicationScope
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val bluetoothRepository: BluetoothRepository,
    private val scannedDeviceRepository: ScannedDeviceRepository
) {
    operator fun invoke() {
        printLog("startCollectingScansAndInsertToDb", "usecase_debug")
        
        // Test the repository connection first
        val flow = bluetoothRepository.scannedDevicesFlow()
        printLog("Successfully got scannedDevicesFlow from repository: $flow", "usecase_debug")
        
        // Launch collection in the application scope to keep it running
        appScope.launch(Dispatchers.IO) {
            try {
                printLog("Starting to collect from scannedDevicesFlow", "usecase_debug")
                bluetoothRepository.scannedDevicesFlow().collect { deviceList ->
                    printLog("2 collect: ${deviceList.size} devices", "usecase_debug")
                    printLog("2 collect devices: $deviceList", "usecase_debug")
                    if (deviceList.isNotEmpty()) {
                        scannedDeviceRepository.insertDeviceList(deviceList)
                        printLog("Successfully inserted ${deviceList.size} devices to database", "usecase_debug")
                    } else {
                        printLog("Received empty device list from SharedFlow", "usecase_debug")
                    }
                }
            } catch (e: Exception) {
                printLog("Error in collection: ${e.message}", "usecase_debug")
            }
        }
    }
}
