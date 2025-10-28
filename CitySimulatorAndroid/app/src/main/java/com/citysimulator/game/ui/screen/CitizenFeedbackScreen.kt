package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.FeedbackType
import com.citysimulator.game.data.model.CitizenFeedback
import com.citysimulator.game.ui.theme.*

/**
 * 市民反馈界面
 * 
 * 显示市民的需求和反馈
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenFeedbackScreen(
    feedbacks: List<CitizenFeedback>,
    isLoading: Boolean = false,
    onNavigateBack: () -> Unit,
    onResolveFeedback: (String) -> Unit = {},
    onDeleteFeedback: (String) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部标题栏
        TopAppBar(
            title = { 
                Text(
                    "市民心声",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        // 反馈统计
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeedbackStatItem(
                    icon = Icons.Default.Warning,
                    count = feedbacks.count { it.priority >= 4 },
                    label = "紧急",
                    color = Color.Red
                )
                
                FeedbackStatItem(
                    icon = Icons.Default.Info,
                    count = feedbacks.count { it.priority == 2 || it.priority == 3 },
                    label = "一般",
                    color = Color.Blue
                )
                
                FeedbackStatItem(
                    icon = Icons.Default.ThumbUp,
                    count = feedbacks.count { it.priority == 1 },
                    label = "低优先级",
                    color = Color.Green
                )
            }
        }
        
        // 反馈列表
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(feedbacks) { feedback ->
                    FeedbackCard(
                        feedback = feedback,
                        onResolve = { onResolveFeedback(feedback.id) },
                        onDelete = { onDeleteFeedback(feedback.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeedbackCard(
    feedback: CitizenFeedback,
    onResolve: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getFeedbackTypeColor(feedback.type)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getFeedbackTypeIcon(feedback.type),
                        contentDescription = null,
                        tint = getFeedbackTypeIconColor(feedback.type),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = getFeedbackTypeLabel(feedback.type),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getFeedbackTypeIconColor(feedback.type)
                    )
                }
                
                Text(
                    text = getPriorityLabel(feedback.priority),
                    fontSize = 12.sp,
                    color = getPriorityColor(feedback.priority),
                    modifier = Modifier
                        .background(
                            color = getPriorityColor(feedback.priority).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 操作按钮
            if (!feedback.isResolved) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onResolve,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Green
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "解决",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("解决")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                }
            } else {
                // 已解决状态
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "已解决",
                        fontSize = 12.sp,
                        color = Color.Green,
                        modifier = Modifier
                            .background(
                                color = Color.Green.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feedback.message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getFeedbackSourceLabel(feedback.source),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
            Text(
                text = feedback.createdAt,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            }
        }
    }
}

@Composable
private fun getFeedbackTypeColor(type: FeedbackType): Color {
    return when (type) {
        FeedbackType.NEED_PARK -> Color(0xFFE8F5E8) // 浅绿色
        FeedbackType.TRAFFIC_CONGESTION -> Color(0xFFFFEBEE) // 浅红色
        FeedbackType.LACK_JOBS -> Color(0xFFFFF3E0) // 浅橙色
        FeedbackType.NEED_EDUCATION -> Color(0xFFE3F2FD) // 浅蓝色
        FeedbackType.NEED_HEALTHCARE -> Color(0xFFF3E5F5) // 浅紫色
        FeedbackType.POLLUTION_COMPLAINT -> Color(0xFFE8F5E8) // 浅绿色
        FeedbackType.HOUSING_SHORTAGE -> Color(0xFFFFEBEE) // 浅红色
        FeedbackType.ENTERTAINMENT_NEED -> Color(0xFFFFF3E0) // 浅橙色
    }
}

@Composable
private fun getFeedbackTypeIcon(type: FeedbackType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        FeedbackType.NEED_PARK -> Icons.Default.Park
        FeedbackType.TRAFFIC_CONGESTION -> Icons.Default.Traffic
        FeedbackType.LACK_JOBS -> Icons.Default.Work
        FeedbackType.NEED_EDUCATION -> Icons.Default.School
        FeedbackType.NEED_HEALTHCARE -> Icons.Default.LocalHospital
        FeedbackType.POLLUTION_COMPLAINT -> Icons.Default.Eco
        FeedbackType.HOUSING_SHORTAGE -> Icons.Default.Home
        FeedbackType.ENTERTAINMENT_NEED -> Icons.Default.Sports
    }
}

@Composable
private fun getFeedbackTypeIconColor(type: FeedbackType): Color {
    return when (type) {
        FeedbackType.NEED_PARK -> Color.Green
        FeedbackType.TRAFFIC_CONGESTION -> Color.Red
        FeedbackType.LACK_JOBS -> Color(0xFFFF9800)
        FeedbackType.NEED_EDUCATION -> Color.Blue
        FeedbackType.NEED_HEALTHCARE -> Color.Magenta
        FeedbackType.POLLUTION_COMPLAINT -> Color.Green
        FeedbackType.HOUSING_SHORTAGE -> Color.Red
        FeedbackType.ENTERTAINMENT_NEED -> Color(0xFFFF9800)
    }
}

@Composable
private fun getFeedbackTypeLabel(type: FeedbackType): String {
    return when (type) {
        FeedbackType.NEED_PARK -> "需要公园"
        FeedbackType.TRAFFIC_CONGESTION -> "交通拥堵"
        FeedbackType.LACK_JOBS -> "缺少工作"
        FeedbackType.NEED_EDUCATION -> "需要教育"
        FeedbackType.NEED_HEALTHCARE -> "需要医疗"
        FeedbackType.POLLUTION_COMPLAINT -> "污染投诉"
        FeedbackType.HOUSING_SHORTAGE -> "住房短缺"
        FeedbackType.ENTERTAINMENT_NEED -> "需要娱乐"
    }
}

@Composable
private fun getPriorityLabel(priority: Int): String {
    return when (priority) {
        1 -> "低"
        2 -> "中"
        3 -> "高"
        4 -> "紧急"
        else -> "未知"
    }
}

@Composable
private fun getPriorityColor(priority: Int): Color {
    return when (priority) {
        1 -> Color.Gray
        2 -> Color.Blue
        3 -> Color(0xFFFF9800)
        4 -> Color.Red
        else -> Color.Gray
    }
}

@Composable
private fun getFeedbackSourceLabel(source: com.citysimulator.game.data.model.FeedbackSource): String {
    return when (source) {
        com.citysimulator.game.data.model.FeedbackSource.CITIZEN -> "市民"
        com.citysimulator.game.data.model.FeedbackSource.ENVIRONMENTAL_GROUP -> "环保组织"
        com.citysimulator.game.data.model.FeedbackSource.EDUCATION_GROUP -> "教育组织"
        com.citysimulator.game.data.model.FeedbackSource.HEALTH_GROUP -> "健康组织"
        else -> "其他"
    }
}

