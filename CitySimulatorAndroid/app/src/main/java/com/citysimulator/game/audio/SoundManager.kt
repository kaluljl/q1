package com.citysimulator.game.audio

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import androidx.annotation.RawRes

/**
 * 游戏音频管理系统
 * 
 * 功能：
 * - 背景音乐播放和管理
 * - 音效播放和管理
 * - 音量控制
 * - 音频开关设置
 */
object SoundManager {
    private const val TAG = "SoundManager"
    private const val PREFS_NAME = "audio_settings"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_MUSIC_VOLUME = "music_volume"
    private const val KEY_SOUND_VOLUME = "sound_volume"
    
    // 背景音乐播放器
    private var musicPlayer: MediaPlayer? = null
    private var currentMusicResId: Int = -1
    
    // 音效播放器（SoundPool 用于短音效）
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundType, Int>()
    
    // 设置
    private var prefs: SharedPreferences? = null
    private var musicEnabled: Boolean = true
    private var soundEnabled: Boolean = true
    private var musicVolume: Float = 0.7f
    private var soundVolume: Float = 0.8f
    
    /**
     * 初始化音频系统
     */
    fun initialize(context: Context) {
        Log.d(TAG, "🎵 初始化音频系统")
        
        // 加载设置
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadSettings()
        
        // 初始化 SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(10) // 最多同时播放10个音效
            .setAudioAttributes(audioAttributes)
            .build()
        
        // 预加载音效（这里使用占位符，实际音频文件需要添加到 res/raw）
        // loadSoundEffects(context)
        
        Log.d(TAG, "✅ 音频系统初始化完成")
    }
    
    /**
     * 加载音效资源
     */
    private fun loadSoundEffects(context: Context) {
        try {
            // 这里添加实际的音效资源
            // soundMap[SoundType.BUTTON_CLICK] = soundPool?.load(context, R.raw.button_click, 1) ?: 0
            // soundMap[SoundType.BUILD_SUCCESS] = soundPool?.load(context, R.raw.build_success, 1) ?: 0
            // ... 其他音效
            
            Log.d(TAG, "✅ 音效加载完成：${soundMap.size} 个")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 音效加载失败", e)
        }
    }
    
