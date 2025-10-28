package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.CitizenFeedback
import com.citysimulator.game.data.model.FeedbackType
import com.citysimulator.game.ai.FeedbackUrgency
import com.citysimulator.game.ui.theme.*

/**
 * 市民心声显示组件
 * 在主界面显示重要的市民反馈，支持紧急程度颜色区分
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun CitizenFeedbackDisplay(
    feedbacks: List<CitizenFeedback>,
    onNavigateToFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 过滤出未解决的心声，按紧急程度排序
    val urgentFeedbacks = feedbacks
        .filter { !it.isResolved }
        .sortedByDescending { getFeedbackUrgency(it.type) }
        .take(3) // 最多显示3条
    
    if (urgentFeedbacks.isEmpty()) {
        return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "市民心声",
                        tint = CityBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "市民心声",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // 新消息数量
                    if (urgentFeedbacks.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = urgentFeedbacks.size.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                // 查看全部按钮
                TextButton(
                    onClick = onNavigateToFeedback,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = CityBlue
                    )
                ) {
                    Text(
                        text = "查看全部",
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "查看全部",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 心声列表
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(urgentFeedbacks) { feedback ->
                    FeedbackItem(
                        feedback = feedback,
                        onClick = onNavigateToFeedback
                    )
                }
            }
        }
    }
}

/**
 * 单个心声项目
 */
@Composable
private fun FeedbackItem(
    feedback: CitizenFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urgency = getFeedbackUrgency(feedback.type)
    val urgencyColor = getUrgencyColor(urgency)
    val urgencyIcon = getUrgencyIcon(urgency)
    
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = urgencyColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 紧急程度图标
            Icon(
                imageVector = urgencyIcon,
                contentDescription = "紧急程度",
                tint = urgencyColor,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 心声内容
            Text(
                text = feedback.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 处理按钮
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "处理",
                    tint = urgencyColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 获取反馈类型的紧急程度
 */
private fun getFeedbackUrgency(type: FeedbackType): FeedbackUrgency {
    return when (type) {
        FeedbackType.POLLUTION_COMPLAINT -> FeedbackUrgency.HIGH
        FeedbackType.LACK_JOBS -> FeedbackUrgency.MEDIUM
        FeedbackType.TRAFFIC_CONGESTION -> FeedbackUrgency.MEDIUM
        FeedbackType.NEED_EDUCATION -> FeedbackUrgency.MEDIUM
        FeedbackType.NEED_HEALTHCARE -> FeedbackUrgency.HIGH
        FeedbackType.NEED_PARK -> FeedbackUrgency.LOW
        else -> FeedbackUrgency.LOW
    }
}

/**
 * 获取紧急程度对应的颜色
 */
private fun getUrgencyColor(urgency: FeedbackUrgency): Color {
    return when (urgency) {
        FeedbackUrgency.LOW -> Color(0xFF2196F3)      // 蓝色
        FeedbackUrgency.MEDIUM -> Color(0xFFFF9800)    // 黄色
        FeedbackUrgency.HIGH -> Color(0xFFF44336)      // 红色
        FeedbackUrgency.CRITICAL -> Color(0xFF8B0000)  // 深红色
    }
}

/**
 * 获取紧急程度对应的图标
 */
private fun getUrgencyIcon(urgency: FeedbackUrgency): ImageVector {
    return when (urgency) {
        FeedbackUrgency.LOW -> Icons.Default.Lightbulb
        FeedbackUrgency.MEDIUM -> Icons.Default.Warning
        FeedbackUrgency.HIGH -> Icons.Default.Error
        FeedbackUrgency.CRITICAL -> Icons.Default.Dangerous
    }
}

/**
 * 扩展的FeedbackType枚举
 */
enum class FeedbackUrgency {
    LOW,        // 蓝色 - 改善建议
    MEDIUM,     // 黄色 - 重要需求  
    HIGH,       // 红色 - 紧急问题
    CRITICAL    // 深红 - 危机事件
}
