package com.example.bluetoothtracker.presentation.screen.home.state.concrete

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.AppState
import com.example.bluetoothtracker.presentation.screen.home.state.DialogType
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext

/**
 * State when permissions are permanently denied
 */
data object PermissionPermanentlyDeniedState : AppState {
    
    override fun handleAction(context: StateContext, action: HomeAction): AppState {
        return when (action) {
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
    
    override fun getDialogToShow(): DialogType = DialogType.PERMISSION_DENIED
    
    override fun canStartScanning(): Boolean = false
    
    override fun getRequiredEvent(): HomeEvent? = null
}