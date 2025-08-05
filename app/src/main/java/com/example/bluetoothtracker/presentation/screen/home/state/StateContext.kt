package com.example.bluetoothtracker.presentation.screen.home.state

import com.example.bluetoothtracker.presentation.screen.home.HomeEvent

/**
 * Context interface that provides access to shared functionality for all states.
 * This allows states to emit events and access current state information.
 */
interface StateContext {
    /**
     * Emit an event to the UI
     */
    fun emitEvent(event: HomeEvent)
    
    /**
     * Get current permission state
     */
    fun getPermissionState(): Boolean?
    
    /**
     * Get current Bluetooth state
     */
    fun getBluetoothState(): Boolean?
    
    /**
     * Get current location services state
     */
    fun getLocationServicesState(): Boolean?
}