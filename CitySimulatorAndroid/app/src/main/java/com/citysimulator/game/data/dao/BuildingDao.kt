package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.BuildingCategory
import com.citysimulator.game.data.model.BuildingStatus
import com.citysimulator.game.data.model.BuildingType
import kotlinx.coroutines.flow.Flow

/**
 * 建筑数据访问对象
 * 
 * 提供建筑数据的CRUD操作接口。
 * 支持按类型、状态、位置等条件查询建筑。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Dao
interface BuildingDao {
    
    /**
     * 获取所有建筑
     * 
     * @return 建筑列表的Flow
     */
    @Query("SELECT * FROM buildings ORDER BY buildTime DESC")
    fun getAllBuildings(): Flow<List<Building>>
    
    /**
     * 根据ID获取建筑
     * 
     * @param id 建筑ID
     * @return 建筑对象的Flow
     */
    @Query("SELECT * FROM buildings WHERE id = :id")
    fun getBuildingById(id: String): Flow<Building?>
    
    /**
     * 根据类型获取建筑
     * 
     * @param type 建筑类型
     * @return 建筑列表的Flow
     */
    @Query("SELECT * FROM buildings WHERE type = :type ORDER BY buildTime DESC")
    fun getBuildingsByType(type: BuildingType): Flow<List<Building>>
    
    /**
     * 根据状态获取建筑
     * 
     * @param status 建筑状态
     * @return 建筑列表的Flow
     */
    @Query("SELECT * FROM buildings WHERE status = :status ORDER BY buildTime DESC")
    fun getBuildingsByStatus(status: BuildingStatus): Flow<List<Building>>
    
    /**
     * 获取需要维护的建筑
     * 
     * @return 需要维护的建筑列表的Flow
     */
    @Query("SELECT * FROM buildings WHERE isMaintenanceRequired = 1 ORDER BY lastMaintenanceDate ASC")
    fun getBuildingsNeedingMaintenance(): Flow<List<Building>>
    
    /**
     * 获取正在建造的建筑
     * 
     * @return 正在建造的建筑列表的Flow
     */
    @Query("SELECT * FROM buildings WHERE isUnderConstruction = 1 ORDER BY buildTime ASC")
    fun getBuildingsUnderConstruction(): Flow<List<Building>>
    
    /**
     * 获取正在升级的建筑
     * 
     * @return 正在升级的建筑列表的Flow
     */
    @Query("SELECT * FROM buildings WHERE isUpgrading = 1 ORDER BY buildTime ASC")
    fun getBuildingsUnderUpgrade(): Flow<List<Building>>
    
    /**
     * 根据位置获取建筑
     * 
     * @param x X坐标
     * @param y Y坐标
     * @return 建筑对象
     */
    @Query("SELECT * FROM buildings WHERE position_x = :x AND position_y = :y")
    suspend fun getBuildingAtPosition(x: Int, y: Int): Building?
    
    /**
     * 检查位置是否被占用
     * 
     * @param x X坐标
     * @param y Y坐标
     * @return 是否被占用
     */
    @Query("SELECT COUNT(*) > 0 FROM buildings WHERE position_x = :x AND position_y = :y")
    suspend fun isPositionOccupied(x: Int, y: Int): Boolean
    
    /**
     * 插入建筑
     * 
     * @param building 要插入的建筑
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: Building)
    
    /**
     * 批量插入建筑
     * 
     * @param buildings 要插入的建筑列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildings(buildings: List<Building>)
    
    /**
     * 更新建筑
     * 
     * @param building 要更新的建筑
     */
    @Update
    suspend fun updateBuilding(building: Building)
    
    /**
     * 删除建筑
     * 
     * @param building 要删除的建筑
     */
    @Delete
    suspend fun deleteBuilding(building: Building)
    
    /**
     * 根据ID删除建筑
     * 
     * @param id 建筑ID
     */
    @Query("DELETE FROM buildings WHERE id = :id")
    suspend fun deleteBuildingById(id: String)
    
    /**
     * 删除所有建筑
     */
    @Query("DELETE FROM buildings")
    suspend fun deleteAllBuildings()
    
    /**
     * 更新建筑状态
     * 
     * @param id 建筑ID
     * @param status 新状态
     */
    @Query("UPDATE buildings SET status = :status WHERE id = :id")
    suspend fun updateBuildingStatus(id: String, status: BuildingStatus)
    
    /**
     * 更新建筑等级
     * 
     * @param id 建筑ID
     * @param level 新等级
     */
    @Query("UPDATE buildings SET level = :level WHERE id = :id")
    suspend fun updateBuildingLevel(id: String, level: Int)
    
    /**
     * 设置建筑建造状态
     * 
     * @param id 建筑ID
     * @param isUnderConstruction 是否正在建造
     */
    @Query("UPDATE buildings SET isUnderConstruction = :isUnderConstruction WHERE id = :id")
    suspend fun setConstructionStatus(id: String, isUnderConstruction: Boolean)
    
    /**
     * 设置建筑升级状态
     * 
     * @param id 建筑ID
     * @param isUpgrading 是否正在升级
     */
    @Query("UPDATE buildings SET isUpgrading = :isUpgrading WHERE id = :id")
    suspend fun setUpgradeStatus(id: String, isUpgrading: Boolean)
    
    /**
     * 设置建筑维护状态
     * 
     * @param id 建筑ID
     * @param isMaintenanceRequired 是否需要维护
     */
    @Query("UPDATE buildings SET isMaintenanceRequired = :isMaintenanceRequired WHERE id = :id")
    suspend fun setMaintenanceStatus(id: String, isMaintenanceRequired: Boolean)
    
    /**
     * 更新建筑最后维护时间
     * 
     * @param id 建筑ID
     * @param lastMaintenanceDate 最后维护时间
     */
    @Query("UPDATE buildings SET lastMaintenanceDate = :lastMaintenanceDate WHERE id = :id")
    suspend fun updateLastMaintenanceDate(id: String, lastMaintenanceDate: Long)
    
    /**
     * 更新建筑效率
     * 
     * @param id 建筑ID
     * @param efficiency 新效率
     */
    @Query("UPDATE buildings SET efficiency = :efficiency WHERE id = :id")
    suspend fun updateBuildingEfficiency(id: String, efficiency: Float)
    
    /**
     * 更新建筑收入
     * 
     * @param id 建筑ID
     * @param income 新收入
     */
    @Query("UPDATE buildings SET income = :income WHERE id = :id")
    suspend fun updateBuildingIncome(id: String, income: Int)
    
    /**
     * 获取建筑统计信息
     * 
     * @return 建筑数量统计
     */
    @Query("SELECT type, COUNT(*) as count FROM buildings GROUP BY type")
    suspend fun getBuildingStatistics(): List<BuildingTypeCount>
}

/**
 * 建筑类型数量数据类
 * 
 * 用于建筑统计查询结果。
 */
data class BuildingTypeCount(
    val type: BuildingType,
    val count: Int
)



