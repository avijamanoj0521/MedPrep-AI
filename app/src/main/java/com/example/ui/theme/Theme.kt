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

private val MedPrepDarkColorScheme = darkColorScheme(
    primary = MedTealAccent,
    onPrimary = MedNavyDark,
    primaryContainer = MedNavyLight,
    onPrimaryContainer = Color.White,
    secondary = MedTealPrimary,
    onSecondary = Color.White,
    secondaryContainer = MedNavyDark,
    onSecondaryContainer = MedTealAccent,
    tertiary = MedAmberAccent,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val MedPrepLightColorScheme = lightColorScheme(
    primary = MedNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = MedTealContainer,
    onPrimaryContainer = MedNavyDark,
    secondary = MedTealPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCECE8),
    onSecondaryContainer = MedTealDark,
    tertiary = MedAmberAccent,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve branded MedPrep medical theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MedPrepDarkColorScheme
        else -> MedPrepLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
