package com.example.bluetoothtracker.presentation.common

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.bluetoothtracker.presentation.utils.printLog

class BluetoothStateObserver(
    private val activity: ComponentActivity,
    var btAdapter: BluetoothAdapter?,
    private val btEnableResultLauncher: ActivityResultLauncher<Intent>,
    private val onBluetoothState: (Boolean) -> Unit,
) : DefaultLifecycleObserver {

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(owner: LifecycleOwner) {
        printLog("BluetoothStateObserver oncreate")
        super.onCreate(owner)
        createBroadcastReceiver()
        onBluetoothState(btAdapter?.isEnabled == true)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        printLog("onStart", "bleCheck")
        registerBroadCast()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onPause(owner)
        printLog("onStop")
        try {
            unRegisterBroadCast()
        } catch (e: Exception) {
            printLog("onStop called error ${e.message}", "bleCheck")
        }
    }

    /*
    * launches a system dialog that prompts the user
    * to enable Bluetooth using an intent with ACTION_REQUEST_ENABLE.
    * The result of the user's interaction with that dialog is handled by the
    * callback registered in registerBluetoothLauncher().
    * */
    fun requestEnableBluetooth() {
        printLog("requestEnableBluetooth", "bleCheck")
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
                            printLog("STATE_OFF", "bleCheck")
                            onBluetoothState(false)
                        }

                        BluetoothAdapter.STATE_ON -> {
                            printLog("STATE_ON", "bleCheck")
                            onBluetoothState(true)
                        }
                    }
                }
            }
        }
    }

    private fun registerBroadCast() {
        printLog("registerBroadCast", "bleCheck")
        activity.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    private fun unRegisterBroadCast() {
        printLog("unRegisterBroadCast", "bleCheck")
        activity.unregisterReceiver(broadcastReceiver)
    }

}