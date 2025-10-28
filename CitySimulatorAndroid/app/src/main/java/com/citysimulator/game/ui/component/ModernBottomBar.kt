package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 现代化底部控制栏
 * 
 * 毛玻璃效果、图标动画、悬浮按钮
 */
@Composable
fun ModernBottomBar(
    onBuildingClick: () -> Unit,
    onTaskClick: () -> Unit,
    onCitizenClick: () -> Unit,
    onPolicyClick: () -> Unit,
    onEconomyClick: (() -> Unit)? = null,
    onCitizenListClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E).copy(alpha = 0.92f),
                        Color(0xFF0D47A1).copy(alpha = 0.95f)
                    )
                )
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernBottomButton(
                icon = Icons.Default.Home,
                label = "建筑",
                color = Color(0xFFFF6B6B),
                onClick = onBuildingClick
            )
            
            ModernBottomButton(
                icon = Icons.Default.Assignment,
                label = "任务",
                color = Color(0xFFFFA726),
                onClick = onTaskClick,
                hasNotification = true
            )
            
            ModernBottomButton(
                icon = Icons.Default.People,
                label = "心声",
                color = Color(0xFF66BB6A),
                onClick = onCitizenClick,
                hasNotification = true
            )
            
            ModernBottomButton(
                icon = Icons.Default.Policy,
                label = "政策",
                color = Color(0xFFEC407A),
                onClick = onPolicyClick
            )
            
            // 经济按钮（可选）
            if (onEconomyClick != null) {
                ModernBottomButton(
                    icon = Icons.Default.ShowChart,
                    label = "经济",
                    color = Color(0xFF26A69A),
                    onClick = onEconomyClick
                )
            }
            
            // 市民列表按钮（可选）
            if (onCitizenListClick != null) {
                ModernBottomButton(
                    icon = Icons.Default.PersonSearch,
                    label = "市民",
                    color = Color(0xFFAB47BC),
                    onClick = onCitizenListClick
                )
            }
        }
    }
}

/**
 * 现代化底部按钮
 * 
 * 带动画、图标、通知标记
 */
@Composable
private fun ModernBottomButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    hasNotification: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    // 点击缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    // 图标旋转动画（仅用于通知）
    val rotation = if (hasNotification) {
        val infiniteTransition = rememberInfiniteTransition(label = "notification_rotation")
        infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        ).value
    } else 0f
    
    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier
                        .size(28.dp)
                )
                
                // 通知角标
                if (hasNotification) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .offset(x = 18.dp, y = (-2).dp)
                            .background(
                                color = Color(0xFFFF4444),
                                shape = CircleShape
                            )
                    )
                }
            }
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        }
    }
    
    // 处理按压状态
    LaunchedEffect(Unit) {
        snapshotFlow { isPressed }
    }
}

