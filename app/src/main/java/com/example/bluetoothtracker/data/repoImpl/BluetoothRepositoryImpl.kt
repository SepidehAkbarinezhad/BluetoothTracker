package com.example.bluetoothtracker.data.repoImpl

import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTracker
import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class BluetoothRepositoryImpl @Inject constructor(private val bluetoothManager: BluetoothDeviceTracker) :
    BluetoothRepository {

    override fun isBluetoothEnabled(): Boolean = bluetoothManager.isBluetoothEnabled()

    override fun startScan() {
        bluetoothManager.startScan()
    }

    override fun stopScan() {
        bluetoothManager.stopScan()
    }

    override fun scannedDevicesFlow(): Flow<List<BluetoothScanResult>> =
        bluetoothManager.scannedDevicesFlow()

}