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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.citysimulator.game.data.model.TechType
import com.citysimulator.game.data.model.Technology
import com.citysimulator.game.ui.theme.*
import com.citysimulator.game.ui.viewmodel.TechTreeViewModel

/**
 * 科技树界面
 * 
 * 显示科技研究进度和可研究的科技
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechTreeScreen(
    onNavigateBack: () -> Unit,
    viewModel: TechTreeViewModel = hiltViewModel()
) {
    val technologies by viewModel.technologies.collectAsState()
    val availableTechnologies by viewModel.availableTechnologies.collectAsState()
    val researchedTechnologies by viewModel.researchedTechnologies.collectAsState()
    val researchingTechnologies by viewModel.researchingTechnologies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("可研究", "研究中", "已研究", "全部")
    
    LaunchedEffect(Unit) {
        viewModel.checkResearchCompletion()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部标题栏
        TopAppBar(
            title = { 
                Text(
                    "科技树",
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
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
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
                0 -> AvailableTechList(
                    technologies = availableTechnologies,
                    onStartResearch = { tech ->
                        viewModel.startResearch(tech, 1000) // TODO: 获取实际金币数量
                    }
                )
                1 -> ResearchingTechList(technologies = researchingTechnologies)
                2 -> ResearchedTechList(technologies = researchedTechnologies)
                3 -> AllTechList(technologies = technologies)
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
private fun AvailableTechList(
    technologies: List<Technology>,
    onStartResearch: (Technology) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(technologies) { tech ->
            AvailableTechCard(
                technology = tech,
                onStartResearch = onStartResearch
            )
        }
    }
}

@Composable
private fun AvailableTechCard(
    technology: Technology,
    onStartResearch: (Technology) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStartResearch(technology) },
        colors = CardDefaults.cardColors(
            containerColor = getTechTypeColor(technology.type)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = technology.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = technology.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${technology.researchCost} 金币",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${technology.researchTime} 分钟",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 效果预览
            if (technology.effects.specialAbilities.isNotEmpty()) {
                Text(
                    text = "效果: ${technology.effects.specialAbilities.joinToString(", ")}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ResearchingTechList(technologies: List<Technology>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(technologies) { tech ->
            ResearchingTechCard(technology = tech)
        }
    }
}

@Composable
private fun ResearchingTechCard(technology: Technology) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = technology.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "研究中...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // 研究进度条
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ResearchedTechList(technologies: List<Technology>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(technologies) { tech ->
            ResearchedTechCard(technology = tech)
        }
    }
}

@Composable
private fun ResearchedTechCard(technology: Technology) {
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
                    text = technology.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "已完成",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = technology.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AllTechList(technologies: List<Technology>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(technologies) { tech ->
            AllTechCard(technology = tech)
        }
    }
}

@Composable
private fun AllTechCard(technology: Technology) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (technology.isResearched) {
                MaterialTheme.colorScheme.tertiary
            } else {
                getTechTypeColor(technology.type)
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
                    text = technology.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (technology.isResearched) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "已完成",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = technology.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun getTechTypeColor(type: TechType): Color {
    return when (type) {
        TechType.INFRASTRUCTURE -> Color(0xFF4CAF50) // 绿色
        TechType.INDUSTRY -> Color(0xFF2196F3) // 蓝色
        TechType.ENVIRONMENT -> Color(0xFF8BC34A) // 浅绿色
        TechType.SOCIAL -> Color(0xFF9C27B0) // 紫色
        TechType.ADVANCED -> Color(0xFFFF9800) // 橙色
    }
}

