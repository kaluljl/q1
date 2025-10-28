package com.citysimulator.game.ui.component

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.citysimulator.game.ai.*
import com.citysimulator.game.ui.theme.*

/**
 * 决策影响预览对话框
 * 
 * 显示决策的多维度影响、连锁反应和长期效应
 */
@Composable
fun DecisionImpactDialog(
    decisionImpact: DecisionImpact,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题栏
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CityBlue, CityGreen)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = decisionImpact.decision.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = decisionImpact.decision.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 总体评分
                        val overallScore = decisionImpact.getOverallScore()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (overallScore >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (overallScore >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "综合评分: ${if (overallScore >= 0) "+" else ""}${overallScore.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // 内容区域
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 立即影响
                    item {
                        Text(
                            text = "📊 立即影响",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CityBlue
                        )
                    }
                    
                    decisionImpact.immediateImpacts.forEach { (category, impacts) ->
                        if (impacts.isNotEmpty()) {
                            item {
                                ImpactCategoryCard(category, impacts)
                            }
                        }
                    }
                    
                    // 连锁反应
                    if (decisionImpact.chainReactions.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🔗 连锁反应",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                        
                        items(decisionImpact.chainReactions.take(5)) { reaction ->
                            ChainReactionCard(reaction)
                        }
                    }
                    
                    // 长期效应
                    if (decisionImpact.longTermEffects.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⏰ 长期效应",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9C27B0)
                            )
                        }
                        
                        items(decisionImpact.longTermEffects) { effect ->
                            LongTermEffectCard(effect)
                        }
                    }
                }
                
                // 按钮区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CityBlue
                        )
                    ) {
                        Text("取消")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CityGreen
                        )
                    ) {
                        Text("确认执行")
                    }
                }
            }
        }
    }
}

/**
 * 影响分类卡片
 */
@Composable
private fun ImpactCategoryCard(
    category: ImpactCategory,
    impacts: List<Impact>
) {
    val (icon, color, label) = when (category) {
        ImpactCategory.ECONOMIC -> Triple("💰", Color(0xFF4CAF50), "经济影响")
        ImpactCategory.ENVIRONMENTAL -> Triple("🌿", Color(0xFF8BC34A), "环境影响")
        ImpactCategory.SOCIAL -> Triple("👥", Color(0xFF2196F3), "社会影响")
        ImpactCategory.INFRASTRUCTURE -> Triple("🏗️", Color(0xFFFF9800), "基础设施")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 分类标题
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 影响列表
            impacts.forEach { impact ->
                ImpactItem(impact)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * 影响项
 */
@Composable
private fun ImpactItem(impact: Impact) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (impact.value >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (impact.value >= 0) CityGreen else Color(0xFFF44336),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = impact.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = impact.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        
        Text(
            text = "${if (impact.value >= 0) "+" else ""}${impact.value.toInt()}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (impact.value >= 0) CityGreen else Color(0xFFF44336)
        )
    }
}

/**
 * 连锁反应卡片
 */
@Composable
private fun ChainReactionCard(reaction: ChainReaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 触发 → 效应
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reaction.trigger.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reaction.effect.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = "${reaction.delay}回合后触发",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "${if (reaction.effect.value >= 0) "+" else ""}${reaction.effect.value.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (reaction.effect.value >= 0) CityGreen else Color.Red
            )
        }
    }
}

/**
 * 长期效应卡片
 */
@Composable
private fun LongTermEffectCard(effect: LongTermEffect) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3E5F5)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = effect.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
                Text(
                    text = effect.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "持续${effect.duration}回合",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "${if (effect.cumulativeImpact.value >= 0) "+" else ""}${effect.cumulativeImpact.value.toInt()}/回合",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (effect.cumulativeImpact.value >= 0) CityGreen else Color.Red
            )
        }
    }
}

/**
 * 简化的决策影响卡片（用于列表显示）
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CompactDecisionImpactCard(
    decisionImpact: DecisionImpact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和评分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = decisionImpact.decision.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                val score = decisionImpact.getOverallScore()
                Surface(
                    color = if (score >= 0) CityGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${if (score >= 0) "+" else ""}${score.toInt()}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (score >= 0) CityGreen else Color.Red
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 主要影响（最多3个）
            val topPositive = decisionImpact.getPositiveImpacts().take(2)
            val topNegative = decisionImpact.getNegativeImpacts().take(2)
            
            if (topPositive.isNotEmpty()) {
                topPositive.forEach { impact ->
                    CompactImpactRow(impact, true)
                }
            }
            
            if (topNegative.isNotEmpty()) {
                topNegative.forEach { impact ->
                    CompactImpactRow(impact, false)
                }
            }
            
            // 连锁反应提示
            if (decisionImpact.chainReactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🔗 ${decisionImpact.chainReactions.size}个连锁反应",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

/**
 * 紧凑影响行
 */
@Composable
private fun CompactImpactRow(impact: Impact, isPositive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isPositive) "✅" else "⚠️",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${impact.name} ${if (impact.value >= 0) "+" else ""}${impact.value.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = if (isPositive) CityGreen else Color.Red
        )
    }
}

