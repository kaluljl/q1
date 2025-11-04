package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 金币收集粒子效果
 * 
 * 建筑建造成功或收取金币时显示
 */
@Composable
fun GoldCollectionEffect(
    isVisible: Boolean,
    position: Offset,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    if (!isVisible) return
    
    var particles by remember { mutableStateOf(createGoldParticles(position)) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            repeat(30) {
                delay(16)
                particles = particles.map { it.update() }.filter { !it.isDead }
            }
            onComplete()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = particle.alpha),
                radius = particle.size,
                center = particle.position
            )
        }
    }
}

/**
 * 建筑建造粒子效果
 * 
 * 放置建筑时的闪光特效
 */
@Composable
fun BuildingPlacementEffect(
    isVisible: Boolean,
    position: Offset,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "building_placement")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // 外圈波纹
        drawCircle(
            color = Color(0xFF4CAF50).copy(alpha = alpha * 0.3f),
            radius = 40f * scale,
            center = position
        )
        
        // 内圈波纹
        drawCircle(
            color = Color(0xFF8BC34A).copy(alpha = alpha * 0.5f),
            radius = 25f * scale,
            center = position
        )
        
        // 星星特效
        val starPoints = 8
        for (i in 0 until starPoints) {
            val angle = (360f / starPoints) * i
            val rad = Math.toRadians(angle.toDouble())
            val x = position.x + cos(rad).toFloat() * 30f * scale
            val y = position.y + sin(rad).toFloat() * 30f * scale
            
            drawCircle(
                color = Color(0xFFFFEB3B).copy(alpha = alpha),
                radius = 3f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * 增强的雨效果 - 更明显的版本
 * 
 * 特性：
 * - 更多雨滴（200个）
 * - 更长更粗的雨滴
 * - 更亮的颜色
 * - 添加雨滴落地水花效果
 */
@Composable
fun EnhancedRainEffect(
    modifier: Modifier = Modifier
) {
    var raindrops by remember { 
        mutableStateOf(List(200) { createRaindrop() }) // 从100增加到200
    }
    
    var splashes by remember {
        mutableStateOf<List<RainSplash>>(emptyList())
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            val newSplashes = mutableListOf<RainSplash>()
            raindrops = raindrops.map { drop ->
                val updated = drop.update()
                // 当雨滴即将离开屏幕时，创建水花
                if (updated.position.y > 1800f && drop.position.y <= 1800f) {
                    newSplashes.add(RainSplash(Offset(drop.position.x, 1800f), 0))
                }
                updated
            }
            splashes = (splashes + newSplashes).map { it.update() }.filter { !it.isDead }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // 绘制雨滴（更长更明显）
        raindrops.forEach { drop ->
            drawLine(
                color = Color(0xFFB3E5FC).copy(alpha = 0.85f), // 更亮的蓝色，更高的不透明度
                start = drop.position,
                end = Offset(drop.position.x - 8f, drop.position.y + 35f), // 更长的雨滴（从20增加到35）
                strokeWidth = 3f // 更粗（从2增加到3）
            )
        }
        
        // 绘制水花效果
        splashes.forEach { splash ->
            val alpha = 1f - (splash.age / 10f)
            drawCircle(
                color = Color(0xFFE1F5FE).copy(alpha = alpha * 0.6f),
                radius = splash.age.toFloat() * 3f,
                center = splash.position
            )
        }
    }
}

/**
 * 增强雪花效果 - 更明显的版本
 * 
 * 特性：
 * - 更多雪花（120个）
 * - 更大的雪花
 * - 更明显的漂移效果
 * - 添加雪花旋转效果
 */
@Composable
fun EnhancedSnowEffect(
    modifier: Modifier = Modifier
) {
    var snowflakes by remember { 
        mutableStateOf(List(120) { createSnowflake() }) // 从60增加到120
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            snowflakes = snowflakes.map { it.update() }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        snowflakes.forEach { flake ->
            // 主雪花（更大更明显）
            drawCircle(
                color = Color.White.copy(alpha = 0.95f), // 更高的不透明度
                radius = flake.size * 1.5f, // 增大尺寸
                center = flake.position
            )
            
            // 雪花光晕效果
            drawCircle(
                color = Color(0xFFE3F2FD).copy(alpha = 0.4f),
                radius = flake.size * 2.5f,
                center = flake.position
            )
            
            // 绘制雪花十字形状（更真实）
            val crossSize = flake.size * 1.2f
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(flake.position.x - crossSize, flake.position.y),
                end = Offset(flake.position.x + crossSize, flake.position.y),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(flake.position.x, flake.position.y - crossSize),
                end = Offset(flake.position.x, flake.position.y + crossSize),
                strokeWidth = 1f
            )
        }
    }
}

/**
 * 星星闪烁效果（夜晚）
 */
@Composable
fun StarsEffect(
    isNight: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isNight) return
    
    val stars = remember { List(50) { createStar() } }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            val twinkle = (System.currentTimeMillis() % 2000) / 2000f
            val alpha = if (star.phase) twinkle else 1f - twinkle
            
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.8f),
                radius = star.size,
                center = star.position
            )
        }
    }
}

