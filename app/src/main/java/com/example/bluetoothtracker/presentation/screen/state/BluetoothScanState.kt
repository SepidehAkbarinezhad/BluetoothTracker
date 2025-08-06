package com.example.bluetoothtracker.presentation.screen.state

import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog

data object BluetoothScanState : ScanState {
    override suspend fun handle(viewModel: HomeViewModel) {
        val isBluetoothOn =viewModel.homeStateValue.value.bluetoothState == true
        printLog("BluetoothScanState $isBluetoothOn","stateCheck")
        if (isBluetoothOn) {
            printLog("BluetoothScanState true","stateCheck")
            viewModel.moveToState(LocationScanState)
        } else {
            printLog("BluetoothScanState false","stateCheck")
            viewModel.showBluetoothAlertDialog (show = true)
        }
    }
}