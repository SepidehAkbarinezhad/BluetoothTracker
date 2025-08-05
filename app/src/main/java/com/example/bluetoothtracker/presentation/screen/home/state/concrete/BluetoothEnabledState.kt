package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when Bluetooth is enabled and we need to check location services
 */
data object BluetoothEnabledState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            is HomeAction.OnUpdateLocationServiceState -> {
                if (action.state) {
                    AllRequirementsMetState
                } else {
                    LocationDisabledState
                }
            }
            
            HomeAction.CheckLocationStatus -> {
                context.emitEvent(HomeEvent.CheckLocationServiceState)
                this
            }
            
            is HomeAction.OnBluetoothStateChange -> {
                if (!action.bluetoothState) {
                    BluetoothDisabledState
                } else {
                    this
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
    
    override fun getRequiredEvent(): HomeEvent = HomeEvent.CheckLocationServiceState
}