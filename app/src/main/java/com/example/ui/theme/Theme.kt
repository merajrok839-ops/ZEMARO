package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ZemaroPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = ZemaroPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = ZemaroAccentCyan,
    onSecondary = Color.Black,
    tertiary = ZemaroAccentAmber,
    onTertiary = Color.Black,
    background = ZemaroBgDark,
    onBackground = ZemaroTextPrimaryDark,
    surface = ZemaroSurfaceDark,
    onSurface = ZemaroTextPrimaryDark,
    surfaceVariant = ZemaroCardDark,
    onSurfaceVariant = ZemaroTextSecondaryDark,
    outline = ZemaroCardBorderDark,
    error = ZemaroAccentRose
)

private val LightColorScheme = lightColorScheme(
    primary = ZemaroPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = ZemaroPrimaryDark,
    secondary = ZemaroAccentCyan,
    onSecondary = Color.Black,
    tertiary = ZemaroAccentAmber,
    onTertiary = Color.Black,
    background = ZemaroBgLight,
    onBackground = ZemaroTextPrimaryLight,
    surface = ZemaroSurfaceLight,
    onSurface = ZemaroTextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = ZemaroTextSecondaryLight,
    outline = ZemaroCardBorderLight,
    error = ZemaroAccentRose
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted luxury brand colors by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
