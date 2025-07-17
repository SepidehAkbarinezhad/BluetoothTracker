package com.example.bluetoothtracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.ScanViewModel
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: ScanViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothTrackerTheme {
                HomeScreenRoot()
            }
        }
    }
}

