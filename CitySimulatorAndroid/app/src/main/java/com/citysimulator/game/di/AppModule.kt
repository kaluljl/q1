package com.citysimulator.game.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 应用依赖注入模块
 * 
 * 提供应用级别的依赖注入配置。
 * 包括DataStore、SharedPreferences等系统服务。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供DataStore实例
     * 
     * @param context 应用上下文
     * @return DataStore实例
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

/**
 * DataStore扩展属性
 * 
 * 为Context提供DataStore实例。
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "city_simulator_preferences")



