package com.example.bluetoothtracker.data.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.di.ApplicationScope
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothDeviceTrackerImpl @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val bluetoothAdapter: BluetoothAdapter?,
) : BluetoothDeviceTracker {

    private val _messages = MutableSharedFlow<String>()

    override fun messageFlow(): Flow<String> = _messages
    private val bluetoothScanner = bluetoothAdapter?.bluetoothLeScanner

    private val _scannedDevices = MutableSharedFlow<List<BluetoothScanResult>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val scannedDevices: SharedFlow<List<BluetoothScanResult>> = _scannedDevices
    private val scannedDeviceCache = mutableMapOf<String, BluetoothScanResult>()

    private var scanJob: Job? = null
    private val scanPeriod = 10_000L  // 10 seconds scan
    private val waitPeriod = 5_000L   // 5 seconds wait


    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            printLog("Scan error $errorCode")
            stopScan()
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let { scanResult ->
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
        // Cancel previous job if any
        scanJob?.cancel()
        scanJob = appScope.launch {
            while (isActive) {
                // Start scanning
                bluetoothScanner?.startScan(scanCallback)
                delay(scanPeriod) // scan for 10 seconds
                // Stop scanning
                bluetoothScanner?.stopScan(scanCallback)
                emitForInsertInRoom()
                delay(waitPeriod) // wait for 5 seconds
            }
        }
    }


    private suspend fun emitForInsertInRoom() {
        val resultList = scannedDeviceCache.values.toList()
        _scannedDevices.emit(resultList)
        scannedDeviceCache.clear()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        scanJob?.cancel()
        try {
            bluetoothScanner?.stopScan(scanCallback)
        }catch (e:Exception){
            emitMessage("exception: ${e.message}")
        }

    }


    override fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>> = scannedDevices

    private fun emitMessage(message: String) {
        appScope.launch {
            _messages.emit(message)
        }
    }
}
