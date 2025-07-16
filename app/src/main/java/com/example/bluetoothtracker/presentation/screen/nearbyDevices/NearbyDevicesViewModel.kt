package com.example.bluetoothtracker.presentation.screen.nearbyDevices

import androidx.lifecycle.ViewModel
import com.example.bluetoothtracker.domain.interactor.BluetoothInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NearbyDevicesViewModel @Inject constructor(private val bluetoothInteractor: BluetoothInteractor): ViewModel() {

    init {
        println("interactro ${bluetoothInteractor.startScan.invoke()}")
    }
    fun test(){
        println("test")

    }
}