package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.presentation.mapper.toDeviceUiList
import com.example.bluetoothtracker.presentation.utils.printLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bluetoothInteractor: BluetoothInteractor,
) :
    ViewModel() {

    init {
        bluetoothInteractor.insertScannedDeviceUseCase()
        viewModelScope.launch {
            bluetoothInteractor.getAllDevicesUseCase().collect { list ->
                val sortedList = list.sortedByDescending { item -> item.rssi }
                homeState.update {
                    it.copy(
                        onLineDevicesList = sortedList.filter { list -> list.isOnline }.toDeviceUiList(),
                        offlineDevicesList = sortedList.filter { list -> !list.isOnline }.toDeviceUiList())
                }
            }
        }
    }

    private val _event = MutableSharedFlow<HomeEvent>(replay = 1)
    val event = _event.asSharedFlow()

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()


    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                printLog("UpdatePermissionState -> vm  ${action.permissionState}")
                updatePermissionState(state = action.permissionState)
                if (action.permissionState) {
                    //update bluetooth state just when permissions are granted because if it is off and want to turn it on it needs permissions
                    sendEvent(HomeEvent.CheckBluetoothState)
                } else {
                    // Show dialog: "Bluetooth features won't work without permission"
                    showPermissionAlertDialog(true)
                }
            }

            is HomeAction.OnBluetoothStateChange -> {
                printLog("OnBluetoothStateChange ->")
                updateBluetoothState(state = action.bluetoothState)
                if (action.bluetoothState) {
                    checkLocationStatus()
                } else {
                    if (homeState.value.permissionState == true) {
                        showBluetoothAlertDialog(true)
                    }
                }
            }

            is HomeAction.OnUpdateLocationServiceState -> {
                printLog("before ${homeState.value.permissionState}  ${homeState.value.bluetoothState}")
                updateLocationServiceState(state = action.state)
                if (!action.state) showLocationAlertDialog(true)
            }

            HomeAction.OnPermissionAlertDialogConfirm -> {
                showPermissionAlertDialog(show = false)
                sendEvent(HomeEvent.RequestBluetoothPermission)
            }

            HomeAction.OnPermissionAlertDialogDismiss -> showPermissionAlertDialog(show = false)
            HomeAction.OnGrantPermissionConfirmed -> {
                updatePermissionState(true)
                sendEvent(HomeEvent.CheckBluetoothState)
            }

            HomeAction.OnGrantPermissionCancelled -> {
                updatePermissionState(false)
                showPermissionDeniedDialog(true)
            }

            HomeAction.OnBluetoothAlertDialogConfirmed -> {
                showBluetoothAlertDialog(false)
                sendEvent(HomeEvent.RequestEnableBluetooth)
            }

            HomeAction.OnBluetoothAlertDialogDismiss -> showBluetoothAlertDialog(false)
            HomeAction.OnPermissionDeniedDialogDismiss -> showPermissionDeniedDialog(show = false)
            HomeAction.OnLocationAlertDialogConfirmed -> {
                showLocationAlertDialog(false)
                sendEvent(HomeEvent.RequestEnableLocationServices)
            }

            HomeAction.OnLocationAlertDialogDismiss -> showLocationAlertDialog(false)
            HomeAction.CheckLocationStatus -> checkLocationStatus()
        }
    }

    fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun showPermissionAlertDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionAlertDialog = show) }
    }

    private fun showPermissionDeniedDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionDeniedDialog = show) }
    }

    private fun showBluetoothAlertDialog(show: Boolean) {
        homeState.update { it.copy(showBluetoothStateAlertDialog = show) }
    }

    private fun showLocationAlertDialog(show: Boolean) {
        homeState.update { it.copy(showLocationServiceAlertDialog = show) }
    }

    private fun updatePermissionState(state: Boolean) {
        homeState.update { it.copy(permissionState = state) }
    }

    private fun updateBluetoothState(state: Boolean) {
        homeState.update { it.copy(bluetoothState = state) }
    }

    private fun updateLocationServiceState(state: Boolean) {
        printLog("updateLocationServiceState  $state")
        homeState.update { it.copy(locationServicesState = state) }
    }

    private fun checkLocationStatus() {
        /*
        * Ensure requirement sequence: Permissions → Bluetooth → Location
        * Skip location check if earlier requirements aren't met yet
        */
        with(homeState.value) {
            if (permissionState != true || bluetoothState != true)
                return
        }
        sendEvent(HomeEvent.CheckLocationServiceState)
    }

    fun startScan() {
        printLog("vm startScan", "sdkTag")
        bluetoothInteractor.startScnBluetooth()
    }

    fun stopScan() {
        printLog("vm stopScan", "sdkTag")
        bluetoothInteractor.stopScnBluetoothUseCase
    }


}