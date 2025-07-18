package com.example.bluetoothtracker.presentation.screen.nearbyDevices

sealed interface HomeAction {
    data class OnPermissionGrantedChange(val permissionGranted: Boolean) : HomeAction
    data class OnBluetoothStateChange(val bluetoothState: Boolean) : HomeAction
    data object startScan : HomeAction
}