package com.example.bluetoothtracker.presentation.common

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothtracker.presentation.utils.printLog
import timber.log.Timber

class BleObserver(
    private val activity: ComponentActivity,
    var btAdapter: BluetoothAdapter?,
    private val onBluetoothEnabled: () -> Unit,
    private val onBluetoothDenied: () -> Unit,
    private val onPause: () -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        registerLauncher()
        createBroadcastReceiver()
        if (btAdapter?.isEnabled==false) {
            launchEnableBtAdapter()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        printLog("onPause called")
        try {
            activity.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            printLog("onPause called error ${e.message}")
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
                        BluetoothAdapter.STATE_OFF -> launchEnableBtAdapter()
                        BluetoothAdapter.STATE_ON -> onBluetoothEnabled()
                    }
                }
            }
        }
    }

    private fun registerLauncher() {
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

    private fun launchEnableBtAdapter() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        btEnableResultLauncher.launch(intent)
    }

}