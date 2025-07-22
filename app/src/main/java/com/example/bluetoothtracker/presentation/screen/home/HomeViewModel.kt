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
) : ViewModel() {

    private val _event = MutableSharedFlow<HomeEvent>(replay = 1)
    val event = _event.asSharedFlow()

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()

    init {
        listenToScanAndInsertInDb()
        getDevices()
        observeMessage()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                updatePermissionState(state = action.permissionState)
                if (action.permissionState) {
                    //update bluetooth state just when permissions are granted
                    sendEvent(HomeEvent.CheckBluetoothState)
                } else {
                    // Show dialog: "Bluetooth features won't work without permission"
                    showPermissionAlertDialog(true)
                }
            }

            is HomeAction.OnBluetoothStateChange -> {
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

    private fun listenToScanAndInsertInDb(){
        bluetoothInteractor.insertScannedDeviceUseCase()
    }
    private fun getDevices(){
        viewModelScope.launch {
            bluetoothInteractor.getAllDevicesUseCase().collect { list ->
                val sortedList = list.sortedByDescending { item -> item.rssi }
                homeState.update {
                    it.copy(
                        onLineDevicesList = sortedList.filter { device -> device.isOnline }
                            .toDeviceUiList()
                            .also { list -> showOnlineDevicesLoading(list.isEmpty()) },
                        offlineDevicesList = sortedList.filter { device -> !device.isOnline }
                            .toDeviceUiList()
                            .also { list -> showEmptyOfflineMessage(list.isEmpty()) })
                }
            }
        }
    }
    private fun observeMessage(){
        viewModelScope.launch { bluetoothInteractor.observeMessagesUseCase().collect{message->
            sendEvent(HomeEvent.ShowToast(message = message))
        } }
    }
    private fun sendEvent(event: HomeEvent) {
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
        homeState.update { it.copy(locationServicesState = state) }
    }

    private fun checkLocationStatus() {
        sendEvent(HomeEvent.CheckLocationServiceState)
    }

    private fun showOnlineDevicesLoading(show: Boolean) {
        homeState.update { it.copy(onlineDevicesLoading = show) }
    }

    private fun showEmptyOfflineMessage(show: Boolean) {
        homeState.update { it.copy(emptyOfflineMessage = show) }
    }

    fun startScan() {
printLog("startScan()")
        /*
        * Ensure the required conditions are met in the following sequence before starting the scan: Permissions → Bluetooth → Location Services
        * If any of these are not satisfied, the corresponding dialog is handled to show within the Composable screen to request the necessary access
        * */
        if (homeStateValue.value.allRequiredReady) {
            bluetoothInteractor.startScnBluetooth()
        } else {
            /*
            * - the `else` block handles the case when the user returns from Location Settings,
            * since the Location intent doesn't return a result like Bluetooth does.
            * - Skip the location check if earlier requirements (permissions or Bluetooth) are not yet met ,
            * This prevents unnecessary checks during the first call to onResume.
            * */
            with(homeState.value) {
                if (permissionState != true || bluetoothState != true)
                    return
            }
            checkLocationStatus()

        }
    }

    fun stopScan() {
        bluetoothInteractor.stopScnBluetoothUseCase()
    }


}