package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllDevicesUseCase @Inject constructor(
    private val repository: ScannedDeviceRepository
) {
    operator fun invoke(): Flow<List<BluetoothScanResult>> {
        return repository.getAllDevices()
    }
}