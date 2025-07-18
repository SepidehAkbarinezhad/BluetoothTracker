@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.bluetoothtracker.presentation.screen.nearbyDevices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bluetoothtracker.presentation.common.permissionsList
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.utils.printLog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenRoot(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val bluetoothPermissionsState = rememberMultiplePermissionsState(permissions = permissionsList)
    val state by viewModel.homeStateValue.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.permissionGranted, key2 = state.bluetoothState) {
        if (state.permissionGranted==false) {// TODO:  show dialog Bluetooth features won't work without permission
            printLog("LaunchedEffect permissions not granted")
        } else if(state.permissionGranted==true){
            printLog("LaunchedEffect permissions not granted else")
            if (state.bluetoothState==true){viewModel.startScan()}
        }
    }


    HomeScreen(bluetoothPermissionsState = bluetoothPermissionsState, state = state)

}

@Composable
fun HomeScreen(
    bluetoothPermissionsState: MultiplePermissionsState,
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

    }

}


