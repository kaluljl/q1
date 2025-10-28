package com.citysimulator.game.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.Gender

/**
 * 简单市民标记
 * 
 * 在城市网格上显示简单的市民图标
 */
@Composable
fun SimpleCitizenMarker(
    citizen: Citizen,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(getCitizenColor(citizen)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (citizen.gender == Gender.MALE) "👨" else "👩",
            fontSize = 8.sp
        )
    }
}

/**
 * 市民网格覆盖层
 * 
 * 在整个城市网格上显示所有市民的位置
 */
@Composable
fun CitizenOverlay(
    citizens: List<Citizen>,
    gridColumns: Int,
    gridRows: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        citizens.forEach { citizen ->
            // 计算市民在网格中的位置
            val xPercent = citizen.currentX.toFloat() / gridColumns
            val yPercent = citizen.currentY.toFloat() / gridRows
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (xPercent * modifier.fillMaxWidth().hashCode()).dp,
                        y = (yPercent * modifier.fillMaxHeight().hashCode()).dp
                    )
            ) {
                SimpleCitizenMarker(citizen = citizen)
            }
        }
    }
}

/**
 * 获取市民颜色（根据状态）
 */
private fun getCitizenColor(citizen: Citizen): Color {
    return when {
        citizen.happiness > 0.7f -> Color(0xFF4CAF50) // 绿色 - 快乐
        citizen.happiness > 0.4f -> Color(0xFFFFC107) // 黄色 - 一般
        else -> Color(0xFFF44336) // 红色 - 不满
    }
}

