package com.citysimulator.game.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.audio.SoundManager
import com.citysimulator.game.ui.theme.GameThemeColors
import com.citysimulator.game.ui.theme.ThemeManager

/**
 * 音频设置面板组件
 */
@Composable
fun AudioSettingsPanel(
    modifier: Modifier = Modifier
) {
    val currentTheme = ThemeManager.getCurrentTheme()
    
    // 音频设置状态
    var musicEnabled by remember { mutableStateOf(SoundManager.isMusicEnabled()) }
    var soundEnabled by remember { mutableStateOf(SoundManager.isSoundEnabled()) }
    var musicVolume by remember { mutableStateOf(SoundManager.getMusicVolume()) }
    var soundVolume by remember { mutableStateOf(SoundManager.getSoundVolume()) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Text(
                text = "🎵 音频设置",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 背景音乐开关
            AudioToggleItem(
                title = "背景音乐",
                icon = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                enabled = musicEnabled,
                onToggle = { enabled ->
                    musicEnabled = enabled
                    SoundManager.setMusicEnabled(enabled)
                },
                themeColors = currentTheme
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 背景音乐音量滑块
            if (musicEnabled) {
                AudioVolumeSlider(
                    label = "音乐音量",
                    volume = musicVolume,
                    onVolumeChange = { volume ->
                        musicVolume = volume
                        SoundManager.setMusicVolume(volume)
                    },
                    themeColors = currentTheme
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // 音效开关
            AudioToggleItem(
                title = "音效",
                icon = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                enabled = soundEnabled,
                onToggle = { enabled ->
                    soundEnabled = enabled
                    SoundManager.setSoundEnabled(enabled)
                },
                themeColors = currentTheme
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 音效音量滑块
            if (soundEnabled) {
                AudioVolumeSlider(
                    label = "音效音量",
                    volume = soundVolume,
                    onVolumeChange = { volume ->
                        soundVolume = volume
                        SoundManager.setSoundVolume(volume)
                    },
                    themeColors = currentTheme
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 提示信息
            Text(
                text = "💡 提示：关闭音效可以提升游戏性能",
                fontSize = 11.sp,
                color = currentTheme.textSecondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * 音频开关项
 */
@Composable
private fun AudioToggleItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    themeColors: GameThemeColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled) themeColors.primary else themeColors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = themeColors.textPrimary
            )
        }
        
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = themeColors.primary,
                checkedTrackColor = themeColors.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = themeColors.textSecondary,
                uncheckedTrackColor = themeColors.textSecondary.copy(alpha = 0.3f)
            )
        )
    }
}

/**
 * 音量滑块
 */
@Composable
private fun AudioVolumeSlider(
    label: String,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    themeColors: GameThemeColors
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = themeColors.textSecondary
            )
            
            Text(
                text = "${(volume * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.primary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = themeColors.primary,
                activeTrackColor = themeColors.primary,
                inactiveTrackColor = themeColors.divider
            )
        )
    }
}

