package com.example.bluetoothtracker.presentation.screen.home

sealed interface HomeAction {
    data class OnUpdatePermissionState(val permissionState: Boolean) : HomeAction
    data class OnBluetoothStateChange(val bluetoothState: Boolean) : HomeAction
    data class OnUpdateLocationServiceState(val state: Boolean) : HomeAction
    data object OnGrantPermissionConfirmed : HomeAction
    data object OnGrantPermissionCancelled : HomeAction
    data object OnPermissionAlertDialogConfirm : HomeAction
    data object OnPermissionAlertDialogDismiss : HomeAction
    data object OnPermissionDeniedDialogDismiss : HomeAction
    data object OnBluetoothAlertDialogConfirmed : HomeAction
    data object OnBluetoothAlertDialogDismiss : HomeAction
    data object OnLocationAlertDialogConfirmed : HomeAction
    data object OnLocationAlertDialogDismiss : HomeAction
}