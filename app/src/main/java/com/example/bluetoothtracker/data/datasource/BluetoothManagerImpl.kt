package com.example.bluetoothtracker.data.datasource

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.content.Context
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.presentation.screen.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber


class BluetoothManagerImpl(private val context: Context) : BluetoothManager {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        bluetoothManager?.adapter
    }

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val _scannedDevices = MutableSharedFlow<BluetoothScanResult>()
    val scannedDevices: SharedFlow<BluetoothScanResult> = _scannedDevices.asSharedFlow()

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
            result?.device?.let { device ->
                printLog(
                    "Device found: ${device.address}, RSSI: ${result.rssi}, Name: ${device.name}"
                )
            }
        }

    }

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    override fun startDiscovery() {
        printLog("startDiscovery")

        // Cancel previous job if any
        scanJob?.cancel()
        scanJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                // Start scanning
                bluetoothLeScanner?.startScan(scanCallback)
                delay(scanPeriod) // scan for 10 seconds

                // Stop scanning
                bluetoothLeScanner?.stopScan(scanCallback)
                delay(waitPeriod) // wait for 5 seconds
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() {
        printLog("stopDiscovery")
        scanJob?.cancel()
        bluetoothLeScanner?.stopScan(scanCallback)
    }


    @SuppressLint("MissingPermission")
    private fun stopScan() {
        try {
            Timber.d("stopScan")
            if (isBluetoothEnabled()) {
                stopDiscovery()
            }
        } catch (e: Exception) {
            Timber.d("${e.message}")
        } finally {

        }
    }
}
