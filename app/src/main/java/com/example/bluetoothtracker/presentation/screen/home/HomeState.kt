package com.example.bluetoothtracker.presentation.screen.home

import com.example.bluetoothtracker.domain.data.Device

// UI state
data class HomeState(
    val showPermissionAlertDialog: Boolean = false,
    val showPermissionDeniedDialog: Boolean = false,
    val showBluetoothStateAlertDialog: Boolean = false,
    val showLocationServiceAlertDialog: Boolean = false,
    val permissionState: Boolean? = null,
    val bluetoothState: Boolean? = null,
    val locationServicesState: Boolean? = null,
    val onLineDevicesList: List<Device> = emptyList(),
    val offlineDevicesList: List<Device> = emptyList()
)
