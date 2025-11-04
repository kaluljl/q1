package com.citysimulator.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel? = null,
    cityHappiness: Float = 0.6f, // 新增：城市幸福度
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    // 获取当前主题
    val currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
    
    // ✅ 从ViewModel获取真实AI数据
    val aiPersonality = remember(citizen.id) {
        if (citizenViewModel != null) {
            citizenViewModel.getOrGeneratePersonality(citizen.id)
        } else {
            citizen.personalityTraits ?: PersonalityTraits.generateRandom()
        }
    }
    
    // ✅ 需求数据 - 基于市民实际状态计算
    val aiNeeds = remember(citizen.id, citizen.happiness, citizen.wealth, citizen.health) {
        if (citizenViewModel != null) {
            citizenViewModel.getOrGenerateNeeds(citizen.id, citizen)
        } else {
            MaslowNeeds()
        }
    }
    
    // ✅ 记忆数据 - 基于市民生活经历生成
    val aiMemories = remember(citizen.id) {
        if (citizenViewModel != null) {
            citizenViewModel.getOrGenerateMemories(citizen.id, citizen)
        } else {
            CitizenMemoryCollection(citizen.id)
        }
    }
    
    // ✅ 社交网络 - 基于真实市民关系生成
    val aiSocialNetwork = remember(citizen.id) {
        if (citizenViewModel != null) {
            citizenViewModel.getOrGenerateSocialNetwork(citizen.id)
        } else {
            SocialNetwork(citizen.id)
        }
    }
    
    // ✅ 八卦数据 - 动态生成，反映城市状态
    val aiGossips = remember(citizen.id, cityHappiness) {
        if (citizenViewModel != null) {
            citizenViewModel.getCitizenGossips(citizen.id, cityHappiness)
        } else {
            emptyList()
        }
    }
    
    // ✅ 事件数据 - 动态生成，反映城市状态
    val aiEvents = remember(citizen.id, cityHappiness) {
        if (citizenViewModel != null) {
            citizenViewModel.getCitizenEvents(citizen.id, cityHappiness)
        } else {
            emptyList()
        }
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("基本信息", "🎭 人格", "🎯 需求", "📖 记忆", "👥 社交", "💬 八卦", "🎪 事件", "📝 日记")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(citizen.name, color = currentTheme.textPrimary)
                        Text(
                            text = aiPersonality.getDominantPersonalityType().getDisplayName(),
                            style = MaterialTheme.typography.bodySmall,
                            color = currentTheme.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回", tint = currentTheme.textPrimary)
                    }
                },
                actions = {
                    // 市长建议按钮
                    var showAdviceDialog by remember { mutableStateOf(false) }
                    IconButton(onClick = { showAdviceDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "市长建议",
                            tint = Color(0xFFFFD700) // 金色突出显示
                        )
                    }
                    
                    // AI对话按钮
                    if (onStartAIChat != null) {
                        IconButton(onClick = { onStartAIChat(citizen) }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "AI对话",
                                tint = currentTheme.textPrimary
                            )
                        }
                    }
                    
                    // 市长建议对话框
                    if (showAdviceDialog) {
                        MayorAdviceDialog(
                            citizen = citizen,
                            citizenViewModel = citizenViewModel,
                            onDismiss = { showAdviceDialog = false },
                            themeColors = currentTheme
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentTheme.primary
                )
            )
        },
        containerColor = currentTheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(com.citysimulator.game.ui.theme.getThemeBackgroundBrush(currentTheme))
        ) {
            // 标签页选择
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = currentTheme.cardBackground,
                contentColor = currentTheme.textPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title,
                                color = if (selectedTab == index) currentTheme.primary else currentTheme.textSecondary
                            ) 
                        }
                    )
                }
            }
            
            // 标签页内容
            when (selectedTab) {
                0 -> BasicInfoTab(citizen, thoughts, scrollState, modifier, currentTheme)
                1 -> PersonalityTab(aiPersonality, scrollState, currentTheme)
                2 -> NeedsTab(aiNeeds, scrollState, currentTheme)
                3 -> MemoryTab(aiMemories, scrollState, currentTheme)
                4 -> SocialNetworkTab(aiSocialNetwork, scrollState, currentTheme)
                5 -> GossipTab(aiGossips, scrollState, currentTheme)
                6 -> EventTab(aiEvents, scrollState, currentTheme)
                7 -> DiaryTab(citizen, scrollState, currentTheme)
            }
        }
    }
}

