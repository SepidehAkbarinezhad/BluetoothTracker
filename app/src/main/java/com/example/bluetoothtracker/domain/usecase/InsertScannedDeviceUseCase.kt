package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.data.model.BluetoothScanResult
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository

class InsertScannedDeviceUseCase(
    private val repository: ScannedDeviceRepository
) {
    suspend operator fun invoke(device: BluetoothScanResult) {
        repository.insertDevice(device)
    }
}