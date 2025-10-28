package com.citysimulator.game.utils

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API密钥初始化器
 * 用于在应用启动时设置默认的API密钥
 */
@Singleton
class APIKeyInitializer @Inject constructor(
    private val apiKeyManager: APIKeyManager
) {
    
    /**
     * 初始化API密钥
     * 如果当前没有有效的API密钥，则设置提供的密钥
     */
    fun initializeApiKey(apiKey: String) {
        if (!apiKeyManager.hasValidApiKey() && apiKey.isNotEmpty()) {
            apiKeyManager.setDeepSeekApiKey(apiKey)
        }
    }
    
    /**
     * 检查API密钥状态
     */
    fun isApiKeyConfigured(): Boolean {
        return apiKeyManager.hasValidApiKey()
    }
}
