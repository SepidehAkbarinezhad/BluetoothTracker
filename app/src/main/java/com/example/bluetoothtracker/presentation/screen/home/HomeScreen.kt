@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.bluetoothtracker.presentation.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bluetoothtracker.presentation.common.permissionsList
import com.example.bluetoothtracker.presentation.screen.home.components.DeviceList
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionDeniedDialog
import com.example.bluetoothtracker.presentation.utils.printLog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenRoot(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val bluetoothPermissionsState = rememberMultiplePermissionsState(permissions = permissionsList)
    val state by viewModel.homeStateValue.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = state.permissionGranted, key2 = state.bluetoothState) {
        printLog("LaunchedEffect  $state")
        if (state.permissionGranted == true && state.bluetoothState == true) {
            printLog("LaunchedEffect permissions granted ")
            viewModel.startScan()
        } else if (state.permissionGranted == false || state.bluetoothState == false) {
            viewModel.stopScan()
        }
    }

    HomeScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        onEvent = { event -> viewModel.sendEvent(event) })

}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        DeviceList(state = state, onAction = onAction)
       /* if (state.showPermissionAlertDialog) {
            printLog("show permission alert dialog")
            PermissionAlertDialog {
                onAction(HomeAction.ShowPermissionAlertDialog(false))
                onEvent(HomeEvent.RequestBluetoothPermission)
            }
        } else if (state.showPermissionDeniedDialog) {
            PermissionDeniedDialog {
                onAction(HomeAction.ShowPermissionDeniedDialog(false))
            }
        }*/
    }

}


