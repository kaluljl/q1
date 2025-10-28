package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.ai.*
import com.citysimulator.game.ui.theme.*

/**
 * 资源面板
 * 
 * 显示电力、水力、垃圾处理等资源的供需情况
 */
@Composable
fun ResourcePanel(
    balance: ResourceBalance,
    supply: ResourceSupply,
    demand: ResourceDemand,
    warnings: List<ResourceWarning>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "资源管理",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        // 资源卡片
        ResourceCard(
            icon = Icons.Default.ElectricBolt,
            iconColor = Color(0xFFFFEB3B),
            label = "电力",
            supply = supply.power,
            demand = demand.power,
            ratio = balance.powerRatio,
            status = balance.powerStatus
        )
        
        ResourceCard(
            icon = Icons.Default.WaterDrop,
            iconColor = Color(0xFF2196F3),
            label = "水力",
            supply = supply.water,
            demand = demand.water,
            ratio = balance.waterRatio,
            status = balance.waterStatus
        )
        
        ResourceCard(
            icon = Icons.Default.Delete,
            iconColor = Color(0xFF9E9E9E),
            label = "垃圾处理",
            supply = balance.wasteCapacity,
            demand = demand.waste,
            ratio = balance.wasteRatio,
            status = balance.wasteStatus
        )
        
        // 警告列表
        if (warnings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ 资源警告",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            warnings.forEach { warning ->
                ResourceWarningCard(warning)
            }
        }
    }
}

/**
 * 资源卡片
 */
@Composable
private fun ResourceCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    supply: Int,
    demand: Int,
    ratio: Float,
    status: ResourceHealthStatus
) {
    val statusColor = getStatusColor(status)
    val statusText = getStatusText(status)
    
    // 动画进度
    val animatedRatio by animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500), 
        label = "resourceRatio"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor
                        )
                    }
                }
                
                // 供需比例
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(ratio * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Text(
                        text = "${supply}/${demand}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 进度条
            Box(modifier = Modifier.fillMaxWidth()) {
                // 背景条
                LinearProgressIndicator(
                    progress = 1f ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color.LightGray.copy(alpha = 0.3f),
                    trackColor = Color.Transparent
                )
                
                // 实际进度条
                LinearProgressIndicator(
                    progress = animatedRatio ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = statusColor,
                    trackColor = Color.Transparent
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 详细信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("供应", supply.toString(), CityGreen)
                InfoItem("需求", demand.toString(), Color(0xFFFF9800))
                InfoItem("余量", (supply - demand).toString(), 
                    if (supply >= demand) CityGreen else Color.Red)
            }
        }
    }
}

/**
 * 信息项
 */
@Composable
private fun InfoItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * 资源警告卡片
 */
@Composable
private fun ResourceWarningCard(warning: ResourceWarning) {
    val backgroundColor = when (warning.severity) {
        WarningSeverity.CRITICAL -> Color(0xFFFFCDD2)
        WarningSeverity.HIGH -> Color(0xFFFFE0B2)
        WarningSeverity.MEDIUM -> Color(0xFFFFF9C4)
        WarningSeverity.LOW -> Color(0xFFE1F5FE)
    }
    
    val iconColor = when (warning.severity) {
        WarningSeverity.CRITICAL -> Color(0xFFD32F2F)
        WarningSeverity.HIGH -> Color(0xFFFF6F00)
        WarningSeverity.MEDIUM -> Color(0xFFFBC02D)
        WarningSeverity.LOW -> Color(0xFF1976D2)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = warning.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 ${warning.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 紧凑资源显示
 */
@Composable
fun CompactResourceDisplay(
    balance: ResourceBalance,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CompactResourceItem(
            icon = "⚡",
            ratio = balance.powerRatio,
            status = balance.powerStatus
        )
        
        CompactResourceItem(
            icon = "💧",
            ratio = balance.waterRatio,
            status = balance.waterStatus
        )
        
        CompactResourceItem(
            icon = "🗑️",
            ratio = balance.wasteRatio,
            status = balance.wasteStatus
        )
    }
}

/**
 * 紧凑资源项
 */
@Composable
private fun CompactResourceItem(
    icon: String,
    ratio: Float,
    status: ResourceHealthStatus
) {
    val statusColor = getStatusColor(status)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        CircularProgressIndicator(
            progress = ratio.coerceIn(0f, 1f) ,
            modifier = Modifier.size(32.dp),
            color = statusColor,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${(ratio * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

// 辅助函数

private fun getStatusColor(status: ResourceHealthStatus): Color {
    return when (status) {
        ResourceHealthStatus.HEALTHY -> Color(0xFF4CAF50)
        ResourceHealthStatus.EXCELLENT -> Color(0xFF4CAF50)
        ResourceHealthStatus.GOOD -> Color(0xFF8BC34A)
        ResourceHealthStatus.WARNING -> Color(0xFFFFC107)
        ResourceHealthStatus.CRITICAL -> Color(0xFFFF9800)
        ResourceHealthStatus.CRISIS -> Color(0xFFF44336)
    }
}

private fun getStatusText(status: ResourceHealthStatus): String {
    return when (status) {
        ResourceHealthStatus.HEALTHY -> "健康"
        ResourceHealthStatus.EXCELLENT -> "优秀"
        ResourceHealthStatus.GOOD -> "良好"
        ResourceHealthStatus.WARNING -> "警告"
        ResourceHealthStatus.CRITICAL -> "危机"
        ResourceHealthStatus.CRISIS -> "紧急"
    }
}

