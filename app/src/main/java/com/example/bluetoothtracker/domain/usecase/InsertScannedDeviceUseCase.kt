package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.di.ApplicationScope
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class InsertScannedDeviceUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    private val scannedDeviceRepository: ScannedDeviceRepository
) {
    
    // Use GlobalScope temporarily to test if injection was the issue
    private val appScope: CoroutineScope = GlobalScope
    
    private var collectionJob: Job? = null
    
    operator fun invoke() {
        // Prevent multiple collections
        if (collectionJob?.isActive == true) {
            printLog("Collection already active, skipping", "usecase_debug")
            return
        }
        
        printLog("startCollectingScansAndInsertToDb", "usecase_debug")
        
        // Test the repository connection first
        val flow = bluetoothRepository.scannedDevicesFlow()
        printLog("Successfully got scannedDevicesFlow from repository: $flow", "usecase_debug")
        
        // Launch collection in the application scope to keep it running
        printLog("About to launch collection coroutine", "usecase_debug")
        collectionJob = appScope.launch(Dispatchers.IO) {
            printLog("INSIDE collection coroutine - coroutine started", "usecase_debug")
            try {
                printLog("About to get scannedDevicesFlow", "usecase_debug")
                val flow = bluetoothRepository.scannedDevicesFlow()
                printLog("Got flow: $flow", "usecase_debug")
                
                printLog("About to start collecting - COLLECTOR IS NOW ACTIVE", "usecase_debug")
                flow.collect { deviceList ->
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
                printLog("Exception stacktrace: ${e.stackTrace.contentToString()}", "usecase_debug")
            }
        }
        printLog("Collection coroutine launched, job: $collectionJob", "usecase_debug")
    }
}
