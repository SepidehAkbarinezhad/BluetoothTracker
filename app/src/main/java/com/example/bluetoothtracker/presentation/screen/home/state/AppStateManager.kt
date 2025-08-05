package com.example.bluetoothtracker.presentation.screen.home.state

import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.concrete.InitialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the application state and handles state transitions using the State pattern.
 * This replaces the complex conditional logic in the original ViewModel.
 */
class AppStateManager(
    private val context: StateContext
) {
    private val _currentState = MutableStateFlow<AppState>(InitialState)
    val currentState: StateFlow<AppState> = _currentState.asStateFlow()
    
    /**
     * Handle an action and potentially transition to a new state
     */
    fun handleAction(action: HomeAction) {
        val newState = _currentState.value.handleAction(context, action)
        if (newState != _currentState.value) {
            _currentState.value = newState
            
            // Automatically emit required events for the new state
            newState.getRequiredEvent()?.let { event ->
                context.emitEvent(event)
            }
        }
    }
    
    /**
     * Get the dialog type that should be shown for the current state
     */
    fun getCurrentDialogType(): DialogType {
        return _currentState.value.getDialogToShow()
    }
    
    /**
     * Check if scanning can be started in the current state
     */
    fun canStartScanning(): Boolean {
        return _currentState.value.canStartScanning()
    }
    
    /**
     * Force a state transition (useful for initialization)
     */
    fun transitionToState(newState: AppState) {
        _currentState.value = newState
        
        // Automatically emit required events for the new state
        newState.getRequiredEvent()?.let { event ->
            context.emitEvent(event)
        }
    }
}