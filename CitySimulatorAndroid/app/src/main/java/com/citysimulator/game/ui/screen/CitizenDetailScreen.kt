package com.citysimulator.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.data.model.*
import com.citysimulator.game.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 市民详情屏幕
 * 
 * 显示单个市民的详细信息和生活状态
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenDetailScreen(
    citizen: Citizen,
    thoughts: List<CitizenThought> = emptyList(),
    onBack: () -> Unit,
    onStartAIChat: ((Citizen) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(citizen.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    // AI对话按钮
                    if (onStartAIChat != null) {
                        IconButton(onClick = { onStartAIChat(citizen) }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "AI对话",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "AI对话",
                            color = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CityBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CityBlue.copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // 市民头像和基本信息
            CitizenHeader(citizen)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 当前状态
            CurrentStatusCard(citizen)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 需求条
            NeedsCard(citizen)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 日常信息
            DailyInfoCard(citizen)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 最近想法
            if (thoughts.isNotEmpty()) {
                ThoughtsCard(thoughts)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // 统计信息
            StatisticsCard(citizen)
        }
    }
}

/**
 * 市民头像和基本信息
 */
@Composable
private fun CitizenHeader(citizen: Citizen) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when (citizen.gender) {
                            Gender.MALE -> Color(0xFF2196F3)
                            Gender.FEMALE -> Color(0xFFE91E63)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (citizen.gender) {
                        Gender.MALE -> "👨"
                        Gender.FEMALE -> "👩"
                    },
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 基本信息
            Column {
                Text(
                    text = citizen.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${citizen.age}岁 · ${getGenderText(citizen.gender)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = citizen.occupation ?: "无业",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (citizen.occupation != null) CityGreen else Color.Red
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 幸福度指示器
                HappinessIndicator(citizen.happiness)
            }
        }
    }
}

/**
 * 幸福度指示器
 */
@Composable
private fun HappinessIndicator(happiness: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val emoji = when {
            happiness > 0.8f -> "😄"
            happiness > 0.6f -> "🙂"
            happiness > 0.4f -> "😐"
            happiness > 0.2f -> "😟"
            else -> "😠"
        }
        
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(8.dp))
        
        LinearProgressIndicator(
            progress = happiness,
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                happiness > 0.6f -> CityGreen
                happiness > 0.3f -> Color(0xFFFFC107)
                else -> Color.Red
            }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(happiness * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * 当前状态卡片
 */
@Composable
private fun CurrentStatusCard(citizen: Citizen) {
    InfoCard(
        title = "当前状态",
        icon = Icons.Default.Info
    ) {
        InfoItem("活动", getActivityText(citizen.currentActivity))
        InfoItem("位置", "(${citizen.currentX}, ${citizen.currentY})")
        if (citizen.destinationX != null && citizen.destinationY != null) {
            InfoItem("目的地", "(${citizen.destinationX}, ${citizen.destinationY})")
        }
    }
}

/**
 * 需求卡片
 */
@Composable
private fun NeedsCard(citizen: Citizen) {
    InfoCard(
        title = "需求",
        icon = Icons.Default.FavoriteBorder
    ) {
        NeedBar("食物", citizen.needsFood, "🍔")
        NeedBar("休息", citizen.needsRest, "😴")
        NeedBar("娱乐", citizen.needsEntertainment, "🎉")
        if (citizen.needsMedical > 0) {
            NeedBar("医疗", citizen.needsMedical, "💊")
        }
    }
}

/**
 * 需求进度条
 */
@Composable
private fun NeedBar(label: String, value: Float, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(60.dp)
        )
        LinearProgressIndicator(
            progress = value,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                value > 0.7f -> Color.Red
                value > 0.4f -> Color(0xFFFFC107)
                else -> CityGreen
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * 日常信息卡片
 */
@Composable
private fun DailyInfoCard(citizen: Citizen) {
    InfoCard(
        title = "日常信息",
        icon = Icons.Default.Home
    ) {
        InfoItem("家庭地址", "(${citizen.homeX}, ${citizen.homeY})")
        if (citizen.workplaceX != null && citizen.workplaceY != null) {
            InfoItem("工作地点", "(${citizen.workplaceX}, ${citizen.workplaceY})")
            InfoItem("平均通勤", "${citizen.averageCommuteTime}分钟")
        }
        InfoItem("收入", "¥${citizen.salary}/月")
        InfoItem("财富", "¥${citizen.wealth}")
        InfoItem("性格", getPersonalityText(citizen.personality))
    }
}

/**
 * 想法卡片
 */
@Composable
private fun ThoughtsCard(thoughts: List<CitizenThought>) {
    InfoCard(
        title = "最近想法",
        icon = Icons.Default.Face
    ) {
        thoughts.take(5).forEach { thought ->
            ThoughtItem(thought)
        }
    }
}

/**
 * 想法项
 */
@Composable
private fun ThoughtItem(thought: CitizenThought) {
    val emoji = when (thought.type) {
        ThoughtType.HAPPY -> "😄"
        ThoughtType.SATISFIED -> "🙂"
        ThoughtType.NEUTRAL -> "😐"
        ThoughtType.CONCERNED -> "😟"
        ThoughtType.ANGRY -> "😠"
        ThoughtType.NEED -> "💭"
        ThoughtType.COMPLAINT -> "😤"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = emoji)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = thought.message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 统计信息卡片
 */
@Composable
private fun StatisticsCard(citizen: Citizen) {
    InfoCard(
        title = "统计信息",
        icon = Icons.Default.Star
    ) {
        InfoItem("健康", "${(citizen.health * 100).toInt()}%")
        InfoItem("教育", getEducationText(citizen.education))
        InfoItem("抱怨次数", citizen.complaints.toString())
        InfoItem("搬家次数", citizen.movedTimes.toString())
        InfoItem("换工作次数", citizen.jobChanges.toString())
    }
}

/**
 * 信息卡片容器
 */
@Composable
private fun InfoCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CityBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * 信息项
 */
@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// 辅助函数
private fun getGenderText(gender: Gender) = when (gender) {
    Gender.MALE -> "男"
    Gender.FEMALE -> "女"
}

private fun getActivityText(activity: CitizenActivity) = when (activity) {
    CitizenActivity.SLEEPING -> "睡觉中"
    CitizenActivity.AT_HOME -> "在家"
    CitizenActivity.COMMUTING_TO_WORK -> "通勤上班"
    CitizenActivity.WORKING -> "工作中"
    CitizenActivity.COMMUTING_HOME -> "通勤回家"
    CitizenActivity.SHOPPING -> "购物"
    CitizenActivity.ENTERTAINMENT -> "娱乐"
    CitizenActivity.DINING -> "用餐"
    CitizenActivity.MEDICAL -> "就医"
    CitizenActivity.SCHOOL -> "上学"
    CitizenActivity.PARK -> "公园休闲"
    CitizenActivity.EXERCISING -> "锻炼"
    CitizenActivity.SOCIALIZING -> "社交"
}

private fun getPersonalityText(personality: CitizenPersonality) = when (personality) {
    CitizenPersonality.WORKAHOLIC -> "工作狂"
    CitizenPersonality.FAMILY_ORIENTED -> "家庭为重"
    CitizenPersonality.SOCIAL -> "社交达人"
    CitizenPersonality.HEALTH_CONSCIOUS -> "健康主义"
    CitizenPersonality.BALANCED -> "平衡型"
    CitizenPersonality.DEMANDING -> "苛刻型"
}

private fun getEducationText(education: EducationLevel) = when (education) {
    EducationLevel.PRIMARY -> "小学"
    EducationLevel.SECONDARY -> "初中"
    EducationLevel.HIGH_SCHOOL -> "高中"
    EducationLevel.COLLEGE -> "大学"
    EducationLevel.GRADUATE -> "硕士"
    EducationLevel.PHD -> "博士"
    EducationLevel.PRIMARY -> "无"
}

