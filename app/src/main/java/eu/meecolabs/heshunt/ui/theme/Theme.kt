package eu.meecolabs.heshunt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HesColorScheme = darkColorScheme(
    primary = HesGreen,
    onPrimary = Color.White,
    secondary = HesBlue,
    onSecondary = Color.White,
    tertiary = HesDurness,
    onTertiary = OffBlack,
    background = DarkGrey4,
    surface = DarkGrey3,
    onBackground = LightGrey2,
    onSurface = LightGrey2,
    surfaceVariant = DarkGrey2,
    onSurfaceVariant = LightGrey3,
    outline = MidGrey3,
    error = HesErrorRed,
    onError = Color.White
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HesColorScheme,
        content = content
    )
}
