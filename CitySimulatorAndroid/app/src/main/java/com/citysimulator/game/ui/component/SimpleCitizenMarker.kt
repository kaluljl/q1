package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.CitizenActivity
import com.citysimulator.game.data.model.Gender

/**
 * 改进的市民标记 - 更好的视觉效果
 * 
 * 特性：
 * - 更大更清晰的图标
 * - 渐变色背景
 * - 呼吸动画效果
 * - 阴影和边框
 * - 根据活动显示不同图标
 */
@Composable
fun SimpleCitizenMarker(
    citizen: Citizen,
    modifier: Modifier = Modifier
) {
    // 呼吸动画效果
    val infiniteTransition = rememberInfiniteTransition(label = "citizen_breathe")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(16.dp) // 增大尺寸从12dp -> 16dp
            .scale(scale) // 添加呼吸动画
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                spotColor = getCitizenColor(citizen).copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        getCitizenColor(citizen).copy(alpha = 0.9f),
                        getCitizenColor(citizen).copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.6f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // 根据活动显示不同的emoji
        Text(
            text = getCitizenEmoji(citizen),
            fontSize = 10.sp
        )
    }
}

/**
 * 市民网格覆盖层
 * 
 * 在整个城市网格上显示所有市民的位置
 */
@Composable
fun CitizenOverlay(
    citizens: List<Citizen>,
    gridColumns: Int,
    gridRows: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        citizens.forEach { citizen ->
            // 计算市民在网格中的位置
            val xPercent = citizen.currentX.toFloat() / gridColumns
            val yPercent = citizen.currentY.toFloat() / gridRows
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (xPercent * modifier.fillMaxWidth().hashCode()).dp,
                        y = (yPercent * modifier.fillMaxHeight().hashCode()).dp
                    )
            ) {
                SimpleCitizenMarker(citizen = citizen)
            }
        }
    }
}

/**
 * 获取市民颜色（根据状态）- 更丰富的颜色系统
 */
private fun getCitizenColor(citizen: Citizen): Color {
    return when {
        citizen.happiness > 0.8f -> Color(0xFF66BB6A) // 亮绿色 - 非常快乐
        citizen.happiness > 0.6f -> Color(0xFF4CAF50) // 绿色 - 快乐
        citizen.happiness > 0.4f -> Color(0xFFFFC107) // 黄色 - 一般
        citizen.happiness > 0.2f -> Color(0xFFFF9800) // 橙色 - 不满
        else -> Color(0xFFF44336) // 红色 - 很不满
    }
}

/**
 * 根据市民活动和性别获取对应的emoji图标
 * 简化版：只显示人物，通过颜色区分状态
 */
private fun getCitizenEmoji(citizen: Citizen): String {
    // 统一显示人物图标，通过背景颜色来区分心情状态
    return if (citizen.gender == Gender.MALE) "👨" else "👩"
}

