package com.citysimulator.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
 * 设置屏幕
 * 
 * 提供游戏设置选项，包括音效、音乐、显示等设置。
 * 
 * @param onNavigateBack 返回导航回调
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
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
                    text = "音频设置",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            item {
                SettingItem(
                    title = "音效",
                    description = "游戏音效开关",
                    isEnabled = true,
                    onToggle = { /* TODO: 切换音效 */ }
                )
            }
            
            item {
                SettingItem(
                    title = "背景音乐",
                    description = "背景音乐开关",
                    isEnabled = true,
                    onToggle = { /* TODO: 切换背景音乐 */ }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "显示设置",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            item {
                SettingItem(
                    title = "暗色主题",
                    description = "使用暗色主题",
                    isEnabled = false,
                    onToggle = { /* TODO: 切换主题 */ }
                )
            }
            
            item {
                SettingItem(
                    title = "动画效果",
                    description = "显示动画效果",
                    isEnabled = true,
                    onToggle = { /* TODO: 切换动画 */ }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "游戏设置",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CityOnSurface
                )
            }
            
            item {
                SettingItem(
                    title = "自动保存",
                    description = "自动保存游戏进度",
                    isEnabled = true,
                    onToggle = { /* TODO: 切换自动保存 */ }
                )
            }
            
            item {
                SettingItem(
                    title = "推送通知",
                    description = "接收游戏通知",
                    isEnabled = true,
                    onToggle = { /* TODO: 切换通知 */ }
                )
            }
        }
    }
}

/**
 * 设置项
 * 
 * @param title 设置标题
 * @param description 设置描述
 * @param isEnabled 是否启用
 * @param onToggle 切换回调
 */
@Composable
private fun SettingItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
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
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CityWhite,
                    checkedTrackColor = CityBlue,
                    uncheckedThumbColor = CityWhite,
                    uncheckedTrackColor = CityGray
                )
            )
        }
    }
}
