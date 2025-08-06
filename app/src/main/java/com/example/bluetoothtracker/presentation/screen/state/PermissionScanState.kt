package com.example.bluetoothtracker.presentation.screen.state

import com.example.bluetoothtracker.presentation.screen.home.HomeEvent
import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog

data object PermissionScanState  : ScanState {
    override suspend fun handle(viewModel: HomeViewModel) {
        printLog("PermissionScanState","stateCheck")
        if (viewModel.homeStateValue.value.permissionState == true) {
            printLog("PermissionScanState true","stateCheck")
            viewModel.sendEvent(HomeEvent.CheckBluetoothState)
            viewModel.moveToState(BluetoothScanState)
        } else {
            printLog("PermissionScanState false","stateCheck")
            viewModel.showPermissionAlertDialog(show = true)
        }
    }
}