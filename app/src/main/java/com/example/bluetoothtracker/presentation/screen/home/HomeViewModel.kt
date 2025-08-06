package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.presentation.mapper.toDeviceUiList
import com.example.bluetoothtracker.presentation.screen.state.PermissionScanState
import com.example.bluetoothtracker.presentation.screen.state.ScanState
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

    private var currentScanState: ScanState = PermissionScanState


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
            }

            is HomeAction.OnBluetoothStateChange -> {
                printLog("HomeAction.OnBluetoothStateChange ->  ${action.bluetoothState}","stateCheck")
                updateBluetoothState(state = action.bluetoothState)
            }

            is HomeAction.OnUpdateLocationServiceState -> {
                updateLocationServiceState(state = action.state)
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
        }
    }

    private fun listenToScanAndInsertInDb() {
        bluetoothInteractor.insertScannedDeviceUseCase()
    }

    private fun getDevices() {
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

    private fun observeMessage() {
        viewModelScope.launch {
            bluetoothInteractor.observeMessagesUseCase().collect { message ->
                sendEvent(HomeEvent.ShowToast(message = message))
            }
        }
    }

    fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun showPermissionAlertDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionAlertDialog = show) }
    }

    private fun showPermissionDeniedDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionDeniedDialog = show) }
    }

    fun showBluetoothAlertDialog(show: Boolean) {
        homeState.update { it.copy(showBluetoothStateAlertDialog = show) }
    }

    fun showLocationAlertDialog(show: Boolean) {
        printLog("showLocationAlertDialog  $show","stateCheck")
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

    fun checkLocationStatus() {
        sendEvent(HomeEvent.CheckLocationServiceState)
    }

    private fun showOnlineDevicesLoading(show: Boolean) {
        homeState.update { it.copy(onlineDevicesLoading = show) }
    }

    private fun showEmptyOfflineMessage(show: Boolean) {
        homeState.update { it.copy(emptyOfflineMessage = show) }
    }

    fun startScan() {
        printLog("startScan","stateCheck")
        viewModelScope.launch {
            currentScanState.handle(this@HomeViewModel)
        }
    }
    fun moveToState(nextState: ScanState) {
        currentScanState = nextState
        viewModelScope.launch {
            currentScanState.handle(this@HomeViewModel)
        }
    }

    fun startScanNow() {
        printLog("startScanNow","stateCheck")
        bluetoothInteractor.startScnBluetooth()
    }


    fun stopScan() {
        printLog("stopScan","stateCheck")
        bluetoothInteractor.stopScnBluetoothUseCase()
    }


}