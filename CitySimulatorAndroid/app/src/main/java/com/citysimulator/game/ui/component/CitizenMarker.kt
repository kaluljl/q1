package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.CitizenActivity
import com.citysimulator.game.data.model.Gender

/**
 * 市民标记组件
 * 
 * 在城市网格上显示市民的位置和状态
 */
@Composable
fun CitizenMarker(
    citizen: Citizen,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 脉冲动画
    val infiniteTransition = rememberInfiniteTransition(label = "citizen_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // 选中时的光圈动画
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = modifier
            .size(32.dp)
            .scale(if (isSelected) scale else 1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 选中时的光圈
        if (isSelected) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF4CAF50).copy(alpha = glowAlpha),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 4f)
                )
            }
        }
        
        // 市民头像
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when (citizen.gender) {
                        Gender.MALE -> Color(0xFF2196F3)
                        Gender.FEMALE -> Color(0xFFE91E63)
                    }
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF4CAF50) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (citizen.gender) {
                    Gender.MALE -> "👨"
                    Gender.FEMALE -> "👩"
                },
                fontSize = 12.sp
            )
        }
        
        // 活动状态指示器
        Box(
            modifier = Modifier
                .size(8.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(getActivityColor(citizen.currentActivity))
                .border(1.dp, Color.White, CircleShape)
        )
    }
}

/**
 * 市民群组标记
 * 
 * 当多个市民在同一位置时显示
 */
@Composable
fun CitizenGroupMarker(
    citizenCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF607D8B))
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👥",
                fontSize = 14.sp
            )
        }
        
        // 数量标记
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.Red)
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = citizenCount.toString(),
                fontSize = 8.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 市民移动轨迹
 */
@Composable
fun CitizenTrail(
    fromX: Float,
    fromY: Float,
    toX: Float,
    toY: Float,
    color: Color = Color(0xFF4CAF50),
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            color = color.copy(alpha = 0.5f),
            start = Offset(fromX, fromY),
            end = Offset(toX, toY),
            strokeWidth = 2f
        )
        
        // 箭头
        val arrowSize = 10f
        val angle = kotlin.math.atan2((toY - fromY).toDouble(), (toX - fromX).toDouble())
        val arrowAngle1 = angle + Math.PI / 6
        val arrowAngle2 = angle - Math.PI / 6
        
        drawLine(
            color = color.copy(alpha = 0.5f),
            start = Offset(toX, toY),
            end = Offset(
                (toX - arrowSize * kotlin.math.cos(arrowAngle1)).toFloat(),
                (toY - arrowSize * kotlin.math.sin(arrowAngle1)).toFloat()
            ),
            strokeWidth = 2f
        )
        
        drawLine(
            color = color.copy(alpha = 0.5f),
            start = Offset(toX, toY),
            end = Offset(
                (toX - arrowSize * kotlin.math.cos(arrowAngle2)).toFloat(),
                (toY - arrowSize * kotlin.math.sin(arrowAngle2)).toFloat()
            ),
            strokeWidth = 2f
        )
    }
}

/**
 * 市民信息浮窗
 */
@Composable
fun CitizenInfoPopup(
    citizen: Citizen,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .background(
                color = Color.White.copy(alpha = 0.95f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            // 名字和年龄
            Text(
                text = citizen.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${citizen.age}岁 · ${getActivityText(citizen.currentActivity)}",
                fontSize = 11.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 职业
            if (citizen.occupation != null) {
                Text(
                    text = "💼 ${citizen.occupation}",
                    fontSize = 11.sp,
                    color = Color.Black
                )
            }
            
            // 幸福度
            Row(verticalAlignment = Alignment.CenterVertically) {
                val emoji = when {
                    citizen.happiness > 0.7f -> "😄"
                    citizen.happiness > 0.4f -> "🙂"
                    else -> "😟"
                }
                Text(text = emoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${(citizen.happiness * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 获取活动颜色
 */
private fun getActivityColor(activity: CitizenActivity): Color {
    return when (activity) {
        CitizenActivity.SLEEPING -> Color(0xFF9C27B0)
        CitizenActivity.AT_HOME -> Color(0xFF4CAF50)
        CitizenActivity.WORKING -> Color(0xFF2196F3)
        CitizenActivity.COMMUTING_TO_WORK, CitizenActivity.COMMUTING_HOME -> Color(0xFFFF9800)
        CitizenActivity.SHOPPING -> Color(0xFFE91E63)
        CitizenActivity.ENTERTAINMENT, CitizenActivity.SOCIALIZING -> Color(0xFFE91E63)
        CitizenActivity.DINING -> Color(0xFFFF5722)
        CitizenActivity.MEDICAL -> Color(0xFFF44336)
        CitizenActivity.SCHOOL -> Color(0xFF3F51B5)
        CitizenActivity.PARK, CitizenActivity.EXERCISING -> Color(0xFF8BC34A)
    }
}

/**
 * 获取活动文本
 */
private fun getActivityText(activity: CitizenActivity): String {
    return when (activity) {
        CitizenActivity.SLEEPING -> "睡觉中"
        CitizenActivity.AT_HOME -> "在家"
        CitizenActivity.COMMUTING_TO_WORK -> "去上班"
        CitizenActivity.WORKING -> "工作中"
        CitizenActivity.COMMUTING_HOME -> "回家"
        CitizenActivity.SHOPPING -> "购物"
        CitizenActivity.ENTERTAINMENT -> "娱乐"
        CitizenActivity.DINING -> "用餐"
        CitizenActivity.MEDICAL -> "就医"
        CitizenActivity.SCHOOL -> "上学"
        CitizenActivity.PARK -> "公园"
        CitizenActivity.EXERCISING -> "锻炼"
        CitizenActivity.SOCIALIZING -> "社交"
    }
}

