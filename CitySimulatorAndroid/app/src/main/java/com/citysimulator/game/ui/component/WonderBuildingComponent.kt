package com.citysimulator.game.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.citysimulator.game.ai.WonderBuilding
import com.citysimulator.game.ai.WonderBuildingSystem

/**
 * 奇观建造界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WonderBuildingScreen(
    currentGold: Int,
    builtWonders: List<String> = emptyList(),  // 已建造的奇观ID列表
    wondersInProgress: Map<String, Int> = emptyMap(),  // 正在建造的奇观 (ID -> 剩余月数)
    onBack: () -> Unit,
    onBuildWonder: (WonderBuilding) -> Unit,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    val allWonders = remember { WonderBuildingSystem.getAllWonders() }
    var selectedWonder by remember { mutableStateOf<WonderBuilding?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏛️ 世界奇观", color = themeColors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = themeColors.textPrimary
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentGold.toString(),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColors.primary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = themeColors.backgroundGradient
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顶部说明
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "✨ 关于奇观",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "奇观是城市的象征，建造奇观将永久改变所有市民的集体意识和性格特质。每座奇观都有独特的文化影响力！",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
                
                // 奇观列表
            items(allWonders) { wonder ->
                val isBuilt = builtWonders.contains(wonder.type.name)
                val isInProgress = wondersInProgress.containsKey(wonder.type.name)
                val remainingMonths = wondersInProgress[wonder.type.name]
                val canAfford = currentGold >= wonder.buildCost

                WonderCard(
                    wonder = wonder,
                    isBuilt = isBuilt,
                    isInProgress = isInProgress,
                    remainingMonths = remainingMonths,
                    canAfford = canAfford,
                    onClick = {
                        if (!isBuilt && !isInProgress) {
                            selectedWonder = wonder
                        }
                    },
                    themeColors = themeColors,
                    currentGold = currentGold
                )
            }
            }
            
            // 奇观详情对话框
            selectedWonder?.let { wonder ->
                WonderDetailDialog(
                    wonder = wonder,
                    currentGold = currentGold,
                    canAfford = currentGold >= wonder.buildCost,
                    onDismiss = { selectedWonder = null },
                    onConfirm = {
                        onBuildWonder(wonder)
                        selectedWonder = null
                    },
                    themeColors = themeColors
                )
            }
        }
    }
}

/**
 * 奇观卡片
 */
