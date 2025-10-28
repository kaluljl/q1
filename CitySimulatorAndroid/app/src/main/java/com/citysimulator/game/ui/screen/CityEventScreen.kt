package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.EventType
import com.citysimulator.game.data.model.CityEvent
import com.citysimulator.game.data.model.EventOption
import com.citysimulator.game.ui.theme.*

/**
 * 城市事件界面
 * 
 * 显示城市事件和决策选项
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityEventScreen(
    event: CityEvent,
    onNavigateBack: () -> Unit,
    onEventResolved: (CityEvent, Int) -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部标题栏
        TopAppBar(
            title = { 
                Text(
                    "城市事件",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = getEventTypeColor(event.type),
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        // 事件内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 事件标题
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = getEventTypeColor(event.type)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = event.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    
                    Text(
                        text = getEventTypeDescription(event.type),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 事件描述
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = event.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 选项列表
            Text(
                text = "请选择你的决策：",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(event.options.size) { index ->
                    val option = event.options[index]
                    EventOptionCard(
                        option = option,
                        index = index,
                        isSelected = selectedOption == index,
                        onSelect = { selectedOption = index }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 确认按钮
            Button(
                onClick = {
                    selectedOption?.let { optionIndex ->
                        onEventResolved(event, optionIndex)
                        onNavigateBack()
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "确认决策",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EventOptionCard(
    option: EventOption,
    index: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${index + 1}. ${option.text}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            // 显示效果预览
            if (option.effect.goldChange != 0 || 
                option.effect.populationChange != 0 || 
                option.effect.prosperityChange != 0f) {
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (option.effect.goldChange != 0) {
                        EffectBadge(
                            text = if (option.effect.goldChange > 0) {
                                "+${option.effect.goldChange} 金币"
                            } else {
                                "${option.effect.goldChange} 金币"
                            },
                            color = if (option.effect.goldChange > 0) Color.Green else Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (option.effect.populationChange != 0) {
                        EffectBadge(
                            text = if (option.effect.populationChange > 0) {
                                "+${option.effect.populationChange} 人口"
                            } else {
                                "${option.effect.populationChange} 人口"
                            },
                            color = if (option.effect.populationChange > 0) Color.Green else Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (option.effect.prosperityChange != 0f) {
                        EffectBadge(
                            text = if (option.effect.prosperityChange > 0) {
                                "+${option.effect.prosperityChange.toInt()} 繁荣度"
                            } else {
                                "${option.effect.prosperityChange.toInt()} 繁荣度"
                            },
                            color = if (option.effect.prosperityChange > 0) Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun getEventTypeColor(type: EventType): Color {
    return when (type) {
        EventType.OPPORTUNITY -> Color(0xFF4CAF50) // 绿色
        EventType.CRISIS -> Color(0xFFF44336) // 红色
        EventType.DILEMMA -> Color(0xFFFF9800) // 橙色
    }
}

@Composable
private fun getEventTypeDescription(type: EventType): String {
    return when (type) {
        EventType.OPPORTUNITY -> "机遇事件"
        EventType.CRISIS -> "危机事件"
        EventType.DILEMMA -> "两难抉择"
    }
}
