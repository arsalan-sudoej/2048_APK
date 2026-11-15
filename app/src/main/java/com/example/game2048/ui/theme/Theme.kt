package com.example.game2048.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Game-specific palette that follows the user's chosen theme
 * (manual dark mode override is possible via GameColors).
 */
data class GameColors(
    val background: Color,
    val boardBackground: Color,
    val boardEmpty: Color,
    val text: Color,
    val textBright: Color,
    val button: Color,
    val buttonText: Color,
    val scoreBackground: Color,
    val overlayScrim: Color
)

val LightGameColors = GameColors(
    background = UiBackground,
    boardBackground = BoardBackground,
    boardEmpty = BoardEmpty,
    text = UiText,
    textBright = UiButtonText,
    button = UiButton,
    buttonText = UiButtonText,
    scoreBackground = UiScoreBackground,
    overlayScrim = UiOverlayScrim
)

val DarkGameColors = GameColors(
    background = UiBackgroundDark,
    boardBackground = BoardBackgroundDark,
    boardEmpty = BoardEmptyDark,
    text = UiTextDark,
    textBright = UiButtonTextDark,
    button = UiButtonDark,
    buttonText = UiButtonTextDark,
    scoreBackground = UiScoreBackgroundDark,
    overlayScrim = UiOverlayScrimDark
)

val LocalGameColors = staticCompositionLocalOf { LightGameColors }

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

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
    primary = UiButtonDark,
    onPrimary = UiButtonTextDark,
    secondary = BoardBackgroundDark,
    background = UiBackgroundDark,
    onBackground = UiTextDark,
    surface = UiBackgroundDark,
    onSurface = UiTextDark
)

@Composable
fun Game2048Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalGameColors provides if (darkTheme) DarkGameColors else LightGameColors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            shapes = AppShapes,
            typography = Typography(),
            content = content
        )
    }
}

/** Access the current game palette. */
object GameTheme {
    val colors: GameColors
        @Composable get() = LocalGameColors.current
}