@Composable
private fun WonderCard(
    wonder: WonderBuilding,
    isBuilt: Boolean,
    isInProgress: Boolean = false,
    remainingMonths: Int? = null,
    canAfford: Boolean,
    onClick: () -> Unit,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors,
    currentGold: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isBuilt && !isInProgress, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isBuilt -> Color(0xFFE8F5E9)  // 绿色背景（已建造）
                isInProgress -> Color(0xFFFFF9C4)  // 黄色背景（建造中）
                canAfford -> themeColors.cardBackground  // 正常背景
                else -> Color(0xFFFFEBEE)  // 红色背景（金币不足）
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when {
                isBuilt -> 8.dp
                isInProgress -> 6.dp
                else -> 4.dp
            }
        ),
        border = when {
            isBuilt -> BorderStroke(2.dp, Color(0xFF4CAF50))
            isInProgress -> BorderStroke(2.dp, Color(0xFFFFA726))
            else -> null
        }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 奇观图标和名称
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = getWonderIcon(wonder.type.name),
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = wonder.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isBuilt) Color(0xFF2C2C2C) else themeColors.textPrimary
                        )
                        when {
                            isBuilt -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "已建造",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            isInProgress -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🔨",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "建造中 (剩余${remainingMonths}月)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFFA726),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 描述
            Text(
                text = wonder.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isBuilt) Color(0xFF666666) else themeColors.textSecondary,
                lineHeight = 22.sp
            )
            
            if (!isBuilt && !isInProgress) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // 数据卡片
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 成本
                    InfoChip(
                        icon = "💰",
                        label = "成本",
                        value = wonder.buildCost.toString(),
                        valueColor = if (canAfford) Color(0xFF4CAF50) else Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 建造时间
                    InfoChip(
                        icon = "⏱️",
                        label = "工期",
                        value = "${wonder.buildTime}天",
                        valueColor = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 幸福度加成
                    InfoChip(
                        icon = "😊",
                        label = "幸福",
                        value = "+${(wonder.numericBonuses.happinessBonus * 100).toInt()}%",
                        valueColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 核心效果预览
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🌟 集体意识影响",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "文化: ${wonder.collectiveEffect.culturalImpact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                        wonder.collectiveEffect.valueShift.entries.take(2).forEach { (key, _) ->
                            Text(
                                text = "• $key",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                        if (wonder.collectiveEffect.valueShift.size > 2) {
                            Text(
                                text = "...等${wonder.collectiveEffect.valueShift.size}项",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
                
                if (!canAfford) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠️ 金币不足，还需要 ${wonder.buildCost - currentGold} 金币",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 已建造的效果总结
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "✅ 正在生效的加成",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 每月收入: +${wonder.numericBonuses.goldPerMonth}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "• 全体幸福度: +${(wonder.numericBonuses.happinessBonus * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "• 文化氛围: ${wonder.collectiveEffect.culturalImpact.take(30)}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 奇观详情对话框（确认建造）
 */
@Composable
private fun WonderDetailDialog(
    wonder: WonderBuilding,
    currentGold: Int,
    canAfford: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = getWonderIcon(wonder.type.name), fontSize = 32.sp)
                Text(
                    text = wonder.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = wonder.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 建造数据
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 建造数据",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("成本", style = MaterialTheme.typography.bodySmall, color = Color(0xFF666666))
                                Text(
                                    "${wonder.buildCost} 金币",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canAfford) Color(0xFF4CAF50) else Color(0xFFFF5722)
                                )
                            }
                            Column {
                                Text("建造时间", style = MaterialTheme.typography.bodySmall, color = Color(0xFF666666))
                                Text(
                                    "${wonder.buildTime}天",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                            Column {
                                Text("每月收入", style = MaterialTheme.typography.bodySmall, color = Color(0xFF666666))
                                Text(
                                    "+${wonder.numericBonuses.goldPerMonth}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 集体意识影响
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8EAF6)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🌍 集体意识影响",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "文化氛围: ${wonder.collectiveEffect.culturalImpact}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        wonder.collectiveEffect.valueShift.entries.forEach { (key, value) ->
                            Text(
                                text = "• $key: +${(value * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 人格特质提升
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎭 全体市民人格提升",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val modifiers = wonder.collectiveEffect.behaviorModifiers
                        if (modifiers.curiosityBoost > 0) {
                            Text("• 好奇心 +${(modifiers.curiosityBoost * 100).toInt()}%", 
                                 style = MaterialTheme.typography.bodyMedium, 
                                 color = Color(0xFF4CAF50))
                        }
                        if (modifiers.creativityBoost > 0) {
                            Text("• 创造力 +${(modifiers.creativityBoost * 100).toInt()}%", 
                                 style = MaterialTheme.typography.bodyMedium, 
                                 color = Color(0xFF4CAF50))
                        }
                        if (modifiers.ambitionBoost > 0) {
                            Text("• 野心 +${(modifiers.ambitionBoost * 100).toInt()}%", 
                                 style = MaterialTheme.typography.bodyMedium, 
                                 color = Color(0xFF4CAF50))
                        }
                        if (modifiers.friendlinessBoost > 0) {
                            Text("• 友善 +${(modifiers.friendlinessBoost * 100).toInt()}%", 
                                 style = MaterialTheme.typography.bodyMedium, 
                                 color = Color(0xFF4CAF50))
                        }
                        if (modifiers.diligenceBoost > 0) {
                            Text("• 勤奋 +${(modifiers.diligenceBoost * 100).toInt()}%", 
                                 style = MaterialTheme.typography.bodyMedium, 
                                 color = Color(0xFF4CAF50))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 警告文本
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚠️ 重要提示",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "奇观的效果是永久的！它将改变所有市民（包括未来新生儿）的性格和价值观，塑造城市的独特文化。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color(0xFFBDBDBD)
                )
            ) {
                Text(
                    text = if (canAfford) "开始建造" else "金币不足",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 信息芯片
 */
@Composable
private fun InfoChip(
    icon: String,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

/**
 * 获取奇观图标
 */
private fun getWonderIcon(wonderType: String): String {
    return when (wonderType) {
        "SPACE_ELEVATOR" -> "🚀"
        "GREAT_LIBRARY" -> "📚"
        "CITY_COLOSSUS" -> "🗿"
        "TECH_TOWER" -> "🔬"
        "HARMONY_TEMPLE" -> "☮️"
        "GOLDEN_PALACE" -> "👑"
        "ETERNAL_MONUMENT" -> "🏛️"
        else -> "🏛️"
    }
}

