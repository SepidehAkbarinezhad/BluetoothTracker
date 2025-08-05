package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when location services are disabled
 */
data object LocationDisabledState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            is HomeAction.OnUpdateLocationServiceState -> {
                if (action.state) {
                    AllRequirementsMetState
                } else {
                    this
                }
            }
            
            HomeAction.OnLocationAlertDialogConfirmed -> {
                context.emitEvent(HomeEvent.RequestEnableLocationServices)
                this
            }
            
            HomeAction.OnLocationAlertDialogDismiss -> this
            
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
    
    override fun getDialogToShow(): DialogType = DialogType.LOCATION_SERVICE_ALERT
    
    override fun canStartScanning(): Boolean = false
    
    override fun getRequiredEvent(): HomeEvent = HomeEvent.RequestEnableLocationServices
}