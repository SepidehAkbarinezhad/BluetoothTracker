package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeState
import com.example.bluetoothtracker.presentation.utils.printLog

@Composable
fun DeviceList(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize().background(Color.Black)
            .statusBarsPadding(),
        state = scrollState
    ) {
        item {
            HomeTabs(tabIndex = tabIndex, onTabClicked = { index -> tabIndex = index })
        }
        printLog("ffffffffff ${state.devicesList}")

        items(items = state.devicesList, key = { it.macAddress }) { device ->
           /* Box(modifier.fillMaxWidth().height(100.dp)){
                AppText(text = device.name, color = Color.Black)
            }*/
            DeviceListItem(device = device)
        }
    }
}