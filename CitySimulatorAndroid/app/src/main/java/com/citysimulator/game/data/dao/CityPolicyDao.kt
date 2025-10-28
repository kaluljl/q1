package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.CityPolicy
import kotlinx.coroutines.flow.Flow

/**
 * 城市政策数据访问对象
 * 
 * 提供城市政策数据的数据库操作接口
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Dao
interface CityPolicyDao {
    
    /**
     * 获取所有政策
     */
    @Query("SELECT * FROM city_policies ORDER BY type, level, name")
    fun getAllPolicies(): Flow<List<CityPolicy>>
    
    /**
     * 获取已实施的政策
     */
    @Query("SELECT * FROM city_policies WHERE isImplemented = 1 ORDER BY implementedAt DESC")
    fun getImplementedPolicies(): Flow<List<CityPolicy>>
    
    /**
     * 获取未实施的政策
     */
    @Query("SELECT * FROM city_policies WHERE isImplemented = 0 ORDER BY type, level, name")
    fun getAvailablePolicies(): Flow<List<CityPolicy>>
    
    /**
     * 根据ID获取政策
     */
    @Query("SELECT * FROM city_policies WHERE id = :id")
    suspend fun getPolicyById(id: String): CityPolicy?
    
    /**
     * 根据类型获取政策
     */
    @Query("SELECT * FROM city_policies WHERE type = :type ORDER BY level, name")
    fun getPoliciesByType(type: String): Flow<List<CityPolicy>>
    
    /**
     * 根据等级获取政策
     */
    @Query("SELECT * FROM city_policies WHERE level = :level ORDER BY type, name")
    fun getPoliciesByLevel(level: String): Flow<List<CityPolicy>>
    
    /**
     * 插入政策
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: CityPolicy)
    
    /**
     * 插入多个政策
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicies(policies: List<CityPolicy>)
    
    /**
     * 更新政策
     */
    @Update
    suspend fun updatePolicy(policy: CityPolicy)
    
    /**
     * 删除政策
     */
    @Delete
    suspend fun deletePolicy(policy: CityPolicy)
    
    /**
     * 实施政策
     */
    @Query("UPDATE city_policies SET isImplemented = 1, implementedAt = :implementedAt WHERE id = :id")
    suspend fun implementPolicy(id: String, implementedAt: Long)
    
    /**
     * 撤销政策
     */
    @Query("UPDATE city_policies SET isImplemented = 0, implementedAt = NULL WHERE id = :id")
    suspend fun revokePolicy(id: String)
    
    /**
     * 重置所有政策
     */
    @Query("UPDATE city_policies SET isImplemented = 0, implementedAt = NULL")
    suspend fun resetAllPolicies()
    
    /**
     * 获取政策实施进度
     */
    @Query("SELECT COUNT(*) FROM city_policies WHERE isImplemented = 1")
    suspend fun getImplementationProgress(): Int
    
    /**
     * 获取总政策数量
     */
    @Query("SELECT COUNT(*) FROM city_policies")
    suspend fun getTotalPolicies(): Int
    
    /**
     * 获取每月政策维护成本
     */
    @Query("SELECT SUM(monthlyCost) FROM city_policies WHERE isImplemented = 1")
    suspend fun getMonthlyPolicyCost(): Int
    
    /**
     * 检查政策是否已实施
     */
    @Query("SELECT isImplemented FROM city_policies WHERE id = :id")
    suspend fun isPolicyImplemented(id: String): Boolean?
}

