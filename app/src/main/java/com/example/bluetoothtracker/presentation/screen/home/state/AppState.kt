package com.example.bluetoothtracker.presentation.screen.home.state

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent

/**
 * Base interface for all application states in the State pattern.
 * Each concrete state handles specific actions and determines what dialogs to show.
 */
sealed interface AppState {
    /**
     * Handle an action in the current state and potentially transition to a new state
     */
    fun handleAction(context: StateContext, action: HomeAction): AppState
    
    /**
     * Determine what dialog should be shown in this state
     */
    fun getDialogToShow(): DialogType
    
    /**
     * Check if scanning can be started in this state
     */
    fun canStartScanning(): Boolean
    
    /**
     * Get the next required action/event for this state
     */
    fun getRequiredEvent(): HomeEvent?
}

/**
 * Represents the different types of dialogs that can be shown
 */
enum class DialogType {
    NONE,
    PERMISSION_ALERT,
    PERMISSION_DENIED,
    BLUETOOTH_ALERT,
    LOCATION_SERVICE_ALERT
}