@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.*
import com.citysimulator.game.data.model.WeatherType
import com.citysimulator.game.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 顶部状态栏组件
 * 
 * 显示城市基本信息、时间、天气等状态信息。
 * 使用Material Design 3设计，支持动态更新。
 * 
 * @param city 当前城市信息
 * @param currentTime 当前时间
 * @param weatherType 天气类型
 * @param modifier 修饰符
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun TopStatusBar(
    city: City?,
    currentTime: Date,
    weatherType: WeatherType,
    goldAmount: Int = 1000,
    monthlyIncome: Int = 0,
    currentPopulation: Int = 20,
    populationCapacity: Int = 0,
    growthRate: Double = 0.0,
    prosperityScore: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1A237E).copy(alpha = 0.95f),
                        Color(0xFF283593).copy(alpha = 0.95f),
                        Color(0xFF3F51B5).copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧金币、人口和繁荣度显示
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GoldWithIncomeDisplay(
                    goldAmount = goldAmount,
                    monthlyIncome = monthlyIncome
                )

                AnimatedStatDisplay(
                    icon = Icons.Default.People,
                    value = currentPopulation,
                    color = Color(0xFF4CAF50),
                    label = "人口"
                )

                AnimatedStatDisplay(
                    icon = Icons.Default.TrendingUp,
                    value = prosperityScore,
                    color = Color(0xFF2196F3),
                    label = "繁荣"
                )
            }

            // 右侧时间与天气显示
            EnhancedTimeWeatherSection(
                currentTime = currentTime,
                weatherType = weatherType
            )
        }
    }
}

/**
 * 金币与收入显示组件
 * 
 * 显示金币数量和每月收入
 * 
 * @param goldAmount 金币数量
 * @param monthlyIncome 每月收入
 * @param modifier 修饰符
 */
@Composable
private fun GoldWithIncomeDisplay(
    goldAmount: Int,
    monthlyIncome: Int,
    modifier: Modifier = Modifier
) {
    // 数值变化动画
    val animatedGold by animateIntAsState(
        targetValue = goldAmount,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "gold_animation"
    )
    
    // 图标脉冲动画
    val infiniteTransition = rememberInfiniteTransition(label = "gold_pulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )
    
    Row(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFFFD700).copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "金币",
            tint = Color(0xFFFFD700),
            modifier = Modifier
                .size(22.dp)
                .scale(iconScale)
        )
        
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = animatedGold.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = if (monthlyIncome >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (monthlyIncome >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = if (monthlyIncome >= 0) "+$monthlyIncome/月" else "$monthlyIncome/月",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (monthlyIncome >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * 动画数据显示组件
 * 
 * 带有图标和动画效果的数据显示
 */
@Composable
private fun AnimatedStatDisplay(
    icon: ImageVector,
    value: Int,
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    // 数值变化动画
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "stat_animation"
    )
    
    // 图标脉冲动画
    val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )
    
    Row(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier
                .size(20.dp)
                .scale(iconScale)
        )
        
        Text(
            text = animatedValue.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 增强的时间天气显示
 */
@Composable
private fun EnhancedTimeWeatherSection(
    currentTime: Date,
    weatherType: WeatherType
) {
    // 格式化为游戏日期和时间
    val calendar = remember(currentTime) { Calendar.getInstance().apply { time = currentTime } }
    val dateString = remember(currentTime) {
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        "${month}月${day}日"
    }
    val timeString = remember(currentTime) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        String.format("%02d:%02d", hour, minute)
    }
    
    val weatherIcon = when (weatherType) {
        WeatherType.SUNNY -> "☀️"
        WeatherType.CLOUDY -> "☁️"
        WeatherType.RAINY -> "🌧️"
        WeatherType.SNOWY -> "❄️"
        WeatherType.FOGGY -> "🌫️"
        WeatherType.STORMY -> "⛈️"
    }
    
    Row(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = weatherIcon,
            style = MaterialTheme.typography.titleLarge
        )
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = dateString,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Normal
            )
            Text(
                text = timeString,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 城市信息区域
 * 
 * 显示城市名称、等级、经验等信息。
 * 
 * @param city 城市信息
 */
@Composable
private fun CityInfoSection(city: City?) {
    Column {
        // 城市名称和等级
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city?.name ?: "我的城市",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CityOnSurface
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 城市等级徽章
            CityLevelBadge(level = city?.level ?: 1)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 经验进度条
        city?.let {
            ExperienceProgressBar(
                currentExp = it.experience,
                requiredExp = it.getRequiredExperienceForNextLevel(),
                level = it.level
            )
        }
    }
}

/**
 * 时间和天气区域
 * 
 * 显示当前时间和天气信息。
 * 
 * @param currentTime 当前时间
 * @param weatherType 天气类型
 */
@Composable
private fun TimeWeatherSection(
    currentTime: Date,
    weatherType: WeatherType
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // 时间显示
        TimeDisplay(currentTime = currentTime)
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 天气显示
        WeatherDisplay(weatherType = weatherType)
    }
}

/**
 * 城市等级徽章
 * 
 * 显示城市等级，带有动画效果。
 * 
 * @param level 城市等级
 */
@Composable
fun CityLevelBadge(level: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "level_badge")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .scale(scale)
            .size(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = CityBlue
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = CityWhite
            )
        }
    }
}

