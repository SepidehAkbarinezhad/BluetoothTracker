package com.example.bluetoothtracker.presentation.screen.home.state

import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.utils.printLog

/**
 * Context class for the Scan Flow State Machine
 * This class manages the current state and delegates operations to the appropriate handlers
 */
class ScanFlowContext(
    private val eventSender: (HomeEvent) -> Unit,
    private val bluetoothInteractor: BluetoothInteractor,
    private val dialogController: DialogController
) {
    private var currentState: ScanFlowState = IdleState
    private var isBluetoothEnabled: Boolean = false
    private var isLocationEnabled: Boolean = false
    
    // State management
    fun setState(newState: ScanFlowState) {
        printLog("ScanFlow: ${currentState.getStateName()} -> ${newState.getStateName()}")
        currentState = newState
    }
    
    fun getCurrentState(): ScanFlowState = currentState
    
    // Public interface for external events
    fun startScan() {
        printLog("ScanFlow: startScan() in ${currentState.getStateName()}")
        currentState.handleStartScan(this)
    }
    
    fun stopScan() {
        printLog("ScanFlow: stopScan() in ${currentState.getStateName()}")
        currentState.handleStopScan(this)
    }
    
    fun onPermissionResult(granted: Boolean) {
        printLog("ScanFlow: onPermissionResult($granted) in ${currentState.getStateName()}")
        currentState.handlePermissionResult(this, granted)
    }
    
    fun onBluetoothStateChange(enabled: Boolean) {
        printLog("ScanFlow: onBluetoothStateChange($enabled) in ${currentState.getStateName()}")
        currentState.handleBluetoothStateChange(this, enabled)
    }
    
    fun onLocationStateChange(enabled: Boolean) {
        printLog("ScanFlow: onLocationStateChange($enabled) in ${currentState.getStateName()}")
        currentState.handleLocationStateChange(this, enabled)
    }
    
    // Internal state updates (called by states)
    fun updateBluetoothEnabled(enabled: Boolean) {
        isBluetoothEnabled = enabled
    }
    
    fun updateLocationEnabled(enabled: Boolean) {
        isLocationEnabled = enabled
    }
    
    // Actions that states can perform (called by states)
    fun requestPermissions() {
        eventSender(HomeEvent.RequestBluetoothPermission)
    }
    
    fun checkBluetoothState() {
        eventSender(HomeEvent.CheckBluetoothState)
    }
    
    fun checkLocationState() {
        eventSender(HomeEvent.CheckLocationServiceState)
    }
    
    fun startActualScan() {
        bluetoothInteractor.startScan()
    }
    
    fun stopActualScan() {
        bluetoothInteractor.stopScan()
    }
    
    // Dialog management (called by states)
    fun showPermissionAlertDialog() {
        dialogController.showPermissionAlertDialog()
    }
    
    fun showPermissionDeniedDialog() {
        dialogController.showPermissionDeniedDialog()
    }
    
    fun showBluetoothDisabledDialog() {
        dialogController.showBluetoothDisabledDialog()
    }
    
    fun showLocationDisabledDialog() {
        dialogController.showLocationDisabledDialog()
    }
    
    // Utility methods
    fun isInScanningState(): Boolean = currentState is ScanningState
    
    fun getCurrentStateName(): String = currentState.getStateName()
    
    fun areAllPrerequisitesMet(): Boolean = isBluetoothEnabled && isLocationEnabled
}

/**
 * Interface for Bluetooth operations
 * This abstracts the actual Bluetooth operations from the state machine
 */
interface BluetoothInteractor {
    fun startScan()
    fun stopScan()
}

/**
 * Interface for dialog management
 * This abstracts dialog operations from the state machine
 */
interface DialogController {
    fun showPermissionAlertDialog()
    fun showPermissionDeniedDialog()
    fun showBluetoothDisabledDialog()
    fun showLocationDisabledDialog()
}