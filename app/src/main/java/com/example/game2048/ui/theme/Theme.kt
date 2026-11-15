package com.example.game2048.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = UiButton,
    onPrimary = UiButtonText,
    secondary = BoardBackground,
    background = UiBackground,
    onBackground = UiText,
    surface = UiBackground,
    onSurface = UiText
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8F7A65),
    onPrimary = Color(0xFFF9F6F2),
    secondary = Color(0xFF9E8B78),
    background = Color(0xFF2B2825),
    onBackground = Color(0xFFEEE4DA),
    surface = Color(0xFF2B2825),
    onSurface = Color(0xFFEEE4DA)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private val AppTypography = Typography()

@Composable
fun Game2048Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
