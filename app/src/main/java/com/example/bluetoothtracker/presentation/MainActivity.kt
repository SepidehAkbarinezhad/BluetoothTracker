package com.example.bluetoothtracker.presentation

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.ScanViewModel
import com.example.bluetoothtracker.presentation.common.BleObserver
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @JvmField
    var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: ScanViewModel by viewModels()
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(
            observer = BleObserver(
                activity = this,
                btAdapter = bluetoothAdapter,
                onBluetoothEnabled = { viewModel.startScan() },
                onBluetoothDenied = { viewModel.onBluetoothTurnedOff() },
                onPause = {}
            )
        )
        enableEdgeToEdge()
        setContent {
            BluetoothTrackerTheme {
                HomeScreenRoot()
            }
        }
    }
}

