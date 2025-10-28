package com.citysimulator.game.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.citysimulator.game.ai.*
import com.citysimulator.game.ui.theme.*

/**
 * 问题指示器面板
 * 
 * 通过数据可视化和间接提示展示城市问题
 */
@Composable
fun ProblemIndicatorPanel(
    problems: List<CityProblem>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // 概览栏
        ProblemSummaryBar(
            problems = problems,
            expanded = expanded,
            onToggle = { expanded = !expanded }
        )
        
        // 详细列表
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(problems) { problem ->
                    ProblemCard(problem)
                }
            }
        }
    }
}

/**
 * 问题摘要栏
 */
@Composable
private fun ProblemSummaryBar(
    problems: List<CityProblem>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val criticalCount = problems.count { it.severity == ProblemSeverity.CRITICAL }
    val highCount = problems.count { it.severity == ProblemSeverity.HIGH }
    val mediumCount = problems.count { it.severity == ProblemSeverity.MEDIUM }
    val lowCount = problems.count { it.severity == ProblemSeverity.LOW }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = when {
                criticalCount > 0 -> Color(0xFFFFCDD2)
                highCount > 0 -> Color(0xFFFFE0B2)
                mediumCount > 0 -> Color(0xFFFFF9C4)
                else -> Color(0xFFE8F5E9)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = CityBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "城市诊断",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (problems.isEmpty()) {
                        Text(
                            text = "✨ 城市运转良好",
                            style = MaterialTheme.typography.bodySmall,
                            color = CityGreen
                        )
                    } else {
                        Text(
                            text = "发现 ${problems.size} 个关注点",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (criticalCount > 0) {
                    SeverityBadge(criticalCount, Color(0xFFD32F2F))
                }
                if (highCount > 0) {
                    SeverityBadge(highCount, Color(0xFFFF6F00))
                }
                if (mediumCount > 0) {
                    SeverityBadge(mediumCount, Color(0xFFFBC02D))
                }
                if (lowCount > 0) {
                    SeverityBadge(lowCount, Color(0xFF1976D2))
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}

/**
 * 严重程度徽章
 */
@Composable
private fun SeverityBadge(count: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 问题卡片
 */
@Composable
private fun ProblemCard(problem: CityProblem) {
    var showDetails by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetails = !showDetails },
        colors = CardDefaults.cardColors(
            containerColor = getSeverityColor(problem.severity).copy(alpha = 0.1f)
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = getProblemIcon(problem.category),
                        contentDescription = null,
                        tint = getSeverityColor(problem.severity),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = problem.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getCategoryText(problem.category),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Surface(
                    color = getSeverityColor(problem.severity).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = getSeverityText(problem.severity),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = getSeverityColor(problem.severity),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 症状（始终显示）
            Text(
                text = "📋 观察到的现象:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            problem.symptoms.take(2).forEach { symptom ->
                Text(
                    text = "• $symptom",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
            
            // 详细信息（展开显示）
            AnimatedVisibility(visible = showDetails) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 数据信号
                    Text(
                        text = "📊 关键数据:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    problem.dataSignals.entries.take(3).forEach { (key, value) ->
                        DataSignalRow(key, value)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 视觉提示
                    Text(
                        text = "👁️ 查看提示:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    problem.visualHints.forEach { hint ->
                        Text(
                            text = "• $hint",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 间接建议
                    Text(
                        text = "💡 分析方向:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CityBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    problem.indirectSuggestions.forEach { suggestion ->
                        Text(
                            text = "• $suggestion",
                            style = MaterialTheme.typography.bodySmall,
                            color = CityBlue
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 展开/收起提示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { showDetails = !showDetails }) {
                    Text(
                        text = if (showDetails) "收起详情" else "查看详情",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        imageVector = if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 数据信号行
 */
@Composable
private fun DataSignalRow(key: String, value: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatDataKey(key),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        
        // 数值条
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            ) {
                val progress = (value / 100f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            when {
                                value < 30 -> Color.Red
                                value < 70 -> Color(0xFFFF9800)
                                else -> CityGreen
                            }
                        )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f", value),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 紧凑问题指示器（用于主界面）
 */
@Composable
fun CompactProblemIndicator(
    problems: List<CityProblem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (problems.isEmpty()) return
    
    val criticalCount = problems.count { it.severity == ProblemSeverity.CRITICAL }
    val highCount = problems.count { it.severity == ProblemSeverity.HIGH }
    
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when {
                criticalCount > 0 -> Color(0xFFFFCDD2)
                highCount > 0 -> Color(0xFFFFE0B2)
                else -> Color(0xFFFFF9C4)
            }.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = when {
                    criticalCount > 0 -> Color(0xFFD32F2F)
                    highCount > 0 -> Color(0xFFFF6F00)
                    else -> Color(0xFFFBC02D)
                },
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = "${problems.size}个需要关注",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// 辅助函数

private fun getSeverityColor(severity: ProblemSeverity): Color {
    return when (severity) {
        ProblemSeverity.CRITICAL -> Color(0xFFD32F2F)
        ProblemSeverity.HIGH -> Color(0xFFFF6F00)
        ProblemSeverity.MEDIUM -> Color(0xFFFBC02D)
        ProblemSeverity.LOW -> Color(0xFF1976D2)
    }
}

private fun getSeverityText(severity: ProblemSeverity): String {
    return when (severity) {
        ProblemSeverity.CRITICAL -> "危急"
        ProblemSeverity.HIGH -> "严重"
        ProblemSeverity.MEDIUM -> "中等"
        ProblemSeverity.LOW -> "轻微"
    }
}

private fun getProblemIcon(category: ProblemCategory): ImageVector {
    return when (category) {
        ProblemCategory.ECONOMIC -> Icons.Default.AttachMoney
        ProblemCategory.ENVIRONMENTAL -> Icons.Default.Eco
        ProblemCategory.SOCIAL -> Icons.Default.People
        ProblemCategory.INFRASTRUCTURE -> Icons.Default.Construction
        ProblemCategory.RESOURCE -> Icons.Default.Inventory
    }
}

private fun getCategoryText(category: ProblemCategory): String {
    return when (category) {
        ProblemCategory.ECONOMIC -> "经济"
        ProblemCategory.ENVIRONMENTAL -> "环境"
        ProblemCategory.SOCIAL -> "社会"
        ProblemCategory.INFRASTRUCTURE -> "基础设施"
        ProblemCategory.RESOURCE -> "资源"
    }
}

private fun formatDataKey(key: String): String {
    return when (key) {
        "dailyDeficit" -> "日赤字"
        "treasuryTrend" -> "国库趋势"
        "unemploymentRate" -> "失业率"
        "powerRatio" -> "电力比例"
        "waterRatio" -> "水力比例"
        "wasteRatio" -> "垃圾处理率"
        "educationCoverage" -> "教育覆盖"
        "healthcareCoverage" -> "医疗覆盖"
        "housingOccupancy" -> "住房占用率"
        "pollutionRatio" -> "污染比例"
        "roadCoverage" -> "道路覆盖"
        else -> key
    }
}

