# 🚀 市民系统快速集成指南

## 第一步：更新底部控制栏

修改 `ModernBottomBar.kt`，添加市民按钮：

```kotlin
// 找到这部分代码并添加市民按钮
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceEvenly
) {
    // ... 现有按钮 ...
    
    // 新增：市民按钮
    GlassmorphismCard(
        onClick = onCitizenClick,  // 新参数
        modifier = Modifier.weight(1f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.People,  // 新图标
                contentDescription = "市民",
                tint = CityBlue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "市民",
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}
```

并在函数签名中添加参数：

```kotlin
@Composable
fun ModernBottomBar(
    // ... 现有参数 ...
    onCitizenClick: () -> Unit = {},  // 新参数
    modifier: Modifier = Modifier
)
```

## 第二步：在主游戏中添加导航

修改 `MainGameScreen.kt`：

```kotlin
// 在 MainGameScreen 函数参数中添加
onNavigateToCitizenList: () -> Unit,

// 在 ModernBottomBar 调用中添加
ModernBottomBar(
    // ... 现有参数 ...
    onCitizenClick = onNavigateToCitizenList,  // 新参数
    modifier = Modifier.fillMaxWidth()
)
```

## 第三步：添加导航路由

修改 `CitySimulatorNavigation.kt`：

```kotlin
// 在 CitySimulatorNavigation 函数中添加

// 市民列表路由
composable(CitySimulatorRoutes.CITIZEN_LIST) {
    val citizenViewModel: CitizenViewModel = hiltViewModel()
    val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
    
    CitizenListScreen(
        citizens = citizens,
        onCitizenClick = { citizen ->
            citizenViewModel.selectCitizen(citizen)
            navController.navigate("citizen_detail/${citizen.id}")
        },
        onBack = { navController.popBackStack() }
    )
}

// 市民详情路由
composable(
    route = "citizen_detail/{citizenId}",
    arguments = listOf(navArgument("citizenId") { type = NavType.StringType })
) { backStackEntry ->
    val citizenViewModel: CitizenViewModel = hiltViewModel()
    val selectedCitizen by citizenViewModel.selectedCitizen.collectAsStateWithLifecycle()
    val citizenId = backStackEntry.arguments?.getString("citizenId")
    
    selectedCitizen?.let { citizen ->
        val thoughts = citizenViewModel.getCitizenThoughts(citizen.id)
        CitizenDetailScreen(
            citizen = citizen,
            thoughts = thoughts,
            onBack = {
                citizenViewModel.deselectCitizen()
                navController.popBackStack()
            }
        )
    }
}
```

并在 MAIN_GAME 路由中添加参数：

```kotlin
composable(CitySimulatorRoutes.MAIN_GAME) {
    MainGameScreen(
        // ... 现有参数 ...
        onNavigateToCitizenList = {  // 新参数
            navController.navigate(CitySimulatorRoutes.CITIZEN_LIST)
        }
    )
}
```

## 第四步：初始化市民系统

修改 `MainGameScreen.kt`，在组件内添加：

```kotlin
// 获取 CitizenViewModel
val citizenViewModel: CitizenViewModel = hiltViewModel()

// 初始化市民（当人口变化时）
LaunchedEffect(currentPopulation, buildings) {
    if (currentPopulation > 0 && buildings.isNotEmpty()) {
        citizenViewModel.initializeCitizens(
            population = minOf(currentPopulation, 50),  // 限制显示50个市民以提高性能
            buildings = buildings
        )
    }
}
```

## 第五步：在城市网格上显示市民（可选）

如果想在城市网格上显示市民标记，在 `MainGameScreen.kt` 的 `CityGrid` Box 中添加：

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    CityGrid(/* ... */)
    
    // 天气特效
    // ...
    
    // 新增：市民标记
    val citizens by citizenViewModel.citizens.collectAsStateWithLifecycle()
    val selectedCitizen by citizenViewModel.selectedCitizen.collectAsStateWithLifecycle()
    
    citizens.forEach { citizen ->
        // 计算市民在屏幕上的位置
        // 这需要根据网格的实际布局来调整
        Box(
            modifier = Modifier
                .offset(
                    x = (citizen.currentX * cellSize).dp,
                    y = (citizen.currentY * cellSize).dp
                )
        ) {
            CitizenMarker(
                citizen = citizen,
                isSelected = selectedCitizen?.id == citizen.id,
                onClick = {
                    citizenViewModel.selectCitizen(citizen)
                    // 可选：导航到详情页
                    // navController.navigate("citizen_detail/${citizen.id}")
                }
            )
        }
    }
}
```

## 第六步：添加必要的导入

确保在相关文件中添加导入：

```kotlin
// MainGameScreen.kt
import com.citysimulator.game.ui.viewmodel.CitizenViewModel
import com.citysimulator.game.ui.component.CitizenMarker

// CitySimulatorNavigation.kt
import com.citysimulator.game.ui.screen.CitizenListScreen
import com.citysimulator.game.ui.screen.CitizenDetailScreen
import com.citysimulator.game.ui.viewmodel.CitizenViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

// ModernBottomBar.kt
import androidx.compose.material.icons.filled.People
```

## 测试步骤

1. **编译应用**
2. **启动游戏**
3. **建造一些住宅和工作建筑**
4. **等待人口增长**
5. **点击底部的"市民"按钮**
6. **查看市民列表**
7. **点击任意市民查看详情**
8. **观察市民的活动随时间变化**

## 🎉 完成！

现在您的城市模拟器有了完整的微观视角系统！

玩家可以：
- 👥 查看所有市民
- 🔍 搜索和筛选市民
- 👤 查看市民的详细信息
- 📊 了解市民的需求和幸福度
- 💭 查看市民的想法
- 🏃 观察市民的日常活动

enjoy your enhanced city simulator! 🏙️✨