/**
 * 日记Tab - 显示AI生成的市民日记
 */
@Composable
private fun DiaryTab(
    citizen: com.citysimulator.game.data.model.Citizen,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    var diary by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // 自动生成日记
    LaunchedEffect(citizen.id) {
        isLoading = true
        diary = com.citysimulator.game.ai.CitizenTextGenerator.generateDiary(
            citizen = citizen,
            recentEvents = emptyList()
        )
        isLoading = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 日记标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📝 ${citizen.name}的日记",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = themeColors.textPrimary
            )
            
            // 日期
            Text(
                text = java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.CHINA)
                    .format(java.util.Date()),
                style = MaterialTheme.typography.bodyMedium,
                color = themeColors.textSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 日记内容卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF9E6) // 温暖的米黄色，像纸张
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (isLoading) {
                    // 加载状态
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = themeColors.primary
                        )
                    }
                    Text(
                        text = "正在回忆今天发生的事情...",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // 日记内容
                    Text(
                        text = diary ?: "今天没什么特别的事情发生。",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        color = Color(0xFF2C2C2C), // 深灰色文字，像墨水
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 市民状态信息
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = themeColors.cardBackground
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 今日状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 幸福度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("心情", color = themeColors.textSecondary, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val moodEmoji = when {
                            citizen.happiness > 0.7f -> "😊"
                            citizen.happiness > 0.4f -> "😐"
                            else -> "😢"
                        }
                        Text(moodEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${(citizen.happiness * 100).toInt()}%",
                            color = themeColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 健康度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("健康", color = themeColors.textSecondary, fontSize = 15.sp)
                    Text(
                        "${(citizen.health * 100).toInt()}%",
                        color = themeColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 财富
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("财富", color = themeColors.textSecondary, fontSize = 15.sp)
                    Text(
                        "${citizen.wealth} 金币",
                        color = themeColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 提示信息
        Text(
            text = "💡 这篇日记是基于${citizen.name}的当前状态自动生成的",
            style = MaterialTheme.typography.bodySmall,
            color = themeColors.textSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BasicInfoTab(
    citizen: Citizen,
    thoughts: List<CitizenThought>,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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

// 人格标签页
@Composable
private fun PersonalityTab(
    personality: PersonalityTraits,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("主导人格：${personality.getDominantPersonalityType().getDisplayName()}", 
                     fontWeight = FontWeight.Bold, fontSize = 18.sp, color = themeColors.textPrimary)
                Text(personality.getDominantPersonalityType().getDescription(), 
                     fontSize = 14.sp, color = themeColors.textSecondary)
            }
        }
        
        Text("人格特质详情", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeColors.textPrimary)
        PersonalityTraitBar("外向性", personality.extraversion, 
            if (personality.isExtroverted()) "外向" else if (personality.isIntroverted()) "内向" else "均衡", themeColors)
        PersonalityTraitBar("勤奋度", personality.diligence,
            if (personality.isDiligent()) "勤奋" else if (personality.isLazy()) "懒散" else "均衡", themeColors)
        PersonalityTraitBar("好奇心", personality.curiosity,
            if (personality.isCurious()) "好奇" else if (personality.isConservative()) "保守" else "均衡", themeColors)
        PersonalityTraitBar("友善度", personality.kindness,
            if (personality.isKind()) "友善" else if (personality.isSelfish()) "自私" else "均衡", themeColors)
        PersonalityTraitBar("创造力", personality.creativity,
            if (personality.isCreative()) "富有创意" else "务实", themeColors)
        PersonalityTraitBar("野心", personality.ambition,
            if (personality.isAmbitious()) "野心勃勃" else "知足常乐", themeColors)
    }
}

@Composable
private fun PersonalityTraitBar(
    label: String, 
    value: Float, 
    description: String,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, color = themeColors.textPrimary)
            Text(description, fontSize = 12.sp, color = themeColors.textSecondary)
        }
        LinearProgressIndicator(
            progress = value,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = when {
                value > 0.6f -> Color(0xFF4CAF50)
                value < 0.4f -> Color(0xFFFF5722)
                else -> Color(0xFFFFC107)
            },
            trackColor = themeColors.cardBackground.copy(alpha = 0.3f)
        )
    }
}

// 需求标签页
@Composable
private fun NeedsTab(
    needs: MaslowNeeds,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("总体满足度：${(needs.getOverallSatisfaction() * 100).toInt()}%",
                     fontWeight = FontWeight.Bold, fontSize = 18.sp, color = themeColors.textPrimary)
                Text("最紧迫：${needs.getMostUrgentNeed().getDisplayName()}",
                     color = Color(0xFFFF5722), fontSize = 14.sp)
            }
        }
        
        MaslowNeedCard("1️⃣ 生理需求", needs.physiological.getAverage(), themeColors)
        MaslowNeedCard("2️⃣ 安全需求", needs.safety.getAverage(), themeColors)
        MaslowNeedCard("3️⃣ 社交需求", needs.social.getAverage(), themeColors)
        MaslowNeedCard("4️⃣ 尊重需求", needs.esteem.getAverage(), themeColors)
        MaslowNeedCard("5️⃣ 自我实现", needs.selfActualization.getAverage(), themeColors)
    }
}

