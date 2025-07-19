package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.utils.Tabs

@Composable
fun HomeTabs(modifier: Modifier = Modifier, tabIndex: Int, onTabClicked: (Int) -> Unit) {
    val tabs = Tabs.entries
    TabRow(selectedTabIndex = tabIndex) {
        tabs.forEachIndexed { index, tab ->
            Tab(text = { AppText(text = tab.title, color = Color.White) },
                selected = tabIndex == index,
                onClick = { onTabClicked(index) }
            )
        }
    }
}