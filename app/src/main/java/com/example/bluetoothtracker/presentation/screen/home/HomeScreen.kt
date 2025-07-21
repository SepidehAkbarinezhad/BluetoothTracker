package com.example.bluetoothtracker.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bluetoothtracker.presentation.screen.home.components.BluetoothStateAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.HomeTabs
import com.example.bluetoothtracker.presentation.screen.home.components.LocationServiceAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionDeniedDialog
import com.example.bluetoothtracker.presentation.screen.home.components.TabContent
import com.example.bluetoothtracker.presentation.utils.printLog

@Composable
fun HomeScreenRoot(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.homeStateValue.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.permissionState, key2 = state.bluetoothState, key3 = state.locationServicesState) {
        printLog("LaunchedEffect  $state")
        with(state) {
            when {
                permissionState == true && bluetoothState == true && locationServicesState == true -> { viewModel.startScan() }
                permissionState == false || bluetoothState == false -> { viewModel.stopScan() }
            }
        }
    }

    HomeScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        HomeContent(state = state, onAction = onAction)
        if (state.showPermissionAlertDialog) {
            PermissionAlertDialog(onConfirm = {
                onAction(HomeAction.OnPermissionAlertDialogConfirm)
            }, onDismissRequest = { onAction(HomeAction.OnPermissionAlertDialogDismiss) })
        } else if (state.showPermissionDeniedDialog) {
            PermissionDeniedDialog {
                onAction(HomeAction.OnPermissionDeniedDialogDismiss)
            }
        } else if (state.showBluetoothStateAlertDialog) {
            BluetoothStateAlertDialog(
                onConfirm = { onAction(HomeAction.OnBluetoothAlertDialogConfirmed) },
                onDismissRequest = { onAction(HomeAction.OnBluetoothAlertDialogDismiss) }
            )
        } else if(state.showLocationServiceAlertDialog){
            LocationServiceAlertDialog(
                onConfirm = { onAction(HomeAction.OnLocationAlertDialogConfirmed) },
                onDismissRequest = { onAction(HomeAction.OnLocationAlertDialogDismiss) }
            )
        }
    }

}

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding(),
    ) {

        HomeTabs(tabIndex = tabIndex, onTabClicked = { index -> tabIndex = index })
        TabContent(state = state, onAction = onAction, tabIndex = tabIndex)

    }
}


