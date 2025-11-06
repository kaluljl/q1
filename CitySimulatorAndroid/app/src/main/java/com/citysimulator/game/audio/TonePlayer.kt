package com.citysimulator.game.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 程序化音效播放器
 * 
 * 用于在没有音效资源的情况下，使用系统音调生成简单的提示音
 * 这是一个临时方案，后续可以替换为真实的音效文件
 */
object TonePlayer {
    private const val TAG = "TonePlayer"
    private var toneGen: ToneGenerator? = null
    
    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 80) // 音量80%
            Log.d(TAG, "✅ ToneGenerator 初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ToneGenerator 初始化失败", e)
        }
    }
    
    /**
     * 播放音效（使用程序化音调）
     */
    fun playTone(soundType: SoundType) {
        if (toneGen == null) return
        
        // 在协程中异步播放，避免阻塞UI
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (soundType) {
                    // UI音效
                    SoundType.BUTTON_CLICK -> {
                        // 短促的嘟声
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                    }
                    
                    SoundType.MENU_OPEN -> {
                        // 向上的音调
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100)
                    }
                    
                    SoundType.MENU_CLOSE -> {
                        // 向下的音调
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 100)
                    }
                    
                    // 建造音效
                    SoundType.BUILD_SUCCESS -> {
                        // 积极的提示音
                        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                    }
                    
                    SoundType.BUILD_FAIL -> {
                        // 错误提示音
                        toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 150)
                    }
                    
                    SoundType.DEMOLISH -> {
                        // 低沉的提示音
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 200)
                    }
                    
                    // 经济音效
                    SoundType.COIN_EARN -> {
                        // 清脆的提示音（双音）
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                        Thread.sleep(50)
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
                    }
                    
                    SoundType.COIN_SPEND -> {
                        // 单音
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                    }
                    
                    // 事件音效
                    SoundType.LEVEL_UP -> {
                        // 上升音调序列
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                        Thread.sleep(50)
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
                        Thread.sleep(50)
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100)
                    }
                    
                    SoundType.ACHIEVEMENT -> {
                        // 胜利音效
                        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                    }
                    
                    SoundType.WARNING -> {
                        // 警告音
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                    }
                    
                    SoundType.ERROR -> {
                        // 错误音
                        toneGen?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 200)
                    }
                    
                    // 市民音效
                    SoundType.CITIZEN_HAPPY -> {
                        // 欢快的音调
                        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                    }
                    
                    SoundType.CITIZEN_SAD -> {
                        // 低沉的音调
                        toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 150)
                    }
                    
                    // 特殊音效
                    SoundType.WONDER_COMPLETE -> {
                        // 史诗级音效（音调序列）
                        repeat(3) {
                            toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                            Thread.sleep(100)
                        }
                    }
                    
                    SoundType.SEASON_CHANGE -> {
                        // 平缓的过渡音
                        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                    }
                }
                
                Log.d(TAG, "🔊 播放音效：$soundType")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 播放音效失败：$soundType", e)
            }
        }
    }
    
    /**
     * 释放资源
     */
    fun release() {
        try {
            toneGen?.release()
            toneGen = null
            Log.d(TAG, "🗑️ ToneGenerator 已释放")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 释放 ToneGenerator 失败", e)
        }
    }
}

