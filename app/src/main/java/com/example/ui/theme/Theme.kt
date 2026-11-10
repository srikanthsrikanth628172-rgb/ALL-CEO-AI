package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CommandCenterDarkColorScheme = darkColorScheme(
  primary = CyberCyan,
  onPrimary = Color(0xFF03101E),
  primaryContainer = Color(0xFF00364F),
  onPrimaryContainer = Color(0xFFBCE9FF),
  secondary = ElectricPurple,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF4C1D95),
  onSecondaryContainer = Color(0xFFE9D5FF),
  tertiary = LaserGreen,
  onTertiary = Color.Black,
  tertiaryContainer = Color(0xFF064E3B),
  onTertiaryContainer = Color(0xFFA7F3D0),
  background = ObsidianBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = NeonBorder,
  outlineVariant = NeonBorderSubtle,
  error = ErrorCrimson,
  onError = Color.White
)

@Composable
fun MyAiSuperstarTheme(
  darkTheme: Boolean = true, // Futuristic dark dashboard is the signature design
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CommandCenterDarkColorScheme,
    typography = Typography,
    content = content
  )
}
