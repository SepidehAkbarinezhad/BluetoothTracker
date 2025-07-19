package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllDevicesUseCase @Inject constructor(
    private val repository: ScannedDeviceRepository
) {
    operator fun invoke(): Flow<List<Device>> {
        printLog("GetAllDevicesUseCase invoke","listTag")
        return repository.getAllDevices()
    }
}