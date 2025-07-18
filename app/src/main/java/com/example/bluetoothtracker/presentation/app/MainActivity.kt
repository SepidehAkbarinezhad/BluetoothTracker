package com.example.bluetoothtracker.presentation.app

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bluetoothtracker.presentation.common.BluetoothStateObserver
import com.example.bluetoothtracker.presentation.common.PermissionManager
import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        HomeEvent.RequestBluetoothPermission -> {
                            permissionManager.requestBluetoothPermissions()
                        }
                        HomeEvent.RequestEnableBluetooth -> {
                            bluetoothStateObserver.requestEnableBluetooth()
                        }
                    }
                }
            }
        }
        enableEdgeToEdge()
        setContent {
            BluetoothTrackerTheme {
                HomeScreenRoot(viewModel = viewModel)
            }
        }
    }

    private fun initObservers(){
        printLog("initObservers")
        permissionManager = PermissionManager(
            activity = this,
            onUpdatePermissionState = { hasPermission ->
                if (hasPermission) {
                    printLog("if (hasPermission)")
                    // Only create BluetoothObserver AFTER permission is granted
                    addBluetoothObserver()
                } else {
                    printLog("if (hasPermission) else")
                    // Show UI: "Bluetooth features won't work without permission"
                    viewModel.onAction(HomeAction.ShowPermissionAlertDialog(true))

                }
            },
            onPermissionGranted = { granted ->
                printLog("onPermissionGranted $granted")
                viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = granted)))
                if (granted) {
                    // Re-check or trigger Bluetooth enabling here
                    addBluetoothObserver()
                } else {
                    // Show UI: "Bluetooth features won't work without permission"
                    //it is handled in Home screen
                }
            }
        )
    }
    private fun addPermissionObserver(){
        printLog("addPermissionObserver")
        lifecycle.addObserver(permissionManager)
    }

    private fun addBluetoothObserver() {
        printLog("addBluetoothObserver()")
        lifecycle.addObserver(
            observer = BluetoothStateObserver(
                activity = this,
                btAdapter = bluetoothAdapter,
                onBluetoothState = { isEnabled ->
                    viewModel.onAction(HomeAction.OnBluetoothStateChange(bluetoothState = isEnabled))
                },
            )
        )
    }
}

