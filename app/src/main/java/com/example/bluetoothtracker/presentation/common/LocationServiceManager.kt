package com.example.bluetoothtracker.presentation.common

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.ComponentActivity

class LocationServiceManager(private val activity: ComponentActivity) {

    fun updateLocationServiceState(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //Scanning will return an empty list if location services are not enabled
    fun promptEnableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }
}