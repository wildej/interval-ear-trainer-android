package com.muxaeji.intervalo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA29BFE),
    onPrimary = Color(0xFF17132E),
    secondary = Color(0xFF00D2B8),
    onSecondary = Color(0xFF01211C),
    background = Color(0xFF11131A),
    onBackground = Color(0xFFE7E9F1),
    surface = Color(0xFF1A1E29),
    onSurface = Color(0xFFE7E9F1),
    tertiary = Color(0xFF6C5CE7)
)

@Composable
fun IntervaloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}
