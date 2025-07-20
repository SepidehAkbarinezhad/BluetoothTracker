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
        printLog("=== USECASE INVOKE STARTED ===", "checkDebug")
        
        // Prevent multiple collections
        if (collectionJob?.isActive == true) {
            printLog("Collection already active, skipping", "checkDebug")
            return
        }
        
        printLog("startCollectingScansAndInsertToDb2 ${appScope.isActive}", "checkDebug")
        
        // Test the repository connection first
        val flow = bluetoothRepository.scannedDevicesFlow()
        printLog("Successfully got scannedDevicesFlow from repository: $flow", "checkDebug")
        
        // Launch collection in the application scope to keep it running
        printLog("About to launch collection coroutine", "checkDebug")
        collectionJob = appScope.launch(Dispatchers.IO) {
            printLog("🚀 INSIDE COROUTINE - COROUTINE STARTED!", "checkDebug")
            try {
                printLog("About to get scannedDevicesFlow", "checkDebug")
                val flowInside = bluetoothRepository.scannedDevicesFlow()
                printLog("Got flow inside coroutine: $flowInside", "checkDebug")
                
                printLog("🎯 ABOUT TO START COLLECTING - COLLECTOR IS NOW ACTIVE!", "checkDebug")
                flowInside.collect { deviceList ->
                    printLog("🎉 COLLECT SUCCESS: ${deviceList.size} devices", "checkDebug")
                    printLog("🎉 COLLECT DEVICES: $deviceList", "checkDebug")
                    if (deviceList.isNotEmpty()) {
                        scannedDeviceRepository.insertDeviceList(deviceList)
                        printLog("✅ Successfully inserted ${deviceList.size} devices to database", "checkDebug")
                    } else {
                        printLog("📭 Received empty device list from SharedFlow", "checkDebug")
                    }
                }
            } catch (e: Exception) {
                printLog("❌ Error in collection: ${e.message}", "checkDebug")
                printLog("❌ Exception stacktrace: ${e.stackTrace.contentToString()}", "checkDebug")
            }
        }
        printLog("Collection coroutine launched, job: $collectionJob", "checkDebug")
        printLog("=== USECASE INVOKE FINISHED ===", "checkDebug")
    }
}
