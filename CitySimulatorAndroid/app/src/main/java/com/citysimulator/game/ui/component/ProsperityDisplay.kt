package com.citysimulator.game.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.CityProsperity
import com.citysimulator.game.data.model.ProsperityLevel

/**
 * 城市繁荣度显示组件
 * 
 * 简洁显示城市总体繁荣度，包括：
 * - 繁荣度等级（贫困/发展中/中等/繁荣/兴旺/大都市）
 * - 繁荣度数值（无上限增长，基于建筑数量）
 * 
 * 繁荣度数值不代表百分比，而是象征城市的繁荣程度
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun ProsperityDisplay(
    prosperity: CityProsperity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：标题和等级
            Column {
                Text(
                    text = "城市繁荣度",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // 繁荣度等级标签
                Surface(
                    color = getProsperityColor(prosperity.prosperityLevel),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = prosperity.prosperityLevel.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 右侧：繁荣度数值
            Text(
                text = "${prosperity.overallProsperity.toInt()}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = getProsperityColor(prosperity.prosperityLevel)
            )
        }
    }
}

/**
 * 获取繁荣度等级对应的颜色
 */
private fun getProsperityColor(level: ProsperityLevel): Color {
    return when (level) {
        ProsperityLevel.POOR -> Color(0xFFFF5722)
        ProsperityLevel.DEVELOPING -> Color(0xFFFF9800)
        ProsperityLevel.MODERATE -> Color(0xFFFFC107)
        ProsperityLevel.PROSPEROUS -> Color(0xFF4CAF50)
        ProsperityLevel.THRIVING -> Color(0xFF2196F3)
        ProsperityLevel.METROPOLIS -> Color(0xFF9C27B0)
    }
}