/**
 * 经验进度条
 * 
 * 显示当前等级的经验进度。
 * 
 * @param currentExp 当前经验值
 * @param requiredExp 升级所需经验值
 * @param level 当前等级
 */
@Composable
fun ExperienceProgressBar(
    currentExp: Int,
    requiredExp: Int,
    level: Int
) {
    val progress = if (requiredExp > 0) {
        (currentExp.toFloat() / requiredExp).coerceIn(0f, 1f)
    } else 1f
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "经验",
                style = MaterialTheme.typography.labelMedium,
                color = CityOnSurfaceVariant
            )
            Text(
                text = "$currentExp / $requiredExp",
                style = MaterialTheme.typography.labelMedium,
                color = CityOnSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = CityBlue,
            trackColor = CitySurfaceVariant
        )
    }
}

/**
 * 时间显示组件
 * 
 * 显示当前游戏时间。
 * 
 * @param currentTime 当前时间
 */
@Composable
fun TimeDisplay(currentTime: Date) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
    
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = timeFormat.format(currentTime),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CityOnSurface
        )
        Text(
            text = dateFormat.format(currentTime),
            style = MaterialTheme.typography.labelSmall,
            color = CityOnSurfaceVariant
        )
    }
}

/**
 * 天气显示组件
 * 
 * 显示当前天气信息。
 * 
 * @param weatherType 天气类型
 */
@Composable
fun WeatherDisplay(weatherType: WeatherType) {
    val (icon, color, text) = when (weatherType) {
        WeatherType.SUNNY -> Triple(Icons.Default.WbSunny, SunnyColor, "晴朗")
        WeatherType.CLOUDY -> Triple(Icons.Default.Cloud, CloudyColor, "多云")
        WeatherType.RAINY -> Triple(Icons.Default.Grain, RainyColor, "下雨")
        WeatherType.STORMY -> Triple(Icons.Default.Thunderstorm, StormyColor, "暴风雨")
        WeatherType.SNOWY -> Triple(Icons.Default.AcUnit, SnowyColor, "下雪")
        WeatherType.FOGGY -> Triple(Icons.Default.BlurOn, FoggyColor, "雾霾")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = CityOnSurfaceVariant
        )
    }
}

/**
 * 建筑组件
 * 
 * 显示单个建筑，支持点击交互和状态显示。
 * 
 * @param building 建筑信息
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun BuildingComponent(
    building: Building,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "building")
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .scale(scaleAnimation)
            .border(
                width = 1.dp,
                color = getBuildingColor(building.type).copy(alpha = glowAnimation),
                shape = RoundedCornerShape(4.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = getBuildingColor(building.type).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (building.isUnderConstruction) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            // 建筑图标
            Icon(
                imageVector = getBuildingIcon(building.type),
                contentDescription = building.getDisplayName(),
                tint = getBuildingColor(building.type),
                modifier = Modifier.size(16.dp)
            )
            
            // 状态指示器
            if (building.isUnderConstruction || building.isUpgrading || building.isMaintenanceRequired) {
                StatusIndicator(building = building)
            }
        }
    }
}

/**
 * 状态指示器
 * 
 * 显示建筑的状态信息。
 * 
 * @param building 建筑信息
 */
