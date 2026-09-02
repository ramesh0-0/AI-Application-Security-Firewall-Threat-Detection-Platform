package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AIShieldDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2B3160),
    onSecondaryContainer = Color(0xFFD4D7FF),
    tertiary = CyberBlue,
    onTertiary = Color(0xFF003549),
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberBorder,
    error = SeverityCritical,
    onError = Color.White
)

@Composable
fun AIShieldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AIShieldDarkColorScheme,
        typography = Typography,
        content = content
    )
}
