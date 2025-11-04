package com.citysimulator.game.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.citysimulator.game.ui.theme.GameThemeColors
import com.citysimulator.game.ui.theme.getThemeBackgroundBrush
import com.citysimulator.game.ui.viewmodel.ThemeViewModel

/**
 * 主题选择界面
 * 
 * 允许玩家选择和切换游戏主题
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectorScreen(
    onNavigateBack: () -> Unit,
    onThemeChanged: () -> Unit = {},  // 新增：主题变化回调
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    // 使用全局ThemeManager
    var currentTheme by remember { mutableStateOf(com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()) }
    val selectedThemeType = com.citysimulator.game.ui.theme.ThemeManager.getCurrentThemeType()
    val allThemes = remember { com.citysimulator.game.ui.theme.ThemeManager.getAllThemes() }
    
    // 监听主题变化
    DisposableEffect(Unit) {
        val listener: (com.citysimulator.game.ui.theme.ThemeType) -> Unit = { newThemeType ->
            currentTheme = com.citysimulator.game.ui.theme.ThemeManager.getCurrentTheme()
        }
        com.citysimulator.game.ui.theme.ThemeManager.addListener(listener)
        onDispose {
            com.citysimulator.game.ui.theme.ThemeManager.removeListener(listener)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getThemeBackgroundBrush(currentTheme))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏
            TopAppBar(
                title = {
                    Text(
                        text = "🎨 主题设置",
                        color = currentTheme.textPrimary,
                        fontWeight = FontWeight.Bold
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
                    containerColor = currentTheme.cardBackground.copy(alpha = 0.95f)
                )
            )
            
            // 当前主题预览
            CurrentThemePreview(currentTheme)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 主题列表
            Text(
                text = "选择主题风格",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(allThemes) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.themeType == selectedThemeType,
                        currentTheme = currentTheme,
                        onThemeSelected = { 
                            println("🎨 [ThemeSelectorScreen] 用户选择主题: ${theme.themeName}")
                            com.citysimulator.game.ui.theme.ThemeManager.changeTheme(theme.themeType)
                            // 通知主界面主题已变化
                            onThemeChanged()
                        }
                    )
                }
            }
        }
    }
}

/**
 * 当前主题预览
 */
@Composable
private fun CurrentThemePreview(theme: GameThemeColors) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = theme.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "当前主题",
                fontSize = 14.sp,
                color = theme.textSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${theme.themeIcon} ${theme.themeName}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = theme.themeDescription,
                fontSize = 12.sp,
                color = theme.textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 颜色预览
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorPreviewBox(color = theme.residentialColor, label = "住宅")
                ColorPreviewBox(color = theme.commercialColor, label = "商业")
                ColorPreviewBox(color = theme.industrialColor, label = "工业")
                ColorPreviewBox(color = theme.publicColor, label = "公共")
            }
        }
    }
}

/**
 * 主题卡片
 */
@Composable
private fun ThemeCard(
    theme: GameThemeColors,
    isSelected: Boolean,
    currentTheme: GameThemeColors,
    onThemeSelected: () -> Unit
) {
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 1.dp,
        animationSpec = spring(),
        label = "border"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) currentTheme.primary else currentTheme.divider,
        label = "color"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onThemeSelected() }
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = theme.cardBackground.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 主题图标和信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = theme.themeIcon,
                        fontSize = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = theme.themeName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary
                        )
                        
                        Text(
                            text = theme.themeDescription,
                            fontSize = 12.sp,
                            color = theme.textSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 颜色预览条
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(theme.residentialColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(theme.commercialColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(theme.industrialColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(theme.publicColor)
                    )
                }
            }
            
            // 选中标记
            if (isSelected) {
                Spacer(modifier = Modifier.width(12.dp))
                
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选中",
                    tint = currentTheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * 颜色预览框
 */
@Composable
private fun ColorPreviewBox(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

