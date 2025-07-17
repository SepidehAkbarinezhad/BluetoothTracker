package com.example.bluetoothtracker.presentation.screen.nearbyDevices

interface HomeAction {
    data class OnBluetoothStateChange(val bluetoothState: Boolean) : HomeAction
}