package com.citysimulator.game.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ui.theme.GameThemeColors
import com.citysimulator.game.ui.theme.ThemeType
import com.citysimulator.game.ui.theme.getThemeColors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// DataStore 扩展
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

/**
 * 主题管理ViewModel
 * 
 * 管理游戏主题的选择和切换，支持持久化保存
 * 使用Activity作用域确保全局单例
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private val THEME_TYPE_KEY = stringPreferencesKey("selected_theme_type")
    }
    
    private val _currentTheme = MutableStateFlow(getThemeColors(ThemeType.DEFAULT))
    val currentTheme: StateFlow<GameThemeColors> = _currentTheme
    
    private val _selectedThemeType = MutableStateFlow(ThemeType.DEFAULT)
    val selectedThemeType: StateFlow<ThemeType> = _selectedThemeType
    
    init {
        loadTheme()
    }
    
    /**
     * 加载保存的主题（只加载一次，不持续监听）
     */
    private fun loadTheme() {
        viewModelScope.launch {
            try {
                val preferences = context.themeDataStore.data.first()
                val savedThemeType = preferences[THEME_TYPE_KEY]
                
                if (savedThemeType != null) {
                    val themeType = ThemeType.valueOf(savedThemeType)
                    _selectedThemeType.value = themeType
                    _currentTheme.value = getThemeColors(themeType)
                    println("🎨 [loadTheme] 加载已保存主题: ${_currentTheme.value.themeName} (${themeType})")
                } else {
                    println("🎨 [loadTheme] 没有保存的主题，使用默认主题: ${_currentTheme.value.themeName}")
                }
            } catch (e: Exception) {
                println("❌ [loadTheme] 加载主题失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 保存主题设置
     */
    private suspend fun saveTheme(themeType: ThemeType) {
        try {
            context.themeDataStore.edit { preferences ->
                preferences[THEME_TYPE_KEY] = themeType.name
            }
            println("💾 保存主题: ${themeType.name}")
        } catch (e: Exception) {
            println("❌ 保存主题失败: ${e.message}")
        }
    }
    
    /**
     * 切换主题
     */
    fun changeTheme(themeType: ThemeType) {
        println("🎨 [changeTheme] ========== 开始切换主题 ==========")
        println("🎨 [changeTheme] 目标主题: ${themeType.name}")
        println("🎨 [changeTheme] 当前主题: ${_currentTheme.value.themeType.name}")
        
        // 立即同步更新（不在协程中，确保立即生效）
        _selectedThemeType.value = themeType
        _currentTheme.value = getThemeColors(themeType)
        
        println("🎨 [changeTheme] 状态已更新:")
        println("🎨 [changeTheme]   _selectedThemeType.value = ${_selectedThemeType.value}")
        println("🎨 [changeTheme]   _currentTheme.value.themeType = ${_currentTheme.value.themeType}")
        println("🎨 [changeTheme]   _currentTheme.value.themeName = ${_currentTheme.value.themeName}")
        
        // 异步保存到DataStore
        viewModelScope.launch {
            saveTheme(themeType)
            println("🎨 [changeTheme] 主题已保存到DataStore")
        }
        
        println("🎨 [changeTheme] ========== 切换完成 ==========")
    }
    
    /**
     * 获取所有可用主题
     */
    fun getAllThemes(): List<GameThemeColors> {
        return ThemeType.values().map { getThemeColors(it) }
    }
}

