package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * Initial state when the app starts and permissions haven't been checked yet
 */
data object InitialState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                if (action.permissionState) {
                    // Permissions granted, check Bluetooth next
                    context.emitEvent(HomeEvent.CheckBluetoothState)
                    PermissionGrantedState
                } else {
                    // Permissions denied, show alert
                    PermissionDeniedState
                }
            }
            
            HomeAction.OnPermissionAlertDialogConfirm -> {
                context.emitEvent(HomeEvent.RequestBluetoothPermission)
                this
            }
            
            HomeAction.OnPermissionAlertDialogDismiss -> this
            
            else -> this
        }
    }
    
    override fun getDialogToShow(): DialogType = DialogType.PERMISSION_ALERT
    
    override fun canStartScanning(): Boolean = false
    
    override fun getRequiredEvent(): HomeEvent = HomeEvent.RequestBluetoothPermission
}