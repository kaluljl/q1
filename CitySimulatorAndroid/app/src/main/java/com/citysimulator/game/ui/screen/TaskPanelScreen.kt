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

/**
 * 任务面板屏幕
 * 
 * 显示游戏任务，包括主线、支线、日常任务。
 * 
 * @param onNavigateBack 返回导航回调
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPanelScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务中心") },
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
                    text = "主线任务",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(3) { index ->
                TaskItem(
                    title = when (index) {
                        0 -> "建设第一座住宅"
                        1 -> "建造商业建筑"
                        else -> "发展工业区"
                    },
                    description = when (index) {
                        0 -> "建造一座住宅建筑"
                        1 -> "建造一座商业建筑"
                        else -> "建造一座工业建筑"
                    },
                    progress = when (index) {
                        0 -> "1/1"
                        1 -> "0/1"
                        else -> "0/1"
                    },
                    reward = when (index) {
                        0 -> "100金币"
                        1 -> "200金币"
                        else -> "300金币"
                    },
                    isCompleted = index == 0
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "日常任务",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(2) { index ->
                TaskItem(
                    title = when (index) {
                        0 -> "收集资源"
                        else -> "维护建筑"
                    },
                    description = when (index) {
                        0 -> "收集100单位木材"
                        else -> "维护5座建筑"
                    },
                    progress = when (index) {
                        0 -> "75/100"
                        else -> "3/5"
                    },
                    reward = when (index) {
                        0 -> "50金币"
                        else -> "30金币"
                    },
                    isCompleted = false
                )
            }
        }
    }
}

/**
 * 任务项
 * 
 * @param title 任务标题
 * @param description 任务描述
 * @param progress 任务进度
 * @param reward 任务奖励
 * @param isCompleted 是否已完成
 */
@Composable
private fun TaskItem(
    title: String,
    description: String,
    progress: String,
    reward: String,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) CityGreen.copy(alpha = 0.1f) else CitySurface
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
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Assignment,
                contentDescription = null,
                tint = if (isCompleted) CityGreen else CityBlue,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CityOnSurfaceVariant
                )
                Text(
                    text = "进度: $progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = CityBlue
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = reward,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = GoldColor
                )
                if (isCompleted) {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.labelSmall,
                        color = CityGreen
                    )
                }
            }
        }
    }
}

@Composable
fun TaskPanelScreenPlaceholderColor() = GoldColor
