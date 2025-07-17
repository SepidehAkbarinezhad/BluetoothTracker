package com.example.bluetoothtracker.presentation.screen.nearbyDevices

import androidx.lifecycle.ViewModel
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import com.example.bluetoothtracker.presentation.screen.printLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NearbyDevicesViewModel @Inject constructor(private val bluetoothInteractor: BluetoothInteractor): ViewModel() {

    init {
        printLog("interactro ${bluetoothInteractor.startScan.invoke()}")
    }

}