@Composable
private fun StatusIndicator(building: Building) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        when {
            building.isUnderConstruction -> {
                StatusDot(
                    color = ConstructionColor,
                    icon = Icons.Default.Build
                )
            }
            building.isUpgrading -> {
                StatusDot(
                    color = UpgradeColor,
                    icon = Icons.Default.TrendingUp
                )
            }
            building.isMaintenanceRequired -> {
                StatusDot(
                    color = MaintenanceColor,
                    icon = Icons.Default.Warning
                )
            }
        }
    }
}

/**
 * 状态点
 * 
 * 显示建筑状态的小圆点。
 * 
 * @param color 颜色
 * @param icon 图标
 */
@Composable
private fun StatusDot(
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.size(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CityWhite,
                modifier = Modifier.size(8.dp)
            )
        }
    }
}

/**
 * 获取建筑颜色
 * 
 * @param type 建筑类型
 * @return 建筑对应的颜色
 */
private fun getBuildingColor(type: BuildingType): Color {
    return when (type) {
        BuildingType.HOUSE, BuildingType.APARTMENT, BuildingType.VILLA, BuildingType.SKYSCRAPER -> ResidentialColor
        BuildingType.SHOP, BuildingType.SUPERMARKET, BuildingType.MALL, BuildingType.RESTAURANT, BuildingType.HOTEL, BuildingType.BANK, BuildingType.OFFICE -> CommercialColor
        BuildingType.FARM, BuildingType.LUMBER_MILL, BuildingType.QUARRY, BuildingType.STEEL_MILL, BuildingType.FOOD_FACTORY, BuildingType.BAKERY, BuildingType.SAWMILL, BuildingType.POWER_PLANT, BuildingType.FACTORY, BuildingType.SOLAR_PLANT, BuildingType.WIND_FARM, BuildingType.RECYCLING_CENTER, BuildingType.WASTE_MANAGEMENT, BuildingType.WATER_TOWER, BuildingType.WATER_TREATMENT_PLANT -> IndustrialColor
        BuildingType.SCHOOL, BuildingType.HOSPITAL, BuildingType.POLICE_STATION, BuildingType.FIRE_STATION, BuildingType.PARK, BuildingType.LIBRARY, BuildingType.UNIVERSITY, BuildingType.MUSEUM, BuildingType.THEATER, BuildingType.SMART_CENTER, BuildingType.SPACE_CENTER, BuildingType.AI_CENTER, BuildingType.STADIUM -> PublicColor
        BuildingType.ROAD, BuildingType.BRIDGE, BuildingType.BUS_STOP, BuildingType.SUBWAY_STATION -> TransportationColor
    }
}

/**
 * 获取建筑图标
 * 
 * @param type 建筑类型
 * @return 建筑对应的图标
 */
