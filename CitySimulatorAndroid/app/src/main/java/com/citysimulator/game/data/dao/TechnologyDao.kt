package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.Technology
import kotlinx.coroutines.flow.Flow

/**
 * 科技树数据访问对象
 * 
 * 提供科技数据的数据库操作接口
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Dao
interface TechnologyDao {
    
    /**
     * 获取所有科技
     */
    @Query("SELECT * FROM technologies ORDER BY type, name")
    fun getAllTechnologies(): Flow<List<Technology>>
    
    /**
     * 获取已研究的科技
     */
    @Query("SELECT * FROM technologies WHERE isResearched = 1 ORDER BY researchCompleteTime DESC")
    fun getResearchedTechnologies(): Flow<List<Technology>>
    
    /**
     * 获取正在研究的科技
     */
    @Query("SELECT * FROM technologies WHERE isResearched = 0 AND researchStartTime IS NOT NULL")
    fun getResearchingTechnologies(): Flow<List<Technology>>
    
    /**
     * 根据ID获取科技
     */
    @Query("SELECT * FROM technologies WHERE id = :id")
    suspend fun getTechnologyById(id: String): Technology?
    
    /**
     * 根据类型获取科技
     */
    @Query("SELECT * FROM technologies WHERE type = :type ORDER BY name")
    fun getTechnologiesByType(type: String): Flow<List<Technology>>
    
    /**
     * 插入科技
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnology(technology: Technology)
    
    /**
     * 插入多个科技
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnologies(technologies: List<Technology>)
    
    /**
     * 更新科技
     */
    @Update
    suspend fun updateTechnology(technology: Technology)
    
    /**
     * 删除科技
     */
    @Delete
    suspend fun deleteTechnology(technology: Technology)
    
    /**
     * 开始研究科技
     */
    @Query("UPDATE technologies SET isResearched = 0, researchStartTime = :startTime WHERE id = :id")
    suspend fun startResearch(id: String, startTime: Long)
    
    /**
     * 完成研究科技
     */
    @Query("UPDATE technologies SET isResearched = 1, researchCompleteTime = :completeTime WHERE id = :id")
    suspend fun completeResearch(id: String, completeTime: Long)
    
    /**
     * 重置所有科技
     */
    @Query("UPDATE technologies SET isResearched = 0, researchStartTime = NULL, researchCompleteTime = NULL")
    suspend fun resetAllTechnologies()
    
    /**
     * 获取科技研究进度
     */
    @Query("SELECT COUNT(*) FROM technologies WHERE isResearched = 1")
    suspend fun getResearchProgress(): Int
    
    /**
     * 获取总科技数量
     */
    @Query("SELECT COUNT(*) FROM technologies")
    suspend fun getTotalTechnologies(): Int
}
