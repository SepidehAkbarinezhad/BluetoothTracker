package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllDevicesUseCase @Inject constructor(
    private val repository: ScannedDeviceRepository
) {
    private val timerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())     // ← Emit current timestamp
            delay(30_000)               // ← Wait 30 seconds (30,000 ms)
        }
    }

    operator fun invoke(): Flow<List<Device>> {
        return repository.getAllDevices().combine(timerFlow) { devices, timeForUpdate ->
            val twoMinutesAgo = timeForUpdate - (2 * 60 * 1000)
            devices.map { device ->
                val isOnline = device.lastSeen > twoMinutesAgo
                device.copy(isOnline = isOnline)
            }
        }
    }

}