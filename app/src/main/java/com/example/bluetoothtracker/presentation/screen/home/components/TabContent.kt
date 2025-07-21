package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.R
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.screen.home.HomeAction
import com.example.bluetoothtracker.presentation.screen.home.HomeState
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun TabContent(
    state: HomeState,
    tabIndex: Int,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    val isOnlineSelected = tabIndex == 0
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color.Black)
                .statusBarsPadding(),
            state = scrollState
        ) {
            items(
                items = if (isOnlineSelected) state.onLineDevicesList else state.offlineDevicesList,
                key = { it.macAddress }) { device ->
                DeviceListItem(
                    device = device
                )
            }
        }
        if (state.allRequiredReady)
            MessageContainer(isOnlineSelected = isOnlineSelected, state = state)
    }
}

@Composable
fun BoxScope.MessageContainer(isOnlineSelected: Boolean, state: HomeState) {
    if (state.onlineDevicesLoading && isOnlineSelected)
        AppText(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            text = R.string.waiting_message,
            color = Color.White, textType = TextType.SubTitle
        )
    else if (state.emptyOfflineMessage && !isOnlineSelected)
        AppText(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            text = R.string.empty_offline_message,
            color = Color.White, textType = TextType.SubTitle
        )
}