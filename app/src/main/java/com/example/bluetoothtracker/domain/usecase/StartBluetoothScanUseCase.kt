package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import javax.inject.Inject

class StartBluetoothScanUseCase @Inject constructor(val bluetoothRepository: BluetoothRepository) {
    operator fun invoke() {
        printLog("Bluetooth is on ?: ${bluetoothRepository.isBluetoothEnabled()}")
        if (bluetoothRepository.isBluetoothEnabled()) {
            bluetoothRepository.startScan()
        }
    }
}