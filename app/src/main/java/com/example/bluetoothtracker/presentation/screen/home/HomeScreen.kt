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
import com.example.bluetoothtracker.presentation.screen.home.components.HomeTabs
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.screen.home.components.PermissionDeniedDialog
import com.example.bluetoothtracker.presentation.screen.home.components.TabContent
import com.example.bluetoothtracker.presentation.utils.printLog
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun HomeScreenRoot(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.homeStateValue.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = state.permissionGranted, key2 = state.bluetoothState, key3 = state.locationServicesEnabled) {
        printLog("LaunchedEffect state: $state", "flowDebug")
        
        when {
            // Step 1: Check permissions first
            state.permissionGranted == false -> {
                printLog("❌ Permissions not granted", "flowDebug")
                viewModel.stopScan()
            }
            state.permissionGranted == null -> {
                printLog("⏳ Permissions unknown, waiting...", "flowDebug")
            }
            
            // Step 2: Check Bluetooth (permissions OK)
            state.bluetoothState == false -> {
                printLog("❌ Bluetooth disabled", "flowDebug") 
                viewModel.stopScan()
            }
            state.bluetoothState == null -> {
                printLog("⏳ Bluetooth state unknown, waiting...", "flowDebug")
            }
            
            // Step 3: Check Location Services (permissions + BT OK)
            state.locationServicesEnabled == false -> {
                printLog("❌ Location services disabled", "flowDebug")
                viewModel.stopScan()
            }
            state.locationServicesEnabled == null -> {
                printLog("⏳ Location services unknown, waiting...", "flowDebug")
            }
            
            // Step 4: All good - Start scanning!
            state.permissionGranted == true && 
            state.bluetoothState == true && 
            state.locationServicesEnabled == true -> {
                printLog("✅ All requirements met - Starting scan!", "flowDebug")
                viewModel.startScan()
            }
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


