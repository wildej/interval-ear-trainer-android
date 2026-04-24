package com.muxaeji.intervalo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF6C5CE7),
    onPrimary = Color.White,
    secondary = Color(0xFF00D2B8),
    onSecondary = Color(0xFF0B1F1A),
    background = Color(0xFFF5F6FA),
    onBackground = Color(0xFF2D3436),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2D3436),
    tertiary = Color(0xFFA29BFE)
)

@Composable
fun IntervaloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}
