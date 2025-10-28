package com.citysimulator.game.di

import com.citysimulator.game.data.dao.*
import com.citysimulator.game.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库依赖注入模块
 * 
 * 提供各种Repository的依赖注入配置。
 * 使用Hilt进行依赖管理，确保单例模式。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * 提供城市仓库
     * 
     * @param cityDao 城市DAO
     * @return 城市仓库
     */
    @Provides
    @Singleton
    fun provideCityRepository(cityDao: CityDao): CityRepository {
        return CityRepository(cityDao)
    }
    
    /**
     * 提供建筑仓库
     * 
     * @param buildingDao 建筑DAO
     * @return 建筑仓库
     */
    @Provides
    @Singleton
    fun provideBuildingRepository(buildingDao: BuildingDao): BuildingRepository {
        return BuildingRepository(buildingDao)
    }
    
    /**
     * 提供资源仓库
     * 
     * @param resourceDao 资源DAO
     * @return 资源仓库
     */
    @Provides
    @Singleton
    fun provideResourceRepository(resourceDao: ResourceDao): ResourceRepository {
        return ResourceRepository(resourceDao)
    }
    
    /**
     * 提供人口仓库
     * 
     * @param populationDao 人口DAO
     * @return 人口仓库
     */
    @Provides
    @Singleton
    fun providePopulationRepository(populationDao: PopulationDao): PopulationRepository {
        return PopulationRepository(populationDao)
    }
}
