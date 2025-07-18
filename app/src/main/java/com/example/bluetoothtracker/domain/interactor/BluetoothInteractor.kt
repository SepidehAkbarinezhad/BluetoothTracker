package com.example.bluetoothtracker.domain.interactor

import com.example.bluetoothtracker.domain.usecase.StartScnBluetoothUseCase
import jakarta.inject.Inject

data class BluetoothInteractor @Inject constructor(
    val startScnBluetooth: StartScnBluetoothUseCase,
)
