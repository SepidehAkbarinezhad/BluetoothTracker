package com.example.bluetoothtracker.presentation.common

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothtracker.presentation.utils.permissionsArray
import com.example.bluetoothtracker.presentation.utils.permissionsList
import com.example.bluetoothtracker.presentation.utils.printLog

class BleObserver(
    private val activity: ComponentActivity,
    var btAdapter: BluetoothAdapter?,
    private val onBluetoothEnabled: () -> Unit,
    private val onBluetoothDenied: () -> Unit,
    private val onPause: () -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        printLog("onCreate")
        registerBluetoothLauncher()
        registerPermissionLauncher()
        createBroadcastReceiver()
        if (hasRequiredPermissions()) {
            if (btAdapter?.isEnabled == false) {
                launchEnableBtAdapter()
            }
        } else {
            requestBluetoothPermissions()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        printLog("onStart")
        registerBroadCast()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onPause(owner)
        printLog("onStop")
        try {
            unRegisterBroadCast()
        } catch (e: Exception) {
            printLog("onStop called error ${e.message}")
        } finally {
            onPause
        }
    }

    private fun createBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (btAdapter?.state) {
                        BluetoothAdapter.STATE_OFF -> {
                            printLog("STATE_OFF")
                            onBluetoothDenied()
                        }

                        BluetoothAdapter.STATE_ON -> {
                            printLog("STATE_ON")
                            onBluetoothEnabled()
                        }

                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            printLog("STATE_TURNING_OFF")
                            onBluetoothDenied()
                        }

                        BluetoothAdapter.STATE_TURNING_ON -> {
                            printLog("STATE_TURNING_ON")
                            onBluetoothEnabled()
                        }
                    }
                }
            }
        }
    }

    private fun registerBroadCast() {
        activity.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    private fun unRegisterBroadCast() {
        activity.unregisterReceiver(broadcastReceiver)
    }

    private fun registerBluetoothLauncher() {
        btEnableResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                printLog("Activity.RESULT_OK")
                onBluetoothEnabled()
            } else {
                printLog("Activity.RESULT_NOT_OK")
                onBluetoothDenied()
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return permissionsList.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestBluetoothPermissions() {
        val permissions = permissionsArray
        permissionLauncher.launch(permissions)
    }
    private fun registerPermissionLauncher(){
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                if (btAdapter?.isEnabled == false) {
                    launchEnableBtAdapter()
                }
            } else {
                onBluetoothDenied()
            }
        }
    }

    private fun launchEnableBtAdapter() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        btEnableResultLauncher.launch(intent)
    }

}