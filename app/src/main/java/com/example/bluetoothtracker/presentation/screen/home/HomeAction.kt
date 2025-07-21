package com.example.bluetoothtracker.presentation.screen.home

sealed interface HomeAction {
    data class ShowPermissionAlertDialog(val show: Boolean) : HomeAction
    data class ShowPermissionDeniedDialog(val show: Boolean) : HomeAction
    data class ShowBluetoothAlertDialog(val show: Boolean) : HomeAction
    data class UpdatePermissionState(val permissionState: Boolean) : HomeAction
    data object OnGrantPermissionConfirmed : HomeAction
    data object OnGrantPermissionCancelled : HomeAction
    data class BluetoothStateChange(val bluetoothState: Boolean) : HomeAction
    data object OnPermissionAlertDialogConfirm : HomeAction
    data object OnPermissionAlertDialogDismiss : HomeAction
    data object OnPermissionDeniedDialogDismiss : HomeAction
    data object OnBluetoothAlertDialogConfirmed : HomeAction
    data object OnBluetoothAlertDialogDismiss : HomeAction
}