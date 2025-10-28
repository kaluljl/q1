package com.citysimulator.game.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.content.Context
import com.citysimulator.game.ui.screen.MainGameScreen
import com.citysimulator.game.ui.screen.BuildingMenuScreen
import com.citysimulator.game.ui.screen.ResourcePanelScreen
import com.citysimulator.game.ui.screen.SupabaseConfigScreen
import com.citysimulator.game.ui.screen.TechTreeScreen
import com.citysimulator.game.ui.screen.CityPolicyScreen
import com.citysimulator.game.ui.screen.CityEventScreen
import com.citysimulator.game.ui.screen.CitizenFeedbackScreen
// import com.citysimulator.game.ui.screen.EconomyPanelScreen // 暂时注释
import com.citysimulator.game.ui.screen.CitizenListScreen
import com.citysimulator.game.ui.screen.CitizenDetailScreen
import com.citysimulator.game.ui.screen.CitizenAIChatScreen
import com.citysimulator.game.ui.screen.SimplifiedBuildingMenuScreen
import com.citysimulator.game.ui.screen.TaskListScreen
import com.citysimulator.game.ui.viewmodel.BuildingPlacementViewModel
import com.citysimulator.game.ui.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Date

/**
 * 城市模拟器导航路由
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
object CitySimulatorRoutes {
    const val MAIN_GAME = "main_game"
    const val BUILDING_MENU = "building_menu"
    const val SIMPLIFIED_BUILDING_MENU = "simplified_building_menu"
    const val RESOURCE_PANEL = "resource_panel"
    const val TASK_PANEL = "task_panel"
    const val ACHIEVEMENT_PANEL = "achievement_panel"
    const val SETTINGS = "settings"
    const val SUPABASE_CONFIG = "supabase_config"
    const val TECH_TREE = "tech_tree"
    const val CITY_POLICY = "city_policy"
    const val CITY_EVENT = "city_event"
    const val CITIZEN_FEEDBACK = "citizen_feedback"
    const val CITIZEN_LIST = "citizen_list"
    const val CITIZEN_DETAIL = "citizen_detail/{citizenId}"
    const val CITIZEN_AI_CHAT = "citizen_ai_chat/{citizenId}"
    const val ECONOMY_DASHBOARD = "economy_dashboard"
}

/**
 * 城市模拟器导航组件
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Composable
fun CitySimulatorNavigation(
    navController: NavHostController = rememberNavController(),
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = CitySimulatorRoutes.MAIN_GAME
    ) {
        // 主游戏界面
        composable(CitySimulatorRoutes.MAIN_GAME) { backStackEntry ->
            val placementViewModel: BuildingPlacementViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            val taskViewModel: TaskViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            val citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            
            MainGameScreen(
                placementViewModel = placementViewModel,
                taskViewModel = taskViewModel,
                onNavigateToBuildingMenu = {
                    navController.navigate(CitySimulatorRoutes.BUILDING_MENU)
                },
                onNavigateToResourcePanel = {
                    navController.navigate(CitySimulatorRoutes.RESOURCE_PANEL)
                },
                onNavigateToTaskPanel = {
                    navController.navigate(CitySimulatorRoutes.TASK_PANEL)
                },
                onNavigateToAchievementPanel = { },
                onNavigateToSettings = { },
                onNavigateToSupabaseConfig = {
                    navController.navigate(CitySimulatorRoutes.SUPABASE_CONFIG)
                },
                onNavigateToTechTree = {
                    navController.navigate(CitySimulatorRoutes.TECH_TREE)
                },
                onNavigateToCityPolicy = {
                    navController.navigate(CitySimulatorRoutes.CITY_POLICY)
                },
                onNavigateToCityEvent = {
                    navController.navigate(CitySimulatorRoutes.CITY_EVENT)
                },
                onNavigateToCitizenFeedback = {
                    navController.navigate(CitySimulatorRoutes.CITIZEN_FEEDBACK)
                },
                onNavigateToEconomy = null,
                onNavigateToCitizenList = {
                    navController.navigate(CitySimulatorRoutes.CITIZEN_LIST)
                },
                onNavigateToCitizenAIChat = { citizenId ->
                    navController.navigate("citizen_ai_chat/$citizenId")
                }
            )
        }
        
        // 建筑菜单界面 - 使用简化版
        composable(CitySimulatorRoutes.BUILDING_MENU) { backStackEntry ->
            // 使用与主界面相同的ViewModel实例
            val placementViewModel: BuildingPlacementViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            SimplifiedBuildingMenuScreen(
                currentGold = 1000, // TODO: 从ViewModel获取实际金币
                onBuildingSelected = { buildingType ->
                    // 设置选中的建筑类型，进入放置模式
                    println("🏗️ 选中建筑: ${buildingType.getDisplayName()}")
                    placementViewModel.selectBuilding(buildingType)
                    println("🏗️ ViewModel状态已设置，返回主界面")
                    // 返回主界面进行放置
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 资源面板界面
        composable(CitySimulatorRoutes.RESOURCE_PANEL) {
            ResourcePanelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 任务面板界面
        composable(CitySimulatorRoutes.TASK_PANEL) {
            // 使用与主界面相同的TaskViewModel实例，确保数据一致
            val taskViewModel: TaskViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
            val completedTaskCount by taskViewModel.completedTaskCount.collectAsStateWithLifecycle()
            
            TaskListScreen(
                tasks = tasks,
                completedTaskCount = completedTaskCount,
                onTaskAccept = { taskId -> taskViewModel.acceptTask(taskId) },
                onTaskReject = { taskId -> taskViewModel.rejectTask(taskId) },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 成就面板界面
        composable(CitySimulatorRoutes.ACHIEVEMENT_PANEL) {
            Text("成就面板 - 开发中")
        }
        
        // 设置界面
        composable(CitySimulatorRoutes.SETTINGS) {
            Text("设置 - 开发中")
        }
        
        // Supabase配置界面
        composable(CitySimulatorRoutes.SUPABASE_CONFIG) {
            SupabaseConfigScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 科技树界面
        composable(CitySimulatorRoutes.TECH_TREE) {
            TechTreeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 城市政策界面
        composable(CitySimulatorRoutes.CITY_POLICY) {
            val supabaseViewModel: com.citysimulator.game.ui.viewmodel.SupabaseGameViewModel = hiltViewModel(
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            )
            val goldAmount by supabaseViewModel.goldAmount.collectAsStateWithLifecycle()
            
            CityPolicyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                currentGold = goldAmount,
                onGoldChange = { newGold ->
                    supabaseViewModel.updateGoldAmount(newGold)
                }
            )
        }
        
        // 城市事件界面
        composable(CitySimulatorRoutes.CITY_EVENT) {
            // TODO: 需要CityEvent实例和回调
            Text("城市事件界面 - 开发中\n随机事件系统")
        }
        
        // 市民反馈屏幕
        composable(CitySimulatorRoutes.CITIZEN_FEEDBACK) {
            // 使用 CitizenFeedbackViewModel（带智能限流）
            val feedbackViewModel: com.citysimulator.game.ui.viewmodel.CitizenFeedbackViewModel = hiltViewModel()
            val gameTimeViewModel: com.citysimulator.game.ui.viewmodel.GameTimeViewModel = hiltViewModel()
            val feedbacks by feedbackViewModel.feedbacks.collectAsStateWithLifecycle()
            val isLoading by feedbackViewModel.isLoading.collectAsStateWithLifecycle()
            val gameDate by gameTimeViewModel.gameDate.collectAsStateWithLifecycle()
            
            // 获取主游戏的建筑数据
            val mainGameEntry = remember(navController) {
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            }
            val supabaseViewModel: com.citysimulator.game.ui.viewmodel.SupabaseGameViewModel = hiltViewModel(mainGameEntry)
            val buildings by supabaseViewModel.buildings.collectAsStateWithLifecycle()
            
            // 模拟资源和其他数据（实际应该从游戏状态获取）
            val resources = remember { emptyList<com.citysimulator.game.data.model.Resource>() }
            val goldAmount = remember { 500 }
            val population = remember { 100 }
            
            // 计算游戏年月
            val calendar = remember(gameDate) { 
                java.util.Calendar.getInstance().apply { time = gameDate }
            }
            val gameYear = remember(calendar) { calendar.get(java.util.Calendar.YEAR) }
            val gameMonth = remember(calendar) { calendar.get(java.util.Calendar.MONTH) + 1 }
            
            // 初始化时生成反馈（只在首次加载且为空时）
            LaunchedEffect(Unit) {
                if (feedbacks.isEmpty()) {
                    feedbackViewModel.generateFeedback(
                        buildings = buildings,
                        resources = resources,
                        goldAmount = goldAmount,
                        population = population,
                        gameYear = gameYear,
                        gameMonth = gameMonth
                    )
                }
            }
            
            // 显示反馈（每月自动生成）
            CitizenFeedbackScreen(
                feedbacks = feedbacks,
                isLoading = isLoading,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResolveFeedback = { feedbackId ->
                    feedbackViewModel.resolveFeedback(feedbackId)
                },
                onDeleteFeedback = { feedbackId ->
                    feedbackViewModel.deleteFeedback(feedbackId)
                },
                onRefresh = {
                    // 手动刷新
                    feedbackViewModel.generateFeedback(
                        buildings = buildings,
                        resources = resources,
                        goldAmount = goldAmount,
                        population = population,
                        gameYear = gameYear,
                        gameMonth = gameMonth
                    )
                }
            )
        }
        
        // 经济管理面板
        composable(CitySimulatorRoutes.ECONOMY_DASHBOARD) {
            // 简化版UI
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("💰 财政管理", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("功能开发中...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("将包含：")
                Text("• 税收设置")
                Text("• 贷款管理")
                Text("• 经济指标")
                Text("• 财政报表")
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("返回")
                }
            }
        }
        
        // 市民列表
        composable(CitySimulatorRoutes.CITIZEN_LIST) {
            // 获取共享的 CitizenViewModel
            val mainGameEntry = remember(navController) {
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            }
            val citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel(mainGameEntry)
            val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
            
            CitizenListScreen(
                citizens = citizens,
                onCitizenClick = { citizen ->
                    // 导航到市民详情页面
                    citizenViewModel.selectCitizen(citizen)
                    navController.navigate("citizen_detail/${citizen.id}")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // 市民详情页面
        composable(
            route = CitySimulatorRoutes.CITIZEN_DETAIL,
            arguments = listOf(navArgument("citizenId") { type = NavType.StringType })
        ) { backStackEntry ->
            val citizenId = backStackEntry.arguments?.getString("citizenId") ?: return@composable
            val mainGameEntry = remember(backStackEntry) {
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            }
            val citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel(mainGameEntry)
            val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
            val selectedCitizen = citizens.find { it.id == citizenId }
            
            if (selectedCitizen != null) {
                CitizenDetailScreen(
                    citizen = selectedCitizen,
                    thoughts = emptyList(), // 可以从ViewModel获取
                    onBack = {
                        navController.popBackStack()
                    },
                    onStartAIChat = { citizen ->
                        navController.navigate("citizen_ai_chat/${citizen.id}")
                    }
                )
            } else {
                // 市民未找到，返回上一页
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        
        // 市民AI对话页面
        composable(
            route = CitySimulatorRoutes.CITIZEN_AI_CHAT,
            arguments = listOf(navArgument("citizenId") { type = NavType.StringType })
        ) { backStackEntry ->
            val citizenId = backStackEntry.arguments?.getString("citizenId") ?: return@composable
            val mainGameEntry = remember(backStackEntry) {
                navController.getBackStackEntry(CitySimulatorRoutes.MAIN_GAME)
            }
            val citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel(mainGameEntry)
            val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
            val selectedCitizen = citizens.find { it.id == citizenId }
            
            if (selectedCitizen != null) {
                CitizenAIChatScreen(
                    citizen = selectedCitizen,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}