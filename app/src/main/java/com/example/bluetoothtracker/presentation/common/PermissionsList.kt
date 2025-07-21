package com.example.bluetoothtracker.presentation.common

import android.Manifest
import android.os.Build

val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    // Android 12+ (API 31+)
    listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
} else {
    // Android 11 and below (API 30 and below)
    listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}

val permissionsArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    // Android 12+ (API 31+)
    arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
} else {
    // Android 11 and below (API 30 and below)
    arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}

