package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
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
                        onLineDevicesList = sortedList.filter { list -> list.isOnline },
                        offlineDevicesList = sortedList.filter { list -> !list.isOnline })
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
            is HomeAction.ShowPermissionAlertDialog -> updateShowPermissionAlertDialog(show = action.show)
            is HomeAction.ShowPermissionDeniedDialog -> updateShowPermissionDeniedDialog(action.show)
            is HomeAction.ShowBluetoothAlertDialog -> updateShowBluetoothAlertDialog(action.show)
            is HomeAction.UpdatePermissionState -> {
                printLog("UpdatePermissionState -> vm  ${action.permissionState}")
                updatePermissionState(state = action.permissionState)
                if (action.permissionState) {
                    //update bluetooth state just when permissions are granted because if it is off and want to turn it on it needs permissions
                    sendEvent(HomeEvent.UpdateBluetoothState)
                } else {
                    // Show dialog: "Bluetooth features won't work without permission"
                    updateShowPermissionAlertDialog(true)
                }
            }

            is HomeAction.BluetoothStateChange -> {
                homeState.update { it.copy(bluetoothState = action.bluetoothState) }
                updateShowBluetoothAlertDialog(!action.bluetoothState)
            }

            HomeAction.OnPermissionAlertDialogConfirm -> {
                updateShowPermissionAlertDialog(show = false)
                sendEvent(HomeEvent.RequestBluetoothPermission)
            }

            HomeAction.OnPermissionAlertDialogDismiss -> updateShowPermissionAlertDialog(show = false)

            HomeAction.OnGrantPermissionConfirmed -> {
                updatePermissionState(true)
                sendEvent(HomeEvent.UpdateBluetoothState)
            }

            HomeAction.OnGrantPermissionCancelled -> {
                updatePermissionState(false)
                onAction(HomeAction.ShowPermissionDeniedDialog(true))
            }

            HomeAction.OnBluetoothAlertDialogConfirmed -> {
                updateShowBluetoothAlertDialog(false)
                sendEvent(HomeEvent.RequestEnableBluetooth)
            }

            HomeAction.OnBluetoothAlertDialogDismiss -> updateShowBluetoothAlertDialog(false)
            HomeAction.OnPermissionDeniedDialogDismiss -> updateShowPermissionDeniedDialog(show = false)
        }
    }

    fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun updateShowPermissionAlertDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionAlertDialog = show) }
    }

    private fun updateShowPermissionDeniedDialog(show: Boolean) {
        homeState.update { it.copy(showPermissionDeniedDialog = show) }
    }

    private fun updateShowBluetoothAlertDialog(show: Boolean) {
        homeState.update { it.copy(showBluetoothStateDialog = show) }
    }

    private fun updatePermissionState(state: Boolean) {
        homeState.update { it.copy(permissionState = state) }
    }

    fun startScan() {
        printLog("vm startScan","sdkTag")
        bluetoothInteractor.startScnBluetooth()
    }

    fun stopScan() {
        printLog("vm stopScan","sdkTag")
        bluetoothInteractor.stopScnBluetoothUseCase
    }


}