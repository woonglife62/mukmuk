package com.example.mukmuk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = GoldAccentDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
)

val MaterialTheme.mukmukColors: MukmukExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMukmukColors.current

@Composable
fun MukmukTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMukmukColors provides DarkMukmukColors) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}
