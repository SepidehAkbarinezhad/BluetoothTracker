package com.example.bluetoothtracker.presentation.screen.home

// UI state
data class HomeState(
    val showPermissionAlertDialog : Boolean = false,
    val showPermissionDeniedDialog : Boolean = false,
    val permissionGranted : Boolean? = null,
    val bluetoothState : Boolean? = null,
)
