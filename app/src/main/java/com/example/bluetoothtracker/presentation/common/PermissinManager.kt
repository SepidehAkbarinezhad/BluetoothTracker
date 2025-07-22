package com.example.bluetoothtracker.presentation.common

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothtracker.presentation.utils.printLog

class PermissionManager(
    private val activity: ComponentActivity,
    private val onUpdatePermissionState: (Boolean) -> Unit,
    private val onPermissionGranted: (Boolean) -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        registerPermissionLauncher()
        onUpdatePermissionState(hasRequiredPermissions())
    }

    private fun hasRequiredPermissions(): Boolean {
        return permissionsArray.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerPermissionLauncher() {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            onPermissionGranted(allGranted)
        }
    }

    fun requestBluetoothPermissions() {
         if (hasRequiredPermissions()) {
             onPermissionGranted(true)
         } else {
             permissionLauncher.launch(permissionsArray)
         }
    }

}