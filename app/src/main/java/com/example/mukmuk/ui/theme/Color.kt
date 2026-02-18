package com.example.mukmuk.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF0F0F23)
val DarkSurface = Color(0xFF1A1A2E)
val DarkSurfaceVariant = Color(0xFF16213E)
val GoldAccent = Color(0xFFFFB800)
val GoldAccentDark = Color(0xFFFF8C00)
val TextPrimary = Color.White
val TextSecondary = Color.White.copy(alpha = 0.6f)
val TextTertiary = Color.White.copy(alpha = 0.4f)
val TextHint = Color.White.copy(alpha = 0.3f)
val CardBackground = Color.White.copy(alpha = 0.07f)
val CardBorder = Color.White.copy(alpha = 0.10f)
val ChipBorder = Color.White.copy(alpha = 0.12f)

data class MukmukExtendedColors(
    val textSecondary: Color,
    val textTertiary: Color,
    val textHint: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val chipBorder: Color,
    val goldAccentDark: Color,
    val error: Color
)

val DarkMukmukColors = MukmukExtendedColors(
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    textHint = TextHint,
    cardBackground = CardBackground,
    cardBorder = CardBorder,
    chipBorder = ChipBorder,
    goldAccentDark = GoldAccentDark,
    error = Color(0xFFFF6B6B)
)

// Light theme colors
val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8E8E8)
val LightTextPrimary = Color(0xFF1A1A1A)
val LightTextSecondary = Color(0xFF1A1A1A).copy(alpha = 0.6f)
val LightTextTertiary = Color(0xFF1A1A1A).copy(alpha = 0.4f)
val LightTextHint = Color(0xFF1A1A1A).copy(alpha = 0.3f)
val LightCardBackground = Color(0xFF000000).copy(alpha = 0.06f)
val LightCardBorder = Color(0xFF000000).copy(alpha = 0.10f)
val LightChipBorder = Color(0xFF000000).copy(alpha = 0.12f)

val LightMukmukColors = MukmukExtendedColors(
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    textHint = LightTextHint,
    cardBackground = LightCardBackground,
    cardBorder = LightCardBorder,
    chipBorder = LightChipBorder,
    goldAccentDark = GoldAccentDark,
    error = Color(0xFFFF6B6B)
)

val LocalMukmukColors = staticCompositionLocalOf { DarkMukmukColors }
