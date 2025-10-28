package com.citysimulator.game.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// DataStore 扩展
private val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_time")

/**
 * 游戏时间ViewModel
 * 
 * 管理游戏内的时间流逝，并持久化保存
 * 游戏时间流速: 20分钟真实时间 = 1个月游戏时间 (20分钟 = 30天)
 * 即: 40秒真实时间 = 1天游戏时间
 */
@HiltViewModel
class GameTimeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private val GAME_DATE_KEY = longPreferencesKey("game_date_timestamp")
        private val LAST_SAVE_TIME_KEY = longPreferencesKey("last_save_time")
    }
    
    // 游戏开始时间 (2024年1月1日)
    private val gameStartTime = Calendar.getInstance().apply {
        set(2024, Calendar.JANUARY, 1, 8, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
    
    private val _gameDate = MutableStateFlow(gameStartTime)
    val gameDate: StateFlow<Date> = _gameDate.asStateFlow()
    
    private val _isRunning = MutableStateFlow(true)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    private val _timeSpeed = MutableStateFlow(1f) // 时间流速倍数
    val timeSpeed: StateFlow<Float> = _timeSpeed.asStateFlow()
    
    init {
        loadGameTime()
        startGameTime()
        startAutoSave()
    }
    
    /**
     * 加载保存的游戏时间
     */
    private fun loadGameTime() {
        viewModelScope.launch {
            try {
                val preferences = context.gameDataStore.data.first()
                val savedTimestamp = preferences[GAME_DATE_KEY]
                val lastSaveTime = preferences[LAST_SAVE_TIME_KEY]
                
                if (savedTimestamp != null && lastSaveTime != null) {
                    // 计算离线期间流逝的时间
                    val offlineTime = System.currentTimeMillis() - lastSaveTime
                    // 离线期间也按游戏速度流逝（1秒真实 = 36分钟游戏）
                    val offlineGameTime = (offlineTime / 1000) * (36 * 60 * 1000)
                    
                    val savedDate = Date(savedTimestamp)
                    val calendar = Calendar.getInstance().apply { time = savedDate }
                    calendar.add(Calendar.MILLISECOND, offlineGameTime.toInt())
                    
                    _gameDate.value = calendar.time
                    println("⏰ 加载游戏时间: ${getFormattedDate()} ${getFormattedTime()}")
                    println("📊 离线时长: ${offlineTime / 1000 / 60}分钟，游戏时间推进: ${offlineGameTime / 1000 / 60 / 60}小时")
                } else {
                    println("🆕 首次启动，使用初始游戏时间: ${getFormattedDate()}")
                }
            } catch (e: Exception) {
                println("❌ 加载游戏时间失败: ${e.message}")
            }
        }
    }
    
    /**
     * 保存游戏时间
     */
    private suspend fun saveGameTime() {
        try {
            context.gameDataStore.edit { preferences ->
                preferences[GAME_DATE_KEY] = _gameDate.value.time
                preferences[LAST_SAVE_TIME_KEY] = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            println("❌ 保存游戏时间失败: ${e.message}")
        }
    }
    
    /**
     * 自动保存（每分钟保存一次）
     */
    private fun startAutoSave() {
        viewModelScope.launch {
            while (true) {
                delay(60_000) // 每分钟保存一次
                saveGameTime()
                println("💾 自动保存游戏时间: ${getFormattedDate()} ${getFormattedTime()}")
            }
        }
    }
    
    /**
     * 启动游戏时间
     */
    private fun startGameTime() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // 每秒更新一次
                
                if (_isRunning.value) {
                    // 计算游戏时间增量
                    // 20分钟真实时间 = 1个月游戏时间 = 30天
                    // 40秒真实时间 = 1天游戏时间 = 24小时
                    // 1秒真实时间 = 36分钟游戏时间
                    val gameTimeIncrement = (36 * 60 * 1000 * _timeSpeed.value).toLong() // 36分钟的毫秒数
                    
                    val currentDate = _gameDate.value
                    val calendar = Calendar.getInstance()
                    calendar.time = currentDate
                    calendar.add(Calendar.MILLISECOND, gameTimeIncrement.toInt())
                    
                    _gameDate.value = calendar.time
                }
            }
        }
    }
    
    /**
     * 暂停/继续游戏时间
     */
    fun togglePause() {
        _isRunning.value = !_isRunning.value
    }
    
    /**
     * 设置时间流速
     */
    fun setTimeSpeed(speed: Float) {
        _timeSpeed.value = speed.coerceIn(0.5f, 5f) // 限制在0.5x到5x之间
    }
    
    /**
     * 重置游戏时间
     */
    fun resetGameTime() {
        _gameDate.value = gameStartTime
    }
    
    /**
     * 获取格式化的游戏日期
     */
    fun getFormattedDate(): String {
        val calendar = Calendar.getInstance()
        calendar.time = _gameDate.value
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "${year}年${month}月${day}日"
    }
    
    /**
     * 获取格式化的游戏时间
     */
    fun getFormattedTime(): String {
        val calendar = Calendar.getInstance()
        calendar.time = _gameDate.value
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }
    
    /**
     * ViewModel销毁时保存游戏时间
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            saveGameTime()
            println("🔄 应用退出，保存游戏时间")
        }
    }
}

