package com.citysimulator.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.ui.theme.*
import com.citysimulator.game.ui.state.BuildingSelectionState
import com.citysimulator.game.data.model.BuildingType

/**
 * 建筑菜单屏幕
 * 
 * 显示可建造的建筑类型，支持分类浏览和建造。
 * 
 * @param onNavigateBack 返回导航回调
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingMenuScreen(
    onNavigateBack: () -> Unit,
    onBuildingSelected: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("建筑菜单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "住宅建筑",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(4) { index ->
                val buildingType = when (index) {
                    0 -> "住宅"
                    1 -> "公寓"
                    2 -> "别墅"
                    else -> "摩天大楼"
                }
                val cost = when (index) {
                    0 -> "100金币"
                    1 -> "200金币"
                    2 -> "500金币"
                    else -> "1000金币"
                }
                BuildingMenuItem(
                    name = buildingType,
                    description = "为居民提供住所",
                    cost = cost,
                    onClick = { 
                        // 选择建筑类型并设置全局状态
                        val buildingTypeEnum = when (buildingType) {
                            "住宅" -> BuildingType.HOUSE
                            "公寓" -> BuildingType.APARTMENT
                            "别墅" -> BuildingType.VILLA
                            "摩天大楼" -> BuildingType.SKYSCRAPER
                            else -> BuildingType.HOUSE
                        }
                        println("选择建筑类型: $buildingTypeEnum")
                        BuildingSelectionState.selectBuilding(buildingTypeEnum)
                        println("全局状态设置完成: ${BuildingSelectionState.selectedBuildingType}")
                        onNavigateBack()
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "商业建筑",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(5) { index ->
                val buildingType = when (index) {
                    0 -> "商店"
                    1 -> "超市"
                    2 -> "商场"
                    3 -> "餐厅"
                    else -> "酒店"
                }
                val cost = when (index) {
                    0 -> "150金币"
                    1 -> "300金币"
                    2 -> "800金币"
                    3 -> "400金币"
                    else -> "600金币"
                }
                BuildingMenuItem(
                    name = buildingType,
                    description = "提供商业服务",
                    cost = cost,
                    onClick = { 
                        // 选择建筑类型并设置全局状态
                        val buildingTypeEnum = when (buildingType) {
                            "商店" -> BuildingType.SHOP
                            "超市" -> BuildingType.SUPERMARKET
                            "商场" -> BuildingType.MALL
                            "餐厅" -> BuildingType.RESTAURANT
                            "酒店" -> BuildingType.HOTEL
                            else -> BuildingType.SHOP
                        }
                        println("选择建筑类型: $buildingTypeEnum")
                        BuildingSelectionState.selectBuilding(buildingTypeEnum)
                        println("全局状态设置完成: ${BuildingSelectionState.selectedBuildingType}")
                        onNavigateBack()
                    }
                )
            }
        }
    }
}

/**
 * 建筑菜单项
 * 
 * @param name 建筑名称
 * @param description 建筑描述
 * @param cost 建造成本
 * @param onClick 点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildingMenuItem(
    name: String,
    description: String,
    cost: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CitySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = CityBlue,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CityOnSurfaceVariant
                )
            }
            
            Text(
                text = cost,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = GoldColor
            )
        }
    }
}

@Composable
fun BuildingMenuScreenPlaceholderColor() = GoldColor
