package com.example.bluetoothtracker.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionDeniedDialog
import com.example.bluetoothtracker.presentation.screen.home.components.TabContent
import com.example.bluetoothtracker.presentation.utils.printLog

@Composable
fun HomeScreenRoot(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.homeStateValue.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = state.permissionGranted, key2 = state.bluetoothState) {
        printLog("LaunchedEffect  $state")
        if (state.permissionGranted == true && state.bluetoothState == true) {
            printLog("LaunchedEffect permissions granted ", "bleCheck")
            viewModel.startScan()
        } else if (state.permissionGranted == false || state.bluetoothState == false) {
            printLog("else if", "bleCheck")
            viewModel.stopScan()
            viewModel.onAction(HomeAction.ShowBluetoothAlertDialog(true))
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
    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(state = state, onAction = onAction)
        if (state.showPermissionAlertDialog) {
            printLog("show permission alert dialog")
            PermissionAlertDialog(onConfirm = {
                onAction(HomeAction.ShowPermissionAlertDialog(false))
                onEvent(HomeEvent.RequestBluetoothPermission)
            }, onDismissRequest = { onAction(HomeAction.ShowPermissionAlertDialog(false)) })
        } else if (state.showPermissionDeniedDialog) {
            PermissionDeniedDialog {
                onAction(HomeAction.ShowPermissionDeniedDialog(false))
            }
        } else if (state.showBluetoothStateDialog) {
            BluetoothStateAlertDialog(
                onConfirm = { onEvent(HomeEvent.RequestEnableBluetooth) },
                onDismissRequest = { onAction(HomeAction.ShowBluetoothAlertDialog(false))}
            )
        }
    }

}

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding(),
    ) {

        HomeTabs(tabIndex = tabIndex, onTabClicked = { index -> tabIndex = index })
        TabContent(state = state, onAction = onAction, tabIndex = tabIndex)

    }
}


