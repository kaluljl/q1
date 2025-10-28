package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.SimplifiedBuildingType
import com.citysimulator.game.data.model.SimplifiedBuildingCategory

/**
 * 简化的建筑菜单屏幕
 * 
 * 按照四大类别清晰展示所有建筑
 * 每个建筑显示：图标、名称、成本、描述、升级路径
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplifiedBuildingMenuScreen(
    currentGold: Int = 1000,
    onBuildingSelected: (SimplifiedBuildingType) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<SimplifiedBuildingCategory?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (selectedCategory == null) "建筑菜单" else selectedCategory!!.getDisplayName(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedCategory != null) {
                            selectedCategory = null
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // 显示当前金币
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
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFBBDEFB)
                        )
                    )
                )
        ) {
            if (selectedCategory == null) {
                // 显示四大类别
                CategorySelectionView(
                    onCategorySelected = { category ->
                        selectedCategory = category
                    }
                )
            } else {
                // 显示该类别下的所有建筑
                BuildingListView(
                    category = selectedCategory!!,
                    currentGold = currentGold,
                    onBuildingSelected = onBuildingSelected
                )
            }
        }
    }
}

/**
 * 类别选择视图
 */
@Composable
fun CategorySelectionView(
    onCategorySelected: (SimplifiedBuildingCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "选择建筑类别",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 四大类别卡片
        SimplifiedBuildingCategory.entries.forEach { category ->
            CategoryCard(
                category = category,
                onClick = { onCategorySelected(category) }
            )
        }
        
        // 游戏提示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF9C4)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 游戏提示",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57F17)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• 先建【住宅】获得人口\n" +
                          "• 再建【经济建筑】赚取金币\n" +
                          "• 【公共服务】满足居民需求\n" +
                          "• 用【道路】连接所有建筑",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * 类别卡片
 */
@Composable
fun CategoryCard(
    category: SimplifiedBuildingCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (category) {
                SimplifiedBuildingCategory.RESIDENTIAL -> Color(0xFFFFCDD2)
                SimplifiedBuildingCategory.ECONOMIC -> Color(0xFFC8E6C9)
                SimplifiedBuildingCategory.PUBLIC_SERVICE -> Color(0xFFBBDEFB)
                SimplifiedBuildingCategory.INFRASTRUCTURE -> Color(0xFFF0F0F0)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.getEmoji(),
                    fontSize = 48.sp
                )
                Column {
                    Text(
                        text = category.getDisplayName(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (category) {
                            SimplifiedBuildingCategory.RESIDENTIAL -> "提供人口"
                            SimplifiedBuildingCategory.ECONOMIC -> "赚取金币"
                            SimplifiedBuildingCategory.PUBLIC_SERVICE -> "满足需求"
                            SimplifiedBuildingCategory.INFRASTRUCTURE -> "连接城市"
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * 建筑列表视图
 */
@Composable
fun BuildingListView(
    category: SimplifiedBuildingCategory,
    currentGold: Int,
    onBuildingSelected: (SimplifiedBuildingType) -> Unit
) {
    val buildings = remember(category) {
        SimplifiedBuildingType.entries.filter { it.getCategory() == category }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "共 ${buildings.size} 个建筑",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(buildings) { building ->
            BuildingCard(
                building = building,
                currentGold = currentGold,
                onClick = { onBuildingSelected(building) }
            )
        }
    }
}

/**
 * 建筑卡片
 */
@Composable
fun BuildingCard(
    building: SimplifiedBuildingType,
    currentGold: Int,
    onClick: () -> Unit
) {
    val canAfford = currentGold >= building.getBuildCost()
    val upgradeBuilding = building.getUpgradeTo()
    val income = building.getMonthlyIncome()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (canAfford) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) Color.White else Color(0xFFE0E0E0)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = building.getEmoji(),
                        fontSize = 36.sp
                    )
                    Column {
                        Text(
                            text = building.getDisplayName(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) Color.Black else Color.Gray
                        )
                        Text(
                            text = building.getDescription(),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 成本
                InfoChip(
                    label = "成本",
                    value = "${building.getBuildCost()} 💰",
                    color = Color(0xFFFF9800)
                )
                
                // 收入/维护
                InfoChip(
                    label = if (income > 0) "收入" else "维护",
                    value = "${if (income > 0) "+" else ""}$income/月",
                    color = if (income > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
            
            // 升级路径
            if (upgradeBuilding != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⬆️ 可升级为: ",
                        fontSize = 12.sp,
                        color = Color(0xFF388E3C)
                    )
                    Text(
                        text = upgradeBuilding.getEmoji() + " " + upgradeBuilding.getDisplayName(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                }
            }
            
            // 不可购买提示
            if (!canAfford) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ 金币不足",
                    fontSize = 12.sp,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/**
 * 信息标签
 */
@Composable
fun InfoChip(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

