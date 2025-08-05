package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.presentation.mapper.toDeviceUiList
import com.example.bluetoothtracker.presentation.screen.home.state.AppStateManager
import com.example.bluetoothtracker.presentation.screen.home.state.StateContext
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
) : ViewModel(), StateContext {

    private val _event = MutableSharedFlow<HomeEvent>(replay = 1)
    val event = _event.asSharedFlow()

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()
    
    // State pattern implementation
    private val stateManager = AppStateManager(this)

    init {
        listenToScanAndInsertInDb()
        getDevices()
        observeMessage()
        observeStateChanges()
    }
    
    // StateContext implementation
    override fun emitEvent(event: HomeEvent) {
        sendEvent(event)
    }
    
    override fun getPermissionState(): Boolean? = homeState.value.permissionState
    override fun getBluetoothState(): Boolean? = homeState.value.bluetoothState
    override fun getLocationServicesState(): Boolean? = homeState.value.locationServicesState
    
    private fun observeStateChanges() {
        viewModelScope.launch {
            stateManager.currentState.collect { appState ->
                val dialogType = stateManager.getCurrentDialogType()
                homeState.update { it.copy(currentDialogType = dialogType) }
            }
        }
    }

    fun onAction(action: HomeAction) {
        // Update the relevant state first
        when (action) {
            is HomeAction.OnUpdatePermissionState -> {
                updatePermissionState(state = action.permissionState)
            }
            is HomeAction.OnBluetoothStateChange -> {
                updateBluetoothState(state = action.bluetoothState)
            }
            is HomeAction.OnUpdateLocationServiceState -> {
                updateLocationServiceState(state = action.state)
            }
            else -> { /* No state update needed for other actions */ }
        }
        
        // Let the state manager handle the action and determine transitions
        stateManager.handleAction(action)
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

    // Dialog management is now handled by the State pattern
    // These methods are no longer needed as the state manager
    // determines which dialogs to show based on the current state

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
        
        if (stateManager.canStartScanning()) {
            bluetoothInteractor.startScnBluetooth()
        } else {
            // The state manager will handle showing appropriate dialogs
            // and guiding the user through the required steps
            with(homeState.value) {
                if (permissionState == true && bluetoothState == true) {
                    // Check location status if other requirements are met
                    checkLocationStatus()
                }
            }
        }
    }

    fun stopScan() {
        bluetoothInteractor.stopScnBluetoothUseCase()
    }


}