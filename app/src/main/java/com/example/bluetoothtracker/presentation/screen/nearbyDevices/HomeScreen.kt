@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.bluetoothtracker.presentation.screen.nearbyDevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bluetoothtracker.presentation.screen.nearbyDevices.components.PermissionAlertDialog
import com.example.bluetoothtracker.presentation.utils.permissionsList
import com.example.bluetoothtracker.presentation.utils.printLog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenRoot(modifier: Modifier = Modifier) {
    val viewModel: ScanViewModel = hiltViewModel()
    val bluetoothPermissionsState = rememberMultiplePermissionsState(permissions = permissionsList)
    val allPermissionGranted = bluetoothPermissionsState.allPermissionsGranted

    LaunchedEffect(key1 = allPermissionGranted) {
        when (allPermissionGranted) {
            true -> {
                printLog("allPermissionGranted")
                viewModel.startScan()}
            false -> {
                printLog("permission denied")

                viewModel.stopScan()}
        }
    }
    HomeScreen(bluetoothPermissionsState = bluetoothPermissionsState)

}

@Composable
fun HomeScreen(bluetoothPermissionsState: MultiplePermissionsState, modifier: Modifier = Modifier) {
   if(!bluetoothPermissionsState.allPermissionsGranted){
       PermissionAlertDialog {
           printLog("onclick")

           bluetoothPermissionsState.launchMultiplePermissionRequest() }
   }
}


