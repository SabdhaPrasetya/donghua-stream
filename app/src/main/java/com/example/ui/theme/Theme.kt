package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElegantDarkColorScheme = darkColorScheme(
    primary = LilacPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = LilacContainer,
    onPrimaryContainer = OnLilacContainer,
    secondary = CyanSecondary,
    onSecondary = Color(0xFF332D41),
    secondaryContainer = CyanContainer,
    onSecondaryContainer = CyanLight,
    tertiary = LotusPink,
    onTertiary = Color(0xFF492532),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceHighlight,
    outlineVariant = Color(0xFF49454F),
    error = CrimsonRed,
    onError = Color(0xFF601410)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElegantDarkColorScheme,
        typography = Typography,
        content = content
    )
}

