package com.example.bluetoothtracker.presentation.screen.home.state

/**
 * State Design Pattern for Bluetooth Scan Flow
 * Manages the complex flow of permission checking, Bluetooth enabling, location services, and scanning
 */
sealed interface ScanFlowState {
    /**
     * Handle the start scan request in the current state
     * @param context The scan flow context that manages state transitions
     */
    fun handleStartScan(context: ScanFlowContext)
    
    /**
     * Handle permission result in the current state
     */
    fun handlePermissionResult(context: ScanFlowContext, granted: Boolean)
    
    /**
     * Handle Bluetooth state change in the current state
     */
    fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean)
    
    /**
     * Handle location service state change in the current state
     */
    fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean)
    
    /**
     * Handle stop scan request in the current state
     */
    fun handleStopScan(context: ScanFlowContext)
    
    /**
     * Get the current state name for debugging/logging
     */
    fun getStateName(): String
}

/**
 * Initial state - nothing has been checked yet
 */
object IdleState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        context.requestPermissions()
        context.setState(PermissionCheckingState)
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        // No-op in idle state
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        // Update internal state but don't transition
        context.updateBluetoothEnabled(enabled)
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        // Update internal state but don't transition
        context.updateLocationEnabled(enabled)
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        // Already stopped in idle state
    }
    
    override fun getStateName() = "Idle"
}

/**
 * Waiting for permission result
 */
object PermissionCheckingState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        // Already in progress, ignore
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (granted) {
            context.checkBluetoothState()
            context.setState(BluetoothCheckingState)
        } else {
            context.showPermissionDeniedDialog()
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "PermissionChecking"
}

/**
 * Permissions were denied
 */
object PermissionDeniedState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        context.showPermissionAlertDialog()
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (granted) {
            context.checkBluetoothState()
            context.setState(BluetoothCheckingState)
        }
        // If denied again, stay in this state
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "PermissionDenied"
}

/**
 * Permissions granted, checking Bluetooth state
 */
object BluetoothCheckingState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        // Already in progress, ignore
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (enabled) {
            context.checkLocationState()
            context.setState(LocationCheckingState)
        } else {
            context.showBluetoothDisabledDialog()
            context.setState(BluetoothDisabledState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "BluetoothChecking"
}

/**
 * Bluetooth is disabled
 */
object BluetoothDisabledState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        context.showBluetoothDisabledDialog()
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (enabled) {
            context.checkLocationState()
            context.setState(LocationCheckingState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "BluetoothDisabled"
}

/**
 * Bluetooth enabled, checking location services
 */
object LocationCheckingState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        // Already in progress, ignore
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (!enabled) {
            context.setState(BluetoothDisabledState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
        if (enabled) {
            context.startActualScan()
            context.setState(ScanningState)
        } else {
            context.showLocationDisabledDialog()
            context.setState(LocationDisabledState)
        }
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "LocationChecking"
}

/**
 * Location services are disabled
 */
object LocationDisabledState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        context.showLocationDisabledDialog()
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (!enabled) {
            context.setState(BluetoothDisabledState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
        if (enabled) {
            context.startActualScan()
            context.setState(ScanningState)
        }
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "LocationDisabled"
}

/**
 * All prerequisites met, ready to scan
 */
object ReadyState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        context.startActualScan()
        context.setState(ScanningState)
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (!enabled) {
            context.setState(BluetoothDisabledState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
        if (!enabled) {
            context.setState(LocationDisabledState)
        }
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.setState(IdleState)
    }
    
    override fun getStateName() = "Ready"
}

/**
 * Currently scanning for devices
 */
object ScanningState : ScanFlowState {
    override fun handleStartScan(context: ScanFlowContext) {
        // Already scanning, ignore
    }
    
    override fun handlePermissionResult(context: ScanFlowContext, granted: Boolean) {
        if (!granted) {
            context.stopActualScan()
            context.setState(PermissionDeniedState)
        }
    }
    
    override fun handleBluetoothStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateBluetoothEnabled(enabled)
        if (!enabled) {
            context.stopActualScan()
            context.setState(BluetoothDisabledState)
        }
    }
    
    override fun handleLocationStateChange(context: ScanFlowContext, enabled: Boolean) {
        context.updateLocationEnabled(enabled)
        if (!enabled) {
            context.stopActualScan()
            context.setState(LocationDisabledState)
        }
    }
    
    override fun handleStopScan(context: ScanFlowContext) {
        context.stopActualScan()
        context.setState(IdleState)
    }
    
    override fun getStateName() = "Scanning"
}