package com.citysimulator.game.di

import com.citysimulator.game.data.supabase.SupabaseConfig
import com.citysimulator.game.data.supabase.SupabaseRepository
import com.citysimulator.game.data.supabase.RealSupabaseClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Supabase依赖注入模块
 * 提供Supabase相关的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseConfig(): SupabaseConfig {
        return SupabaseConfig()
    }

    @Provides
    @Singleton
    fun provideRealSupabaseClient(): RealSupabaseClient {
        return RealSupabaseClient()
    }

    @Provides
    @Singleton
    fun provideSupabaseRepository(
        supabaseConfig: SupabaseConfig,
        realSupabaseClient: RealSupabaseClient
    ): SupabaseRepository {
        return SupabaseRepository(supabaseConfig, realSupabaseClient)
    }
}
