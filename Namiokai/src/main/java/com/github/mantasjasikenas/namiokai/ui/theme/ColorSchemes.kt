package com.github.mantasjasikenas.namiokai.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.github.mantasjasikenas.namiokai.model.theme.ThemePreferences
import com.github.mantasjasikenas.namiokai.model.theme.Theme
import com.github.mantasjasikenas.namiokai.model.theme.ThemeType
import com.google.android.material.color.utilities.Scheme

@Composable
fun getColorScheme(
    themePreferences: ThemePreferences
): ColorScheme {
    return getColorScheme(
        themeType = themePreferences.themeType,
        theme = themePreferences.theme,
        customColor = themePreferences.accentColor,
    )
}

@Composable
fun getColorScheme(
    themeType: ThemeType,
    theme: Theme,
    customColor: Color,
): ColorScheme {

    val dynamicColorsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    return when (themeType) {
        ThemeType.DARK -> {
            when {
                theme == Theme.DEFAULT -> DarkColorScheme
                theme == Theme.AMOLED -> AmoledColorScheme
                theme == Theme.DYNAMIC && dynamicColorsAvailable -> dynamicDarkColorScheme(LocalContext.current)
                theme == Theme.CUSTOM -> customDarkColorScheme(customColor)
                else -> DarkColorScheme
            }
        }

        ThemeType.LIGHT -> {
            when {
                theme == Theme.DEFAULT -> LightColorScheme
                theme == Theme.DYNAMIC && dynamicColorsAvailable -> dynamicLightColorScheme(LocalContext.current)
                theme == Theme.CUSTOM -> customLightColorScheme(customColor)
                else -> LightColorScheme
            }
        }

        ThemeType.AUTOMATIC -> {
            when {
                isSystemInDarkTheme() -> DarkColorScheme
                else -> LightColorScheme
            }
        }
    }
}

val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

val AmoledColorScheme =
    darkColorScheme(background = md_theme_amoled_background, surface = md_theme_amoled_surface)
/*DarkColorScheme.copy(
background = md_theme_amoled_background,
surface = md_theme_amoled_surface,
)*/

@SuppressLint("RestrictedApi")
fun customDarkColorScheme(color: Color): ColorScheme {
    val scheme = Scheme.dark(color.toArgb())
    return scheme.toDarkColorScheme()
}

@SuppressLint("RestrictedApi")
fun customLightColorScheme(color: Color): ColorScheme {
    val scheme = Scheme.light(color.toArgb())
    return scheme.toLightColorScheme()
}

@SuppressLint("RestrictedApi")
fun Scheme.toDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        inversePrimary = Color(inversePrimary),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim),
    )
}

@SuppressLint("RestrictedApi")
fun Scheme.toLightColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        inversePrimary = Color(inversePrimary),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim),
    )
}