package com.example.bluetoothtracker.presentation.screen.state

import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog

data object LocationScanState : ScanState {
    override suspend fun handle(viewModel: HomeViewModel) {
        printLog("LocationScanState","stateCheck")
        viewModel.checkLocationStatus()
        val isOn = viewModel.homeStateValue.value.locationServicesState
        if (isOn == true) {
            printLog("LocationScanState true","stateCheck")
            viewModel.moveToState(ReadyToScanState)
        } else {
            printLog("LocationScanState false","stateCheck")
            viewModel.showLocationAlertDialog(true)
        }
    }
}