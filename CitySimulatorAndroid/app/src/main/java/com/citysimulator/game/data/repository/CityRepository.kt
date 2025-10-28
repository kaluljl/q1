package com.citysimulator.game.data.repository

import com.citysimulator.game.data.dao.CityDao
import com.citysimulator.game.data.model.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 城市数据仓库
 * 
 * 提供城市数据的访问接口，封装数据访问逻辑。
 * 使用Repository模式，为ViewModel提供统一的数据访问接口。
 * 
 * @property cityDao 城市数据访问对象
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Singleton
class CityRepository @Inject constructor(
    private val cityDao: CityDao
) {
    
    /**
     * 获取所有城市
     * 
     * @return 城市列表的Flow
     */
    fun getAllCities(): Flow<List<City>> {
        return cityDao.getAllCities()
    }
    
    /**
     * 根据ID获取城市
     * 
     * @param id 城市ID
     * @return 城市对象的Flow
     */
    fun getCityById(id: String): Flow<City?> {
        return cityDao.getCityById(id)
    }
    
    /**
     * 获取当前活跃城市
     * 
     * @return 当前城市的Flow
     */
    fun getCurrentCity(): Flow<City?> {
        return cityDao.getCurrentCity()
    }
    
    /**
     * 插入城市
     * 
     * @param city 要插入的城市
     */
    suspend fun insertCity(city: City) {
        cityDao.insertCity(city)
    }
    
    /**
     * 更新城市
     * 
     * @param city 要更新的城市
     */
    suspend fun updateCity(city: City) {
        cityDao.updateCity(city)
    }
    
    /**
     * 删除城市
     * 
     * @param city 要删除的城市
     */
    suspend fun deleteCity(city: City) {
        cityDao.deleteCity(city)
    }
    
    /**
     * 根据ID删除城市
     * 
     * @param id 城市ID
     */
    suspend fun deleteCityById(id: String) {
        cityDao.deleteCityById(id)
    }
    
    /**
     * 更新城市最后游戏时间
     * 
     * @param id 城市ID
     * @param lastPlayedDate 最后游戏时间
     */
    suspend fun updateLastPlayedDate(id: String, lastPlayedDate: Long) {
        cityDao.updateLastPlayedDate(id, lastPlayedDate)
    }
    
    /**
     * 增加城市经验值
     * 
     * @param id 城市ID
     * @param experience 要增加的经验值
     */
    suspend fun addExperience(id: String, experience: Int) {
        cityDao.addExperience(id, experience)
    }
    
    /**
     * 更新城市等级
     * 
     * @param id 城市ID
     * @param level 新等级
     */
    suspend fun updateLevel(id: String, level: Int) {
        cityDao.updateLevel(id, level)
    }
    
    /**
     * 更新城市人口
     * 
     * @param id 城市ID
     * @param population 新人口数量
     */
    suspend fun updatePopulation(id: String, population: Int) {
        cityDao.updatePopulation(id, population)
    }
    
    /**
     * 更新城市各项指数
     * 
     * @param id 城市ID
     * @param happiness 幸福指数
     * @param environment 环境指数
     * @param economy 经济指数
     * @param education 教育指数
     * @param health 健康指数
     * @param safety 安全指数
     * @param transportation 交通指数
     */
    suspend fun updateCityIndices(
        id: String,
        happiness: Float,
        environment: Float,
        economy: Float,
        education: Float,
        health: Float,
        safety: Float,
        transportation: Float
    ) {
        cityDao.updateCityIndices(
            id, happiness, environment, economy,
            education, health, safety, transportation
        )
    }
}
