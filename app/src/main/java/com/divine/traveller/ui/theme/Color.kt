package com.divine.traveller.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

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

val DarkLavender = Color(0xFF9B59B6)
val LightLavender = Color(0xFFD1C4E9)
val DeepPurple = Color(0xFF673AB7)
val LavenderGray = Color(0xFF8E8E93)
val DarkBackground = Color(0xFF1A1A1F)
val DarkSurface = Color(0xFF2D2D35)
val LavenderAccent = Color(0xFFB39DDB)

val DarkColorScheme = darkColorScheme(
    primary = DarkLavender,
    onPrimary = Color.White,
    primaryContainer = DeepPurple,
    onPrimaryContainer = LightLavender,
    secondary = LavenderAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A148C),
    onSecondaryContainer = LightLavender,
    tertiary = LavenderGray,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3C3C44),
    onSurfaceVariant = LightLavender,
    outline = LavenderGray,
    outlineVariant = Color(0xFF5A5A60),
)