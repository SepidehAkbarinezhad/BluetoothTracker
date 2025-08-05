package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when all requirements are met and scanning can begin
 */
data object AllRequirementsMetState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                if (!action.permissionState) {
                    PermissionDeniedState
                } else {
                    this
                }
            }
            
            is HomeAction.OnBluetoothStateChange -> {
                if (!action.bluetoothState) {
                    BluetoothDisabledState
                } else {
                    this
                }
            }
            
            is HomeAction.OnUpdateLocationServiceState -> {
                if (!action.state) {
                    LocationDisabledState
                } else {
                    this
                }
            }
            
            HomeAction.CheckLocationStatus -> {
                context.emitEvent(HomeEvent.CheckLocationServiceState)
                this
            }
            
            else -> this
        }
    }
    
    override fun getDialogToShow(): DialogType = DialogType.NONE
    
    override fun canStartScanning(): Boolean = true
    
    override fun getRequiredEvent(): HomeEvent? = null
}