package dev.tswanson.fetchquest.android.ui.theme


import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable



@Composable
fun FetchQuestTheme(
    content: @Composable () -> Unit
) {
    val colors = lightColorScheme(
        primary = primaryColor,
        secondary = secondaryColor,
        background = backgroundColor,
        onPrimary = onPrimary,
        surfaceVariant = surfaceVariant
    )
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}