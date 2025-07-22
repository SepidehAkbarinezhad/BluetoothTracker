package com.example.bluetoothtracker.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.presentation.utils.TextType


@Composable
fun AppButton(
    text: Int,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = Color.Black),
    textColor: Color = Color.White,
    border: BorderStroke? = null,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 8.dp),
        onClick = onClick,
        colors = colors,
        border = border
    ) {
        AppText(
            text = stringResource(text),
            textType = TextType.SubTitle,
            color = textColor
        )
    }
}

@Composable
fun AppRowButtons(
    firstButtonTitle: Int,
    onFirstButtonClick: () -> Unit,
    secondButtonTitle: Int,
    onSecondButtonClick: () -> Unit,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AppButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = firstButtonTitle,
            onClick = onFirstButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = color)
        )
        AppButton(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = secondButtonTitle,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            textColor = color,
            border = BorderStroke(width = 1.dp, color = color),
            onClick = onSecondButtonClick,
        )
    }
}

