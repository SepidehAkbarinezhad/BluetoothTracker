package com.example.bluetoothtracker.presentation.common

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.ComponentActivity

class BluetoothRequirementsManager(
    private val activity: ComponentActivity,
    private val permissionManager: PermissionManager,
    private val bluetoothStateObserver: BluetoothStateObserver?,
    private val onRequirementsChange: (RequirementsState) -> Unit
) {
    
    data class RequirementsState(
        val permissionsGranted: Boolean = false,
        val bluetoothEnabled: Boolean = false,
        val locationServicesEnabled: Boolean = false
    ) {
        val allRequirementsMet: Boolean
            get() = permissionsGranted && bluetoothEnabled && locationServicesEnabled
            
        val nextRequiredAction: RequiredAction?
            get() = when {
                !permissionsGranted -> RequiredAction.GRANT_PERMISSIONS
                !bluetoothEnabled -> RequiredAction.ENABLE_BLUETOOTH
                !locationServicesEnabled -> RequiredAction.ENABLE_LOCATION_SERVICES
                else -> null
            }
    }
    
    enum class RequiredAction {
        GRANT_PERMISSIONS,
        ENABLE_BLUETOOTH,
        ENABLE_LOCATION_SERVICES
    }
    
    fun checkAllRequirements(): RequirementsState {
        val state = RequirementsState(
            permissionsGranted = permissionManager.hasBluetoothPermissions(),
            bluetoothEnabled = isBluetoothEnabled(),
            locationServicesEnabled = isLocationServicesEnabled()
        )
        
        onRequirementsChange(state)
        return state
    }
    
    fun requestPermissions() {
        permissionManager.requestBluetoothPermissions()
    }
    
    fun requestEnableBluetooth() {
        bluetoothStateObserver?.requestEnableBluetooth()
    }
    
    fun requestEnableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }
    
    fun handleNextRequiredAction(): Boolean {
        val state = checkAllRequirements()
        return when (state.nextRequiredAction) {
            RequiredAction.GRANT_PERMISSIONS -> {
                requestPermissions()
                true
            }
            RequiredAction.ENABLE_BLUETOOTH -> {
                requestEnableBluetooth()
                true
            }
            RequiredAction.ENABLE_LOCATION_SERVICES -> {
                requestEnableLocationServices()
                true
            }
            null -> false // All requirements met
        }
    }
    
    private fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }
    
    private fun isLocationServicesEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}