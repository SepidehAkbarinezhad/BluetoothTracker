package com.example.bluetoothtracker.presentation.app

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bluetoothtracker.presentation.common.BluetoothStateObserver
import com.example.bluetoothtracker.presentation.common.PermissionManager
import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @JvmField
    var bluetoothAdapter: BluetoothAdapter? = null
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var permissionManager: PermissionManager
    private lateinit var bluetoothStateObserver: BluetoothStateObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
        addPermissionObserver()
        addBluetoothObserver()
        enableEdgeToEdge()
        setContent {
            val event by viewModel.event.collectAsStateWithLifecycle(null)
            LaunchedEffect(event) {
                when (event) {
                    is HomeEvent.RequestBluetoothPermission -> {
                        printLog("HomeEvent.RequestBluetoothPermission -> ", "bleCheck")
                        permissionManager.requestBluetoothPermissions()
                    }

                    is HomeEvent.RequestEnableBluetooth -> {
                        printLog("initilizaBug HomeEvent.RequestEnableBluetooth ->", "bleCheck")
                        bluetoothStateObserver.requestEnableBluetooth()
                    }

                    else -> {}
                }
            }
            BluetoothTrackerTheme {
                HomeScreenRoot(viewModel = viewModel)
            }
        }
    }


    private fun initObservers() {
        printLog("initilizaBug initObservers")
        bluetoothStateObserver = BluetoothStateObserver(
            activity = this,
            btAdapter = bluetoothAdapter,
            onBluetoothStateChange = { isEnabled ->
                viewModel.onAction(HomeAction.OnBluetoothStateChange(bluetoothState = isEnabled))
            },
        )
        permissionManager = PermissionManager(
            activity = this,
            onUpdatePermissionState = { hasPermission ->
                if (hasPermission) {
                    printLog("initilizaBug hasPermission", "bleCheck")
                    viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = true)))
                    //update bluetooth state just when permissions are granted
                    bluetoothStateObserver.updateBluetoothState()
                } else {
                    // Show UI: "Bluetooth features won't work without permission"
                    viewModel.onAction(HomeAction.ShowPermissionAlertDialog(true))

                }
            },
            onPermissionGranted = { granted ->
                printLog("onPermissionGranted $granted", "bleCheck")
                viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = granted)))
                if (granted) {
                    viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = true)))
                    bluetoothStateObserver.updateBluetoothState()
                } else {
                    printLog("onPermissionGranted not", "bleCheck")

                    // Show UI: "Bluetooth features won't work without permission"
                    viewModel.onAction(HomeAction.ShowPermissionDeniedDialog(true))
                }
            }
        )

    }

    private fun addPermissionObserver() {
        printLog("initilizaBug addPermissionObserver", "bleCheck")
        lifecycle.addObserver(permissionManager)
    }

    private fun addBluetoothObserver() {
        printLog("initilizaBug addBluetoothObserver", "bleCheck")
        lifecycle.addObserver(
            observer = bluetoothStateObserver
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopScan()
    }
}