private fun getBuildingIcon(type: BuildingType): ImageVector {
    return when (type) {
        BuildingType.HOUSE -> Icons.Default.Home
        BuildingType.APARTMENT -> Icons.Default.Apartment
        BuildingType.VILLA -> Icons.Default.Villa
        BuildingType.SKYSCRAPER -> Icons.Default.Business
        BuildingType.SHOP -> Icons.Default.Store
        BuildingType.SUPERMARKET -> Icons.Default.ShoppingCart
        BuildingType.MALL -> Icons.Default.ShoppingBag
        BuildingType.RESTAURANT -> Icons.Default.Restaurant
        BuildingType.HOTEL -> Icons.Default.Hotel
        BuildingType.FARM -> Icons.Default.Agriculture
        BuildingType.LUMBER_MILL -> Icons.Default.Forest
        BuildingType.QUARRY -> Icons.Default.Landscape
        BuildingType.STEEL_MILL -> Icons.Default.PrecisionManufacturing
        BuildingType.FOOD_FACTORY -> Icons.Default.Agriculture
        BuildingType.BAKERY -> Icons.Default.Restaurant
        BuildingType.SAWMILL -> Icons.Default.Construction
        BuildingType.POWER_PLANT -> Icons.Default.Power
        BuildingType.SCHOOL -> Icons.Default.School
        BuildingType.HOSPITAL -> Icons.Default.LocalHospital
        BuildingType.POLICE_STATION -> Icons.Default.LocalPolice
        BuildingType.FIRE_STATION -> Icons.Default.LocalFireDepartment
        BuildingType.PARK -> Icons.Default.Park
        BuildingType.LIBRARY -> Icons.Default.LocalLibrary
        BuildingType.ROAD -> Icons.Default.AltRoute
        BuildingType.BRIDGE -> Icons.Default.Architecture
        BuildingType.BUS_STOP -> Icons.Default.BusAlert
        BuildingType.SUBWAY_STATION -> Icons.Default.Train
        BuildingType.FACTORY -> Icons.Default.PrecisionManufacturing
        BuildingType.SOLAR_PLANT -> Icons.Default.SolarPower
        BuildingType.WIND_FARM -> Icons.Default.Air
        BuildingType.RECYCLING_CENTER -> Icons.Default.Recycling
        BuildingType.SMART_CENTER -> Icons.Default.SmartToy
        BuildingType.UNIVERSITY -> Icons.Default.School
        BuildingType.MUSEUM -> Icons.Default.Museum
        BuildingType.THEATER -> Icons.Default.TheaterComedy
        BuildingType.SPACE_CENTER -> Icons.Default.RocketLaunch
        BuildingType.AI_CENTER -> Icons.Default.Psychology
        BuildingType.WASTE_MANAGEMENT -> Icons.Default.Delete
        BuildingType.WATER_TOWER -> Icons.Default.Water
        BuildingType.WATER_TREATMENT_PLANT -> Icons.Default.WaterDrop
        BuildingType.BANK -> Icons.Default.AccountBalance
        BuildingType.OFFICE -> Icons.Default.Business
        BuildingType.STADIUM -> Icons.Default.Stadium
    }
}

/**
 * 人口显示组件
 *
 * 显示当前人口数量，支持动画效果。
 *
 * @param currentPopulation 当前人口
 * @param populationCapacity 人口容量
 * @param growthRate 增长率
 * @param modifier 修饰符
 */
@Composable
fun PopulationDisplay(
    currentPopulation: Int,
    populationCapacity: Int,
    growthRate: Double,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "population")
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CityPurple.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = CityPurple.copy(alpha = glowAnimation),
                    shape = RoundedCornerShape(6.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = "人口",
                tint = CityPurple,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = currentPopulation.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CityOnSurface
            )
        }
    }
}

/**
 * 金币显示组件
 *
 * 显示当前金币数量，支持动画效果。
 *
 * @param goldAmount 金币数量
 * @param modifier 修饰符
 */
@Composable
fun GoldDisplay(
    goldAmount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gold")
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CityGold.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = CityGold.copy(alpha = glowAnimation),
                    shape = RoundedCornerShape(6.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "金币",
                tint = CityGold,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = goldAmount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = CityOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 人口紧凑显示组件
 * 
 * 以紧凑的方式显示城市人口，类似金币显示。
 * 
 * @param currentPopulation 当前人口
 * @param modifier 修饰符
 */
@Composable
fun PopulationCompactDisplay(
    currentPopulation: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "population_glow")
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
            containerColor = CityGreen.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = CityGreen.copy(alpha = glowAnimation),
                    shape = RoundedCornerShape(6.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = "人口",
                tint = CityGreen,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = currentPopulation.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = CityOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 繁荣度紧凑显示组件
 * 
 * 以紧凑的方式显示城市繁荣度，类似金币显示。
 * 
 * @param prosperityScore 繁荣度分数
 * @param modifier 修饰符
 */
@Composable
fun ProsperityCompactDisplay(
    prosperityScore: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "prosperity_glow")
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
            containerColor = CityBlue.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = CityBlue.copy(alpha = glowAnimation),
                    shape = RoundedCornerShape(6.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = "繁荣度",
                tint = CityBlue,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = prosperityScore.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = CityOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

