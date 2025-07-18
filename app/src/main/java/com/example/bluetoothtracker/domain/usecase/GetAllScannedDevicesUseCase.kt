package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import kotlinx.coroutines.flow.Flow

class GetAllDevicesUseCase(
    private val repository: ScannedDeviceRepository
) {
    operator fun invoke(): Flow<List<BluetoothScanResult>> {
        return repository.getAllDevices()
    }
}