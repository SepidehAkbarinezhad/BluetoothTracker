package com.example.bluetoothtracker.presentation.app

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.bluetoothtracker.presentation.common.BluetoothStateObserver
import com.example.bluetoothtracker.presentation.common.LocationServiceManager
import com.example.bluetoothtracker.presentation.common.PermissionManager
import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.HomeScreenRoot
import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog
import com.example.bluetoothtracker.ui.theme.BluetoothTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @JvmField
    var bluetoothAdapter: BluetoothAdapter? = null
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var permissionManager: PermissionManager
    private lateinit var bluetoothStateObserver: BluetoothStateObserver
    private lateinit var locationServiceManager: LocationServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
        addPermissionObserver()
        addBluetoothObserver()
        initLocationServiceManager()
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is HomeEvent.CheckBluetoothState -> {
                            bluetoothStateObserver.updateBluetoothState()
                        }
                        is HomeEvent.RequestBluetoothPermission -> {
                            permissionManager.requestBluetoothPermissions()
                        }
                        is HomeEvent.RequestEnableBluetooth -> {
                            bluetoothStateObserver.requestEnableBluetooth()
                        }
                        is HomeEvent.CheckLocationServiceState -> {
                            val isLocationOn = locationServiceManager.updateLocationServiceState()
                            viewModel.onAction(HomeAction.OnUpdateLocationServiceState(state = isLocationOn))
                        }
                        is HomeEvent.RequestEnableLocationServices -> {
                            locationServiceManager.promptEnableLocationServices()
                        }
                        else -> {}
                    }
                }
            }
            BluetoothTrackerTheme(eventFlow = viewModel.event) {
                HomeScreenRoot(viewModel = viewModel)
            }
        }
    }


    private fun initObservers() {
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
                printLog("onUpdatePermissionState $hasPermission")
                viewModel.onAction((HomeAction.OnUpdatePermissionState(permissionState = hasPermission)))
            },
            onPermissionGranted = { granted ->
                printLog("onPermissionGranted $granted")
                if (granted)
                    viewModel.onAction((HomeAction.OnGrantPermissionConfirmed))
                else
                    viewModel.onAction((HomeAction.OnGrantPermissionCancelled))
            }
        )

    }

    private fun addPermissionObserver() {
        lifecycle.addObserver(permissionManager)
    }

    private fun addBluetoothObserver() {
        lifecycle.addObserver(
            observer = bluetoothStateObserver
        )
    }

    private fun initLocationServiceManager() {
        locationServiceManager = LocationServiceManager(this)
    }

    override fun onResume() {
        printLog("onResume()","stateCheck")
        super.onResume()
        viewModel.startScan()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopScan()
    }

}

