package com.example.bluetoothtracker.presentation

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.bluetoothtracker.presentation.common.BleObserver
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.HomeAction
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.HomeViewModel
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @JvmField
    var bluetoothAdapter: BluetoothAdapter? = null
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObserver()
        enableEdgeToEdge()
        setContent {
            BluetoothTrackerTheme {
                HomeScreenRoot(viewModel = viewModel)
            }
        }
    }

    private fun addObserver(){
        lifecycle.addObserver(
            observer = BleObserver(
                activity = this,
                btAdapter = bluetoothAdapter,
                onBluetoothEnabled = {
                    viewModel.onAction(
                        HomeAction.OnBluetoothStateChange(
                            bluetoothState = true
                        )
                    )
                },
                onBluetoothDenied = {
                    viewModel.onAction(
                        HomeAction.OnBluetoothStateChange(
                            bluetoothState = false
                        )
                    )
                },
                onPause = {}
            )
        )
    }

}

