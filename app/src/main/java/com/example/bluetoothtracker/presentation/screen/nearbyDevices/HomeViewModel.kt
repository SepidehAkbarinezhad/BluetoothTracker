package com.example.bluetoothtracker.presentation.screen.nearbyDevices

import androidx.lifecycle.ViewModel
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val bluetoothInteractor: BluetoothInteractor) :
    ViewModel() {

    private val homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeStateValue: StateFlow<HomeState> = homeState.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnBluetoothStateChange -> homeState.update { it.copy(bluetoothState = action.bluetoothState) }
        }
    }

    fun startScan() {
        bluetoothInteractor.startScan
    }

    fun stopScan() {

    }


}