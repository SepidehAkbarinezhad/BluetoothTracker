package com.example.bluetoothtracker.data.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanCallback.*
import android.content.Context
import com.example.bluetoothtracker.data.mapper.toEntityList
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.di.ApplicationScope
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber


@Singleton
class BluetoothDeviceTrackerImpl @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val scannedDeviceRepository : ScannedDeviceRepository
) : BluetoothDeviceTracker {

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val _scannedDevices = MutableSharedFlow<List<BluetoothScanResult>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val scannedDevices: SharedFlow<List<BluetoothScanResult>> = _scannedDevices.asSharedFlow()
    private val scannedDeviceCache = mutableMapOf<String, BluetoothScanResult>()

    private var scanJob: Job? = null
    private val scanPeriod = 10_000L  // 10 seconds scan
    private val waitPeriod = 5_000L   // 5 seconds wait


    private val scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            printLog("❌ Scan FAILED with error code: $errorCode", "scanDebug")
            when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> printLog("SCAN_FAILED_ALREADY_STARTED", "scanDebug")
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> printLog("SCAN_FAILED_APPLICATION_REGISTRATION_FAILED", "scanDebug")
                SCAN_FAILED_FEATURE_UNSUPPORTED -> printLog("SCAN_FAILED_FEATURE_UNSUPPORTED", "scanDebug")
                SCAN_FAILED_INTERNAL_ERROR -> printLog("SCAN_FAILED_INTERNAL_ERROR", "scanDebug")
                SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> printLog("SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES", "scanDebug")
            }
            stop()
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
            printLog("✅ onScanResult called!", "scanDebug")
            super.onScanResult(callbackType, result)
            result?.let { scanResult ->
                printLog("📱 Found device: ${scanResult.device.name ?: "Unknown"} - ${scanResult.device.address} - RSSI: ${scanResult.rssi}", "scanDebug")

                val mac = scanResult.device.address ?: return
                val device = BluetoothScanResult(
                    name = scanResult.device.name.orEmpty(),
                    macAddress = mac,
                    rssi = scanResult.rssi,
                    lastSeen = System.currentTimeMillis()
                )

                /*
                * Add or update the scanned device in the cache using MAC as the unique key.
                * This avoids duplicates and ensures we always keep the latest scan info.
                */
                scannedDeviceCache[mac] = device
            }
        }

    }

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    override fun startScan() {
        printLog("🚀 Starting Bluetooth LE scan...", "scanDebug")
        
        // Check if Bluetooth is enabled
        if (bluetoothAdapter?.isEnabled != true) {
            printLog("❌ Bluetooth is not enabled!", "scanDebug")
            return
        }
        
        // Check if scanner is available
        if (bluetoothLeScanner == null) {
            printLog("❌ BluetoothLeScanner is null!", "scanDebug")
            return
        }
        
        printLog("✅ Bluetooth enabled, scanner available", "scanDebug")
        
        // Cancel previous job if any
        scanJob?.cancel()
        
        scanJob = appScope.launch {
            // Wait 5 seconds to ensure collector is definitely running
            printLog("⏳ Waiting 5 seconds to ensure collector is ready...", "scanDebug")
            delay(5000)
            printLog("✅ 5 seconds passed, starting actual scanning now", "scanDebug")
            
            while (isActive) {
                // Start scanning
                printLog("🔍 Starting BLE scan...", "scanDebug")
                bluetoothLeScanner?.startScan(scanCallback)
                delay(scanPeriod) // scan for 10 seconds
                printLog("🛑 Stopping BLE scan...", "scanDebug")
                printLog("📊 Scan results: ${scannedDeviceCache.size} devices found", "scanDebug")
                printLog("📋 Device list: ${scannedDeviceCache.values}", "scanDebug")
                // Stop scanning
                bluetoothLeScanner?.stopScan(scanCallback)
                emitForInsertInRoom()
                delay(waitPeriod) // wait for 5 seconds
            }
        }
    }


    private  suspend fun emitForInsertInRoom(){
        val resultList = scannedDeviceCache.values.toList()
        printLog("1 emitForInsertInRoom result: ${resultList.size}","uicheck")
        printLog("1 emitForInsertInRoom : $resultList","uicheck")
        
        // Emit to the SharedFlow so UseCase can collect and insert to database
        printLog("About to emit to SharedFlow with ${resultList.size} devices", "emission_debug")
        _scannedDevices.emit(resultList)
        printLog("Successfully emitted to SharedFlow", "emission_debug")
        
        // Comment out direct database insertion - let the UseCase handle it
        // scannedDeviceRepository.insertDeviceList(resultList)
        scannedDeviceCache.clear()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        printLog("stopDiscovery")
        scanJob?.cancel()
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    override fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>> = scannedDevices

    @SuppressLint("MissingPermission")
    private fun stop() {
        try {
            Timber.d("stopScan")
            if (isBluetoothEnabled()) {
                stopScan()
            }
        } catch (e: Exception) {
            Timber.d("${e.message}")
        } finally {

        }
    }
}