    /**
     * 播放背景音乐
     */
    fun playMusic(context: Context, @RawRes musicResId: Int, loop: Boolean = true) {
        if (!musicEnabled) {
            Log.d(TAG, "🔇 背景音乐已禁用")
            return
        }
        
        // 如果是相同的音乐
        if (currentMusicResId == musicResId && musicPlayer != null) {
            // 如果正在播放，不做任何操作
            if (musicPlayer?.isPlaying == true) {
                Log.d(TAG, "🎵 音乐已在播放中，跳过")
                return
            }
            // 如果暂停了，恢复播放
            else {
                Log.d(TAG, "🎵 恢复播放音乐")
                resumeMusic()
                return
            }
        }
        
        try {
            // 停止当前音乐
            stopMusic()
            
            // 创建新的音乐播放器
            musicPlayer = MediaPlayer.create(context, musicResId)?.apply {
                isLooping = loop
                setVolume(musicVolume, musicVolume)
                setOnPreparedListener { 
                    start()
                    Log.d(TAG, "🎵 开始播放背景音乐：$musicResId")
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "❌ 音乐播放错误：what=$what, extra=$extra")
                    true
                }
            }
            
            currentMusicResId = musicResId
        } catch (e: Exception) {
            Log.e(TAG, "❌ 播放音乐失败", e)
        }
    }
    
    /**
     * 停止背景音乐
     */
    fun stopMusic() {
        try {
            musicPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            musicPlayer = null
            currentMusicResId = -1
            Log.d(TAG, "⏹️ 背景音乐已停止")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 停止音乐失败", e)
        }
    }
    
    /**
     * 暂停背景音乐
     */
    fun pauseMusic() {
        try {
            musicPlayer?.pause()
            Log.d(TAG, "⏸️ 背景音乐已暂停")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 暂停音乐失败", e)
        }
    }
    
    /**
     * 恢复背景音乐
     */
    fun resumeMusic() {
        if (!musicEnabled) return
        
        try {
            musicPlayer?.start()
            Log.d(TAG, "▶️ 背景音乐已恢复")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 恢复音乐失败", e)
        }
    }
    
    /**
     * 播放音效
     */
    fun playSound(soundType: SoundType) {
        if (!soundEnabled) return
        
        val soundId = soundMap[soundType]
        if (soundId != null && soundId > 0) {
            try {
                soundPool?.play(soundId, soundVolume, soundVolume, 1, 0, 1.0f)
                Log.d(TAG, "🔊 播放音效：$soundType")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 播放音效失败：$soundType", e)
            }
        } else {
            // 如果没有加载音效资源，使用程序化音调作为备用
            TonePlayer.playTone(soundType)
        }
    }
    
    /**
     * 设置背景音乐音量
     */
    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        musicPlayer?.setVolume(musicVolume, musicVolume)
        saveSettings()
        Log.d(TAG, "🔊 背景音乐音量：${(musicVolume * 100).toInt()}%")
    }
    
    /**
     * 设置音效音量
     */
    fun setSoundVolume(volume: Float) {
        soundVolume = volume.coerceIn(0f, 1f)
        saveSettings()
        Log.d(TAG, "🔊 音效音量：${(soundVolume * 100).toInt()}%")
    }
    
    /**
     * 启用/禁用背景音乐
     */
    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (!enabled) {
            pauseMusic()
        } else {
            resumeMusic()
        }
        saveSettings()
        Log.d(TAG, "🎵 背景音乐：${if (enabled) "开启" else "关闭"}")
    }
    
    /**
     * 启用/禁用音效
     */
    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
        saveSettings()
        Log.d(TAG, "🔊 音效：${if (enabled) "开启" else "关闭"}")
    }
    
    /**
     * 获取当前设置
     */
    fun isMusicEnabled() = musicEnabled
    fun isSoundEnabled() = soundEnabled
    fun getMusicVolume() = musicVolume
    fun getSoundVolume() = soundVolume
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        prefs?.let {
            musicEnabled = it.getBoolean(KEY_MUSIC_ENABLED, true)
            soundEnabled = it.getBoolean(KEY_SOUND_ENABLED, true)
            musicVolume = it.getFloat(KEY_MUSIC_VOLUME, 0.7f)
            soundVolume = it.getFloat(KEY_SOUND_VOLUME, 0.8f)
        }
    }
    
    /**
     * 保存设置
     */
    private fun saveSettings() {
        prefs?.edit()?.apply {
            putBoolean(KEY_MUSIC_ENABLED, musicEnabled)
            putBoolean(KEY_SOUND_ENABLED, soundEnabled)
            putFloat(KEY_MUSIC_VOLUME, musicVolume)
            putFloat(KEY_SOUND_VOLUME, soundVolume)
            apply()
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        try {
            stopMusic()
            soundPool?.release()
            soundPool = null
            soundMap.clear()
            TonePlayer.release()
            Log.d(TAG, "🗑️ 音频系统资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 释放音频资源失败", e)
        }
    }
}

/**
 * 音效类型枚举
 */
enum class SoundType {
    // UI 音效
    BUTTON_CLICK,       // 按钮点击
    MENU_OPEN,          // 菜单打开
    MENU_CLOSE,         // 菜单关闭
    
    // 建造音效
    BUILD_SUCCESS,      // 建造成功
    BUILD_FAIL,         // 建造失败
    DEMOLISH,           // 拆除建筑
    
    // 经济音效
    COIN_EARN,          // 获得金币
    COIN_SPEND,         // 消费金币
    
    // 事件音效
    LEVEL_UP,           // 升级
    ACHIEVEMENT,        // 成就达成
    WARNING,            // 警告
    ERROR,              // 错误
    
    // 市民音效
    CITIZEN_HAPPY,      // 市民开心
    CITIZEN_SAD,        // 市民不满
    
    // 特殊音效
    WONDER_COMPLETE,    // 奇观完成
    SEASON_CHANGE,      // 季节变化
}

/**
 * 背景音乐类型
 */
enum class MusicType {
    MAIN_THEME,         // 主题音乐
    PEACEFUL,           // 和平发展
    PROSPEROUS,         // 繁荣发展
    CRISIS,             // 危机时刻
    VICTORY,            // 胜利
}

