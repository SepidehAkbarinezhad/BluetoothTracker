package com.example.bluetoothtracker.presentation.screen.home

import com.example.bluetoothtracker.domain.data.Device

// UI state
data class HomeState(
    val showPermissionAlertDialog : Boolean = false,
    val showPermissionDeniedDialog : Boolean = false,
    val showLocationServicesDialog : Boolean = false,
    val permissionGranted : Boolean? = null,
    val bluetoothState : Boolean? = null,
    val locationServicesEnabled : Boolean? = null,
    val onLineDevicesList : List<Device> = emptyList(),
    val offlineDevicesList : List<Device> = emptyList()
)
