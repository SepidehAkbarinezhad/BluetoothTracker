package com.example.bluetoothtracker.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.presentation.utils.TextType

@Composable
fun AppRowItem(title: Int,value : String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AppText(modifier = Modifier.padding(end = 4.dp), text = title, textType = TextType.SubTitle)
        AppText(text = value)
    }
}