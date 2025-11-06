package com.citysimulator.game.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.citysimulator.game.data.model.WeatherType
import com.citysimulator.game.data.model.Gender
import com.citysimulator.game.ui.component.*
import com.citysimulator.game.ui.theme.*
import com.citysimulator.game.ui.viewmodel.MainGameViewModel
import com.citysimulator.game.ui.viewmodel.SupabaseGameViewModel
import com.citysimulator.game.ui.viewmodel.PopulationViewModel
import com.citysimulator.game.ui.state.BuildingSelectionState
import com.citysimulator.game.ai.CityProsperityEngine
import com.citysimulator.game.data.model.CityProsperity
import java.util.Calendar
import java.util.Date

/**
 * 主游戏屏幕
 * 
 * 城市模拟经营游戏的核心界面，包含城市网格、状态栏和控制栏。
 * 使用Material Design 3设计规范，提供现代化的用户体验。
 * 
 * @param onNavigateToBuildingMenu 导航到建筑菜单的回调
 * @param onNavigateToResourcePanel 导航到资源面板的回调
 * @param onNavigateToTaskPanel 导航到任务面板的回调
 * @param onNavigateToAchievementPanel 导航到成就面板的回调
 * @param onNavigateToSettings 导航到设置的回调
 * @param viewModel 主游戏视图模型
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameScreen(
    placementViewModel: com.citysimulator.game.ui.viewmodel.BuildingPlacementViewModel,
    taskViewModel: com.citysimulator.game.ui.viewmodel.TaskViewModel,
    onNavigateToBuildingMenu: () -> Unit,
    onNavigateToResourcePanel: () -> Unit,
    onNavigateToTaskPanel: () -> Unit,
    onNavigateToAchievementPanel: () -> Unit,
    onNavigateToSupabaseConfig: () -> Unit,
    onNavigateToTechTree: () -> Unit,
    onNavigateToCityPolicy: () -> Unit,
    onNavigateToCityEvent: () -> Unit,
    onNavigateToCitizenFeedback: () -> Unit,
    onNavigateToEconomy: (() -> Unit)? = null,
    onNavigateToCitizenList: (() -> Unit)? = null,
    onNavigateToCitizenAIChat: ((String) -> Unit)? = null, // 新增：导航到AI对话
    onNavigateToSettings: (() -> Unit)? = null, // 新增：导航到设置
    viewModel: MainGameViewModel = hiltViewModel(),
    supabaseViewModel: SupabaseGameViewModel = hiltViewModel(),
    populationViewModel: PopulationViewModel = hiltViewModel(),
    citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel(),
    policyViewModel: com.citysimulator.game.ui.viewmodel.CityPolicyViewModel = hiltViewModel(),
    gameTimeViewModel: com.citysimulator.game.ui.viewmodel.GameTimeViewModel = hiltViewModel(),
    feedbackViewModel: com.citysimulator.game.ui.viewmodel.CitizenFeedbackViewModel = hiltViewModel(),
    themeViewModel: com.citysimulator.game.ui.viewmodel.ThemeViewModel = hiltViewModel()
) {
    // 使用全局ThemeManager而不是ViewModel
    var currentTheme by remember { mutableStateOf(com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()) }
    
    // 监听主题变化
    DisposableEffect(Unit) {
        println("🎨 [MainGameScreen] 注册主题监听器")
        val listener: (com.citysimulator.game.ui.theme.ThemeType) -> Unit = { newThemeType ->
            println("🎨 [MainGameScreen] 收到主题变化通知: $newThemeType")
            currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
        }
        com.citysimulator.game.ui.theme.ThemeManager.addListener(listener)
        
        onDispose {
            println("🎨 [MainGameScreen] 移除主题监听器")
            com.citysimulator.game.ui.theme.ThemeManager.removeListener(listener)
        }
    }
    
    // 播放背景音乐（只在第一次进入时启动，之后持续播放）
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        println("🎵 [MainGameScreen] 确保背景音乐播放中")
        // 尝试播放音乐（如果已经在播放则不会重复播放）
        com.citysimulator.game.audio.SoundManager.playMusic(
            context = context,
            musicResId = com.citysimulator.game.R.raw.main_theme,
            loop = true
        )
    }
    
    // 建筑选择状态管理 - 使用本地状态
    var selectedBuildingType by remember { mutableStateOf<com.citysimulator.game.data.model.BuildingType?>(null) }
    var pendingBuildingPosition by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    
    // 从placementViewModel获取简化建筑类型
    val selectedSimplifiedBuilding by placementViewModel.selectedBuildingType.collectAsStateWithLifecycle()
    val isPlacementMode by placementViewModel.isPlacementMode.collectAsStateWithLifecycle()
    
    // 优化：移除调试日志，减少不必要的LaunchedEffect
    // LaunchedEffect(selectedSimplifiedBuilding, isPlacementMode) {
    //     println("🎮 MainGameScreen - 选中建筑: $selectedSimplifiedBuilding")
    //     println("🎮 MainGameScreen - 放置模式: $isPlacementMode")
    // }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var buildingToDelete by remember { mutableStateOf<com.citysimulator.game.data.model.Building?>(null) }
    
    // 市民交互
    var showCitizenDialog by remember { mutableStateOf(false) }
    var selectedCitizen by remember { mutableStateOf<com.citysimulator.game.data.model.Citizen?>(null) }
    
    // 防抖：记录最后一次建筑放置时间
    var lastBuildTime by remember { mutableStateOf(0L) }
    
    // 任务完成提示
    val justCompletedTask by taskViewModel.justCompletedTask.collectAsStateWithLifecycle()
    var showTaskCompletionDialog by remember { mutableStateOf(false) }
    var completedTaskForDialog by remember { mutableStateOf<com.citysimulator.game.data.model.Task?>(null) }
    
    // 监听任务完成
    LaunchedEffect(justCompletedTask) {
        if (justCompletedTask != null) {
            completedTaskForDialog = justCompletedTask
            showTaskCompletionDialog = true
        }
    }
    
    // 繁荣度系统
    val prosperityEngine = remember { CityProsperityEngine() }
    var cityProsperity by remember { mutableStateOf<CityProsperity?>(null) }
    
    // 公用事业系统
    var utilityStatus by remember { mutableStateOf<com.citysimulator.game.ai.UtilityStatus?>(null) }
    var utilityImpact by remember { mutableStateOf<com.citysimulator.game.ai.UtilityImpact?>(null) }
    // 默认收起资源面板，节省屏幕空间
    var showUtilityPanel by rememberSaveable { mutableStateOf(false) }
    
    // 人口系统
    val currentPopulation by populationViewModel.currentPopulation.collectAsStateWithLifecycle()
    val populationGrowthResult by populationViewModel.populationGrowthResult.collectAsStateWithLifecycle()
    val populationHistory by populationViewModel.populationHistory.collectAsStateWithLifecycle()
    
    // 市民系统
    val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
    
    // 优化：使用 DisposableEffect 确保只执行一次
    var hasInitializedPopulation by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        if (!hasInitializedPopulation && citizens.isNotEmpty() && currentPopulation < citizens.size) {
            populationViewModel.setPopulation(citizens.size)
            hasInitializedPopulation = true
        }
        onDispose { }
    }
    
    // 从 ViewModel 加载建筑数据（从本地数据库持久化存储）
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var buildings by remember { mutableStateOf(uiState.buildings) }
    val currentDistrict = uiState.currentDistrict
    
    // 监听 ViewModel 的建筑数据变化
    LaunchedEffect(uiState.buildings) {
        if (uiState.buildings.isNotEmpty() && buildings.isEmpty()) {
            buildings = uiState.buildings
            println("📦 从 ViewModel 加载了 ${buildings.size} 座建筑")
            
            // 为已有建筑初始化市民
            println("🔧 准备初始化市民，当前建筑列表: ${buildings.map { "${it.getDisplayName()}(${it.position.x},${it.position.y})" }}")
            citizenViewModel.initializeCitizens(buildings)
        }
    }
    
    // 如果没有建筑但需要测试市民，手动添加一些测试市民
    var hasInitializedTestCitizens by remember { mutableStateOf(false) }
    LaunchedEffect(citizens.size, buildings.size) {
        if (!hasInitializedTestCitizens && citizens.isEmpty() && buildings.isNotEmpty()) {
            println("⚠️ 发现有建筑但没有市民，重新初始化...")
            citizenViewModel.initializeCitizens(buildings)
            hasInitializedTestCitizens = true
        }
        if (citizens.isNotEmpty()) {
            println("✅ 当前市民数量: ${citizens.size}")
        }
    }
    
    // 金币数据（使用rememberSaveable保存）
    var goldAmount by rememberSaveable { mutableStateOf(1500) }
    
    // 同步金币到 ViewModel（供其他界面访问）
    LaunchedEffect(goldAmount) {
        supabaseViewModel.updateGoldAmount(goldAmount)
    }
    
    // 监听游戏时间变化
    val currentGameDate by gameTimeViewModel.gameDate.collectAsStateWithLifecycle()
    
    // 同步游戏时间到 MainGameViewModel（用于季节性天气系统）
    LaunchedEffect(currentGameDate) {
        viewModel.updateCurrentTime(currentGameDate)
    }
    
    // 监听游戏时间变化，每个月生成市民心声
    LaunchedEffect(currentGameDate) {
        // 注意：只监听 currentGameDate，不监听其他状态
        // 这样才能确保"每个游戏月生成一次"，而不是"状态变化就生成"
        val calendar = Calendar.getInstance().apply { time = currentGameDate }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH 是 0-11
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val gameTimeString = String.format("%d年%02d月%02d日 %02d:%02d", year, month, day, hour, minute)
        
        // 生成市民心声（每个月生成一次）
        // ViewModel 内部会检查月份，避免重复生成
        feedbackViewModel.generateFeedback(
            buildings = buildings,
            resources = listOf(), // 使用空列表，因为resources还未定义
            goldAmount = goldAmount,
            population = currentPopulation,
            gameYear = year,
            gameMonth = month,
            gameTime = gameTimeString
        )
        
        // 更新奇观建造进度（每个月）
        supabaseViewModel.updateWonderProgress { wonderTypeName ->
            // 奇观建造完成回调
            println("🏛️ 奇观建造完成事件触发: $wonderTypeName")
            
            // 获取奇观对象
            val wonder = com.citysimulator.game.ai.WonderBuildingSystem.getAllWonders()
                .find { it.type.name == wonderTypeName }
            
            wonder?.let {
                // 生成奇观完成叙事事件
                val event = com.citysimulator.game.ai.WonderBuildingSystem
                    .generateWonderCompletionEvent(wonder)
                println("📜 奇观完成事件: ${event.eventTitle}")
                println("📜 ${event.eventDescription}")
                
                // TODO: 应用奇观效果到所有市民
                // 这需要访问市民数据，可以在CitizenViewModel中实现
                println("✅ 奇观 ${wonder.name} 建造完成！")
            }
        }
    }
    
    // 获取已实施的政策
    val implementedPolicies by policyViewModel.implementedPolicies.collectAsStateWithLifecycle()
    
    // 计算每月收入（包含政策效果）
    val monthlyIncome = remember(buildings, implementedPolicies) {
        val baseIncome = buildings.sumOf { it.income }
        val incomeWithPolicy = com.citysimulator.game.ai.PolicyEffectCalculator.applyPolicyToGoldIncome(
            baseIncome,
            implementedPolicies
        )
        val monthlyCost = com.citysimulator.game.ai.PolicyEffectCalculator.getTotalMonthlyCost(implementedPolicies)
        incomeWithPolicy - monthlyCost
    }
    
    var resources by rememberSaveable { mutableStateOf(listOf(
        com.citysimulator.game.data.model.Resource(
            id = "wood_1",
            type = com.citysimulator.game.data.model.ResourceType.WOOD,
            amount = 500.0,
            maxCapacity = 1000.0,
            productionRate = 10.0,
            consumptionRate = 5.0
        ),
        com.citysimulator.game.data.model.Resource(
            id = "stone_1",
            type = com.citysimulator.game.data.model.ResourceType.STONE,
            amount = 300.0,
            maxCapacity = 1000.0,
            productionRate = 8.0,
            consumptionRate = 3.0
        ),
        com.citysimulator.game.data.model.Resource(
            id = "steel_1",
            type = com.citysimulator.game.data.model.ResourceType.STEEL,
            amount = 200.0,
            maxCapacity = 1000.0,
            productionRate = 5.0,
            consumptionRate = 2.0
        ),
        com.citysimulator.game.data.model.Resource(
            id = "food_1",
            type = com.citysimulator.game.data.model.ResourceType.FOOD,
            amount = 800.0,
            maxCapacity = 1000.0,
            productionRate = 15.0,
            consumptionRate = 10.0
        )
    )) }
    
    // 计算公用事业状态
    LaunchedEffect(buildings, currentPopulation) {
        val status = com.citysimulator.game.ai.UtilityManagementSystem.calculateUtilityStatus(
            buildings = buildings,
            population = currentPopulation
        )
        val impact = com.citysimulator.game.ai.UtilityManagementSystem.calculateUtilityImpact(status)
        
        utilityStatus = status
        utilityImpact = impact
        
        // 打印状态
        println("⚡ 电力: ${status.electricitySupply}/${status.electricityDemand} (${(status.electricityRatio * 100).toInt()}%)")
        println("💧 水源: ${status.waterSupply}/${status.waterDemand} (${(status.waterRatio * 100).toInt()}%)")
        println("🗑️ 垃圾: ${status.wasteCapacity}/${status.wasteProduction} (${(status.wasteRatio * 100).toInt()}%)")
        println("🏗️ 建筑效率: ${(impact.buildingEfficiency * 100).toInt()}%")
    }
    
    // 计算城市繁荣度（应用政策效果）
    LaunchedEffect(buildings, goldAmount, resources, implementedPolicies) {
        val baseProsperity = prosperityEngine.calculateProsperity(
            buildings = buildings,
            resources = resources,
            population = currentPopulation,
            goldAmount = goldAmount
        )
        
        // 应用政策加成到繁荣度
        val policyBonus = com.citysimulator.game.ai.PolicyEffectCalculator.calculateTotalEffects(implementedPolicies).prosperityBonus
        val adjustedProsperity = baseProsperity.copy(
            overallProsperity = baseProsperity.overallProsperity + policyBonus
        )
        
        cityProsperity = adjustedProsperity
        
        if (policyBonus > 0) {
            println("城市繁荣度更新: ${adjustedProsperity.prosperityLevel.displayName} (${String.format("%.1f", adjustedProsperity.overallProsperity)}%, 政策加成: +${policyBonus.toInt()})")
        } else {
            println("城市繁荣度更新: ${adjustedProsperity.prosperityLevel.displayName} (${String.format("%.1f", adjustedProsperity.overallProsperity)}%)")
        }
    }
    
    // 优化：降低更新频率，使用 debounce
    LaunchedEffect(buildings.size, goldAmount) {
        delay(2000) // 防抖2秒
        populationViewModel.updatePopulation(
            buildings = buildings,
            resources = resources,
            goldAmount = goldAmount,
            timeElapsed = 1.0
        )
    }
    
    // 监听全局状态变化 - 只在首次设置时更新
    LaunchedEffect(Unit) {
        val globalSelection = BuildingSelectionState.selectedBuildingType
        if (globalSelection != null && selectedBuildingType == null) {
            selectedBuildingType = globalSelection
            println("从全局状态初始化选择: $globalSelection")
        }
    }
    
        // 优化：大幅降低资源更新频率到 20 秒
        LaunchedEffect(Unit) {
            while (true) {
                delay(20000) // 从3秒改为20秒
                resources = resources.map { resource ->
                    val growth = resource.calculateGrowth(20) // 20秒的增长
                    val newAmount = (resource.amount + growth).coerceAtMost(resource.maxCapacity)
                    resource.copy(amount = newAmount)
                }
            }
        }

        // 金币收入系统（应用政策效果）
        // 游戏时间：每30秒 = 游戏内1个月（平衡游戏节奏）
        LaunchedEffect(buildings, implementedPolicies, currentPopulation) {
            var monthCount = 0
            while (true) {
                kotlinx.coroutines.delay(30000) // 每30秒结算一次，作为游戏内的"一个月"
                monthCount++
                
                // 计算月度收入
                val baseIncome = buildings.sumOf { building -> building.income }
                if (baseIncome > 0) {
                    // 应用政策效果到收入
                    val totalIncome = com.citysimulator.game.ai.PolicyEffectCalculator.applyPolicyToGoldIncome(
                        baseIncome,
                        implementedPolicies
                    )
                    
                    // 扣除政策维护成本（每月）
                    val monthlyCost = com.citysimulator.game.ai.PolicyEffectCalculator.getTotalMonthlyCost(implementedPolicies)
                    val actualIncome = totalIncome - monthlyCost
                    
                    // 更新金币
                    goldAmount += actualIncome
                    
                    if (implementedPolicies.isNotEmpty()) {
                        println("📅 第${monthCount}月结算 - 基础收入: $baseIncome -> 政策加成: $totalIncome 金币，扣除维护: $monthlyCost，实际收入: $actualIncome，总金币: $goldAmount")
                    } else {
                        println("📅 第${monthCount}月结算 - 获得收入: $totalIncome 金币，总金币: $goldAmount")
                    }
                }
            }
        }
        
        // 优化：大幅降低任务更新频率到15秒
        LaunchedEffect(buildings.size, currentPopulation, goldAmount) {
            while (true) {
                delay(15000) // 从5秒改为15秒
                
                // 统计各类建筑数量（优先使用customName反向识别）
                val buildingCount = mutableMapOf<com.citysimulator.game.data.model.SimplifiedBuildingType, Int>()
                buildings.forEach { building ->
                    // 尝试通过customName识别简化建筑类型
                    val simplifiedType = when (building.customName) {
                        "土路" -> com.citysimulator.game.data.model.SimplifiedBuildingType.DIRT_ROAD
                        "柏油路" -> com.citysimulator.game.data.model.SimplifiedBuildingType.PAVED_ROAD
                        "林荫大道" -> com.citysimulator.game.data.model.SimplifiedBuildingType.TREE_LINED_ROAD
                        "小木屋" -> com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_HOUSE
                        "现代化住宅" -> com.citysimulator.game.data.model.SimplifiedBuildingType.MODERN_RESIDENCE
                        "公寓楼" -> com.citysimulator.game.data.model.SimplifiedBuildingType.APARTMENT
                        "便利店" -> com.citysimulator.game.data.model.SimplifiedBuildingType.CONVENIENCE_STORE
                        "购物中心" -> com.citysimulator.game.data.model.SimplifiedBuildingType.SHOPPING_MALL
                        "小农场" -> com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_FARM
                        "食品加工厂" -> com.citysimulator.game.data.model.SimplifiedBuildingType.FOOD_FACTORY
                        "科技园区" -> com.citysimulator.game.data.model.SimplifiedBuildingType.TECH_PARK
                        "风车" -> com.citysimulator.game.data.model.SimplifiedBuildingType.WINDMILL
                        "燃煤发电厂" -> com.citysimulator.game.data.model.SimplifiedBuildingType.COAL_PLANT
                        "水井" -> com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_WELL
                        "水泵站" -> com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_PUMP
                        "回收站" -> com.citysimulator.game.data.model.SimplifiedBuildingType.RECYCLING_CENTER
                        "小诊所" -> com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_CLINIC
                        "医院" -> com.citysimulator.game.data.model.SimplifiedBuildingType.HOSPITAL
                        "派出所", "警察局" -> com.citysimulator.game.data.model.SimplifiedBuildingType.POLICE_STATION
                        "小公园" -> com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_PARK
                        "市民广场" -> com.citysimulator.game.data.model.SimplifiedBuildingType.PLAZA
                        "喷泉" -> com.citysimulator.game.data.model.SimplifiedBuildingType.FOUNTAIN
                        else -> mapOldToSimplifiedBuildingType(building.type) // 如果没有customName，使用旧的映射方法
                    }
                    buildingCount[simplifiedType] = (buildingCount[simplifiedType] ?: 0) + 1
                }
                
                // 计算月收入
                val monthlyIncome = buildings.sumOf { building -> building.income }
                
                // 更新任务进度
                taskViewModel.updateTasksBasedOnCityState(
                    population = currentPopulation,
                    buildingCount = buildingCount,
                    monthlyIncome = monthlyIncome,
                    goldAmount = goldAmount
                )
                
                println("📋 更新任务进度 - 人口: $currentPopulation, 建筑: ${buildingCount.size}种, 月收入: $monthlyIncome, 金币: $goldAmount")
            }
        }
    
    // uiState 已在上面从 ViewModel 加载，这里不需要重复声明
    // 使用游戏时间而不是真实时间
    val gameDate by gameTimeViewModel.gameDate.collectAsStateWithLifecycle()
    val weatherType = remember { WeatherType.SUNNY }
    
    // 使用 DisposableEffect 确保主题变化时强制重组
    DisposableEffect(currentTheme.themeType) {
        println("🎨 [MainGameScreen] DisposableEffect 触发，主题: ${currentTheme.themeName}")
        onDispose { }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // 直接在这里计算，每次重组都会重新计算
                com.citysimulator.game.ui.theme.getThemeBackgroundBrush(currentTheme)
            )
    ) {
        // 顶部状态栏
        TopStatusBar(
            city = uiState.currentCity,
            currentTime = gameDate,
            weatherType = weatherType,
            goldAmount = goldAmount,
            monthlyIncome = monthlyIncome,
            currentPopulation = currentPopulation,
            populationCapacity = populationGrowthResult?.populationCapacity ?: 0,
            growthRate = populationGrowthResult?.growthRate ?: 0.0,
            prosperityScore = cityProsperity?.overallProsperity?.toInt() ?: 0,
            themeColors = currentTheme, // 传入主题颜色
            onSettingsClick = onNavigateToSettings, // 设置按钮点击
            modifier = Modifier.fillMaxWidth()
        )
        
        // 区域名称显示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = currentDistrict.getThemeColor().copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDistrict.icon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = currentDistrict.displayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentDistrict.getThemeColor()
                    )
                    Text(
                        text = currentDistrict.description,
                        fontSize = 12.sp,
                        color = currentTheme.textSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${buildings.size} 座建筑",
                    fontSize = 12.sp,
                    color = currentTheme.textSecondary
                )
            }
        }
        
        // 公用事业状态栏（更紧凑的设计）
        if (utilityStatus != null && utilityImpact != null) {
            androidx.compose.material3.Card(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp) // 减少垂直间距
                    .clickable { showUtilityPanel = !showUtilityPanel },
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = currentTheme.cardBackground
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.padding(8.dp) // 减少内边距
                ) {
                    // 标题行（更紧凑）
                    androidx.compose.foundation.layout.Row(
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = "🏗️ 城市资源",
                            fontSize = 12.sp, // 减小字体
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = currentTheme.textPrimary
                        )
                        androidx.compose.material3.Text(
                            text = if (showUtilityPanel) "▲" else "▼",
                            color = currentTheme.textSecondary,
                            fontSize = 14.sp // 减小箭头大小
                        )
                    }
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                    
                    // 资源状态（始终显示）
                    androidx.compose.foundation.layout.Row(
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {
                        // 电力
                        UtilityStatusItem(
                            icon = "⚡",
                            label = "电力",
                            current = utilityStatus!!.electricitySupply,
                            demand = utilityStatus!!.electricityDemand,
                            ratio = utilityStatus!!.electricityRatio,
                            themeColors = currentTheme
                        )
                        
                        // 水源
                        UtilityStatusItem(
                            icon = "💧",
                            label = "水源",
                            current = utilityStatus!!.waterSupply,
                            demand = utilityStatus!!.waterDemand,
                            ratio = utilityStatus!!.waterRatio,
                            themeColors = currentTheme
                        )
                        
                        // 垃圾
                        UtilityStatusItem(
                            icon = "🗑️",
                            label = "垃圾",
                            current = utilityStatus!!.wasteCapacity,
                            demand = utilityStatus!!.wasteProduction,
                            ratio = utilityStatus!!.wasteRatio,
                            themeColors = currentTheme
                        )
                    }
                    
                    // 详细信息（展开时显示）
                    if (showUtilityPanel) {
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Divider(color = currentTheme.divider)
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        
                        // 建筑效率
                        androidx.compose.material3.Text(
                            text = "🏗️ 建筑效率: ${(utilityImpact!!.buildingEfficiency * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = when {
                                utilityImpact!!.buildingEfficiency >= 1.0f -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                utilityImpact!!.buildingEfficiency >= 0.8f -> currentTheme.textPrimary
                                else -> androidx.compose.ui.graphics.Color(0xFFF44336)
                            }
                        )
                        
                        // 警告信息
                        if (utilityImpact!!.warnings.isNotEmpty()) {
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                            utilityImpact!!.warnings.forEach { warning ->
                                androidx.compose.material3.Text(
                                    text = warning,
                                    fontSize = 11.sp,
                                    color = androidx.compose.ui.graphics.Color(0xFFFF9800)
                                )
                            }
                        }
                        
                        // 建议
                        val suggestions = com.citysimulator.game.ai.UtilityManagementSystem.getSuggestions(utilityStatus!!)
                        if (suggestions.isNotEmpty()) {
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                            suggestions.forEach { suggestion ->
                                androidx.compose.material3.Text(
                                    text = suggestion,
                                    fontSize = 11.sp,
                                    color = androidx.compose.ui.graphics.Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 放置模式提示
        if (isPlacementMode && selectedSimplifiedBuilding != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF9C4)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSimplifiedBuilding!!.getEmoji(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Column {
                            Text(
                                text = "放置模式: ${selectedSimplifiedBuilding!!.getDisplayName()}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFFF57F17)
                            )
                            Text(
                                text = "点击空白网格放置建筑",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    TextButton(
                        onClick = { placementViewModel.cancelPlacement() }
                    ) {
                        Text("取消", color = Color(0xFFF57F17))
                    }
                }
            }
        }
        
        // 主游戏区域 - 毛玻璃效果（移除上下间距）
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.12f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CityGrid(
                    buildings = buildings,
                    selectedBuildingType = selectedBuildingType,
                    selectedSimplifiedBuilding = selectedSimplifiedBuilding,
                    isPlacementMode = isPlacementMode,
                    onBuildingClick = { building ->
                        // 点击建筑时显示拆除选项
                        buildingToDelete = building
                        showDeleteDialog = true
                    },
                    onEmptyGridClick = { x, y ->
                        // 防抖：避免快速连续点击导致崩溃
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastBuildTime < 500) { // 500ms 防抖
                            println("⚠️ 点击过快，忽略本次点击")
                            return@CityGrid
                        }
                        
                        // 点击空网格放置建筑
                        println("点击空网格: ($x, $y), 选择的建筑类型: $selectedBuildingType, 简化建筑: $selectedSimplifiedBuilding")
                        
                        // 优先处理简化建筑系统
                        val simplifiedType = selectedSimplifiedBuilding
                        if (simplifiedType != null) {
                            val buildCost = simplifiedType.getBuildCost()
                            
                            println("🏗️ 建造简化建筑: ${simplifiedType.getDisplayName()} 在 ($x, $y), 成本: $buildCost")
                            
                            // 检查金币是否足够
                            if (goldAmount >= buildCost) {
                                // 更新最后建造时间
                                lastBuildTime = currentTime
                                goldAmount -= buildCost
                                // 创建建筑实例 (转换为旧Building系统以兼容现有代码)
                                val mappedBuildingType = mapSimplifiedToOldBuildingType(simplifiedType)
                                val newBuilding = com.citysimulator.game.data.model.Building(
                                    id = java.util.UUID.randomUUID().toString(),
                                    type = mappedBuildingType,
                                    level = 1,
                                    position = com.citysimulator.game.data.model.BuildingPosition(x, y),
                                    buildTime = Date(),
                                    status = com.citysimulator.game.data.model.BuildingStatus.NORMAL,
                                    isUnderConstruction = true,
                                    isUpgrading = false,
                                    isMaintenanceRequired = false,
                                    lastMaintenanceDate = Date(),
                                    efficiency = 1.0f,
                                    capacity = 10, // 默认容量
                                    income = simplifiedType.getMonthlyIncome(),
                                    maintenanceCost = if (simplifiedType.getMonthlyIncome() < 0) -simplifiedType.getMonthlyIncome() else 1,
                                    customName = simplifiedType.getDisplayName() // 保存简化建筑的显示名称
                                )
                                
                                
                                // 添加到建筑列表
                                buildings = buildings + newBuilding
                                
                                // 保存到本地数据库（让AI能看到新建筑）
                                viewModel.addBuilding(newBuilding)
                                
                                // 保存到Supabase
                                supabaseViewModel.saveBuilding(newBuilding)
                                
                                // 如果是住宅，自动生成市民并同步人口
                                if (mappedBuildingType == com.citysimulator.game.data.model.BuildingType.HOUSE ||
                                    mappedBuildingType == com.citysimulator.game.data.model.BuildingType.APARTMENT ||
                                    mappedBuildingType == com.citysimulator.game.data.model.BuildingType.VILLA ||
                                    mappedBuildingType == com.citysimulator.game.data.model.BuildingType.SKYSCRAPER) {
                                    citizenViewModel.addCitizensForBuilding(newBuilding) { totalCitizens ->
                                        // 同步更新人口数量
                                        populationViewModel.setPopulation(totalCitizens)
                                        println("✅ 人口已同步: $totalCitizens")
                                    }
                                }
                                
                                println("✅ 建造成功！剩余金币: $goldAmount")
                                
                                // 完成放置
                                placementViewModel.completePlacement()
                            } else {
                                println("❌ 金币不足！需要: $buildCost, 当前: $goldAmount")
                                // TODO: 显示金币不足提示
                                placementViewModel.completePlacement()
                            }
                        } else if (selectedBuildingType != null) {
                            // 旧建筑系统的确认对话框
                            pendingBuildingPosition = Pair(x, y)
                            showConfirmDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // 市民显示层（可交互）- 改进的视觉效果 + 平滑移动动画
                if (citizens.isNotEmpty()) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val gridColumns = 20
                        val gridRows = 30
                        val cellWidth = maxWidth / gridColumns
                        val cellHeight = maxHeight / gridRows
                        
                        // 绘制可点击的市民（使用改进的SimpleCitizenMarker组件 + 动画）
                        citizens.forEach { citizen ->
                            // 使用key确保Compose正确追踪每个市民
                            key(citizen.id) {
                                // 计算目标位置
                                val targetX = cellWidth * citizen.currentX + cellWidth / 2 - 8.dp
                                val targetY = cellHeight * citizen.currentY + cellHeight / 2 - 8.dp
                                
                                // 平滑移动动画
                                val animatedX by androidx.compose.animation.core.animateDpAsState(
                                    targetValue = targetX,
                                    animationSpec = androidx.compose.animation.core.spring(
                                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                    ),
                                    label = "citizen_x_${citizen.id}"
                                )
                                val animatedY by androidx.compose.animation.core.animateDpAsState(
                                    targetValue = targetY,
                                    animationSpec = androidx.compose.animation.core.spring(
                                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                    ),
                                    label = "citizen_y_${citizen.id}"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .offset(x = animatedX, y = animatedY)
                                        .clickable {
                                            // 点击市民显示详情
                                            selectedCitizen = citizen
                                            showCitizenDialog = true
                                        }
                                ) {
                                    // 使用带情绪的市民标记
                                    EmotionalCitizenMarker(
                                        citizen = citizen,
                                        showEmotionBubble = true  // 显示情绪气泡
                                    )
                                }
                            }
                        }
                    }
                }

                // 天气特效
                when (weatherType) {
                    WeatherType.RAINY, WeatherType.STORMY -> {
                        EnhancedRainEffect(modifier = Modifier.fillMaxSize())
                    }
                    WeatherType.SNOWY -> {
                        EnhancedSnowEffect(modifier = Modifier.fillMaxSize())
                    }
                    else -> {}
                }
                
                // 夜晚星星
                val cal = Calendar.getInstance().apply { time = gameDate }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                StarsEffect(
                    isNight = hour !in 6..18,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        // 现代化底部控制栏（移除上方间距）
        ModernBottomBar(
            currentDistrict = currentDistrict,
            onDistrictChange = { district ->
                viewModel.switchDistrict(district)
            },
            onBuildingClick = onNavigateToBuildingMenu,
            onTaskClick = onNavigateToTaskPanel,
            onCitizenClick = onNavigateToCitizenFeedback,
            onPolicyClick = onNavigateToCityPolicy,
            onEconomyClick = onNavigateToEconomy,
            onCitizenListClick = onNavigateToCitizenList,
            modifier = Modifier.fillMaxWidth()
        )
    }
        
        // 建筑拆除确认对话框
        if (showDeleteDialog && buildingToDelete != null) {
            val building = buildingToDelete!!
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    buildingToDelete = null
                },
                title = {
                    Text("拆除建筑")
                },
                text = {
                    Text("确定要拆除 ${building.getDisplayName()} 吗？\n\n拆除后将获得部分金币返还。")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // 确认拆除
                            val refundAmount = when (building.type) {
                                com.citysimulator.game.data.model.BuildingType.HOUSE -> 50
                                com.citysimulator.game.data.model.BuildingType.APARTMENT -> 100
                                com.citysimulator.game.data.model.BuildingType.VILLA -> 250
                                com.citysimulator.game.data.model.BuildingType.SKYSCRAPER -> 500
                                com.citysimulator.game.data.model.BuildingType.SHOP -> 150
                                com.citysimulator.game.data.model.BuildingType.SUPERMARKET -> 400
                                com.citysimulator.game.data.model.BuildingType.MALL -> 200
                                com.citysimulator.game.data.model.BuildingType.RESTAURANT -> 300
                                com.citysimulator.game.data.model.BuildingType.HOTEL -> 600
                                else -> 50
                            }
                            
                            // 返还金币
                            goldAmount += refundAmount
                            
                            // 移除建筑相关的市民并更新人口数量
                            println("🏚️ 准备拆除建筑: ${building.getDisplayName()} at (${building.position.x}, ${building.position.y})")
                            println("📊 拆除前人口: $currentPopulation, 市民: ${citizens.size}")
                            
                            citizenViewModel.removeCitizensForBuilding(building) { totalCitizens ->
                                println("🔄 回调收到新市民数量: $totalCitizens")
                                populationViewModel.setPopulation(totalCitizens)
                                println("✅ 拆除建筑后人口已更新: $totalCitizens")
                            }
                            
                            // 从建筑列表中移除
                            buildings = buildings.filter { it.id != building.id }
                            
                            // 从本地数据库删除（让AI能知道建筑被拆除）
                            viewModel.deleteBuilding(building.id)
                            
                            // 从Supabase删除
                            supabaseViewModel.deleteBuilding(building.id)
                            
                            println("建筑拆除成功: ${building.type}，返还金币: $refundAmount")
                            
                            // 清除状态
                            showDeleteDialog = false
                            buildingToDelete = null
                        }
                    ) {
                        Text("确认拆除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            buildingToDelete = null
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
        
        // 建筑建造确认对话框
        if (showConfirmDialog && selectedBuildingType != null && pendingBuildingPosition != null) {
            val (x, y) = pendingBuildingPosition!!
            AlertDialog(
                onDismissRequest = { 
                    showConfirmDialog = false
                    pendingBuildingPosition = null
                },
                title = {
                    Text("确认建造")
                },
                text = {
                    Text("确定要在位置 ($x, $y) 建造 ${selectedBuildingType!!.getDisplayName()} 吗？")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // 确认建造
                            val newBuilding = com.citysimulator.game.data.model.Building(
                                id = java.util.UUID.randomUUID().toString(),
                                type = selectedBuildingType!!,
                                level = 1,
                                position = com.citysimulator.game.data.model.BuildingPosition(x, y),
                                buildTime = Date(),
                                status = com.citysimulator.game.data.model.BuildingStatus.NORMAL,
                                isUnderConstruction = true,
                                isUpgrading = false,
                                isMaintenanceRequired = false,
                                lastMaintenanceDate = Date(),
                                efficiency = 1.0f,
                                capacity = when (selectedBuildingType) {
                                    com.citysimulator.game.data.model.BuildingType.HOUSE -> 4
                                    com.citysimulator.game.data.model.BuildingType.SHOP -> 2
                                    else -> 1
                                },
                                income = when (selectedBuildingType) {
                                    com.citysimulator.game.data.model.BuildingType.HOUSE -> 10
                                    com.citysimulator.game.data.model.BuildingType.SHOP -> 20
                                    else -> 5
                                },
                                maintenanceCost = when (selectedBuildingType) {
                                    com.citysimulator.game.data.model.BuildingType.HOUSE -> 2
                                    com.citysimulator.game.data.model.BuildingType.SHOP -> 3
                                    else -> 1
                                }
                            )
                            println("确认建造: ${newBuilding.type} 在位置 ($x, $y)")
                            
                            // 计算建筑成本
                            val buildingCost = when (selectedBuildingType) {
                                com.citysimulator.game.data.model.BuildingType.HOUSE -> 100
                                com.citysimulator.game.data.model.BuildingType.APARTMENT -> 200
                                com.citysimulator.game.data.model.BuildingType.VILLA -> 500
                                com.citysimulator.game.data.model.BuildingType.SKYSCRAPER -> 1000
                                com.citysimulator.game.data.model.BuildingType.SHOP -> 300
                                com.citysimulator.game.data.model.BuildingType.SUPERMARKET -> 800
                                com.citysimulator.game.data.model.BuildingType.MALL -> 400
                                com.citysimulator.game.data.model.BuildingType.RESTAURANT -> 600
                                com.citysimulator.game.data.model.BuildingType.HOTEL -> 1200
                                else -> 100
                            }
                            
                            // 检查金币是否足够
                            if (goldAmount >= buildingCost) {
                                goldAmount -= buildingCost
                                buildings = buildings + newBuilding
                                
                                // 保存到本地数据库（让AI能看到新建筑）
                                viewModel.addBuilding(newBuilding)
                                
                                // 保存到Supabase
                                supabaseViewModel.saveBuilding(newBuilding)
                                
                                println("建筑列表更新，当前建筑数量: ${buildings.size}")
                                println("扣除金币: $buildingCost，剩余金币: $goldAmount")
                                
                                // 建造成功后清除所有状态
                                showConfirmDialog = false
                                pendingBuildingPosition = null
                                selectedBuildingType = null
                                BuildingSelectionState.clearSelection()
                            } else {
                                println("金币不足！需要: $buildingCost，当前: $goldAmount")
                                
                                // 金币不足时也清除对话框状态
                                showConfirmDialog = false
                                pendingBuildingPosition = null
                            }
                        }
                    ) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            pendingBuildingPosition = null
                            // 取消时也清除建筑选择状态
                            selectedBuildingType = null
                            BuildingSelectionState.clearSelection()
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
        
        // 任务完成对话框
        if (showTaskCompletionDialog && completedTaskForDialog != null) {
            val task = completedTaskForDialog!!
            AlertDialog(
                onDismissRequest = {
                    showTaskCompletionDialog = false
                    taskViewModel.clearCompletedTaskNotification()
                },
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = "🎉",
                            fontSize = 32.sp
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                        androidx.compose.material3.Text(
                            text = "任务完成！",
                            fontSize = 24.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                },
                text = {
                    androidx.compose.foundation.layout.Column {
                        androidx.compose.material3.Text(
                            text = task.title,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        androidx.compose.material3.Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                        androidx.compose.material3.Text(
                            text = "🎁 获得奖励：",
                            fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                        task.rewards.forEach { reward ->
                            androidx.compose.material3.Card(
                                modifier = androidx.compose.ui.Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = androidx.compose.material3.CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color(0xFFFFF3E0)
                                )
                            ) {
                                androidx.compose.foundation.layout.Row(
                                    modifier = androidx.compose.ui.Modifier.padding(12.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    androidx.compose.material3.Text(
                                        text = when (reward.type) {
                                            com.citysimulator.game.data.model.RewardType.GOLD -> "💰"
                                            com.citysimulator.game.data.model.RewardType.POPULATION -> "👥"
                                            com.citysimulator.game.data.model.RewardType.REPUTATION -> "⭐"
                                            else -> "🎁"
                                        },
                                        fontSize = 24.sp
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                                    androidx.compose.material3.Text(
                                        text = when (reward.type) {
                                            com.citysimulator.game.data.model.RewardType.GOLD -> "+${reward.amount} 金币"
                                            com.citysimulator.game.data.model.RewardType.POPULATION -> "+${reward.amount} 人口"
                                            com.citysimulator.game.data.model.RewardType.REPUTATION -> "+${reward.amount} 满意度"
                                            com.citysimulator.game.data.model.RewardType.BUILDING_UNLOCK -> "解锁：${reward.description}"
                                            else -> reward.description
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = {
                            // 发放奖励
                            task.rewards.forEach { reward ->
                                when (reward.type) {
                                    com.citysimulator.game.data.model.RewardType.GOLD -> {
                                        goldAmount += reward.amount
                                        println("💰 获得金币: ${reward.amount}")
                                    }
                                    com.citysimulator.game.data.model.RewardType.POPULATION -> {
                                        println("👥 获得人口: ${reward.amount}")
                                    }
                                    com.citysimulator.game.data.model.RewardType.REPUTATION -> {
                                        println("⭐ 获得满意度: ${reward.amount}")
                                    }
                                    else -> {}
                                }
                            }
                            showTaskCompletionDialog = false
                            taskViewModel.clearCompletedTaskNotification()
                        }
                    ) {
                        androidx.compose.material3.Text("领取奖励")
                    }
                }
            )
        }
        
        // 市民详情对话框（简化版，点击AI对话打开完整对话页面）
        if (showCitizenDialog && selectedCitizen != null) {
            val citizen = selectedCitizen!!
            
            AlertDialog(
                onDismissRequest = {
                    showCitizenDialog = false
                    selectedCitizen = null
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (citizen.gender == Gender.MALE) "👨" else "👩",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = citizen.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = citizen.occupation ?: "无业",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        // 基本信息
                        CitizenInfoRow(label = "年龄", value = "${citizen.age}岁")
                        CitizenInfoRow(label = "职业", value = citizen.occupation ?: "无业")
                        if (citizen.salary > 0) {
                            CitizenInfoRow(label = "月薪", value = "💰 ${citizen.salary}")
                        }
                        CitizenInfoRow(label = "教育", value = citizen.education.getDisplayName())
                        CitizenInfoRow(label = "婚姻", value = citizen.maritalStatus.getDisplayName())
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 状态信息
                        Text(
                            text = "状态",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 幸福度
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("😊 幸福度", modifier = Modifier.width(100.dp))
                            LinearProgressIndicator(
                                progress = citizen.happiness,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = when {
                                    citizen.happiness > 0.7f -> Color(0xFF4CAF50)
                                    citizen.happiness > 0.4f -> Color(0xFFFFC107)
                                    else -> Color(0xFFF44336)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${(citizen.happiness * 100).toInt()}%",
                                fontSize = 12.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 健康度
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💪 健康度", modifier = Modifier.width(100.dp))
                            LinearProgressIndicator(
                                progress = citizen.health,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = when {
                                    citizen.health > 0.7f -> Color(0xFF4CAF50)
                                    citizen.health > 0.4f -> Color(0xFFFFC107)
                                    else -> Color(0xFFF44336)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${(citizen.health * 100).toInt()}%",
                                fontSize = 12.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 当前活动
                        CitizenInfoRow(
                            label = "活动",
                            value = citizen.currentActivity.getDisplayName()
                        )
                        
                        // 位置
                        CitizenInfoRow(
                            label = "位置",
                            value = "(${citizen.currentX}, ${citizen.currentY})"
                        )
                    }
                },
                confirmButton = {
                    Row {
                        // AI对话按钮
                        Button(
                            onClick = {
                                showCitizenDialog = false
                                onNavigateToCitizenAIChat?.invoke(citizen.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Text("💬 AI对话")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // 关闭按钮
                        OutlinedButton(
                            onClick = {
                                showCitizenDialog = false
                                selectedCitizen = null
                            }
                        ) {
                            Text("关闭")
                        }
                    }
                },
                dismissButton = null
            )
        }
    }

/**
 * 市民信息行组件
 */
