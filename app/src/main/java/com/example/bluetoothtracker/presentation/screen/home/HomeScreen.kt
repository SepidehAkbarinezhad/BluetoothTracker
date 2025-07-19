@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.bluetoothtracker.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bluetoothtracker.presentation.common.permissionsList
import com.example.bluetoothtracker.presentation.screen.home.components.DeviceListItem
import com.example.bluetoothtracker.presentation.screen.home.components.HomeTabs
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .statusBarsPadding()
        ) {
            item {
                HomeTabs(tabIndex = tabIndex, onTabClicked = { index -> tabIndex = index })
            }

            items(items = state.devicesList, key = { it.macAddress }) { device ->
                DeviceListItem(device = device)
            }
        }

        if (state.showPermissionAlertDialog) {
            printLog("show permission alert dialog")
            PermissionAlertDialog {
                onAction(HomeAction.ShowPermissionAlertDialog(false))
                onEvent(HomeEvent.RequestBluetoothPermission)
            }
        } else if (state.showPermissionDeniedDialog) {
            PermissionDeniedDialog {
                onAction(HomeAction.ShowPermissionDeniedDialog(false))
            }
        }
    }

}


