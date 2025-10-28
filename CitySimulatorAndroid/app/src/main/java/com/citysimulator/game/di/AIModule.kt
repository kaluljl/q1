package com.citysimulator.game.di

import com.citysimulator.game.ai.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AI系统依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    
    /**
     * 提供NPC行为引擎
     */
    @Provides
    @Singleton
    fun provideNPCBehaviorEngine(): NPCBehaviorEngine {
        return NPCBehaviorEngine()
    }
    
    /**
     * 提供城市规划AI
     */
    @Provides
    @Singleton
    fun provideCityPlanningAI(): CityPlanningAI {
        return CityPlanningAI()
    }
    
    /**
     * 提供AI对话系统
     */
    @Provides
    @Singleton
    fun provideAIDialogueSystem(): AIDialogueSystem {
        return AIDialogueSystem()
    }
    
    /**
     * 提供AI天气系统
     */
    @Provides
    @Singleton
    fun provideAIWeatherSystem(): AIWeatherSystem {
        return AIWeatherSystem()
    }
}
