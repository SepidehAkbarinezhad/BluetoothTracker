package com.example.bluetoothtracker.presentation.screen.home

sealed interface HomeAction {
    data class ShowPermissionAlertDialog(val show : Boolean) :HomeAction
    data class OnPermissionGrantedChange(val permissionGranted: Boolean) : HomeAction
    data class OnBluetoothStateChange(val bluetoothState: Boolean) : HomeAction
}