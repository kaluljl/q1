package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.BuildingType

/**
 * 增强版建筑组件
 * 
 * 包含3D效果、阴影、建造动画
 */
@Composable
fun EnhancedBuildingComponent(
    building: Building,
    isBeingBuilt: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    // 建造动画
    val buildProgress by animateFloatAsState(
        targetValue = if (isBeingBuilt) 1f else 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "build_animation"
    )
    
    // 点击缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "click_scale"
    )
    
    // 悬浮动画
    val infiniteTransition = rememberInfiniteTransition(label = "hover")
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover_offset"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .offset(y = (-hoverOffset).dp)
            .clickable { 
                isPressed = true
                onClick()
                isPressed = false
            }
    ) {
        // 3D阴影效果
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp, x = 4.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        // 主建筑卡片
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = getBuildingColor(building.type)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                getBuildingColor(building.type),
                                getBuildingColor(building.type).copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 建筑图标/emoji
                    Text(
                        text = getBuildingEmoji(building.type),
                        fontSize = 32.sp,
                        modifier = Modifier.scale(buildProgress)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 建筑名称
                    Text(
                        text = building.getDisplayName(),
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
                
                // 建造进度条
                if (isBeingBuilt) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(buildProgress)
                                .height(3.dp)
                                .background(Color(0xFF4CAF50))
                        )
                    }
                }
            }
        }
        
        // 闪光效果（刚建造完成时）
        if (buildProgress in 0.95f..1f && isBeingBuilt) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.White.copy(alpha = (1f - buildProgress) * 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

/**
 * 空网格单元格（增强版）
 * 
 * 带悬停效果和网格动画
 */
@Composable
fun EnhancedEmptyGridCell(
    isSelected: Boolean,
    canBuild: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = modifier
            .clickable(enabled = canBuild) { onClick() }
            .background(
                color = when {
                    isSelected -> Color(0xFF2196F3).copy(alpha = pulseAlpha) // 蓝色高亮
                    canBuild -> Color.White.copy(alpha = 0.05f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = when {
                    isSelected -> Color(0xFF2196F3) // 蓝色边框
                    canBuild -> Color.White.copy(alpha = 0.1f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        if (isSelected) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 获取建筑颜色
 */
private fun getBuildingColor(type: BuildingType): Color {
    // 根据建筑类型分配颜色
    return when {
        type == BuildingType.HOUSE || type == BuildingType.APARTMENT || type == BuildingType.VILLA || type == BuildingType.SKYSCRAPER -> 
            Color(0xFFE57373) // 住宅：红色系
        type == BuildingType.SHOP || type == BuildingType.SUPERMARKET || type == BuildingType.MALL || type == BuildingType.RESTAURANT || type == BuildingType.HOTEL -> 
            Color(0xFFFFA726) // 商业：橙色系
        type == BuildingType.FACTORY || type == BuildingType.STEEL_MILL || type == BuildingType.POWER_PLANT || type == BuildingType.FARM -> 
            Color(0xFF78909C) // 工业：灰色系
        type == BuildingType.SCHOOL || type == BuildingType.HOSPITAL || type == BuildingType.POLICE_STATION || type == BuildingType.FIRE_STATION || type == BuildingType.PARK -> 
            Color(0xFF66BB6A) // 公共：绿色系
        type == BuildingType.ROAD || type == BuildingType.BRIDGE || type == BuildingType.BUS_STOP || type == BuildingType.SUBWAY_STATION -> 
            Color(0xFF757575) // 交通：深灰色系
        else -> Color(0xFF9E9E9E) // 默认：灰色
    }
}

/**
 * 获取建筑Emoji图标
 */
private fun getBuildingEmoji(type: BuildingType): String {
    // 根据建筑类型分配图标
    return when {
        type == BuildingType.HOUSE || type == BuildingType.APARTMENT || type == BuildingType.VILLA || type == BuildingType.SKYSCRAPER -> 
            "🏠"
        type == BuildingType.SHOP || type == BuildingType.SUPERMARKET || type == BuildingType.MALL || type == BuildingType.RESTAURANT || type == BuildingType.HOTEL -> 
            "🏪"
        type == BuildingType.FACTORY || type == BuildingType.STEEL_MILL || type == BuildingType.POWER_PLANT || type == BuildingType.FARM -> 
            "🏭"
        type == BuildingType.SCHOOL || type == BuildingType.HOSPITAL || type == BuildingType.POLICE_STATION || type == BuildingType.FIRE_STATION || type == BuildingType.PARK -> 
            "🏛️"
        type == BuildingType.ROAD || type == BuildingType.BRIDGE || type == BuildingType.BUS_STOP || type == BuildingType.SUBWAY_STATION -> 
            "🛣️"
        else -> "🏗️"
    }
}