@Composable
private fun MaslowNeedCard(
    title: String, 
    satisfaction: Float,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                satisfaction > 0.6f -> Color(0xFFE8F5E9)
                satisfaction < 0.4f -> Color(0xFFFFEBEE)
                else -> Color(0xFFFFF9C4)
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A), fontSize = 15.sp)
            Text("${(satisfaction * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), fontSize = 16.sp)
        }
    }
}

// 记忆标签页
@Composable
private fun MemoryTab(
    memories: CitizenMemoryCollection,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("记忆总数：${memories.memories.size} 条", 
                     fontWeight = FontWeight.Bold, 
                     color = themeColors.textPrimary)
                Text("人生转折点：${memories.getTurningPoints().size} 个", 
                     color = Color(0xFFE91E63))
            }
        }
        
        Text("重要记忆", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeColors.textPrimary)
        memories.getMostImportantMemories(5).forEach { memory ->
            CitizenMemoryCard(memory, themeColors)
        }
    }
}

@Composable
private fun CitizenMemoryCard(
    memory: CitizenMemory,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    val isLightBackground = memory.emotionalImpact > 0.5f || memory.emotionalImpact < -0.5f
    val textColor = if (isLightBackground) Color(0xFF1A1A1A) else themeColors.textPrimary
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                memory.emotionalImpact > 0.5f -> Color(0xFFE8F5E9)
                memory.emotionalImpact < -0.5f -> Color(0xFFFFEBEE)
                else -> themeColors.cardBackground
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(memory.title, 
                     fontWeight = FontWeight.Bold, 
                     fontSize = 15.sp, 
                     color = textColor)
                Text(memory.importance.getDisplayName(), 
                     fontSize = 12.sp, 
                     fontWeight = FontWeight.Medium,
                     color = if (isLightBackground) Color(0xFF555555) else themeColors.textSecondary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(memory.description, 
                 fontSize = 13.sp, 
                 color = if (isLightBackground) Color(0xFF333333) else themeColors.textSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text("情感: ${memory.getEmotionalTone()}", 
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Medium,
                 color = if (isLightBackground) Color(0xFF7B1FA2) else Color(0xFF9C27B0))
        }
    }
}

// 社交网络标签页
@Composable
private fun SocialNetworkTab(
    socialNetwork: SocialNetwork,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("关系总数：${socialNetwork.relationships.size} 个",
                     fontWeight = FontWeight.Bold,
                     color = themeColors.textPrimary)
                Text("亲密关系：${socialNetwork.relationships.filter { it.intimacy > 0.7f }.size} 个",
                     color = Color(0xFFE91E63))
            }
        }
        
        Text("主要关系", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeColors.textPrimary)
        socialNetwork.relationships.take(5).forEach { relationship ->
            SocialRelationshipCard(relationship, themeColors)
        }
    }
}

