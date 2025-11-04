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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.citysimulator.game.data.model.PolicyType
import com.citysimulator.game.data.model.CityPolicy
import com.citysimulator.game.ui.theme.*
import com.citysimulator.game.ui.viewmodel.CityPolicyViewModel

/**
 * 城市政策界面
 * 
 * 显示城市政策实施和管理
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPolicyScreen(
    onNavigateBack: () -> Unit,
    currentGold: Int = 1000,
    onGoldChange: (Int) -> Unit = {},
    viewModel: CityPolicyViewModel = hiltViewModel()
) {
    // 获取当前主题
    val currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
    
    val policies by viewModel.policies.collectAsState()
    val availablePolicies by viewModel.availablePolicies.collectAsState()
    val implementedPolicies by viewModel.implementedPolicies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val goldSpent by viewModel.goldSpent.collectAsState()
    
    // 更新ViewModel的金币状态
    LaunchedEffect(currentGold) {
        viewModel.updateCurrentGold(currentGold)
    }
    
    // 监听金币消耗事件
    LaunchedEffect(goldSpent) {
        goldSpent?.let { spent ->
            onGoldChange(currentGold - spent)
            viewModel.clearGoldSpentEvent()
        }
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("可实施", "已实施", "全部")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.citysimulator.game.ui.theme.getThemeBackgroundBrush(currentTheme))
    ) {
        // 顶部标题栏
        TopAppBar(
            title = { 
                Text(
                    "城市政策",
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
                containerColor = currentTheme.primary,
                titleContentColor = currentTheme.textPrimary,
                navigationIconContentColor = currentTheme.textPrimary
            )
        )
        
        // 金币显示
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "当前金币",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$currentGold",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // 标签页
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // 内容区域
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> AvailablePolicyList(
                    policies = availablePolicies,
                    onImplementPolicy = { policy ->
                        viewModel.implementPolicy(policy)
                    }
                )
                1 -> ImplementedPolicyList(
                    policies = implementedPolicies,
                    onRevokePolicy = { policy ->
                        viewModel.revokePolicy(policy)
                    }
                )
                2 -> AllPolicyList(policies = policies)
            }
            
            // 加载状态
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // 错误消息
        errorMessage?.let { message ->
            LaunchedEffect(message) {
                // 显示错误消息
            }
        }
    }
}

@Composable
private fun AvailablePolicyList(
    policies: List<CityPolicy>,
    onImplementPolicy: (CityPolicy) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(policies) { policy ->
            AvailablePolicyCard(
                policy = policy,
                onImplementPolicy = onImplementPolicy
            )
        }
    }
}

@Composable
private fun AvailablePolicyCard(
    policy: CityPolicy,
    onImplementPolicy: (CityPolicy) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onImplementPolicy(policy) },
        colors = CardDefaults.cardColors(
            containerColor = getPolicyTypeColor(policy.type)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题行 - 名称和等级
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 政策类型图标
                        Icon(
                            imageVector = getPolicyTypeIcon(policy.type),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = policy.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // 等级显示
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = getPolicyLevelText(policy.level),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                // 成本显示
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${policy.implementationCost}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                    Text(
                        text = "每月维护 ${policy.monthlyCost}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 描述
            Text(
                text = policy.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // 数值效果显示
            if (hasNumericEffects(policy.effects)) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (policy.effects.prosperityBonus > 0) {
                        EffectChip(
                            icon = Icons.Default.TrendingUp,
                            label = "繁荣+${policy.effects.prosperityBonus.toInt()}",
                            color = Color(0xFF4CAF50)
                        )
                    }
                    if (policy.effects.happinessBonus > 0) {
                        EffectChip(
                            icon = Icons.Default.SentimentSatisfied,
                            label = "幸福+${policy.effects.happinessBonus.toInt()}",
                            color = Color(0xFFFF9800)
                        )
                    }
                    if (policy.effects.goldMultiplier > 1.0f) {
                        EffectChip(
                            icon = Icons.Default.AttachMoney,
                            label = "收入×${String.format("%.1f", policy.effects.goldMultiplier)}",
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
            
            // 特殊效果
            if (policy.effects.specialEffects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        policy.effects.specialEffects.forEach { effect ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color(0xFFFFD700)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = effect,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // 前置条件提示
            if (policy.prerequisites.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "需要前置政策",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// 效果芯片组件
@Composable
private fun EffectChip(
    icon: ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

// 检查是否有数值效果
private fun hasNumericEffects(effects: com.citysimulator.game.data.model.PolicyEffect): Boolean {
    return effects.prosperityBonus > 0 || 
           effects.happinessBonus > 0 || 
           effects.goldMultiplier > 1.0f ||
           effects.buildingEfficiencyBonus > 0 ||
           effects.pollutionReduction > 0 ||
           effects.crimeReduction > 0
}

@Composable
private fun ImplementedPolicyList(
    policies: List<CityPolicy>,
    onRevokePolicy: (CityPolicy) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(policies) { policy ->
            ImplementedPolicyCard(
                policy = policy,
                onRevokePolicy = onRevokePolicy
            )
        }
    }
}

@Composable
private fun ImplementedPolicyCard(
    policy: CityPolicy,
    onRevokePolicy: (CityPolicy) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = policy.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "已实施",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    if (policy.isReversible) {
                        IconButton(onClick = { onRevokePolicy(policy) }) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = "撤销",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Text(
                text = policy.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = "每月维护成本: ${policy.monthlyCost} 金币",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AllPolicyList(policies: List<CityPolicy>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(policies) { policy ->
            AllPolicyCard(policy = policy)
        }
    }
}

@Composable
private fun AllPolicyCard(policy: CityPolicy) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (policy.isImplemented) {
                MaterialTheme.colorScheme.tertiary
            } else {
                getPolicyTypeColor(policy.type)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = policy.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (policy.isImplemented) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "已实施",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = policy.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun getPolicyTypeColor(type: PolicyType): Color {
    return when (type) {
        PolicyType.ECONOMIC -> Color(0xFF4CAF50) // 绿色
        PolicyType.SOCIAL -> Color(0xFF2196F3) // 蓝色
        PolicyType.ENVIRONMENTAL -> Color(0xFF8BC34A) // 浅绿色
        PolicyType.INFRASTRUCTURE -> Color(0xFF9C27B0) // 紫色
        PolicyType.SECURITY -> Color(0xFFFF9800) // 橙色
        PolicyType.CULTURAL -> Color(0xFFE91E63) // 粉色
    }
}

private fun getPolicyTypeIcon(type: PolicyType): ImageVector {
    return when (type) {
        PolicyType.ECONOMIC -> Icons.Default.ShowChart // 经济
        PolicyType.SOCIAL -> Icons.Default.People // 社会
        PolicyType.ENVIRONMENTAL -> Icons.Default.Nature // 环境
        PolicyType.INFRASTRUCTURE -> Icons.Default.Business // 基础设施
        PolicyType.SECURITY -> Icons.Default.Security // 安全
        PolicyType.CULTURAL -> Icons.Default.Museum // 文化
    }
}

private fun getPolicyLevelText(level: com.citysimulator.game.data.model.PolicyLevel): String {
    return when (level) {
        com.citysimulator.game.data.model.PolicyLevel.BASIC -> "基础"
        com.citysimulator.game.data.model.PolicyLevel.INTERMEDIATE -> "中级"
        com.citysimulator.game.data.model.PolicyLevel.ADVANCED -> "高级"
        com.citysimulator.game.data.model.PolicyLevel.EXPERT -> "专家级"
    }
}

