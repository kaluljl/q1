package com.citysimulator.game.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernBottomBar(
    onBuildingClick: () -> Unit,
    onTaskClick: () -> Unit,
    onCitizenClick: () -> Unit,
    onPolicyClick: () -> Unit,
    onEconomyClick: (() -> Unit)? = null,
    onCitizenListClick: (() -> Unit)? = null,
    onThemeClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = currentTheme.backgroundGradient
                )
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 建筑按钮
        BottomBarButton(
            icon = "🏗️",
            label = "建筑",
            onClick = onBuildingClick,
            hasNotification = false,
            themeColors = currentTheme
        )
        
        // 任务按钮
        BottomBarButton(
            icon = "📋",
            label = "任务",
            onClick = onTaskClick,
            hasNotification = false,
            themeColors = currentTheme
        )
        
        // 心声按钮
        BottomBarButton(
            icon = "💭",
            label = "心声",
            onClick = onCitizenClick,
            hasNotification = false,
            themeColors = currentTheme
        )
        
        // 政策按钮
        BottomBarButton(
            icon = "📜",
            label = "政策",
            onClick = onPolicyClick,
            hasNotification = false,
            themeColors = currentTheme
        )
        
        // 经济按钮（可选）
        if (onEconomyClick != null) {
            BottomBarButton(
                icon = "💰",
                label = "经济",
                onClick = onEconomyClick,
                hasNotification = false,
                themeColors = currentTheme
            )
        }
        
        // 市民列表按钮（可选）
        if (onCitizenListClick != null) {
            BottomBarButton(
                icon = "👥",
                label = "市民",
                onClick = onCitizenListClick,
                hasNotification = false,
                themeColors = currentTheme
            )
        }
        
        // 主题选择按钮（可选）
        if (onThemeClick != null) {
            BottomBarButton(
                icon = "🎨",
                label = "主题",
                onClick = onThemeClick,
                hasNotification = false,
                themeColors = currentTheme
            )
        }
    }
}

@Composable
private fun BottomBarButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    hasNotification: Boolean,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        themeColors.primary.copy(alpha = 0.3f),
                        themeColors.primary.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
                
                // 通知红点
                if (hasNotification) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .background(Color.Red, shape = RoundedCornerShape(50))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = label,
                fontSize = 10.sp,
                color = themeColors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
