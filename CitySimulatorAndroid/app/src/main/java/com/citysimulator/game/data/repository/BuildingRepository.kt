package com.citysimulator.game.data.repository

import com.citysimulator.game.data.dao.BuildingDao
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.BuildingStatus
import com.citysimulator.game.data.model.BuildingType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 建筑数据仓库
 * 
 * 提供建筑数据的访问接口，封装数据访问逻辑。
 * 
 * @property buildingDao 建筑数据访问对象
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Singleton
class BuildingRepository @Inject constructor(
    private val buildingDao: BuildingDao
) {
    
    /**
     * 获取所有建筑
     * 
     * @return 建筑列表的Flow
     */
    fun getAllBuildings(): Flow<List<Building>> {
        return buildingDao.getAllBuildings()
    }
    
    /**
     * 根据ID获取建筑
     * 
     * @param id 建筑ID
     * @return 建筑对象的Flow
     */
    fun getBuildingById(id: String): Flow<Building?> {
        return buildingDao.getBuildingById(id)
    }
    
    /**
     * 根据类型获取建筑
     * 
     * @param type 建筑类型
     * @return 建筑列表的Flow
     */
    fun getBuildingsByType(type: BuildingType): Flow<List<Building>> {
        return buildingDao.getBuildingsByType(type)
    }
    
    /**
     * 根据状态获取建筑
     * 
     * @param status 建筑状态
     * @return 建筑列表的Flow
     */
    fun getBuildingsByStatus(status: BuildingStatus): Flow<List<Building>> {
        return buildingDao.getBuildingsByStatus(status)
    }
    
    /**
     * 获取需要维护的建筑
     * 
     * @return 需要维护的建筑列表的Flow
     */
    fun getBuildingsNeedingMaintenance(): Flow<List<Building>> {
        return buildingDao.getBuildingsNeedingMaintenance()
    }
    
    /**
     * 获取正在建造的建筑
     * 
     * @return 正在建造的建筑列表的Flow
     */
    fun getBuildingsUnderConstruction(): Flow<List<Building>> {
        return buildingDao.getBuildingsUnderConstruction()
    }
    
    /**
     * 获取正在升级的建筑
     * 
     * @return 正在升级的建筑列表的Flow
     */
    fun getBuildingsUnderUpgrade(): Flow<List<Building>> {
        return buildingDao.getBuildingsUnderUpgrade()
    }
    
    /**
     * 根据位置获取建筑
     * 
     * @param x X坐标
     * @param y Y坐标
     * @return 建筑对象
     */
    suspend fun getBuildingAtPosition(x: Int, y: Int): Building? {
        return buildingDao.getBuildingAtPosition(x, y)
    }
    
    /**
     * 检查位置是否被占用
     * 
     * @param x X坐标
     * @param y Y坐标
     * @return 是否被占用
     */
    suspend fun isPositionOccupied(x: Int, y: Int): Boolean {
        return buildingDao.isPositionOccupied(x, y)
    }
    
    /**
     * 插入建筑
     * 
     * @param building 要插入的建筑
     */
    suspend fun insertBuilding(building: Building) {
        buildingDao.insertBuilding(building)
    }
    
    /**
     * 批量插入建筑
     * 
     * @param buildings 要插入的建筑列表
     */
    suspend fun insertBuildings(buildings: List<Building>) {
        buildingDao.insertBuildings(buildings)
    }
    
    /**
     * 更新建筑
     * 
     * @param building 要更新的建筑
     */
    suspend fun updateBuilding(building: Building) {
        buildingDao.updateBuilding(building)
    }
    
    /**
     * 删除建筑
     * 
     * @param building 要删除的建筑
     */
    suspend fun deleteBuilding(building: Building) {
        buildingDao.deleteBuilding(building)
    }
    
    /**
     * 根据ID删除建筑
     * 
     * @param id 建筑ID
     */
    suspend fun deleteBuildingById(id: String) {
        buildingDao.deleteBuildingById(id)
    }
    
    /**
     * 删除所有建筑
     */
    suspend fun deleteAllBuildings() {
        buildingDao.deleteAllBuildings()
    }
    
    /**
     * 更新建筑状态
     * 
     * @param id 建筑ID
     * @param status 新状态
     */
    suspend fun updateBuildingStatus(id: String, status: BuildingStatus) {
        buildingDao.updateBuildingStatus(id, status)
    }
    
    /**
     * 更新建筑等级
     * 
     * @param id 建筑ID
     * @param level 新等级
     */
    suspend fun updateBuildingLevel(id: String, level: Int) {
        buildingDao.updateBuildingLevel(id, level)
    }
    
    /**
     * 设置建筑建造状态
     * 
     * @param id 建筑ID
     * @param isUnderConstruction 是否正在建造
     */
    suspend fun setConstructionStatus(id: String, isUnderConstruction: Boolean) {
        buildingDao.setConstructionStatus(id, isUnderConstruction)
    }
    
    /**
     * 设置建筑升级状态
     * 
     * @param id 建筑ID
     * @param isUpgrading 是否正在升级
     */
    suspend fun setUpgradeStatus(id: String, isUpgrading: Boolean) {
        buildingDao.setUpgradeStatus(id, isUpgrading)
    }
    
    /**
     * 设置建筑维护状态
     * 
     * @param id 建筑ID
     * @param isMaintenanceRequired 是否需要维护
     */
    suspend fun setMaintenanceStatus(id: String, isMaintenanceRequired: Boolean) {
        buildingDao.setMaintenanceStatus(id, isMaintenanceRequired)
    }
    
    /**
     * 更新建筑最后维护时间
     * 
     * @param id 建筑ID
     * @param lastMaintenanceDate 最后维护时间
     */
    suspend fun updateLastMaintenanceDate(id: String, lastMaintenanceDate: Long) {
        buildingDao.updateLastMaintenanceDate(id, lastMaintenanceDate)
    }
    
    /**
     * 更新建筑效率
     * 
     * @param id 建筑ID
     * @param efficiency 新效率
     */
    suspend fun updateBuildingEfficiency(id: String, efficiency: Float) {
        buildingDao.updateBuildingEfficiency(id, efficiency)
    }
    
    /**
     * 更新建筑收入
     * 
     * @param id 建筑ID
     * @param income 新收入
     */
    suspend fun updateBuildingIncome(id: String, income: Int) {
        buildingDao.updateBuildingIncome(id, income)
    }
}
