@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.ui.theme.*

/**
 * 底部控制栏组件
 * 
 * 提供游戏主要功能的快捷访问按钮。
 * 使用Material Design 3设计，支持触觉反馈和动画效果。
 * 
 * @param onBuildingMenuClick 建筑菜单点击回调
 * @param onResourcePanelClick 资源面板点击回调
 * @param onTaskPanelClick 任务面板点击回调
 * @param onAchievementPanelClick 成就面板点击回调
 * @param onSettingsClick 设置点击回调
 * @param modifier 修饰符
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun BottomControlBar(
    onBuildingMenuClick: () -> Unit,
    onResourcePanelClick: () -> Unit,
    onTaskPanelClick: () -> Unit,
    onAchievementPanelClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAIAssistantClick: () -> Unit,
    onSupabaseConfigClick: () -> Unit,
    onTechTreeClick: () -> Unit,
    onCityPolicyClick: () -> Unit,
    onCityEventClick: () -> Unit,
    onCitizenFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CitySurface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 建筑菜单按钮
            ControlButton(
                icon = Icons.Default.Home,
                label = "建筑",
                color = ResidentialColor,
                badgeCount = 0,
                onClick = onBuildingMenuClick
            )
            
            // 资源面板按钮
            ControlButton(
                icon = Icons.Default.Inventory,
                label = "资源",
                color = GoldColor,
                badgeCount = 0,
                onClick = onResourcePanelClick
            )
            
            // 任务面板按钮
            ControlButton(
                icon = Icons.Default.Assignment,
                label = "任务",
                color = CityBlue,
                badgeCount = 3, // 示例：有3个新任务
                onClick = onTaskPanelClick
            )
            
            // 成就面板按钮
            ControlButton(
                icon = Icons.Default.EmojiEvents,
                label = "成就",
                color = CityOrange,
                badgeCount = 1, // 示例：有1个新成就
                onClick = onAchievementPanelClick
            )
            
            // AI助手按钮
            ControlButton(
                icon = Icons.Default.SmartToy,
                label = "AI助手",
                color = CityPurple,
                badgeCount = 0,
                onClick = onAIAssistantClick
            )
            
            // Supabase配置按钮
            ControlButton(
                icon = Icons.Default.Cloud,
                label = "云端",
                color = CityBlue,
                badgeCount = 0,
                onClick = onSupabaseConfigClick
            )
            
            // 科技树按钮
            ControlButton(
                icon = Icons.Default.Science,
                label = "科技",
                color = Color(0xFF9C27B0),
                badgeCount = 0,
                onClick = onTechTreeClick
            )
            
            // 城市政策按钮
            ControlButton(
                icon = Icons.Default.Policy,
                label = "政策",
                color = Color(0xFF4CAF50),
                badgeCount = 0,
                onClick = onCityPolicyClick
            )
            
            // 城市事件按钮
            ControlButton(
                icon = Icons.Default.Event,
                label = "事件",
                color = Color(0xFFFF9800),
                badgeCount = 1, // 示例：有1个新事件
                onClick = onCityEventClick
            )
            
            // 市民反馈按钮
            ControlButton(
                icon = Icons.Default.Chat,
                label = "心声",
                color = Color(0xFF2196F3),
                badgeCount = 0,
                onClick = onCitizenFeedbackClick
            )
            
            // 设置按钮
            ControlButton(
                icon = Icons.Default.Settings,
                label = "设置",
                color = CityGray,
                badgeCount = 0,
                onClick = onSettingsClick
            )
        }
    }
}

/**
 * 控制按钮组件
 * 
 * 单个控制按钮，支持图标、标签、徽章和点击效果。
 * 
 * @param icon 按钮图标
 * @param label 按钮标签
 * @param color 按钮颜色
 * @param badgeCount 徽章数量
 * @param onClick 点击回调
 */
@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    color: Color,
    badgeCount: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "button_glow")
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .scale(scaleAnimation)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                    // 触觉反馈
                    // TODO: 添加触觉反馈
                }
            )
    ) {
        Box {
            // 按钮背景
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.15f)
                ),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isPressed) 2.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color.copy(alpha = glowAnimation * 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // 徽章
            if (badgeCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd),
                    containerColor = CityRed,
                    contentColor = CityWhite
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 按钮标签
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = CityOnSurfaceVariant
        )
    }
}

/**
 * 徽章组件
 * 
 * 显示数量徽章，支持动画效果。
 * 
 * @param count 徽章数量
 * @param color 徽章颜色
 * @param modifier 修饰符
 */
@Composable
fun Badge(
    count: Int,
    color: Color = CityRed,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Card(
        modifier = modifier
            .scale(pulseAnimation)
            .size(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CityWhite
            )
        }
    }
}

