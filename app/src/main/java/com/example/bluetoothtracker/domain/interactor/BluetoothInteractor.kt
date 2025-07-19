package com.example.bluetoothtracker.domain.interactor

import com.example.bluetoothtracker.domain.usecase.GetAllDevicesUseCase
import com.example.bluetoothtracker.domain.usecase.InsertScannedDeviceUseCase
import com.example.bluetoothtracker.domain.usecase.StartScnBluetoothUseCase
import com.example.bluetoothtracker.domain.usecase.StopScnBluetoothUseCase
import jakarta.inject.Inject

data class BluetoothInteractor @Inject constructor(
    val startScnBluetooth: StartScnBluetoothUseCase,
    val stopScnBluetoothUseCase: StopScnBluetoothUseCase,
    val insertScannedDeviceUseCase: InsertScannedDeviceUseCase,
    val getAllDevicesUseCase: GetAllDevicesUseCase
)
