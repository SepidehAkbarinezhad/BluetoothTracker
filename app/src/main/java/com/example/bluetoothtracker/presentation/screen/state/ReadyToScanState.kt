package com.example.bluetoothtracker.presentation.screen.state

import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel
import com.example.bluetoothtracker.presentation.utils.printLog

data object ReadyToScanState : ScanState {
    override suspend fun handle(viewModel: HomeViewModel) {
        printLog("ReadyToScanState","stateCheck")
        viewModel.startScanNow()
    }
}