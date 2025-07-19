package com.example.bluetoothtracker.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
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
class HomeViewModel @Inject constructor(private val bluetoothInteractor: BluetoothInteractor,val bluetoothRepository: BluetoothRepository) :
    ViewModel() {

        init {
            printLog("init vm")
            //viewModelScope.launch { bluetoothInteractor.insertScannedDeviceUseCase() }
          /*  viewModelScope.launch { bluetoothRepository.scannedDevicesFlow().collect{ deviceList ->
                printLog("collect: $deviceList","sharedTag")

            } }*/
            viewModelScope.launch {
                bluetoothInteractor.insertScannedDeviceUseCase()
                 bluetoothInteractor.getAllDevicesUseCase().collect{list->

                    printLog("getAllDevicesUseCase: $list")
                }

            }
        }
    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()

    fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.ShowPermissionAlertDialog -> homeState.update { it.copy(showPermissionAlertDialog = action.show) }
            is HomeAction.ShowPermissionDeniedDialog -> homeState.update { it.copy(showPermissionDeniedDialog = action.show) }
            is HomeAction.OnPermissionGrantedChange -> homeState.update { it.copy(permissionGranted = action.permissionGranted) }
            is HomeAction.OnBluetoothStateChange -> homeState.update { it.copy(bluetoothState = action.bluetoothState) }
        }
    }

    fun startScan() {
        bluetoothInteractor.startScnBluetooth()
    }

    fun stopScan() {
        bluetoothInteractor.stopScnBluetoothUseCase
    }


}