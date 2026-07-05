package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    VIBRANT("Vibrant Palette"),
    LIGHT("Light"),
    DARK("Dark"),
    ICE("Ice"),
    LEMON("Lemon"),
    SHIRAZI("Shirazi"),
    JUNGLE("Jungle"),
    OCEAN("Ocean"),
    GOLD("Gold")
}

private val VibrantThemeColors = lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = VibrantOnPrimary,
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = VibrantSecondary,
    background = VibrantBackground,
    surface = VibrantSurface,
    onBackground = VibrantOnBackground,
    onSurface = VibrantOnSurface,
    surfaceVariant = VibrantSurfaceVariant,
    onSurfaceVariant = VibrantOnSurfaceVariant
)

private val LightThemeColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

private val DarkThemeColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val IceThemeColors = lightColorScheme(
    primary = IcePrimary,
    onPrimary = IceOnPrimary,
    primaryContainer = IcePrimaryContainer,
    onPrimaryContainer = IceOnPrimaryContainer,
    secondary = IceSecondary,
    background = IceBackground,
    surface = IceSurface,
    onBackground = IceOnBackground,
    onSurface = IceOnSurface
)

private val LemonThemeColors = lightColorScheme(
    primary = LemonPrimary,
    onPrimary = LemonOnPrimary,
    primaryContainer = LemonPrimaryContainer,
    onPrimaryContainer = LemonOnPrimaryContainer,
    secondary = LemonSecondary,
    background = LemonBackground,
    surface = LemonSurface,
    onBackground = LemonOnBackground,
    onSurface = LemonOnSurface
)

private val ShiraziThemeColors = lightColorScheme(
    primary = ShiraziPrimary,
    onPrimary = ShiraziOnPrimary,
    primaryContainer = ShiraziPrimaryContainer,
    onPrimaryContainer = ShiraziOnPrimaryContainer,
    secondary = ShiraziSecondary,
    background = ShiraziBackground,
    surface = ShiraziSurface,
    onBackground = ShiraziOnBackground,
    onSurface = ShiraziOnSurface
)

private val JungleThemeColors = lightColorScheme(
    primary = JunglePrimary,
    onPrimary = JungleOnPrimary,
    primaryContainer = JunglePrimaryContainer,
    onPrimaryContainer = JungleOnPrimaryContainer,
    secondary = JungleSecondary,
    background = JungleBackground,
    surface = JungleSurface,
    onBackground = JungleOnBackground,
    onSurface = JungleOnSurface
)

private val OceanThemeColors = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = OceanOnPrimary,
    primaryContainer = OceanPrimaryContainer,
    onPrimaryContainer = OceanOnPrimaryContainer,
    secondary = OceanSecondary,
    background = OceanBackground,
    surface = OceanSurface,
    onBackground = OceanOnBackground,
    onSurface = OceanOnSurface
)

private val GoldThemeColors = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = GoldOnPrimary,
    primaryContainer = GoldPrimaryContainer,
    onPrimaryContainer = GoldOnPrimaryContainer,
    secondary = GoldSecondary,
    background = GoldBackground,
    surface = GoldSurface,
    onBackground = GoldOnBackground,
    onSurface = GoldOnSurface
)

@Composable
fun FinancialAppTheme(
    theme: AppTheme = AppTheme.VIBRANT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.VIBRANT -> VibrantThemeColors
        AppTheme.LIGHT -> LightThemeColors
        AppTheme.DARK -> DarkThemeColors
        AppTheme.ICE -> IceThemeColors
        AppTheme.LEMON -> LemonThemeColors
        AppTheme.SHIRAZI -> ShiraziThemeColors
        AppTheme.JUNGLE -> JungleThemeColors
        AppTheme.OCEAN -> OceanThemeColors
        AppTheme.GOLD -> GoldThemeColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
