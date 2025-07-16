package com.example.bluetoothtracker.domain.interactor

import com.example.bluetoothtracker.domain.usecase.StartBluetoothScanUseCase
import jakarta.inject.Inject

data class BluetoothInteractor @Inject constructor(
    val startScan: StartBluetoothScanUseCase,
)
