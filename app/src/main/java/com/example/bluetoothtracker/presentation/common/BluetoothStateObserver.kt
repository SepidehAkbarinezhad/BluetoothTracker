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
        super.onCreate(owner)
        printLog("onCreate")
        createBroadcastReceiver()
        onBluetoothState(btAdapter?.isEnabled == true)
        //the permissions are granted before adding this observer 
        if(btAdapter?.isEnabled==false){
            try {
                requestEnableBluetooth()
            } catch (e:Exception){
                printLog("enable bluetooth exception: ${e.message}")
            }
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
        }
    }

    /*
    * launches a system dialog that prompts the user
    * to enable Bluetooth using an intent with ACTION_REQUEST_ENABLE.
    * The result of the user's interaction with that dialog is handled by the
    * callback registered in registerBluetoothLauncher().
    * */
    fun requestEnableBluetooth() {
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
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (btAdapter?.state) {
                        BluetoothAdapter.STATE_OFF -> {
                            printLog("STATE_OFF")
                            onBluetoothState(false)
                        }

                        BluetoothAdapter.STATE_ON -> {
                            printLog("STATE_ON")
                            onBluetoothState(true)
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

}