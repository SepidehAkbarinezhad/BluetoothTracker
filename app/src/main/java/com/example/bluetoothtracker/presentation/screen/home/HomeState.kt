package com.example.bluetoothtracker.presentation.screen.home

import com.example.bluetoothtracker.presentation.model.DeviceUiModel
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType

// UI state
data class HomeState(
    val currentDialogType: DialogType = DialogType.NONE,
    val permissionState: Boolean? = null,
    val bluetoothState: Boolean? = null,
    val locationServicesState: Boolean? = null,
    val onLineDevicesList: List<DeviceUiModel> = emptyList(),
    val offlineDevicesList: List<DeviceUiModel> = emptyList(),
    val onlineDevicesLoading: Boolean = false,
    val emptyOfflineMessage: Boolean = false,
){
    val allRequiredReady: Boolean
        get() = permissionState == true &&
                bluetoothState == true &&
                locationServicesState == true
                
    // Backward compatibility properties for existing UI
    val showPermissionAlertDialog: Boolean
        get() = currentDialogType == DialogType.PERMISSION_ALERT
        
    val showPermissionDeniedDialog: Boolean
        get() = currentDialogType == DialogType.PERMISSION_DENIED
        
    val showBluetoothStateAlertDialog: Boolean
        get() = currentDialogType == DialogType.BLUETOOTH_ALERT
        
    val showLocationServiceAlertDialog: Boolean
        get() = currentDialogType == DialogType.LOCATION_SERVICE_ALERT
}
