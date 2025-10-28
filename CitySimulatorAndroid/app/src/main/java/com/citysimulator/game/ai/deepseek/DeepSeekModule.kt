package com.citysimulator.game.ai.deepseek

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.citysimulator.game.utils.APIKeyManager
import com.citysimulator.game.utils.APIKeyInitializer
import javax.inject.Singleton

/**
 * DeepSeek AI模块 - 依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object DeepSeekModule {
    
    /**
     * 提供API密钥管理器
     */
    @Provides
    @Singleton
    fun provideAPIKeyManager(@ApplicationContext context: Context): APIKeyManager {
        return APIKeyManager(context)
    }
    
    /**
     * 提供DeepSeek客户端
     */
    @Provides
    @Singleton
    fun provideDeepSeekClient(apiKeyManager: APIKeyManager): DeepSeekClient {
        val apiKey = apiKeyManager.getDeepSeekApiKey()
        
        return DeepSeekClient(
            apiKey = apiKey,
            baseUrl = "https://api.deepseek.com/v1"
        )
    }
    
    /**
     * 提供智能AI系统
     */
    @Provides
    @Singleton
    fun provideIntelligentAISystem(deepSeekClient: DeepSeekClient): IntelligentAISystem {
        return IntelligentAISystem(deepSeekClient)
    }
    
    /**
     * 提供API密钥初始化器
     */
    @Provides
    @Singleton
    fun provideAPIKeyInitializer(apiKeyManager: APIKeyManager): APIKeyInitializer {
        return APIKeyInitializer(apiKeyManager)
    }
}
