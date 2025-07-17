package com.example.bluetoothtracker.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bluetoothtracker.presentation.utils.TextType


@Composable
fun AppButton(
    text: Int,
    onClick: () -> Unit,
    contentColor: ButtonColors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    textColor: Color = Color.White,
    border: BorderStroke? = null,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(size = 8.dp),
        onClick = onClick,
        colors = contentColor,
        border = border
    ) {
        AppText(
            text = stringResource(text),
            textType = TextType.SubTitle,
            color = textColor
        )
    }
}
