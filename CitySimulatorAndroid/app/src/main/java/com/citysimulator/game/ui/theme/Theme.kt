package com.citysimulator.game.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 城市模拟经营游戏主题颜色配置
 * 
 * 基于Material Design 3设计规范，提供明亮和暗色两种主题。
 * 颜色搭配体现城市建设的特色：蓝色代表天空，绿色代表自然，橙色代表工业。
 */

// 明亮主题颜色
private val LightColorScheme = lightColorScheme(
    primary = CityBlue,
    onPrimary = CityWhite,
    primaryContainer = CityBlueLight,
    onPrimaryContainer = CityBlueDark,
    secondary = CityGreen,
    onSecondary = CityWhite,
    secondaryContainer = CityGreenLight,
    onSecondaryContainer = CityGreenDark,
    tertiary = CityOrange,
    onTertiary = CityWhite,
    tertiaryContainer = CityOrangeLight,
    onTertiaryContainer = CityOrangeDark,
    error = CityRed,
    onError = CityWhite,
    errorContainer = CityRedLight,
    onErrorContainer = CityRedDark,
    background = CityBackground,
    onBackground = CityOnBackground,
    surface = CitySurface,
    onSurface = CityOnSurface,
    surfaceVariant = CitySurfaceVariant,
    onSurfaceVariant = CityOnSurfaceVariant,
    outline = CityOutline,
    outlineVariant = CityOutlineVariant,
    scrim = CityScrim,
    inverseSurface = CityInverseSurface,
    inverseOnSurface = CityInverseOnSurface,
    inversePrimary = CityInversePrimary
)

// 暗色主题颜色
private val DarkColorScheme = darkColorScheme(
    primary = CityBlueLight,
    onPrimary = CityBlueDark,
    primaryContainer = CityBlueDark,
    onPrimaryContainer = CityBlueLight,
    secondary = CityGreenLight,
    onSecondary = CityGreenDark,
    secondaryContainer = CityGreenDark,
    onSecondaryContainer = CityGreenLight,
    tertiary = CityOrangeLight,
    onTertiary = CityOrangeDark,
    tertiaryContainer = CityOrangeDark,
    onTertiaryContainer = CityOrangeLight,
    error = CityRedLight,
    onError = CityRedDark,
    errorContainer = CityRedDark,
    onErrorContainer = CityRedLight,
    background = CityBackgroundDark,
    onBackground = CityOnBackgroundDark,
    surface = CitySurfaceDark,
    onSurface = CityOnSurfaceDark,
    surfaceVariant = CitySurfaceVariantDark,
    onSurfaceVariant = CityOnSurfaceVariantDark,
    outline = CityOutlineDark,
    outlineVariant = CityOutlineVariantDark,
    scrim = CityScrimDark,
    inverseSurface = CityInverseSurfaceDark,
    inverseOnSurface = CityInverseOnSurfaceDark,
    inversePrimary = CityInversePrimaryDark
)

/**
 * 城市模拟经营游戏主题
 * 
 * 根据系统设置自动切换明亮/暗色主题，支持动态颜色（Android 12+）。
 * 自动设置状态栏和导航栏颜色以匹配主题。
 * 
 * @param darkTheme 是否使用暗色主题，默认跟随系统设置
 * @param dynamicColor 是否使用动态颜色，默认跟随系统设置
 * @param content 主题内容
 */
@Composable
fun CitySimulatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CitySimulatorTypography,
        content = content
    )
}
