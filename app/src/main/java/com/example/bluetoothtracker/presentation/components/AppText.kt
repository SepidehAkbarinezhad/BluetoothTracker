package com.example.bluetoothtracker.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.example.bluetoothtracker.presentation.utils.TextType
import com.example.bluetoothtracker.presentation.utils.styleText


@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: Int,
    textType: TextType = TextType.Body,
    color: Color = Black,
    textAlign: TextAlign ? = null,
    textDirection: TextDirection = TextDirection.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        style = styleText(textType).copy(
            textDirection = textDirection,
            textDecoration = textDecoration
        ),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    textType: TextType = TextType.Body,
    color: Color = Black,
    textAlign: TextAlign = TextAlign.Justify,
    textDirection: TextDirection = TextDirection.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        modifier = modifier,
        text = text,
        style = styleText(textType).copy(
            textDirection = textDirection,
            textDecoration = textDecoration
        ),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis
    )
}





