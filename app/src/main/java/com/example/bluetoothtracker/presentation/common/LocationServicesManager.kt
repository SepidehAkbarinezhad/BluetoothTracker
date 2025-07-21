package com.example.bluetoothtracker.presentation.common

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.example.bluetoothtracker.presentation.utils.printLog

class LocationServicesManager(
    private val activity: ComponentActivity
) {
    
    fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    fun promptEnableLocationServices() {
        printLog("Opening location services settings", "locationDebug")
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }
    
    fun getLocationStatus(): LocationStatus {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationStatus(
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER),
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER),
            anyEnabled = isLocationEnabled()
        )
    }
    
    data class LocationStatus(
        val gpsEnabled: Boolean,
        val networkEnabled: Boolean, 
        val anyEnabled: Boolean
    )
}