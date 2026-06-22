package eu.meecolabs.heshunt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HesBlue,
    onPrimary = Color.White,
    secondary = HesGreenTint,
    onSecondary = OffBlack,
    tertiary = HesDurness,
    onTertiary = OffBlack,
    background = OffBlack,
    surface = HesSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    error = HesErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = HesVelvet,
    onPrimary = Color.White,
    secondary = HesBlue,
    onSecondary = Color.White,
    tertiary = HesGreen,
    onTertiary = Color.White,
    background = HesBackgroundLight,
    surface = Color.White,
    onBackground = OffBlack,
    onSurface = OffBlack,
    error = HesErrorRed,
    onError = Color.White
)

@Composable
fun HistoryHuntTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
