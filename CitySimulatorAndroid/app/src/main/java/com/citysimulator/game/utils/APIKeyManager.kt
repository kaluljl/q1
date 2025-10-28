package com.citysimulator.game.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API密钥管理器
 * 用于安全地存储和管理DeepSeek API密钥
 */
@Singleton
class APIKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("ai_config", Context.MODE_PRIVATE)
    
    companion object {
        private const val DEEPSEEK_API_KEY = "deepseek_api_key"
    }
    
    /**
     * 设置DeepSeek API密钥
     */
    fun setDeepSeekApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString(DEEPSEEK_API_KEY, apiKey)
            .apply()
    }
    
    /**
     * 获取DeepSeek API密钥
     */
    fun getDeepSeekApiKey(): String {
        return sharedPreferences.getString(DEEPSEEK_API_KEY, "") ?: ""
    }
    
    /**
     * 检查是否有有效的API密钥
     */
    fun hasValidApiKey(): Boolean {
        val apiKey = getDeepSeekApiKey()
        return apiKey.isNotEmpty() && apiKey.startsWith("sk-")
    }
    
    /**
     * 清除API密钥
     */
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(DEEPSEEK_API_KEY)
            .apply()
    }
}
