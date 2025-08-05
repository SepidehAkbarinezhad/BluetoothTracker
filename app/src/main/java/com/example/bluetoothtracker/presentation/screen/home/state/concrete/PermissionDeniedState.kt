package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when permissions are denied by the user
 */
data object PermissionDeniedState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
            HomeAction.OnGrantPermissionConfirmed -> {
                context.emitEvent(HomeEvent.CheckBluetoothState)
                PermissionGrantedState
            }
            
            HomeAction.OnGrantPermissionCancelled -> {
                PermissionPermanentlyDeniedState
            }
            
            HomeAction.OnPermissionDeniedDialogDismiss -> this
            
            is HomeAction.OnUpdatePermissionState -> {
                if (action.permissionState) {
                    context.emitEvent(HomeEvent.CheckBluetoothState)
                    PermissionGrantedState
                } else {
                    this
                }
            }
            
            else -> this
        }
    }
    
    override fun getDialogToShow(): DialogType = DialogType.PERMISSION_ALERT
    
    override fun canStartScanning(): Boolean = false
    
    override fun getRequiredEvent(): HomeEvent = HomeEvent.RequestBluetoothPermission
}