// ========== 数据类和辅助函数 ==========

private data class Particle(
    val position: Offset,
    val velocity: Offset,
    val size: Float,
    val alpha: Float,
    val lifetime: Int
) {
    val isDead: Boolean get() = lifetime <= 0
    
    fun update(): Particle {
        return copy(
            position = Offset(
                position.x + velocity.x,
                position.y + velocity.y
            ),
            velocity = Offset(velocity.x, velocity.y + 0.5f), // 重力
            alpha = alpha * 0.95f,
            lifetime = lifetime - 1
        )
    }
}

private data class Raindrop(
    val position: Offset,
    val speed: Float
) {
    fun update(): Raindrop {
        val newY = position.y + speed
        return if (newY > 2000f) {
            createRaindrop()
        } else {
            copy(position = Offset(position.x, newY))
        }
    }
}

private data class Snowflake(
    val position: Offset,
    val speed: Float,
    val size: Float,
    val drift: Float
) {
    fun update(): Snowflake {
        val newY = position.y + speed
        val newX = position.x + drift * sin((newY / 50f).toDouble()).toFloat()
        
        return if (newY > 2000f) {
            createSnowflake()
        } else {
            copy(position = Offset(newX, newY))
        }
    }
}

private data class RainSplash(
    val position: Offset,
    val age: Int
) {
    val isDead: Boolean get() = age > 10
    
    fun update(): RainSplash {
        return copy(age = age + 1)
    }
}

private data class Star(
    val position: Offset,
    val size: Float,
    val phase: Boolean
)

private fun createGoldParticles(center: Offset): List<Particle> {
    return List(20) {
        val angle = Random.nextDouble() * 2 * Math.PI
        val speed = Random.nextFloat() * 5f + 2f
        
        Particle(
            position = center,
            velocity = Offset(
                (cos(angle) * speed).toFloat(),
                (sin(angle) * speed).toFloat() - 5f
            ),
            size = Random.nextFloat() * 3f + 2f,
            alpha = 1f,
            lifetime = 30
        )
    }
}

private fun createRaindrop(): Raindrop {
    return Raindrop(
        position = Offset(
            Random.nextFloat() * 1000f,
            Random.nextFloat() * 2000f - 2000f
        ),
        speed = Random.nextFloat() * 20f + 15f // 更快的速度（从15+10改为20+15）
    )
}

private fun createSnowflake(): Snowflake {
    return Snowflake(
        position = Offset(
            Random.nextFloat() * 1000f,
            Random.nextFloat() * 2000f - 2000f
        ),
        speed = Random.nextFloat() * 2.5f + 1.5f, // 稍快一点
        size = Random.nextFloat() * 4f + 3f, // 更大的雪花（从3+2改为4+3）
        drift = Random.nextFloat() * 3f - 1.5f // 更明显的漂移
    )
}

private fun createStar(): Star {
    return Star(
        position = Offset(
            Random.nextFloat() * 1000f,
            Random.nextFloat() * 800f
        ),
        size = Random.nextFloat() * 1.5f + 0.5f,
        phase = Random.nextBoolean()
    )
}

