package com.example.bluetoothtracker.presentation.app

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bluetoothtracker.presentation.common.BluetoothRequirementsManager
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
    private var bluetoothStateObserver: BluetoothStateObserver? = null
    private lateinit var requirementsManager: BluetoothRequirementsManager

    /*
    * registerForActivityResult() must be called before the activity is STARTED, usually in onCreate().
    * in my scenario because i want to add bluetoothStateObserver to lifecycle after being sure permissions are granted so
    * the activity may already be in the STARTED state — too late for safe registration and cause crashes when i put it
    * in the observer class. so Don’t lazily register it when needed — it must be declared early and only once.
    * Activity.registerForActivityResult(...) must be called before the Activity is STARTED.
    * But lifecycle.addObserver(...) can be called anytime,so i move it out from the observer class and pass it.
    * */
    private val btEnableResultLauncher = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            printLog("Activity.RESULT_OK")
            viewModel.onAction(HomeAction.OnBluetoothStateChange(bluetoothState = true))
        } else {
            printLog("Activity.RESULT_NOT_OK")
            viewModel.onAction(HomeAction.OnBluetoothStateChange(bluetoothState = false))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
        addPermissionObserver()
        initRequirementsManager()
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        HomeEvent.RequestBluetoothPermission -> {
                            requirementsManager.requestPermissions()
                        }
                        HomeEvent.RequestEnableBluetooth -> {
                            requirementsManager.requestEnableBluetooth()
                        }
                        HomeEvent.RequestEnableLocationServices -> {
                            requirementsManager.requestEnableLocationServices()
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
        permissionManager = PermissionManager(
            activity = this,
            onUpdatePermissionState = { hasPermission ->
                if (hasPermission) {
                    viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = true)))
                    // Only create BluetoothObserver AFTER permission is granted
                    addBluetoothObserver()
                } else {
                    // Show UI: "Bluetooth features won't work without permission"
                    viewModel.onAction(HomeAction.ShowPermissionAlertDialog(true))

                }
            },
            onPermissionGranted = { granted ->
                printLog("onPermissionGranted $granted")
                viewModel.onAction((HomeAction.OnPermissionGrantedChange(permissionGranted = granted)))
                if (granted) {
                    // Re-check or trigger Bluetooth enabling here
                    // TODO: show a dialog and inform user to requestEnableBluetooth 
                    addBluetoothObserver()
                } else {
                    // Show UI: "Bluetooth features won't work without permission"
                    viewModel.onAction(HomeAction.ShowPermissionDeniedDialog(true))
                }
            }
        )
    }
    private fun addPermissionObserver(){
        lifecycle.addObserver(permissionManager)
    }

    private fun addBluetoothObserver() {
        bluetoothStateObserver = BluetoothStateObserver(
            activity = this,
            btAdapter = bluetoothAdapter,
            btEnableResultLauncher = btEnableResultLauncher,
            onBluetoothState = { isEnabled ->
                viewModel.onAction(HomeAction.OnBluetoothStateChange(bluetoothState = isEnabled))
                // Re-check all requirements when Bluetooth state changes
                requirementsManager.checkAllRequirements()
            }
        )
        lifecycle.addObserver(bluetoothStateObserver!!)
    }

    private fun initRequirementsManager() {
        requirementsManager = BluetoothRequirementsManager(
            activity = this,
            permissionManager = permissionManager,
            bluetoothStateObserver = bluetoothStateObserver,
            onRequirementsChange = { state ->
                // Update ViewModel with all states at once
                viewModel.onAction(HomeAction.OnPermissionGrantedChange(state.permissionsGranted))
                viewModel.onAction(HomeAction.OnBluetoothStateChange(state.bluetoothEnabled))
                viewModel.onAction(HomeAction.OnLocationServicesChange(state.locationServicesEnabled))
            }
        )
        // Check initial state
        requirementsManager.checkAllRequirements()
    }

    override fun onResume() {
        super.onResume()
        // Re-check all requirements when returning from settings
        if (::requirementsManager.isInitialized) {
            requirementsManager.checkAllRequirements()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopScan()
    }
}

