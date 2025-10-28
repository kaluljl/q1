package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Resource
import com.citysimulator.game.data.model.ResourceType
import com.citysimulator.game.ui.theme.*

/**
 * 资源显示组件
 *
 * 显示当前城市的主要资源数量，支持水平滚动查看所有资源。
 * 使用Material Design 3设计，支持动画效果。
 *
 * @param resources 资源列表
 * @param modifier 修饰符
 *
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun ResourceDisplay(
    resources: List<Resource>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CitySurface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(resources) { resource ->
                ResourceItem(resource = resource)
            }
        }
    }
}

/**
 * 单个资源项组件
 *
 * 显示单个资源的图标、名称和数量。
 *
 * @param resource 资源数据
 * @param modifier 修饰符
 */
@Composable
private fun ResourceItem(
    resource: Resource,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "resource")
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = getResourceColor(resource.type).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .border(
                    width = 1.dp,
                    color = getResourceColor(resource.type).copy(alpha = glowAnimation),
                    shape = RoundedCornerShape(8.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getResourceIcon(resource.type),
                contentDescription = resource.getDisplayName(),
                tint = getResourceColor(resource.type),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${resource.getFormattedAmount()}${resource.getUnit()}",
                style = MaterialTheme.typography.bodySmall,
                color = CityOnSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * 获取资源颜色
 *
 * @param resourceType 资源类型
 * @return 对应的颜色
 */
private fun getResourceColor(resourceType: ResourceType): Color {
    return when (resourceType) {
        ResourceType.WOOD -> Color(0xFF8D6E63) // 棕色
        ResourceType.STONE -> Color(0xFF9E9E9E) // 灰色
        ResourceType.STEEL -> Color(0xFF607D8B) // 蓝灰色
        ResourceType.FOOD -> Color(0xFF4CAF50) // 绿色
        ResourceType.GOLD -> CityGold // 金色
        ResourceType.DIAMOND -> Color(0xFFE1F5FE) // 浅蓝色
        ResourceType.OIL -> Color(0xFF424242) // 深灰色
        ResourceType.COAL -> Color(0xFF212121) // 黑色
        ResourceType.ORE -> Color(0xFF795548) // 棕色
        ResourceType.RARE_EARTH -> Color(0xFF9C27B0) // 紫色
        ResourceType.COMPONENT -> Color(0xFF2196F3) // 蓝色
        ResourceType.PROCESSED_FOOD -> Color(0xFFFF9800) // 橙色
    }
}

/**
 * 获取资源图标
 *
 * @param resourceType 资源类型
 * @return 对应的图标
 */
private fun getResourceIcon(resourceType: ResourceType): ImageVector {
    return when (resourceType) {
        ResourceType.WOOD -> Icons.Default.Home
        ResourceType.STONE -> Icons.Default.Landscape
        ResourceType.STEEL -> Icons.Default.Build
        ResourceType.FOOD -> Icons.Default.Restaurant
        ResourceType.GOLD -> Icons.Default.MonetizationOn
        ResourceType.DIAMOND -> Icons.Default.Star
        ResourceType.OIL -> Icons.Default.LocalGasStation
        ResourceType.COAL -> Icons.Default.LocalFireDepartment
        ResourceType.ORE -> Icons.Default.Terrain
        ResourceType.RARE_EARTH -> Icons.Default.Science
        ResourceType.COMPONENT -> Icons.Default.Settings
        ResourceType.PROCESSED_FOOD -> Icons.Default.Fastfood
    }
}
