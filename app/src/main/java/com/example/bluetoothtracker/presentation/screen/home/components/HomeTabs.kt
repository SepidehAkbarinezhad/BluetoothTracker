package com.example.bluetoothtracker.presentation.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.presentation.components.AppText
import com.example.bluetoothtracker.presentation.utils.Tabs
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun HomeTabs(modifier: Modifier = Modifier, tabIndex: Int, onTabClicked: (Int) -> Unit) {
    val tabs = Tabs.entries
    TabRow(selectedTabIndex = tabIndex, indicator = {}, divider = {}) {
        tabs.forEachIndexed { index, tab ->
            val selected = tabIndex == index
            val tabColor = when {
                selected && index == 0 -> Color.Green
                selected && index == 1 -> Color.Red
                else -> Color.Transparent // or TabRowDefaults.containerColor
            }
            Card(modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(12.dp)) {
                Tab(modifier = Modifier.background(tabColor),
                    text = { AppText(text = tab.title, color = Color.White, textType = TextType.SubTitle) },
                    selected = selected,
                    onClick = {
                        onTabClicked(index)
                    }
                )
            }

        }
    }
}