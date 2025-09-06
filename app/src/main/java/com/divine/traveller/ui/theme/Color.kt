package com.divine.traveller.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Light theme colors - Matching HTML design
private val LightPrimary = Color(0xFF13A4EC)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFE0F2FE)
private val LightOnPrimaryContainer = Color(0xFF13A4EC)
private val LightSecondary = Color(0xFFBFDBFE)
private val LightOnSecondary = Color(0xFF13A4EC)
private val LightSecondaryContainer = Color(0xFFE0F2FE)
private val LightOnSecondaryContainer = Color(0xFF13A4EC)
private val LightTertiary = Color(0xFFBFDBFE)
private val LightOnTertiary = Color(0xFF13A4EC)
private val LightTertiaryContainer = Color(0xFFF3F4F6)
private val LightOnTertiaryContainer = Color(0xFF13A4EC)
private val LightError = Color(0xFFDC2626)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFEE2E2)
private val LightOnErrorContainer = Color(0xFF7F1D1D)
private val LightBackground = Color(0xFFF8FAFC)
private val LightOnBackground = Color(0xFF1F2937)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1F2937)
private val LightSurfaceVariant = Color(0xFFF5F5F5)
private val LightOnSurfaceVariant = Color(0xFF4B5563)
private val LightOutline = Color(0xFFE5E7EB)
private val LightOutlineVariant = Color(0xFFF3F4F6)

// Dark theme colors - Based on web color scheme
private val DarkPrimary = Color(0xFF2D9596)        // --primary-color
private val DarkOnPrimary = Color(0xFFFFFFFF)
private val DarkPrimaryContainer = Color(0xFF1F6B6C) // Darker variant of primary
private val DarkOnPrimaryContainer = Color(0xFFB3E5E6) // Light variant of primary
private val DarkSecondary = Color(0xFF3C486B)      // --secondary-color
private val DarkOnSecondary = Color(0xFFF3F4F6)    // --text-primary
private val DarkSecondaryContainer = Color(0xFF2A3441) // Darker variant of secondary
private val DarkOnSecondaryContainer = Color(0xFFD1D5DB) // Lighter variant
private val DarkTertiary = Color(0xFF554971)       // --accent-color
private val DarkOnTertiary = Color(0xFFF3F4F6)     // --text-primary
private val DarkTertiaryContainer = Color(0xFF3F3A54) // Darker variant of accent
private val DarkOnTertiaryContainer = Color(0xFFD4CBE8) // Light variant
private val DarkError = Color(0xFFEF4444)
private val DarkOnError = Color(0xFFFFFFFF)
private val DarkErrorContainer = Color(0xFFDC2626)
private val DarkOnErrorContainer = Color(0xFFFECACA)
private val DarkBackground = Color(0xFF1F2937)     // --background-color
private val DarkOnBackground = Color(0xFFF3F4F6)   // --text-primary
private val DarkSurface = Color(0xFF3C486B)        // --secondary-color (for cards/surfaces)
private val DarkOnSurface = Color(0xFFF3F4F6)      // --text-primary
private val DarkSurfaceVariant = Color(0xFF374151) // Slightly lighter than background
private val DarkOnSurfaceVariant = Color(0xFF9CA3AF) // --text-secondary
private val DarkOutline = Color(0xFF4B5563)        // --border-color
private val DarkOutlineVariant = Color(0xFF374151)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)