@Composable
private fun SocialRelationshipCard(
    relationship: SocialRelationship,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(relationship.relationshipType.getDisplayName(),
                         fontWeight = FontWeight.Medium,
                         fontSize = 14.sp,
                         color = themeColors.textPrimary)
                    Text("市民 ${relationship.citizen2Id.takeLast(3)}",
                         fontSize = 12.sp,
                         color = themeColors.textSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("亲密度: ${(relationship.intimacy * 100).toInt()}%",
                         fontSize = 11.sp,
                         color = Color(0xFFE91E63))
                    Text("信任度: ${(relationship.trust * 100).toInt()}%",
                         fontSize = 11.sp,
                         color = Color(0xFF2196F3))
                }
            }
        }
    }
}

// 八卦标签页
@Composable
private fun GossipTab(
    gossips: List<com.citysimulator.game.ai.Gossip>,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("听到的消息：${gossips.size} 条",
                     fontWeight = FontWeight.Bold,
                     color = themeColors.textPrimary)
                Text("平均可信度：${(gossips.map { it.credibility }.average() * 100).toInt()}%",
                     color = Color(0xFFFF9800))
            }
        }
        
        Text("最近听说", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeColors.textPrimary)
        gossips.forEach { gossip ->
            GossipCard(gossip, themeColors)
        }
    }
}

@Composable
private fun GossipCard(
    gossip: com.citysimulator.game.ai.Gossip,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    val isLightBackground = gossip.credibility > 0.7f || gossip.credibility < 0.5f
    val textColor = if (isLightBackground) Color(0xFF1A1A1A) else themeColors.textPrimary
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                gossip.credibility > 0.7f -> Color(0xFFE8F5E9)
                gossip.credibility < 0.5f -> Color(0xFFFFEBEE)
                else -> themeColors.cardBackground
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("💬 ${gossip.topic}",
                     fontWeight = FontWeight.Bold,
                     fontSize = 14.sp,
                     color = textColor)
                Text("可信度: ${(gossip.credibility * 100).toInt()}%",
                     fontSize = 12.sp,
                     fontWeight = FontWeight.Medium,
                     color = if (isLightBackground) Color(0xFFE65100) else Color(0xFFFF9800))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(gossip.content,
                 fontSize = 14.sp,
                 fontWeight = FontWeight.Medium,
                 color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text("传播速率: ${String.format("%.1f", gossip.spreadRate)}x",
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Medium,
                 color = if (isLightBackground) Color(0xFF555555) else themeColors.textSecondary)
        }
    }
}

// 事件标签页
@Composable
private fun EventTab(
    events: List<com.citysimulator.game.ai.SpontaneousEvent>,
    scrollState: androidx.compose.foundation.ScrollState,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("参与的事件：${events.size} 个",
                     fontWeight = FontWeight.Bold,
                     color = themeColors.textPrimary)
                if (events.isNotEmpty()) {
                    Text("总参与人数：${events.sumOf { it.participants.size }} 人",
                         color = Color(0xFF4CAF50))
                }
            }
        }
        
        Text("最近活动", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeColors.textPrimary)
        events.forEach { event ->
            EventCard(event, themeColors)
        }
    }
}

