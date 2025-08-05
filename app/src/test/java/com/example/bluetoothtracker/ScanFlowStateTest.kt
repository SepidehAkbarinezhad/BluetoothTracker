package com.example.bluetoothtracker

import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.state.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests demonstrating the State Design Pattern for Scan Flow
 * These tests show how the state machine handles different scenarios
 */
class ScanFlowStateTest {
    
    private var lastEvent: HomeEvent? = null
    private var scanStarted = false
    private var scanStopped = false
    private var dialogShown: String? = null
    
    private val mockEventSender: (HomeEvent) -> Unit = { event ->
        lastEvent = event
    }
    
    private val mockBluetoothInteractor = object : BluetoothInteractor {
        override fun startScan() {
            scanStarted = true
        }
        
        override fun stopScan() {
            scanStopped = true
        }
    }
    
    private val mockDialogController = object : DialogController {
        override fun showPermissionAlertDialog() {
            dialogShown = "PermissionAlert"
        }
        
        override fun showPermissionDeniedDialog() {
            dialogShown = "PermissionDenied"
        }
        
        override fun showBluetoothDisabledDialog() {
            dialogShown = "BluetoothDisabled"
        }
        
        override fun showLocationDisabledDialog() {
            dialogShown = "LocationDisabled"
        }
    }
    
    private fun createContext(): ScanFlowContext {
        return ScanFlowContext(
            eventSender = mockEventSender,
            bluetoothInteractor = mockBluetoothInteractor,
            dialogController = mockDialogController
        )
    }
    
    private fun resetMocks() {
        lastEvent = null
        scanStarted = false
        scanStopped = false
        dialogShown = null
    }
    
    @Test
    fun `initial state should be Idle`() {
        val context = createContext()
        assertEquals("Idle", context.getCurrentStateName())
    }
    
    @Test
    fun `startScan from Idle should request permissions and transition to PermissionChecking`() {
        val context = createContext()
        
        context.startScan()
        
        assertEquals("PermissionChecking", context.getCurrentStateName())
        assertEquals(HomeEvent.RequestBluetoothPermission, lastEvent)
    }
    
    @Test
    fun `permission granted should check Bluetooth and transition to BluetoothChecking`() {
        val context = createContext()
        resetMocks()
        
        // Start from PermissionChecking state
        context.startScan()
        resetMocks()
        
        context.onPermissionResult(granted = true)
        
        assertEquals("BluetoothChecking", context.getCurrentStateName())
        assertEquals(HomeEvent.CheckBluetoothState, lastEvent)
    }
    
    @Test
    fun `permission denied should show dialog and transition to PermissionDenied`() {
        val context = createContext()
        
        // Start from PermissionChecking state
        context.startScan()
        resetMocks()
        
        context.onPermissionResult(granted = false)
        
        assertEquals("PermissionDenied", context.getCurrentStateName())
        assertEquals("PermissionDenied", dialogShown)
    }
    
    @Test
    fun `Bluetooth enabled should check location and transition to LocationChecking`() {
        val context = createContext()
        
        // Navigate to BluetoothChecking state
        context.startScan()
        context.onPermissionResult(granted = true)
        resetMocks()
        
        context.onBluetoothStateChange(enabled = true)
        
        assertEquals("LocationChecking", context.getCurrentStateName())
        assertEquals(HomeEvent.CheckLocationServiceState, lastEvent)
    }
    
    @Test
    fun `Bluetooth disabled should show dialog and transition to BluetoothDisabled`() {
        val context = createContext()
        
        // Navigate to BluetoothChecking state
        context.startScan()
        context.onPermissionResult(granted = true)
        resetMocks()
        
        context.onBluetoothStateChange(enabled = false)
        
        assertEquals("BluetoothDisabled", context.getCurrentStateName())
        assertEquals("BluetoothDisabled", dialogShown)
    }
    
    @Test
    fun `location enabled should start scan and transition to Scanning`() {
        val context = createContext()
        
        // Navigate to LocationChecking state
        context.startScan()
        context.onPermissionResult(granted = true)
        context.onBluetoothStateChange(enabled = true)
        resetMocks()
        
        context.onLocationStateChange(enabled = true)
        
        assertEquals("Scanning", context.getCurrentStateName())
        assertTrue(scanStarted)
    }
    
    @Test
    fun `location disabled should show dialog and transition to LocationDisabled`() {
        val context = createContext()
        
        // Navigate to LocationChecking state
        context.startScan()
        context.onPermissionResult(granted = true)
        context.onBluetoothStateChange(enabled = true)
        resetMocks()
        
        context.onLocationStateChange(enabled = false)
        
        assertEquals("LocationDisabled", context.getCurrentStateName())
        assertEquals("LocationDisabled", dialogShown)
    }
    
    @Test
    fun `stopScan from Scanning should stop scan and return to Idle`() {
        val context = createContext()
        
        // Navigate to Scanning state
        context.startScan()
        context.onPermissionResult(granted = true)
        context.onBluetoothStateChange(enabled = true)
        context.onLocationStateChange(enabled = true)
        resetMocks()
        
        context.stopScan()
        
        assertEquals("Idle", context.getCurrentStateName())
        assertTrue(scanStopped)
    }
    
    @Test
    fun `Bluetooth disabled while scanning should stop scan and transition to BluetoothDisabled`() {
        val context = createContext()
        
        // Navigate to Scanning state
        context.startScan()
        context.onPermissionResult(granted = true)
        context.onBluetoothStateChange(enabled = true)
        context.onLocationStateChange(enabled = true)
        resetMocks()
        
        // Bluetooth gets disabled while scanning
        context.onBluetoothStateChange(enabled = false)
        
        assertEquals("BluetoothDisabled", context.getCurrentStateName())
        assertTrue(scanStopped)
        assertEquals("BluetoothDisabled", dialogShown)
    }
    
    @Test
    fun `startScan from PermissionDenied should show permission alert`() {
        val context = createContext()
        
        // Navigate to PermissionDenied state
        context.startScan()
        context.onPermissionResult(granted = false)
        resetMocks()
        
        context.startScan()
        
        assertEquals("PermissionDenied", context.getCurrentStateName())
        assertEquals("PermissionAlert", dialogShown)
    }
}