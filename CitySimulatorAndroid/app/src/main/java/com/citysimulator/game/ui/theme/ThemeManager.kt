package com.citysimulator.game.ui.theme

import android.content.Context
import android.content.SharedPreferences

/**
 * 全局主题管理器 - 使用单例模式
 * 
 * 不依赖ViewModel和StateFlow，直接使用SharedPreferences
 */
object ThemeManager {
    private const val PREFS_NAME = "game_theme_prefs"
    private const val KEY_THEME_TYPE = "theme_type"
    
    private var prefs: SharedPreferences? = null
    private var currentThemeType: ThemeType = ThemeType.DEFAULT
    
    // 主题变化监听器列表
    private val listeners = mutableListOf<(ThemeType) -> Unit>()
    
    /**
     * 初始化（在Application中调用）
     */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // 加载保存的主题
            val savedThemeName = prefs?.getString(KEY_THEME_TYPE, ThemeType.DEFAULT.name)
            currentThemeType = try {
                ThemeType.valueOf(savedThemeName ?: ThemeType.DEFAULT.name)
            } catch (e: Exception) {
                ThemeType.DEFAULT
            }
            println("🎨 [ThemeManager] 初始化完成，当前主题: $currentThemeType")
        }
    }
    
    /**
     * 获取当前主题
     */
    fun getCurrentTheme(): GameThemeColors {
        return getThemeColors(currentThemeType)
    }
    
    /**
     * 获取当前主题类型
     */
    fun getCurrentThemeType(): ThemeType {
        return currentThemeType
    }
    
    /**
     * 切换主题
     */
    fun changeTheme(newThemeType: ThemeType) {
        println("🎨 [ThemeManager] ========== 切换主题 ==========")
        println("🎨 [ThemeManager] 从 $currentThemeType 切换到 $newThemeType")
        
        currentThemeType = newThemeType
        
        // 保存到SharedPreferences
        prefs?.edit()?.putString(KEY_THEME_TYPE, newThemeType.name)?.apply()
        
        println("🎨 [ThemeManager] 主题已保存，通知所有监听器...")
        
        // 通知所有监听器
        listeners.forEach { listener ->
            try {
                listener(newThemeType)
                println("🎨 [ThemeManager] 监听器已通知")
            } catch (e: Exception) {
                println("❌ [ThemeManager] 监听器通知失败: ${e.message}")
            }
        }
        
        println("🎨 [ThemeManager] ========== 切换完成 ==========")
    }
    
    /**
     * 添加主题变化监听器
     */
    fun addListener(listener: (ThemeType) -> Unit) {
        listeners.add(listener)
        println("🎨 [ThemeManager] 添加监听器，当前监听器数量: ${listeners.size}")
    }
    
    /**
     * 移除主题变化监听器
     */
    fun removeListener(listener: (ThemeType) -> Unit) {
        listeners.remove(listener)
        println("🎨 [ThemeManager] 移除监听器，当前监听器数量: ${listeners.size}")
    }
    
    /**
     * 获取所有主题
     */
    fun getAllThemes(): List<GameThemeColors> {
        return ThemeType.values().map { getThemeColors(it) }
    }
}

