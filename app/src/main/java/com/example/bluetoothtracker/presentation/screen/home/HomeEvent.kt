package com.example.bluetoothtracker.presentation.screen.home

// One-time UI events
sealed class HomeEvent {
    data object CheckBluetoothState : HomeEvent()
    data object CheckLocationServiceState : HomeEvent()
    data object RequestEnableLocationServices : HomeEvent()
    data object RequestBluetoothPermission : HomeEvent()
    data object RequestEnableBluetooth : HomeEvent()
}