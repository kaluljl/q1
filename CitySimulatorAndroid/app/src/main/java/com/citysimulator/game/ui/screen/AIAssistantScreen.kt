package com.citysimulator.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.citysimulator.game.ai.deepseek.IntelligentAISystem
import com.citysimulator.game.data.model.AINPC
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.Resource
import com.citysimulator.game.ui.component.TopStatusBar
import com.citysimulator.game.ui.component.BottomControlBar
import com.citysimulator.game.ui.component.GoldDisplay
import com.citysimulator.game.ui.component.ResourceDisplay
import com.citysimulator.game.ui.component.WeatherEffect
import com.citysimulator.game.ui.state.BuildingSelectionState
import com.citysimulator.game.ui.theme.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AI助手屏幕 - 集成DeepSeek AI功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onNavigateBack: () -> Unit,
    intelligentAI: IntelligentAISystem,
    onNavigateToAPIKeyConfig: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(AITab.CITY_ANALYSIS) }
    var isLoading by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🤖 AI智能助手",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                TextButton(onClick = onNavigateToAPIKeyConfig) {
                    Text("🔑 配置")
                }
                TextButton(onClick = onNavigateBack) {
                    Text("← 返回")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 标签页
        TabRow(selectedTabIndex = currentTab.ordinal) {
            AITab.values().forEach { tab ->
                Tab(
                    selected = currentTab == tab,
                    onClick = { currentTab = tab },
                    text = { Text(tab.displayName) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 内容区域
        when (currentTab) {
            AITab.CITY_ANALYSIS -> {
                CityAnalysisTab(
                    intelligentAI = intelligentAI,
                    isLoading = isLoading,
                    aiResponse = aiResponse,
                    errorMessage = errorMessage,
                    onLoadingChange = { isLoading = it },
                    onResponseChange = { aiResponse = it },
                    onErrorChange = { errorMessage = it }
                )
            }
            
            AITab.NPC_CHAT -> {
                NPCChatTab(
                    intelligentAI = intelligentAI,
                    isLoading = isLoading,
                    aiResponse = aiResponse,
                    errorMessage = errorMessage,
                    onLoadingChange = { isLoading = it },
                    onResponseChange = { aiResponse = it },
                    onErrorChange = { errorMessage = it }
                )
            }
            
            AITab.CITY_FORECAST -> {
                CityForecastTab(
                    intelligentAI = intelligentAI,
                    isLoading = isLoading,
                    aiResponse = aiResponse,
                    errorMessage = errorMessage,
                    onLoadingChange = { isLoading = it },
                    onResponseChange = { aiResponse = it },
                    onErrorChange = { errorMessage = it }
                )
            }
            
            AITab.BUILDING_RECOMMENDATION -> {
                BuildingRecommendationTab(
                    intelligentAI = intelligentAI,
                    isLoading = isLoading,
                    aiResponse = aiResponse,
                    errorMessage = errorMessage,
                    onLoadingChange = { isLoading = it },
                    onResponseChange = { aiResponse = it },
                    onErrorChange = { errorMessage = it }
                )
            }
        }
    }
}

@Composable
fun CityAnalysisTab(
    intelligentAI: IntelligentAISystem,
    isLoading: Boolean,
    aiResponse: String,
    errorMessage: String,
    onLoadingChange: (Boolean) -> Unit,
    onResponseChange: (String) -> Unit,
    onErrorChange: (String) -> Unit
) {
    var playerMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "🏙️ 城市分析",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = playerMessage,
            onValueChange = { playerMessage = it },
            label = { Text("输入您的问题...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    onLoadingChange(true)
                    onErrorChange("")
                    try {
                        // 模拟城市数据
                        val cityData = com.citysimulator.game.ai.deepseek.CityData(
                            population = 150,
                            buildingCount = 25,
                            avgHappiness = 65f,
                            avgEnergy = 70f,
                            employmentRate = 80f
                        )
                        
                        val issues = listOf("居民满意度偏低", "缺乏娱乐设施")
                        val response = intelligentAI.intelligentCityAnalysis(
                            buildings = emptyList(),
                            npcs = emptyList(),
                            resources = emptyList()
                        )
                        
                        onResponseChange(response.aiRecommendations)
                    } catch (e: Exception) {
                        onErrorChange("AI分析失败: ${e.message}")
                    } finally {
                        onLoadingChange(false)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "分析中..." else "开始AI分析")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        if (aiResponse.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = aiResponse,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun NPCChatTab(
    intelligentAI: IntelligentAISystem,
    isLoading: Boolean,
    aiResponse: String,
    errorMessage: String,
    onLoadingChange: (Boolean) -> Unit,
    onResponseChange: (String) -> Unit,
    onErrorChange: (String) -> Unit
) {
    var playerMessage by remember { mutableStateOf("") }
    var selectedNPC by remember { mutableStateOf("张三") }
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "💬 NPC智能对话",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = selectedNPC,
            onValueChange = { selectedNPC = it },
            label = { Text("选择NPC") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = playerMessage,
            onValueChange = { playerMessage = it },
            label = { Text("输入对话内容...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    onLoadingChange(true)
                    onErrorChange("")
                    try {
                        // 模拟NPC数据
                        val npc = AINPC(
                            name = selectedNPC,
                            age = 30,
                            profession = com.citysimulator.game.data.model.Profession.TEACHER,
                            personality = com.citysimulator.game.data.model.Personality.EXTROVERT,
                            currentLocation = Pair(5, 5),
                            homeLocation = Pair(3, 3),
                            workLocation = Pair(7, 7),
                            currentActivity = com.citysimulator.game.data.model.NPCActivity.WORKING,
                            happiness = 75f,
                            energy = 60f,
                            hunger = 40f,
                            socialNeed = 30f
                        )
                        
                        val cityContext = com.citysimulator.game.ai.deepseek.CityContext(
                            population = 150,
                            buildingCount = 25,
                            weather = "晴天",
                            avgHappiness = 65f,
                            recentEvents = listOf("新建筑完工", "节日庆典")
                        )
                        
                        val response = intelligentAI.intelligentNPCChat(npc, playerMessage, cityContext)
                        onResponseChange("${response.npcName}: ${response.message}")
                    } catch (e: Exception) {
                        onErrorChange("对话失败: ${e.message}")
                    } finally {
                        onLoadingChange(false)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "对话中..." else "开始对话")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        if (aiResponse.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = aiResponse,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CityForecastTab(
    intelligentAI: IntelligentAISystem,
    isLoading: Boolean,
    aiResponse: String,
    errorMessage: String,
    onLoadingChange: (Boolean) -> Unit,
    onResponseChange: (String) -> Unit,
    onErrorChange: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "🔮 城市发展预测",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    onLoadingChange(true)
                    onErrorChange("")
                    try {
                        val currentData = com.citysimulator.game.ai.deepseek.CityData(
                            population = 150,
                            buildingCount = 25,
                            avgHappiness = 65f,
                            avgEnergy = 70f,
                            employmentRate = 80f
                        )
                        
                        val historicalData = listOf(
                            com.citysimulator.game.ai.deepseek.CitySnapshot(120, 60f, System.currentTimeMillis() - 86400000),
                            com.citysimulator.game.ai.deepseek.CitySnapshot(135, 62f, System.currentTimeMillis() - 43200000),
                            com.citysimulator.game.ai.deepseek.CitySnapshot(150, 65f, System.currentTimeMillis())
                        )
                        
                        val forecast = intelligentAI.intelligentCityForecast(currentData, historicalData)
                        onResponseChange(forecast.forecastText)
                    } catch (e: Exception) {
                        onErrorChange("预测失败: ${e.message}")
                    } finally {
                        onLoadingChange(false)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "预测中..." else "开始AI预测")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        if (aiResponse.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = aiResponse,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun BuildingRecommendationTab(
    intelligentAI: IntelligentAISystem,
    isLoading: Boolean,
    aiResponse: String,
    errorMessage: String,
    onLoadingChange: (Boolean) -> Unit,
    onResponseChange: (String) -> Unit,
    onErrorChange: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "🏗️ 智能建筑推荐",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    onLoadingChange(true)
                    onErrorChange("")
                    try {
                        val cityData = com.citysimulator.game.ai.deepseek.CityData(
                            population = 150,
                            buildingCount = 25,
                            avgHappiness = 65f,
                            avgEnergy = 70f,
                            employmentRate = 80f
                        )
                        
                        val recommendation = intelligentAI.intelligentBuildingRecommendation(
                            cityData = cityData,
                            availableBudget = 5000,
                            buildings = emptyList(),
                            npcs = emptyList()
                        )
                        
                        onResponseChange(recommendation.recommendationText)
                    } catch (e: Exception) {
                        onErrorChange("推荐失败: ${e.message}")
                    } finally {
                        onLoadingChange(false)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "分析中..." else "获取AI推荐")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        if (aiResponse.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = aiResponse,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

enum class AITab(val displayName: String) {
    CITY_ANALYSIS("城市分析"),
    NPC_CHAT("NPC对话"),
    CITY_FORECAST("发展预测"),
    BUILDING_RECOMMENDATION("建筑推荐")
}
