package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import javax.inject.Inject

class StopScnBluetoothUseCase @Inject constructor(val bluetoothRepository: BluetoothRepository) {
    operator fun invoke() {
            bluetoothRepository.stopScan()
    }
}