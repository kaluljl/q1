package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citysimulator.game.data.model.WeatherType
import com.citysimulator.game.ui.theme.FoggyColor
import com.citysimulator.game.ui.theme.RainyColor
import com.citysimulator.game.ui.theme.SnowyColor
import com.citysimulator.game.ui.theme.StormyColor
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 天气效果组件
 * 
 * 根据天气类型显示相应的视觉效果，如雨滴、雪花等。
 * 使用Canvas绘制动画效果，提供沉浸式的天气体验。
 * 
 * @param weatherType 天气类型
 * @param modifier 修饰符
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun WeatherEffect(
    weatherType: WeatherType,
    modifier: Modifier = Modifier
) {
    when (weatherType) {
        WeatherType.RAINY -> RainEffect(modifier = modifier)
        WeatherType.SNOWY -> SnowEffect(modifier = modifier)
        WeatherType.STORMY -> StormEffect(modifier = modifier)
        WeatherType.FOGGY -> FogEffect(modifier = modifier)
        else -> {
            // 晴朗和多云天气不显示特殊效果
        }
    }
}

/**
 * 下雨效果
 * 
 * 绘制雨滴动画效果。
 * 
 * @param modifier 修饰符
 */
@Composable
private fun RainEffect(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val screenWidth = with(density) { 400.dp.toPx() }
    val screenHeight = with(density) { 800.dp.toPx() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = screenHeight,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_offset"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRain(
            offset = rainOffset,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }
}

/**
 * 下雪效果
 * 
 * 绘制雪花动画效果。
 * 
 * @param modifier 修饰符
 */
@Composable
private fun SnowEffect(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val screenWidth = with(density) { 400.dp.toPx() }
    val screenHeight = with(density) { 800.dp.toPx() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val snowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = screenHeight,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_offset"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        drawSnow(
            offset = snowOffset,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }
}

/**
 * 暴风雨效果
 * 
 * 绘制闪电和强风效果。
 * 
 * @param modifier 修饰符
 */
@Composable
private fun StormEffect(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val screenWidth = with(density) { 400.dp.toPx() }
    val screenHeight = with(density) { 800.dp.toPx() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "storm")
    val stormOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = screenHeight,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "storm_offset"
    )
    
    val lightningAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        drawStorm(
            offset = stormOffset,
            lightningAlpha = lightningAlpha,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }
}

/**
 * 雾霾效果
 * 
 * 绘制雾霾覆盖效果。
 * 
 * @param modifier 修饰符
 */
@Composable
private fun FogEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "fog")
    val fogAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fog_alpha"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        drawFog(alpha = fogAlpha)
    }
}

/**
 * 绘制雨滴
 * 
 * @param offset 雨滴偏移量
 * @param screenWidth 屏幕宽度
 * @param screenHeight 屏幕高度
 */
private fun DrawScope.drawRain(
    offset: Float,
    screenWidth: Float,
    screenHeight: Float
) {
    val rainDrops = 50
    val rainColor = RainyColor.copy(alpha = 0.6f)
    
    for (i in 0 until rainDrops) {
        val x = (i * screenWidth / rainDrops) + (offset * 0.1f * (i % 3))
        val y = (offset + i * 20f) % (screenHeight + 100f)
        
        if (y > 0 && y < screenHeight) {
            drawLine(
                color = rainColor,
                start = Offset(x, y),
                end = Offset(x, y + 20f),
                strokeWidth = 2f
            )
        }
    }
}

/**
 * 绘制雪花
 * 
 * @param offset 雪花偏移量
 * @param screenWidth 屏幕宽度
 * @param screenHeight 屏幕高度
 */
private fun DrawScope.drawSnow(
    offset: Float,
    screenWidth: Float,
    screenHeight: Float
) {
    val snowflakes = 30
    val snowColor = SnowyColor.copy(alpha = 0.8f)
    
    for (i in 0 until snowflakes) {
        val x = (i * screenWidth / snowflakes) + (offset * 0.05f * (i % 5))
        val y = (offset + i * 30f) % (screenHeight + 100f)
        val size = 2f + (i % 3) * 2f
        
        if (y > 0 && y < screenHeight) {
            drawCircle(
                color = snowColor,
                radius = size,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * 绘制暴风雨
 * 
 * @param offset 效果偏移量
 * @param lightningAlpha 闪电透明度
 * @param screenWidth 屏幕宽度
 * @param screenHeight 屏幕高度
 */
private fun DrawScope.drawStorm(
    offset: Float,
    lightningAlpha: Float,
    screenWidth: Float,
    screenHeight: Float
) {
    // 绘制强风雨滴
    val rainDrops = 80
    val stormColor = StormyColor.copy(alpha = 0.8f)
    
    for (i in 0 until rainDrops) {
        val x = (i * screenWidth / rainDrops) + (offset * 0.3f * (i % 4))
        val y = (offset + i * 15f) % (screenHeight + 100f)
        
        if (y > 0 && y < screenHeight) {
            drawLine(
                color = stormColor,
                start = Offset(x, y),
                end = Offset(x + 10f, y + 30f),
                strokeWidth = 3f
            )
        }
    }
    
    // 绘制闪电
    if (lightningAlpha > 0.5f) {
        val lightningColor = Color.White.copy(alpha = lightningAlpha * 0.3f)
        drawRect(
            color = lightningColor,
            topLeft = Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(screenWidth, screenHeight)
        )
    }
}

/**
 * 绘制雾霾
 * 
 * @param alpha 雾霾透明度
 */
private fun DrawScope.drawFog(alpha: Float) {
    val fogColor = FoggyColor.copy(alpha = alpha)
    drawRect(
        color = fogColor,
        topLeft = Offset(0f, 0f),
        size = androidx.compose.ui.geometry.Size(
            size.width,
            size.height
        )
    )
}
