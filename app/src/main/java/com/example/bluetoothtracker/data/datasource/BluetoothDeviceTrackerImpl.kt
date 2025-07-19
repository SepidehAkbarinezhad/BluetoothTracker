package com.example.bluetoothtracker.data.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.content.Context
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber


class BluetoothDeviceTrackerImpl(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?
) :
    BluetoothDeviceTracker {

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val _scannedDevices = MutableSharedFlow<List<BluetoothScanResult>>()
    val scannedDevices: SharedFlow<List<BluetoothScanResult>> = _scannedDevices.asSharedFlow()
    private val scannedDeviceCache = mutableMapOf<String, BluetoothScanResult>()

    private var scanJob: Job? = null
    private val scanPeriod = 10_000L  // 10 seconds scan
    private val waitPeriod = 5_000L   // 5 seconds wait

    private val scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            printLog("Scan error $errorCode")
            stop()
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
        printLog("startDiscovery")

        // Cancel previous job if any
        scanJob?.cancel()
        scanJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                // Start scanning
                bluetoothLeScanner?.startScan(scanCallback)
                delay(scanPeriod) // scan for 10 seconds
                printLog("result: ${scannedDeviceCache.values}")
                // Stop scanning
                bluetoothLeScanner?.stopScan(scanCallback)
                emitForInsertInRoom()
                delay(waitPeriod) // wait for 5 seconds
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        printLog("stopDiscovery")
        scanJob?.cancel()
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private suspend fun emitForInsertInRoom(){
        val resultList = scannedDeviceCache.values.toList()
        printLog("result: $resultList")
        _scannedDevices.emit(resultList)
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
