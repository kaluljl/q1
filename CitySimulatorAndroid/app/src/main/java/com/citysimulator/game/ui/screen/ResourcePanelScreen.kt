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

/**
 * 资源面板屏幕
 * 
 * 显示城市资源信息，包括生产、存储、消耗等。
 * 
 * @param onNavigateBack 返回导航回调
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcePanelScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("资源管理") },
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
                    text = "基础资源",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(4) { index ->
                ResourceItem(
                    name = when (index) {
                        0 -> "木材"
                        1 -> "石材"
                        2 -> "钢材"
                        else -> "食品"
                    },
                    amount = when (index) {
                        0 -> "1,250"
                        1 -> "890"
                        2 -> "450"
                        else -> "2,100"
                    },
                    production = when (index) {
                        0 -> "+50/小时"
                        1 -> "+30/小时"
                        2 -> "+20/小时"
                        else -> "+80/小时"
                    },
                    icon = when (index) {
                        0 -> Icons.Default.Forest
                        1 -> Icons.Default.Landscape
                        2 -> Icons.Default.PrecisionManufacturing
                        else -> Icons.Default.Agriculture
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "货币资源",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            items(2) { index ->
                ResourceItem(
                    name = if (index == 0) "金币" else "钻石",
                    amount = if (index == 0) "15,680" else "125",
                    production = if (index == 0) "+500/小时" else "+5/天",
                    icon = if (index == 0) Icons.Default.AttachMoney else Icons.Default.Diamond
                )
            }
        }
    }
}

/**
 * 资源项
 * 
 * @param name 资源名称
 * @param amount 资源数量
 * @param production 生产速率
 * @param icon 资源图标
 */
@Composable
private fun ResourceItem(
    name: String,
    amount: String,
    production: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
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
                imageVector = icon,
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
                    text = "数量: $amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CityOnSurfaceVariant
                )
                Text(
                    text = "生产: $production",
                    style = MaterialTheme.typography.bodySmall,
                    color = CityGreen
                )
            }
        }
    }
}
