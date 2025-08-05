package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.presentation.mapper.toDeviceUiList
import com.example.bluetoothtracker.presentation.screen.home.state.BluetoothInteractor as StateBluetoothInteractor
import com.example.bluetoothtracker.presentation.screen.home.state.DialogController
import com.example.bluetoothtracker.presentation.screen.home.state.ScanFlowContext
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

/**
 * Refactored HomeViewModel using State Design Pattern for scan flow management
 * This version replaces the complex conditional logic with a clean state machine
 */
@HiltViewModel
class HomeViewModelWithState @Inject constructor(
    private val bluetoothInteractor: BluetoothInteractor,
) : ViewModel() {

    private val _event = MutableSharedFlow<HomeEvent>(replay = 1)
    val event = _event.asSharedFlow()

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()

    // State Machine for scan flow management
    private val scanFlowContext = ScanFlowContext(
        eventSender = ::sendEvent,
        bluetoothInteractor = BluetoothInteractorImpl(),
        dialogController = DialogControllerImpl()
    )

    init {
        listenToScanAndInsertInDb()
        getDevices()
        observeMessage()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                updatePermissionState(state = action.permissionState)
                // Delegate to state machine
                scanFlowContext.onPermissionResult(action.permissionState)
            }

            is HomeAction.OnBluetoothStateChange -> {
                updateBluetoothState(state = action.bluetoothState)
                // Delegate to state machine
                scanFlowContext.onBluetoothStateChange(action.bluetoothState)
            }

            is HomeAction.OnUpdateLocationServiceState -> {
                updateLocationServiceState(state = action.state)
                // Delegate to state machine
                scanFlowContext.onLocationStateChange(action.state)
            }

            HomeAction.OnGrantPermissionConfirmed -> {
                updatePermissionState(true)
                scanFlowContext.onPermissionResult(true)
            }

            HomeAction.OnGrantPermissionCancelled -> {
                updatePermissionState(false)
                scanFlowContext.onPermissionResult(false)
            }

            HomeAction.OnPermissionAlertDialogConfirm -> {
                showPermissionAlertDialog(show = false)
                sendEvent(HomeEvent.RequestBluetoothPermission)
            }

            HomeAction.OnPermissionAlertDialogDismiss -> showPermissionAlertDialog(show = false)
            HomeAction.OnPermissionDeniedDialogDismiss -> showPermissionDeniedDialog(show = false)

            HomeAction.OnBluetoothAlertDialogConfirmed -> {
                showBluetoothAlertDialog(false)
                sendEvent(HomeEvent.RequestEnableBluetooth)
            }

            HomeAction.OnBluetoothAlertDialogDismiss -> showBluetoothAlertDialog(false)

            HomeAction.OnLocationAlertDialogConfirmed -> {
                showLocationAlertDialog(false)
                sendEvent(HomeEvent.RequestEnableLocationServices)
            }

            HomeAction.OnLocationAlertDialogDismiss -> showLocationAlertDialog(false)
            HomeAction.CheckLocationStatus -> checkLocationStatus()
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

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    // Dialog management methods (simplified)
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

    // State update methods
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

    /**
     * Simplified startScan method - all complexity is now handled by the state machine
     */
    fun startScan() {
        printLog("startScan() - delegating to state machine (current state: ${scanFlowContext.getCurrentStateName()})")
        scanFlowContext.startScan()
    }

    /**
     * Simplified stopScan method
     */
    fun stopScan() {
        printLog("stopScan() - delegating to state machine")
        scanFlowContext.stopScan()
    }

    /**
     * Get current scan flow state for debugging/UI purposes
     */
    fun getCurrentScanState(): String = scanFlowContext.getCurrentStateName()

    /**
     * Check if currently scanning
     */
    fun isScanning(): Boolean = scanFlowContext.isInScanningState()

    // Implementation of BluetoothInteractor interface for the state machine
    private inner class BluetoothInteractorImpl : StateBluetoothInteractor {
        override fun startScan() {
            bluetoothInteractor.startScnBluetooth()
        }

        override fun stopScan() {
            bluetoothInteractor.stopScnBluetoothUseCase()
        }
    }

    // Implementation of DialogController interface for the state machine
    private inner class DialogControllerImpl : DialogController {
        override fun showPermissionAlertDialog() {
            showPermissionAlertDialog(true)
        }

        override fun showPermissionDeniedDialog() {
            showPermissionDeniedDialog(true)
        }

        override fun showBluetoothDisabledDialog() {
            showBluetoothAlertDialog(true)
        }

        override fun showLocationDisabledDialog() {
            showLocationAlertDialog(true)
        }
    }
}