package com.example.bluetoothtracker.presentation.screen.state

import com.example.bluetoothtracker.presentation.screen.home.HomeViewModel

sealed interface ScanState {
    suspend fun handle(viewModel: HomeViewModel)
}