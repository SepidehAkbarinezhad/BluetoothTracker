package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import javax.inject.Inject

class StartBluetoothScanUseCase @Inject constructor(val bluetoothRepository: BluetoothRepository) {
    operator fun invoke() {
        println("Bluetooth is on ?: ${bluetoothRepository.isBluetoothEnabled()}")
    }
}