@Composable
private fun EventCard(
    event: com.citysimulator.game.ai.SpontaneousEvent,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    val eventIcon = when (event.type) {
        com.citysimulator.game.ai.EventType.CELEBRATION -> "🎉"
        com.citysimulator.game.ai.EventType.PROTEST -> "📢"
        com.citysimulator.game.ai.EventType.COMMUNITY_GATHERING -> "🤝"
        com.citysimulator.game.ai.EventType.FLASH_MOB -> "💃"
        else -> "🎪"
    }
    
    val isLightBackground = event.type == com.citysimulator.game.ai.EventType.CELEBRATION || 
                           event.type == com.citysimulator.game.ai.EventType.PROTEST
    val textColor = if (isLightBackground) Color(0xFF1A1A1A) else themeColors.textPrimary
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (event.type) {
                com.citysimulator.game.ai.EventType.CELEBRATION -> Color(0xFFE8F5E9)
                com.citysimulator.game.ai.EventType.PROTEST -> Color(0xFFFFEBEE)
                else -> themeColors.cardBackground
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$eventIcon ${event.title}",
                     fontWeight = FontWeight.Bold,
                     fontSize = 15.sp,
                     color = textColor)
                Text("${event.participants.size} 人参与",
                     fontSize = 12.sp,
                     fontWeight = FontWeight.Medium,
                     color = if (isLightBackground) Color(0xFF2E7D32) else Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("📍 位置: (${event.location?.first ?: "?"}, ${event.location?.second ?: "?"})",
                 fontSize = 13.sp,
                 fontWeight = FontWeight.Medium,
                 color = if (isLightBackground) Color(0xFF555555) else themeColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(event.description,
                 fontSize = 14.sp,
                 fontWeight = FontWeight.Medium,
                 color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text("强度: ${when {
                event.intensity > 0.7f -> "热烈 🔥"
                event.intensity > 0.4f -> "积极 👍"
                else -> "平静 😐"
            }}",
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Medium,
                 color = if (isLightBackground) Color(0xFF7B1FA2) else Color(0xFF9C27B0))
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
}

/**
 * 市长建议对话框 - 玩家输入建议
 */
@Composable
private fun MayorAdviceDialog(
    citizen: Citizen,
    citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel?,
    onDismiss: () -> Unit,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    // 获取市民对市长的信任度
    val mayorTrust = remember(citizen.id) {
        citizenViewModel?.getCitizenTrust(citizen.id) ?: 0.5f
    }
    
    // 玩家输入的建议
    var playerAdviceText by remember { mutableStateOf("") }
    
    // 显示结果
    var showResult by remember { mutableStateOf(false) }
    var citizenResponse by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(false) }
    var trustChange by remember { mutableStateOf(0f) }
    var moodChange by remember { mutableStateOf(0f) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💡",
                    fontSize = 24.sp
                )
                Text(
                    text = "给${citizen.name}一个建议",
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
                // 市民信息摘要
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = themeColors.cardBackground
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${citizen.name}的当前状态",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "幸福度: ${(citizen.happiness * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeColors.textSecondary
                            )
                            Text(
                                text = "健康: ${(citizen.health * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeColors.textSecondary
                            )
                            Text(
                                text = "财富: ${citizen.wealth}",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeColors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "职业: ${citizen.occupation ?: "无"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = themeColors.textSecondary
                            )
                            Text(
                                text = "信任度: ${(mayorTrust * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (mayorTrust > 0.6f) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!showResult) {
                    // 玩家输入建议
                    Text(
                        text = "你想对${citizen.name}说什么？",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 建议输入框
                    OutlinedTextField(
                        value = playerAdviceText,
                        onValueChange = { playerAdviceText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = "例如：\n• 你为什么不试试去那家新公司面试？\n• 我建议你去学习新技能，提升自己\n• 多出去走走，交些朋友吧",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = themeColors.divider,
                            focusedTextColor = themeColors.textPrimary,
                            unfocusedTextColor = themeColors.textPrimary
                        ),
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 快捷建议按钮
                    Text(
                        text = "💡 快捷建议:",
                        style = MaterialTheme.typography.bodySmall,
                        color = themeColors.textSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickAdviceChip(
                            text = "找工作",
                            onClick = { playerAdviceText = "我建议你去找份工作，提升生活质量" }
                        )
                        QuickAdviceChip(
                            text = "学习",
                            onClick = { playerAdviceText = "你应该去学习新技能，投资自己的未来" }
                        )
                        QuickAdviceChip(
                            text = "交友",
                            onClick = { playerAdviceText = "多出去社交，结识新朋友会让生活更有趣" }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickAdviceChip(
                            text = "锻炼",
                            onClick = { playerAdviceText = "保持健康很重要，每天锻炼一下身体吧" }
                        )
                        QuickAdviceChip(
                            text = "存钱",
                            onClick = { playerAdviceText = "建议你控制开支，多存一些钱以备不时之需" }
                        )
                    }
                } else {
                    // 显示市民的回复
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (accepted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // 结果标题
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (accepted) "✅" else "❌",
                                    fontSize = 48.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = if (accepted) "${citizen.name}接受了你的建议！" else "${citizen.name}拒绝了你的建议",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 玩家的建议
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE3F2FD)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "你的建议:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "\"$playerAdviceText\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF2C2C2C),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // 市民的回复
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${citizen.name}说:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "\"$citizenResponse\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF2C2C2C),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 数据统计
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "信任度",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        text = "${(mayorTrust * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2196F3)
                                    )
                                    Text(
                                        text = if (trustChange > 0) "+${(trustChange * 100).toInt()}%" 
                                               else "${(trustChange * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (trustChange > 0) Color(0xFF4CAF50) else Color(0xFFFF5722)
                                    )
                                }
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "心情变化",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        text = if (moodChange > 0) "+${(moodChange * 100).toInt()}%" 
                                               else "${(moodChange * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (moodChange > 0) Color(0xFF4CAF50) else Color(0xFFFF5722)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (showResult) {
                TextButton(
                    onClick = {
                        showResult = false
                        playerAdviceText = ""
                    }
                ) {
                    Text("再给一个建议")
                }
            } else {
                Button(
                    onClick = {
                        if (playerAdviceText.isNotBlank()) {
                            // 调用AI分析玩家的建议
                            val personality = citizen.personalityTraits 
                                ?: com.citysimulator.game.data.model.PersonalityTraits.generateRandom()
                            
                            // 计算基础接受概率
                            var acceptanceProbability = 0.3f + (mayorTrust * 0.4f)
                            
                            // 根据市民状态调整
                            if (citizen.happiness < 0.4f) acceptanceProbability += 0.1f
                            if (citizen.occupation == null) acceptanceProbability += 0.15f
                            
                            // 随机判断是否接受
                            accepted = Math.random() < acceptanceProbability
                            
                            // 信任度变化
                            trustChange = if (accepted) {
                                0.05f
                            } else {
                                if (mayorTrust < 0.3f) -0.02f else 0.01f
                            }
                            
                            // 心情变化
                            moodChange = if (accepted) {
                                (0.05f + Math.random().toFloat() * 0.05f).toFloat()
                            } else {
                                (-0.03f + Math.random().toFloat() * 0.02f).toFloat()
                            }
                            
                            // 使用AI生成市民回复
                            citizenResponse = generateCitizenResponseToAdvice(
                                citizen = citizen,
                                playerAdvice = playerAdviceText,
                                accepted = accepted,
                                mayorTrust = mayorTrust,
                                personality = personality
                            )
                            
                            // 更新信任度
                            citizenViewModel?.updateCitizenTrust(
                                citizen.id,
                                (mayorTrust + trustChange).coerceIn(0f, 1f)
                            )
                            
                            showResult = true
                        }
                    },
                    enabled = playerAdviceText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color(0xFF2C2C2C)
                    )
                ) {
                    Text("发送建议", fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onDismiss) {
                Text(if (showResult) "完成" else "取消")
            }
        }
    )
}

/**
 * 快捷建议芯片
 */
@Composable
private fun QuickAdviceChip(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 生成市民对建议的AI回复
 */
private fun generateCitizenResponseToAdvice(
    citizen: Citizen,
    playerAdvice: String,
    accepted: Boolean,
    mayorTrust: Float,
    personality: com.citysimulator.game.data.model.PersonalityTraits
): String {
    // 构建AI提示词
    val prompt = buildString {
        appendLine("你是一个城市模拟游戏中的市民，请根据以下信息生成对市长建议的真实回复：")
        appendLine()
        appendLine("【市民信息】")
        appendLine("姓名: ${citizen.name}")
        appendLine("年龄: ${citizen.age}岁")
        appendLine("性别: ${if (citizen.gender == Gender.MALE) "男" else "女"}")
        appendLine("职业: ${citizen.occupation ?: "无业"}")
        appendLine("幸福度: ${(citizen.happiness * 100).toInt()}%")
        appendLine("健康: ${(citizen.health * 100).toInt()}%")
        appendLine("财富: ${citizen.wealth}金币")
        appendLine()
        appendLine("【性格特质】")
        appendLine("外向性: ${(personality.extraversion * 100).toInt()}% ${if (personality.extraversion > 0.6f) "(外向)" else "(内向)"}")
        appendLine("勤奋度: ${(personality.diligence * 100).toInt()}% ${if (personality.diligence > 0.6f) "(勤奋)" else "(懒散)"}")
        appendLine("好奇心: ${(personality.curiosity * 100).toInt()}% ${if (personality.curiosity > 0.6f) "(好奇)" else "(保守)"}")
        appendLine("友善度: ${(personality.kindness * 100).toInt()}% ${if (personality.kindness > 0.6f) "(友善)" else "(冷漠)"}")
        appendLine("稳定性: ${(personality.stability * 100).toInt()}% ${if (personality.stability > 0.6f) "(稳定)" else "(情绪化)"}")
        appendLine("创造力: ${(personality.creativity * 100).toInt()}% ${if (personality.creativity > 0.6f) "(有创意)" else "(务实)"}")
        appendLine("野心: ${(personality.ambition * 100).toInt()}% ${if (personality.ambition > 0.6f) "(有野心)" else "(知足)"}")
        appendLine("叛逆性: ${(personality.rebelliousness * 100).toInt()}% ${if (personality.rebelliousness > 0.6f) "(叛逆)" else "(顺从)"}")
        appendLine()
        appendLine("【对市长的信任度】")
        appendLine("${(mayorTrust * 100).toInt()}% ${when {
            mayorTrust > 0.7f -> "(非常信任)"
            mayorTrust > 0.5f -> "(比较信任)"
            mayorTrust > 0.3f -> "(一般信任)"
            else -> "(不太信任)"
        }}")
        appendLine()
        appendLine("【市长的建议】")
        appendLine("\"$playerAdvice\"")
        appendLine()
        appendLine("【你的决定】")
        appendLine(if (accepted) "接受了这个建议" else "拒绝了这个建议")
        appendLine()
        appendLine("【要求】")
        appendLine("1. 请以第一人称生成${citizen.name}对市长建议的真实回复（30-60字）")
        appendLine("2. 回复要符合${citizen.name}的性格特质")
        appendLine("3. 回复要体现对市长的信任度")
        appendLine("4. 如果接受，要表达感谢和积极态度")
        appendLine("5. 如果拒绝，要给出合理的理由，但态度要礼貌")
        appendLine("6. 不要使用引号，直接输出回复内容")
        appendLine()
        appendLine("回复:")
    }
    
    // 调用AI生成回复（使用CitizenTextGenerator）
    return try {
        // 使用现有的AI文本生成器
        val aiResponse = com.citysimulator.game.ai.CitizenTextGenerator.generateResponseToMayorAdvice(
            citizen = citizen,
            playerAdvice = playerAdvice,
            accepted = accepted,
            mayorTrust = mayorTrust,
            personality = personality
        )
        
        if (aiResponse.isNullOrBlank()) {
            // AI调用失败，使用fallback
            generateFallbackResponse(accepted, mayorTrust, personality, citizen)
        } else {
            aiResponse.trim()
        }
    } catch (e: Exception) {
        // 异常时使用fallback
        generateFallbackResponse(accepted, mayorTrust, personality, citizen)
    }
}

/**
 * 生成fallback回复（AI不可用时）
 */
private fun generateFallbackResponse(
    accepted: Boolean,
    mayorTrust: Float,
    personality: com.citysimulator.game.data.model.PersonalityTraits,
    citizen: Citizen
): String {
    return if (accepted) {
        when {
            mayorTrust > 0.7f -> "市长一直为我们着想，我相信你的建议！"
            personality.curiosity > 0.7f -> "这个建议听起来很有趣，我想试试看。"
            personality.ambition > 0.7f -> "好的，这确实是个不错的主意！"
            personality.kindness > 0.7f -> "谢谢你的关心，我会认真考虑的。"
            else -> "我会试着按照你的建议去做。"
        }
    } else {
        when {
            mayorTrust < 0.3f -> "抱歉，我现在还不太信任这个建议..."
            personality.rebelliousness > 0.7f -> "我有自己的想法，不需要别人告诉我怎么做。"
            personality.stability > 0.7f -> "我现在的生活还可以，暂时不想改变。"
            citizen.happiness < 0.3f -> "我现在心情不太好，不想考虑这些..."
            else -> "谢谢你的建议，但我觉得现在不太合适。"
        }
    }
}

