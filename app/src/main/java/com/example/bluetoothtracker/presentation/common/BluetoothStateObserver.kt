package com.example.bluetoothtracker.presentation.common

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothtracker.presentation.utils.printLog

class BluetoothStateObserver(
    private val activity: ComponentActivity,
    var btAdapter: BluetoothAdapter?,
    private val onBluetoothStateChange: (Boolean) -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(owner: LifecycleOwner) {
        printLog("BluetoothStateObserver oncreate")
        super.onCreate(owner)
        registerBluetoothLauncher()
        createBroadcastReceiver()
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
        }
    }

    private fun registerBluetoothLauncher() {
        btEnableResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                printLog("Activity.RESULT_OK")
                onBluetoothStateChange(true)
            } else {
                printLog("Activity.RESULT_NOT_OK")
                onBluetoothStateChange(false)
            }
        }
    }

    fun updateBluetoothState() {
        printLog("updateBluetoothState")
        onBluetoothStateChange(btAdapter?.isEnabled == true)
    }

    /*
    * launches a system dialog that prompts the user
    * to enable Bluetooth using an intent with ACTION_REQUEST_ENABLE.
    * The result of the user's interaction with that dialog is handled by the
    * callback registered in registerBluetoothLauncher().
    * */
    fun requestEnableBluetooth() {
        printLog("requestEnableBluetooth")
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        btEnableResultLauncher.launch(intent)
    }

    /*
    * BluetoothAdapter broadcasts ACTION_STATE_CHANGED, so you can listen with a BroadcastReceiver
    * The receiver is used to detect when the user manually enables or disables Bluetooth
    * via system UI (e.g., Quick Settings or Settings app), outside the scope of
    * the enable request dialog.
    * */
    private fun createBroadcastReceiver() {
        printLog("createBroadcastReceiver")
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (btAdapter?.state) {
                        BluetoothAdapter.STATE_OFF -> {
                            printLog("STATE_OFF")
                            onBluetoothStateChange(false)
                        }

                        BluetoothAdapter.STATE_ON -> {
                            printLog("STATE_ON")
                            onBluetoothStateChange(true)
                        }
                    }
                }
            }
        }
    }

    private fun registerBroadCast() {
        printLog("registerBroadCast")
        activity.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    private fun unRegisterBroadCast() {
        printLog("unRegisterBroadCast")
        activity.unregisterReceiver(broadcastReceiver)
    }

}