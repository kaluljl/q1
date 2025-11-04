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
    
    // 悬浮动画（减小幅度，更自然）
    val infiniteTransition = rememberInfiniteTransition(label = "hover")
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f, // 从4f减小到1.5f，更subtle
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic), // 从2000ms增加到3000ms，更慢
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
        // 增强的3D阴影效果
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 3.dp, x = 3.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(8.dp),
                    spotColor = Color.Black.copy(alpha = 0.4f)
                )
                .background(
                    color = Color.Black.copy(alpha = 0.25f),
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
                    // 建筑图标/emoji（更大更清晰）
                    Text(
                        text = getBuildingEmoji(building.type),
                        fontSize = 28.sp, // 从32sp改为28sp，更合适的大小
                        modifier = Modifier
                            .scale(buildProgress)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    // 建筑名称（更清晰的文字效果）
                    Text(
                        text = building.getDisplayName(),
                        fontSize = 8.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .shadow(
                                elevation = 1.dp,
                                shape = RoundedCornerShape(2.dp)
                            )
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
 * 获取建筑颜色 - 更丰富的配色方案
 */
private fun getBuildingColor(type: BuildingType): Color {
    return when (type) {
        // 住宅类：温暖的色调
        BuildingType.HOUSE -> Color(0xFFFF8A80) // 粉红色 - 小房子
        BuildingType.APARTMENT -> Color(0xFFFF5252) // 红色 - 公寓
        BuildingType.VILLA -> Color(0xFFFFAB91) // 橙粉色 - 别墅
        BuildingType.SKYSCRAPER -> Color(0xFFE57373) // 深红色 - 摩天大楼
        
        // 商业类：活力的橙黄色调
        BuildingType.SHOP -> Color(0xFFFFD54F) // 黄色 - 商店
        BuildingType.SUPERMARKET -> Color(0xFFFFCA28) // 金黄色 - 超市
        BuildingType.MALL -> Color(0xFFFFA726) // 橙色 - 商场
        BuildingType.RESTAURANT -> Color(0xFFFF9800) // 深橙色 - 餐厅
        BuildingType.HOTEL -> Color(0xFFFFB74D) // 浅橙色 - 酒店
        
        // 工业类：冷静的灰蓝色调
        BuildingType.FACTORY -> Color(0xFF90A4AE) // 蓝灰色 - 工厂
        BuildingType.STEEL_MILL -> Color(0xFF78909C) // 深蓝灰 - 钢铁厂
        BuildingType.POWER_PLANT -> Color(0xFF607D8B) // 灰蓝色 - 发电厂
        BuildingType.FARM -> Color(0xFF8BC34A) // 浅绿色 - 农场
        
        // 公共服务类：清新的绿色调
        BuildingType.SCHOOL -> Color(0xFF81C784) // 绿色 - 学校
        BuildingType.HOSPITAL -> Color(0xFF66BB6A) // 深绿色 - 医院
        BuildingType.POLICE_STATION -> Color(0xFF42A5F5) // 蓝色 - 警察局
        BuildingType.FIRE_STATION -> Color(0xFFEF5350) // 红色 - 消防局
        BuildingType.PARK -> Color(0xFF4CAF50) // 森林绿 - 公园
        
        // 交通类：中性的灰色调
        BuildingType.ROAD -> Color(0xFF9E9E9E) // 灰色 - 道路
        BuildingType.BRIDGE -> Color(0xFF757575) // 深灰色 - 桥梁
        BuildingType.BUS_STOP -> Color(0xFFBDBDBD) // 浅灰色 - 公交站
        BuildingType.SUBWAY_STATION -> Color(0xFF616161) // 炭灰色 - 地铁站
        
        else -> Color(0xFFB0BEC5) // 默认：蓝灰色
    }
}

/**
 * 获取建筑Emoji图标 - 更具体的图标
 */
private fun getBuildingEmoji(type: BuildingType): String {
    return when (type) {
        // 住宅类
        BuildingType.HOUSE -> "🏠" // 小房子
        BuildingType.APARTMENT -> "🏢" // 公寓楼
        BuildingType.VILLA -> "🏘️" // 别墅
        BuildingType.SKYSCRAPER -> "🏙️" // 摩天大楼
        
        // 商业类
        BuildingType.SHOP -> "🏪" // 便利店
        BuildingType.SUPERMARKET -> "🛒" // 超市
        BuildingType.MALL -> "🏬" // 购物中心
        BuildingType.RESTAURANT -> "🍽️" // 餐厅
        BuildingType.HOTEL -> "🏨" // 酒店
        
        // 工业类
        BuildingType.FACTORY -> "🏭" // 工厂
        BuildingType.STEEL_MILL -> "⚙️" // 钢铁厂
        BuildingType.POWER_PLANT -> "⚡" // 发电厂
        BuildingType.FARM -> "🌾" // 农场
        
        // 公共服务类
        BuildingType.SCHOOL -> "🏫" // 学校
        BuildingType.HOSPITAL -> "🏥" // 医院
        BuildingType.POLICE_STATION -> "🚓" // 警察局
        BuildingType.FIRE_STATION -> "🚒" // 消防局
        BuildingType.PARK -> "🌳" // 公园
        
        // 交通类
        BuildingType.ROAD -> "🛣️" // 道路
        BuildingType.BRIDGE -> "🌉" // 桥梁
        BuildingType.BUS_STOP -> "🚏" // 公交站
        BuildingType.SUBWAY_STATION -> "🚇" // 地铁站
        
        else -> "🏗️" // 默认：建设中
    }
}

