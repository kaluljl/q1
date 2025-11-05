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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToThemeSelector: () -> Unit
) {
    val currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
    
    var showAboutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ 设置",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = currentTheme.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentTheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = currentTheme.backgroundGradient
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 外观设置区域
            item {
                SettingsSectionHeader(
                    title = "🎨 外观",
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🎨",
                    title = "主题切换",
                    subtitle = "选择您喜欢的视觉风格",
                    onClick = onNavigateToThemeSelector,
                    themeColors = currentTheme
                )
            }
            
            // 游戏设置区域
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(
                    title = "🎮 游戏",
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🔔",
                    title = "通知设置",
                    subtitle = "管理游戏通知",
                    onClick = { /* TODO: 实现通知设置 */ },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🎵",
                    title = "音效设置",
                    subtitle = "调整音效和音乐",
                    onClick = { /* TODO: 实现音效设置 */ },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "⚡",
                    title = "性能优化",
                    subtitle = "调整游戏性能选项",
                    onClick = { /* TODO: 实现性能设置 */ },
                    themeColors = currentTheme
                )
            }
            
            // 数据设置区域
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(
                    title = "💾 数据",
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "📂",
                    title = "数据备份",
                    subtitle = "备份您的游戏数据",
                    onClick = { /* TODO: 实现数据备份 */ },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🔄",
                    title = "数据恢复",
                    subtitle = "从备份恢复数据",
                    onClick = { /* TODO: 实现数据恢复 */ },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🗑️",
                    title = "清除缓存",
                    subtitle = "清理临时数据",
                    onClick = { /* TODO: 实现清除缓存 */ },
                    themeColors = currentTheme,
                    isDanger = true
                )
            }
            
            // 关于区域
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsSectionHeader(
                    title = "ℹ️ 关于",
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "📱",
                    title = "关于应用",
                    subtitle = "版本 1.0.0",
                    onClick = { showAboutDialog = true },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "📄",
                    title = "用户协议",
                    subtitle = "查看用户协议",
                    onClick = { /* TODO: 实现用户协议 */ },
                    themeColors = currentTheme
                )
            }
            
            item {
                SettingsItem(
                    icon = "🔒",
                    title = "隐私政策",
                    subtitle = "查看隐私政策",
                    onClick = { /* TODO: 实现隐私政策 */ },
                    themeColors = currentTheme
                )
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // 关于对话框
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Text(text = "🏙️", fontSize = 48.sp)
            },
            title = {
                Text(
                    text = "城市模拟器",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("版本：1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("一款深度AI驱动的城市模拟经营游戏")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "特色功能：",
                        fontWeight = FontWeight.Bold
                    )
                    Text("• 深度AI市民系统")
                    Text("• 多样化主题风格")
                    Text("• 奇观建筑系统")
                    Text("• 市长建议系统")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2025 城市模拟器团队",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = themeColors.textPrimary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    themeColors: com.citysimulator.game.ui.theme.GameThemeColors,
    isDanger: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.cardBackground
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDanger) Color(0xFFFFEBEE) else themeColors.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 文字内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDanger) Color(0xFFD32F2F) else themeColors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = themeColors.textSecondary
                )
            }
            
            // 箭头
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = themeColors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
