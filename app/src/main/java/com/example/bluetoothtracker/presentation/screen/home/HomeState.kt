package com.example.bluetoothtracker.presentation.screen.home

import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.presentation.model.DeviceUiModel

// UI state
data class HomeState(
    val showPermissionAlertDialog: Boolean = false,
    val showPermissionDeniedDialog: Boolean = false,
    val showBluetoothStateAlertDialog: Boolean = false,
    val showLocationServiceAlertDialog: Boolean = false,
    val permissionState: Boolean? = null,
    val bluetoothState: Boolean? = null,
    val locationServicesState: Boolean? = null,
    val onLineDevicesList: List<DeviceUiModel> = emptyList(),
    val offlineDevicesList: List<DeviceUiModel> = emptyList()
)
