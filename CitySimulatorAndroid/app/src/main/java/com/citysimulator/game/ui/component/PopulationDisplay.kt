package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.ui.theme.*

/**
 * 人口显示组件
 * 
 * 显示城市人口信息、增长率和容量
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun PopulationDisplay(
    currentPopulation: Int,
    populationCapacity: Int,
    growthRate: Double,
    isAtCapacity: Boolean,
    modifier: Modifier = Modifier
) {
    val capacityRatio = if (populationCapacity > 0) {
        currentPopulation.toFloat() / populationCapacity.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = capacityRatio.coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "population_progress"
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isAtCapacity) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "人口",
                    tint = if (isAtCapacity) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "城市人口",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 容量状态指示器
                if (isAtCapacity) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "容量已满",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 人口数量
            Text(
                text = "$currentPopulation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAtCapacity) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            
            // 容量信息
            Text(
                text = "/ $populationCapacity",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 进度条
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isAtCapacity) {
                    MaterialTheme.colorScheme.error
                } else if (capacityRatio > 0.8f) {
                    Color(0xFFFF9800) // 橙色警告
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 增长率
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "增长率",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = if (growthRate > 0) {
                        "+${String.format("%.1f", growthRate)}%/h"
                    } else {
                        "${String.format("%.1f", growthRate)}%/h"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (growthRate > 0) {
                        Color(0xFF4CAF50) // 绿色
                    } else if (growthRate < 0) {
                        Color(0xFFF44336) // 红色
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

/**
 * 人口增长因素显示组件
 * 
 * 显示影响人口增长的各种因素
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun PopulationFactorsDisplay(
    factors: com.citysimulator.game.ai.PopulationGrowthFactors?,
    modifier: Modifier = Modifier
) {
    if (factors == null) return
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "增长因素",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 住房因素
            FactorItem(
                label = "住房",
                factor = factors.housingFactor,
                icon = Icons.Default.Home
            )
            
            // 就业因素
            FactorItem(
                label = "就业",
                factor = factors.employmentFactor,
                icon = Icons.Default.Work
            )
            
            // 公共服务因素
            FactorItem(
                label = "公共服务",
                factor = factors.serviceFactor,
                icon = Icons.Default.LocalHospital
            )
            
            // 经济因素
            FactorItem(
                label = "经济",
                factor = factors.economicFactor,
                icon = Icons.Default.AttachMoney
            )
            
            // 环境因素
            FactorItem(
                label = "环境",
                factor = factors.environmentalFactor,
                icon = Icons.Default.Eco
            )
            
            // 资源因素
            FactorItem(
                label = "资源",
                factor = factors.resourceFactor,
                icon = Icons.Default.Inventory
            )
        }
    }
}

@Composable
private fun FactorItem(
    label: String,
    factor: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = getFactorColor(factor),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = String.format("%.1f", factor),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = getFactorColor(factor)
        )
    }
}

@Composable
private fun getFactorColor(factor: Double): Color {
    return when {
        factor >= 1.2 -> Color(0xFF4CAF50) // 绿色 - 优秀
        factor >= 1.0 -> Color(0xFF8BC34A) // 浅绿色 - 良好
        factor >= 0.8 -> Color(0xFFFF9800) // 橙色 - 一般
        factor >= 0.6 -> Color(0xFFFF5722) // 深橙色 - 较差
        else -> Color(0xFFF44336) // 红色 - 差
    }
}

/**
 * 人口趋势图表组件
 * 
 * 显示人口变化趋势
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun PopulationTrendChart(
    populationHistory: List<com.citysimulator.game.ui.viewmodel.PopulationRecord>,
    modifier: Modifier = Modifier
) {
    if (populationHistory.isEmpty()) return
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "人口趋势",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 简单的趋势显示
            val maxPopulation = populationHistory.maxOfOrNull { it.population } ?: 0
            val minPopulation = populationHistory.minOfOrNull { it.population } ?: 0
            
            if (maxPopulation > minPopulation) {
                Text(
                    text = "最高: $maxPopulation",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "最低: $minPopulation",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 简单的条形图
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    populationHistory.takeLast(10).forEach { record ->
                        val height = if (maxPopulation > minPopulation) {
                            ((record.population - minPopulation).toFloat() / (maxPopulation - minPopulation) * 40).coerceAtLeast(2f)
                        } else 2f
                        
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(height.dp)
                                .background(
                                    color = if (record.change > 0) {
                                        Color(0xFF4CAF50)
                                    } else if (record.change < 0) {
                                        Color(0xFFF44336)
                                    } else {
                                        Color(0xFF9E9E9E)
                                    },
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}
