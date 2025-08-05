package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when permissions are granted and we need to check Bluetooth
 */
data object PermissionGrantedState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            is HomeAction.OnBluetoothStateChange -> {
                if (action.bluetoothState) {
                    BluetoothEnabledState
                } else {
                    BluetoothDisabledState
                }
            }
            
            is HomeAction.OnUpdatePermissionState -> {
                if (!action.permissionState) {
                    PermissionDeniedState
                } else {
                    this
                }
            }
            
            else -> this
        }
    }
    
    override fun getDialogToShow(): DialogType = DialogType.NONE
    
    override fun canStartScanning(): Boolean = false
    
    override fun getRequiredEvent(): HomeEvent = HomeEvent.CheckBluetoothState
}