package com.citysimulator.game.di

import android.content.Context
import com.citysimulator.game.data.dao.*
import com.citysimulator.game.data.database.CitySimulatorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 * 
 * 提供Room数据库和所有DAO的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * 提供数据库实例
     */
    @Provides
    @Singleton
    fun provideCitySimulatorDatabase(@ApplicationContext context: Context): CitySimulatorDatabase {
        return CitySimulatorDatabase.getDatabase(context)
    }
    
    /**
     * 提供城市DAO
     */
    @Provides
    @Singleton
    fun provideCityDao(database: CitySimulatorDatabase): CityDao {
        return database.cityDao()
    }
    
    /**
     * 提供建筑DAO
     */
    @Provides
    @Singleton
    fun provideBuildingDao(database: CitySimulatorDatabase): BuildingDao {
        return database.buildingDao()
    }
    
    /**
     * 提供资源DAO
     */
    @Provides
    @Singleton
    fun provideResourceDao(database: CitySimulatorDatabase): ResourceDao {
        return database.resourceDao()
    }
    
    /**
     * 提供人口DAO
     */
    @Provides
    @Singleton
    fun providePopulationDao(database: CitySimulatorDatabase): PopulationDao {
        return database.populationDao()
    }
    
    /**
     * 提供科技树DAO
     */
    @Provides
    @Singleton
    fun provideTechnologyDao(database: CitySimulatorDatabase): TechnologyDao {
        return database.technologyDao()
    }
    
    /**
     * 提供城市政策DAO
     */
    @Provides
    @Singleton
    fun provideCityPolicyDao(database: CitySimulatorDatabase): CityPolicyDao {
        return database.cityPolicyDao()
    }
    
    /**
     * 提供AI NPC DAO
     */
    @Provides
    @Singleton
    fun provideAINPCDao(database: CitySimulatorDatabase): AINPCDao {
        return database.ainpcDao()
    }
}

