package com.github.mantasjasikenas.core.ui.theme

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
import com.github.mantasjasikenas.core.domain.model.theme.Theme
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType
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
                theme == Theme.DYNAMIC && dynamicColorsAvailable -> dynamicDarkColorScheme(
                    LocalContext.current
                )

                theme == Theme.CUSTOM -> customDarkColorScheme(customColor)
                else -> DarkColorScheme
            }
        }

        ThemeType.LIGHT -> {
            when {
                theme == Theme.DEFAULT -> LightColorScheme
                theme == Theme.DYNAMIC && dynamicColorsAvailable -> dynamicLightColorScheme(
                    LocalContext.current
                )

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
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)


val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

val AmoledColorScheme = DarkColorScheme.copy(
    background = backgroundAmoled,
    surface = surfaceAmoled,
)

@SuppressLint("RestrictedApi")
fun customDarkColorScheme(color: Color): ColorScheme {
//    val scheme : DynamicScheme = SchemeTonalSpot(Hct.fromInt(color.toArgb()), true, 0.0)
    val scheme = Scheme.dark(color.toArgb())

    return scheme.toDarkColorScheme()
}

@SuppressLint("RestrictedApi")
fun customLightColorScheme(color: Color): ColorScheme {
//    val scheme : DynamicScheme = SchemeTonalSpot(Hct.fromInt(color.toArgb()), false, 0.0)

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