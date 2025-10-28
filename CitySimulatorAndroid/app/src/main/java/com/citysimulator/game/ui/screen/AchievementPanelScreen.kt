package com.citysimulator.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.ui.theme.*
import androidx.compose.ui.graphics.Color
import com.citysimulator.game.ui.theme.GoldColor

/**
 * 成就面板屏幕
 * 
 * 显示游戏成就，包括已解锁和未解锁的成就。
 * 
 * @param onNavigateBack 返回导航回调
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementPanelScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("成就中心") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "城市建设成就",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(4) { index ->
                AchievementItem(
                    title = when (index) {
                        0 -> "第一座建筑"
                        1 -> "住宅专家"
                        2 -> "商业大亨"
                        else -> "工业巨头"
                    },
                    description = when (index) {
                        0 -> "建造第一座建筑"
                        1 -> "建造10座住宅建筑"
                        2 -> "建造5座商业建筑"
                        else -> "建造3座工业建筑"
                    },
                    progress = when (index) {
                        0 -> "1/1"
                        1 -> "3/10"
                        2 -> "1/5"
                        else -> "0/3"
                    },
                    reward = when (index) {
                        0 -> "50金币"
                        1 -> "200金币"
                        2 -> "300金币"
                        else -> "500金币"
                    },
                    isUnlocked = index <= 1
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "资源管理成就",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(3) { index ->
                AchievementItem(
                    title = when (index) {
                        0 -> "资源收集者"
                        1 -> "生产专家"
                        else -> "贸易大师"
                    },
                    description = when (index) {
                        0 -> "收集1000单位木材"
                        1 -> "生产500单位钢材"
                        else -> "完成10次交易"
                    },
                    progress = when (index) {
                        0 -> "250/1000"
                        1 -> "120/500"
                        else -> "3/10"
                    },
                    reward = when (index) {
                        0 -> "100金币"
                        1 -> "150金币"
                        else -> "200金币"
                    },
                    isUnlocked = false
                )
            }
        }
    }
}

/**
 * 成就项
 * 
 * @param title 成就标题
 * @param description 成就描述
 * @param progress 成就进度
 * @param reward 成就奖励
 * @param isUnlocked 是否已解锁
 */
@Composable
private fun AchievementItem(
    title: String,
    description: String,
    progress: String,
    reward: String,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) CityOrange.copy(alpha = 0.1f) else CitySurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) CityOrange else CityGray,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) CityOnSurface else CityGray
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) CityOnSurfaceVariant else CityGray
                )
                Text(
                    text = "进度: $progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) CityBlue else CityGray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = reward,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) GoldColor else CityGray
                )
                if (isUnlocked) {
                    Text(
                        text = "已解锁",
                        style = MaterialTheme.typography.labelSmall,
                        color = CityOrange
                    )
                } else {
                    Text(
                        text = "未解锁",
                        style = MaterialTheme.typography.labelSmall,
                        color = CityGray
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementPanelScreenPlaceholderColor(): Color = GoldColor
