package com.aspoliakov.securenotes.core_ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
        primary = Colors.DarkSkyBlue,
        onPrimary = Colors.GhostWhite,
//        primaryContainer = Colors.Black,
//        onPrimaryContainer = Colors.Black,
        secondary = Colors.GhostWhite,
        onSecondary = Colors.Black,
//        secondaryContainer = Colors.Black,
//        onSecondaryContainer = Colors.Black,
//        tertiary = Colors.Black,
//        onTertiary = Colors.Black,
//        tertiaryContainer = Colors.Black,
//        onTertiaryContainer = Colors.Black,
        error = Colors.DeepRed,
//        onError = Colors.Black,
//        errorContainer = Colors.Black,
//        onErrorContainer = Colors.Black,
//        background = Colors.Black,
//        onBackground = Colors.Black,
//        surface = Colors.Black,
//        onSurface = Colors.Black,
//        surfaceVariant = Colors.Black,
//        onSurfaceVariant = Colors.Black,
//        inverseSurface = Colors.Black,
//        inverseOnSurface = Colors.Black,
//        outline = Colors.Black,
)

@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
        primary = Colors.DarkSkyBlue,
        onPrimary = Colors.GhostWhite,
//        primaryContainer = Colors.Black,
//        onPrimaryContainer = Colors.Black,
        secondary = Colors.Nero2C,
        onSecondary = Colors.White,
//        secondaryContainer = Colors.Black,
//        onSecondaryContainer = Colors.Black,
//        tertiary = Colors.Black,
//        onTertiary = Colors.Black,
//        tertiaryContainer = Colors.Black,
//        onTertiaryContainer = Colors.Black,
        error = Colors.DeepRed,
//        onError = Colors.Black,
//        errorContainer = Colors.Black,
//        onErrorContainer = Colors.Black,
//        background = Colors.Black,
//        onBackground = Colors.Black,
//        surface = Colors.Black,
//        onSurface = Colors.Black,
//        surfaceVariant = Colors.Black,
//        onSurfaceVariant = Colors.Black,
//        inverseSurface = Colors.Black,
//        inverseOnSurface = Colors.Black,
//        outline = Colors.Black,
)

@Immutable
data class CustomColorScheme(
        val tabMenuBackground: Color = Color.Unspecified,
        val tabMenuAccent: Color = Color.Unspecified,
        val tabMenuAccentSelect: Color = Color.Unspecified,
        val tabMenuInactive: Color = Color.Unspecified,
        val debugInfoItem: Color = Color.Unspecified,
        val smallDivider: Color = Color.Unspecified,
)

val LightCustomColorScheme = CustomColorScheme(
        tabMenuBackground = Colors.GhostWhite,
        tabMenuAccent = Colors.DarkSkyBlue,
        tabMenuAccentSelect = Colors.DarkSkyBlue,
        tabMenuInactive = Colors.Grey80,
        debugInfoItem = Colors.GreyDf,
        smallDivider = Colors.Gallery,
)

val DarkCustomColorScheme = CustomColorScheme(
        tabMenuBackground = Colors.Nero2C,
        tabMenuAccent = Colors.White,
        tabMenuAccentSelect = Colors.DarkSkyBlue,
        tabMenuInactive = Colors.DoveGrey,
        debugInfoItem = Colors.Grey41,
        smallDivider = Colors.DarkGrey,
)

val LocalCustomColorSchemeProvider = staticCompositionLocalOf { CustomColorScheme() }

@Composable
fun AppTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit,
) {
    val defaultColorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    val customColorScheme = if (darkTheme) DarkCustomColorScheme else LightCustomColorScheme
    CompositionLocalProvider(
            LocalCustomColorSchemeProvider provides customColorScheme
    ) {
        MaterialTheme(
                colorScheme = defaultColorScheme,
                typography = AppTypography,
                content = content,
        )
    }
}