@Composable
private fun CitizenInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 城市网格组件
 * 
 * 显示城市建筑网格，支持建筑点击和空网格点击。
 * 
 * @param buildings 建筑列表
 * @param onBuildingClick 建筑点击回调
 * @param onEmptyGridClick 空网格点击回调
 * @param modifier 修饰符
 */
@Composable
private fun CityGrid(
    buildings: List<com.citysimulator.game.data.model.Building>,
    selectedBuildingType: com.citysimulator.game.data.model.BuildingType?,
    selectedSimplifiedBuilding: com.citysimulator.game.data.model.SimplifiedBuildingType?,
    isPlacementMode: Boolean,
    onBuildingClick: (com.citysimulator.game.data.model.Building) -> Unit,
    onEmptyGridClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridColumns = 20 // 20列
    val gridRows = 30 // 30行（增加到30行以填满屏幕）
    
    // 优化：预先创建建筑位置索引，避免每次查找
    val buildingMap = remember(buildings) {
        buildings.associateBy { "${it.position.x},${it.position.y}" }
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(0.5.dp),
        verticalArrangement = Arrangement.spacedBy(0.5.dp),
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = false // 禁用滚动，让市民和地图同步
    ) {
        items(gridColumns * gridRows, key = { it }) { index ->
            val x = index % gridColumns
            val y = index / gridColumns
            
            // 优化：使用 Map 快速查找，而不是 find
            val building = buildingMap["$x,$y"]
            
            // 检查是否是街道位置
            val isStreet = isStreetPosition(x, y)
            
            if (building != null) {
                EnhancedBuildingComponent(
                    building = building,
                    isBeingBuilt = building.isUnderConstruction,
                    onClick = { onBuildingClick(building) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            } else if (isStreet) {
                StreetCell(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            } else {
                EnhancedEmptyGridCell(
                    isSelected = isPlacementMode && selectedSimplifiedBuilding != null,
                    canBuild = isPlacementMode || selectedBuildingType != null,
                    onClick = { onEmptyGridClick(x, y) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
    }
}

/**
 * 检查指定位置是否是街道
 * 
 * @param x X坐标
 * @param y Y坐标
 * @return 是否是街道位置
 */
private fun isStreetPosition(x: Int, y: Int): Boolean {
    // 创建街道模式：每隔3-4格有一条街道
    val streetPattern = 4
    
    // 水平街道（每隔4行）
    if (y % streetPattern == 0) return true
    
    // 垂直街道（每隔4列）
    if (x % streetPattern == 0) return true
    
    // 主要街道（更宽的主干道）
    val mainStreetPattern = 8
    if (y % mainStreetPattern == 0 || x % mainStreetPattern == 0) return true
    
    return false
}

/**
 * 街道单元格
 * 
 * 显示街道，不可建造建筑。
 * 
 * @param modifier 修饰符
 */
@Composable
private fun StreetCell(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CityGray.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 街道线条效果
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                CityGray.copy(alpha = 0.3f),
                                CityGray.copy(alpha = 0.7f),
                                CityGray.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * 空网格单元格
 * 
 * 显示空的网格单元格，支持点击建造建筑。
 * 
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyGridCell(
    onClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                CityBlue.copy(alpha = 0.3f)
            } else {
                CitySurfaceVariant.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                // 显示选中状态的指示器
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "可放置建筑",
                    tint = CityBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * 将简化建筑类型映射到旧建筑类型
 * 用于兼容现有的Building系统
 */
private fun mapSimplifiedToOldBuildingType(
    simplifiedType: com.citysimulator.game.data.model.SimplifiedBuildingType
): com.citysimulator.game.data.model.BuildingType {
    return when (simplifiedType) {
        // 住宅区
        com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_HOUSE -> 
            com.citysimulator.game.data.model.BuildingType.HOUSE
        com.citysimulator.game.data.model.SimplifiedBuildingType.APARTMENT -> 
            com.citysimulator.game.data.model.BuildingType.APARTMENT
        com.citysimulator.game.data.model.SimplifiedBuildingType.MODERN_RESIDENCE -> 
            com.citysimulator.game.data.model.BuildingType.VILLA
        
        // 经济建筑
        com.citysimulator.game.data.model.SimplifiedBuildingType.LEMONADE_STAND,
        com.citysimulator.game.data.model.SimplifiedBuildingType.CONVENIENCE_STORE -> 
            com.citysimulator.game.data.model.BuildingType.SHOP
        com.citysimulator.game.data.model.SimplifiedBuildingType.SHOPPING_MALL -> 
            com.citysimulator.game.data.model.BuildingType.MALL
        com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_FARM -> 
            com.citysimulator.game.data.model.BuildingType.FARM
        com.citysimulator.game.data.model.SimplifiedBuildingType.FOOD_FACTORY -> 
            com.citysimulator.game.data.model.BuildingType.FACTORY
        com.citysimulator.game.data.model.SimplifiedBuildingType.TECH_PARK -> 
            com.citysimulator.game.data.model.BuildingType.OFFICE
        
        // 公共服务 - 电力
        com.citysimulator.game.data.model.SimplifiedBuildingType.WINDMILL,
        com.citysimulator.game.data.model.SimplifiedBuildingType.COAL_PLANT,
        com.citysimulator.game.data.model.SimplifiedBuildingType.SOLAR_PLANT -> 
            com.citysimulator.game.data.model.BuildingType.POWER_PLANT
        
        // 公共服务 - 水源
        com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_WELL,
        com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_PUMP,
        com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_PURIFIER -> 
            com.citysimulator.game.data.model.BuildingType.WATER_TOWER
        
        // 公共服务 - 垃圾处理
        com.citysimulator.game.data.model.SimplifiedBuildingType.GARBAGE_DUMP,
        com.citysimulator.game.data.model.SimplifiedBuildingType.RECYCLING_CENTER,
        com.citysimulator.game.data.model.SimplifiedBuildingType.ECO_FACILITY -> 
            com.citysimulator.game.data.model.BuildingType.WASTE_MANAGEMENT
        
        // 公共服务 - 安全与健康
        com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_CLINIC,
        com.citysimulator.game.data.model.SimplifiedBuildingType.POLICE_STATION -> 
            com.citysimulator.game.data.model.BuildingType.POLICE_STATION
        com.citysimulator.game.data.model.SimplifiedBuildingType.HOSPITAL -> 
            com.citysimulator.game.data.model.BuildingType.HOSPITAL
        
        // 公共服务 - 公园与装饰
        com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_PARK,
        com.citysimulator.game.data.model.SimplifiedBuildingType.PLAZA,
        com.citysimulator.game.data.model.SimplifiedBuildingType.FOUNTAIN -> 
            com.citysimulator.game.data.model.BuildingType.PARK
        
        // 装饰与道路
        com.citysimulator.game.data.model.SimplifiedBuildingType.DIRT_ROAD,
        com.citysimulator.game.data.model.SimplifiedBuildingType.PAVED_ROAD,
        com.citysimulator.game.data.model.SimplifiedBuildingType.TREE_LINED_ROAD -> 
            com.citysimulator.game.data.model.BuildingType.ROAD
    }
}

/**
 * 将旧的BuildingType映射到SimplifiedBuildingType
 * 用于任务系统统计建筑数量
 */
private fun mapOldToSimplifiedBuildingType(
    oldType: com.citysimulator.game.data.model.BuildingType
): com.citysimulator.game.data.model.SimplifiedBuildingType {
    return when (oldType) {
        // 住宅类
        com.citysimulator.game.data.model.BuildingType.HOUSE -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_HOUSE
        com.citysimulator.game.data.model.BuildingType.APARTMENT -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.APARTMENT
        com.citysimulator.game.data.model.BuildingType.VILLA -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.MODERN_RESIDENCE
        
        // 经济类
        com.citysimulator.game.data.model.BuildingType.SHOP -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.CONVENIENCE_STORE
        com.citysimulator.game.data.model.BuildingType.MALL -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.SHOPPING_MALL
        com.citysimulator.game.data.model.BuildingType.FARM -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_FARM
        com.citysimulator.game.data.model.BuildingType.FACTORY -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.FOOD_FACTORY
        com.citysimulator.game.data.model.BuildingType.OFFICE -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.TECH_PARK
        com.citysimulator.game.data.model.BuildingType.BANK -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.TECH_PARK
        
        // 公共服务
        com.citysimulator.game.data.model.BuildingType.POWER_PLANT -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.COAL_PLANT
        com.citysimulator.game.data.model.BuildingType.WATER_TOWER,
        com.citysimulator.game.data.model.BuildingType.WATER_TREATMENT_PLANT -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.WATER_PUMP
        com.citysimulator.game.data.model.BuildingType.WASTE_MANAGEMENT -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.RECYCLING_CENTER
        com.citysimulator.game.data.model.BuildingType.POLICE_STATION -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.POLICE_STATION
        com.citysimulator.game.data.model.BuildingType.FIRE_STATION -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.POLICE_STATION
        com.citysimulator.game.data.model.BuildingType.HOSPITAL -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.HOSPITAL
        com.citysimulator.game.data.model.BuildingType.SCHOOL -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_CLINIC
        com.citysimulator.game.data.model.BuildingType.PARK -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_PARK
        com.citysimulator.game.data.model.BuildingType.STADIUM -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.PLAZA
        
        // 基础设施
        com.citysimulator.game.data.model.BuildingType.ROAD -> 
            com.citysimulator.game.data.model.SimplifiedBuildingType.PAVED_ROAD
        
        // 其他未映射的类型，使用默认值
        else -> com.citysimulator.game.data.model.SimplifiedBuildingType.SMALL_HOUSE
    }
}


/**
 * 公用事业状态显示项
 */
@Composable
private fun UtilityStatusItem(
    icon: String,
    label: String,
    current: Int,
    demand: Int,
    ratio: Float,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // 图标（减小）
        androidx.compose.material3.Text(
            text = icon,
            fontSize = 16.sp
        )
        
        // 标签（减小）
        androidx.compose.material3.Text(
            text = label,
            fontSize = 9.sp,
            color = themeColors.textSecondary
        )
        
        // 百分比（更突出，只显示最重要的信息）
        androidx.compose.material3.Text(
            text = "${(ratio * 100).toInt()}%",
            fontSize = 11.sp,
            color = when {
                ratio >= 1.0f -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // 绿色：充足
                ratio >= 0.8f -> themeColors.textPrimary                        // 正常
                ratio >= 0.5f -> androidx.compose.ui.graphics.Color(0xFFFF9800) // 橙色：不足
                else -> androidx.compose.ui.graphics.Color(0xFFF44336)          // 红色：严重不足
            },
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

