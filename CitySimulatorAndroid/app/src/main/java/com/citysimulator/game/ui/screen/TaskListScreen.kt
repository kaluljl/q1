package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Task
import com.citysimulator.game.data.model.TaskStatus
import com.citysimulator.game.data.model.TaskType

/**
 * 任务列表屏幕
 * 
 * 显示所有任务的主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    completedTaskCount: Int,
    onTaskAccept: (String) -> Unit,
    onTaskReject: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部", "新手", "发展", "事件")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("📌 任务中心", fontWeight = FontWeight.Bold)
                        Text(
                            "已完成 $completedTaskCount 个任务",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFBBDEFB)
                        )
                    )
                )
        ) {
            // 分类标签
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF1976D2)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // 任务列表
            val filteredTasks = when (selectedTab) {
                1 -> tasks.filter { it.type == TaskType.TUTORIAL }
                2 -> tasks.filter { it.type == TaskType.DEVELOPMENT }
                3 -> tasks.filter { it.type == TaskType.RANDOM_EVENT }
                else -> tasks
            }.filter { 
                it.status == TaskStatus.AVAILABLE || 
                it.status == TaskStatus.IN_PROGRESS 
            }
            
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "🎉",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "暂无任务",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            "继续发展您的城市以解锁新任务",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 主要任务
                    val mainTasks = filteredTasks.filter { it.isMainTask }
                    if (mainTasks.isNotEmpty()) {
                        item {
                            Text(
                                "🎯 主要目标",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(mainTasks) { task ->
                            TaskCard(
                                task = task,
                                onAccept = { onTaskAccept(task.id) },
                                onReject = { onTaskReject(task.id) }
                            )
                        }
                    }
                    
                    // 其他任务
                    val otherTasks = filteredTasks.filter { !it.isMainTask }
                    if (otherTasks.isNotEmpty()) {
                        item {
                            Text(
                                "🛠️ 近期任务",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(otherTasks) { task ->
                            TaskCard(
                                task = task,
                                onAccept = { onTaskAccept(task.id) },
                                onReject = { onTaskReject(task.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 任务卡片
 */
@Composable
fun TaskCard(
    task: Task,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (task.type) {
                TaskType.TUTORIAL -> Color(0xFFFFF9C4)
                TaskType.DEVELOPMENT -> Color(0xFFE1F5FE)
                TaskType.RANDOM_EVENT -> Color(0xFFF3E5F5)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.getIcon(),
                        fontSize = 24.sp
                    )
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 状态图标
                Text(
                    text = task.getStatusIcon(),
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 描述
            Text(
                text = task.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 进度条（如果任务进行中）
            if (task.status == TaskStatus.IN_PROGRESS) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "进度",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            "${task.currentValue}/${task.targetValue}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = task.getProgressPercentage() / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // 奖励
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🎁",
                    fontSize = 16.sp
                )
                Text(
                    "奖励: ${task.getRewardDescription()}",
                    fontSize = 12.sp,
                    color = Color(0xFFE65100)
                )
            }
            
            // 时间限制提示
            if (task.timeLimit != null && task.expiresAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⏰",
                        fontSize = 14.sp
                    )
                    Text(
                        "限时任务",
                        fontSize = 12.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 操作按钮
            if (task.status == TaskStatus.AVAILABLE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (task.isOptional) 
                        Arrangement.SpaceBetween 
                    else 
                        Arrangement.End
                ) {
                    if (task.isOptional) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("拒绝")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("接受")
                    }
                }
            }
        }
    }
}

