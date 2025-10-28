package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.City
import kotlinx.coroutines.flow.Flow

/**
 * 城市数据访问对象
 * 
 * 提供城市数据的CRUD操作接口。
 * 使用Kotlin Flow实现响应式数据流。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Dao
interface CityDao {
    
    /**
     * 获取所有城市
     * 
     * @return 城市列表的Flow
     */
    @Query("SELECT * FROM cities ORDER BY lastPlayedDate DESC")
    fun getAllCities(): Flow<List<City>>
    
    /**
     * 根据ID获取城市
     * 
     * @param id 城市ID
     * @return 城市对象的Flow
     */
    @Query("SELECT * FROM cities WHERE id = :id")
    fun getCityById(id: String): Flow<City?>
    
    /**
     * 获取当前活跃城市
     * 
     * @return 当前城市的Flow
     */
    @Query("SELECT * FROM cities ORDER BY lastPlayedDate DESC LIMIT 1")
    fun getCurrentCity(): Flow<City?>
    
    /**
     * 插入城市
     * 
     * @param city 要插入的城市
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City)
    
    /**
     * 更新城市
     * 
     * @param city 要更新的城市
     */
    @Update
    suspend fun updateCity(city: City)
    
    /**
     * 删除城市
     * 
     * @param city 要删除的城市
     */
    @Delete
    suspend fun deleteCity(city: City)
    
    /**
     * 根据ID删除城市
     * 
     * @param id 城市ID
     */
    @Query("DELETE FROM cities WHERE id = :id")
    suspend fun deleteCityById(id: String)
    
    /**
     * 更新城市最后游戏时间
     * 
     * @param id 城市ID
     * @param lastPlayedDate 最后游戏时间
     */
    @Query("UPDATE cities SET lastPlayedDate = :lastPlayedDate WHERE id = :id")
    suspend fun updateLastPlayedDate(id: String, lastPlayedDate: Long)
    
    /**
     * 增加城市经验值
     * 
     * @param id 城市ID
     * @param experience 要增加的经验值
     */
    @Query("UPDATE cities SET experience = experience + :experience WHERE id = :id")
    suspend fun addExperience(id: String, experience: Int)
    
    /**
     * 更新城市等级
     * 
     * @param id 城市ID
     * @param level 新等级
     */
    @Query("UPDATE cities SET level = :level WHERE id = :id")
    suspend fun updateLevel(id: String, level: Int)
    
    /**
     * 更新城市人口
     * 
     * @param id 城市ID
     * @param population 新人口数量
     */
    @Query("UPDATE cities SET population = :population WHERE id = :id")
    suspend fun updatePopulation(id: String, population: Int)
    
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
    @Query("""
        UPDATE cities SET 
        happiness = :happiness,
        environment = :environment,
        economy = :economy,
        education = :education,
        health = :health,
        safety = :safety,
        transportation = :transportation
        WHERE id = :id
    """)
    suspend fun updateCityIndices(
        id: String,
        happiness: Float,
        environment: Float,
        economy: Float,
        education: Float,
        health: Float,
        safety: Float,
        transportation: Float
    )
}



