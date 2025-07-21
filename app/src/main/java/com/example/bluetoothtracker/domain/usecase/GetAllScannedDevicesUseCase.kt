package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllDevicesUseCase @Inject constructor(
    private val repository: ScannedDeviceRepository
) {
    //Return a flow that emits the current timestamp every 30 seconds
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
               // A device is considered online if its lastSeen timestamp is within the last 2 minutes, and this status is updated every 30 seconds
                val isOnline = device.lastSeen > twoMinutesAgo
                device.copy(isOnline = isOnline)
            }
        }
    }

}