package com.citysimulator.game.data.repository

import com.citysimulator.game.data.dao.ResourceDao
import com.citysimulator.game.data.model.Resource
import com.citysimulator.game.data.model.ResourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 资源数据仓库
 * 
 * 提供资源数据的访问接口，封装数据访问逻辑。
 * 
 * @property resourceDao 资源数据访问对象
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Singleton
class ResourceRepository @Inject constructor(
    private val resourceDao: ResourceDao
) {
    
    /**
     * 获取所有资源
     * 
     * @return 资源列表的Flow
     */
    fun getAllResources(): Flow<List<Resource>> {
        return resourceDao.getAllResources()
    }
    
    /**
     * 根据ID获取资源
     * 
     * @param id 资源ID
     * @return 资源对象的Flow
     */
    fun getResourceById(id: String): Flow<Resource?> {
        return resourceDao.getResourceById(id)
    }
    
    /**
     * 根据类型获取资源
     * 
     * @param type 资源类型
     * @return 资源对象的Flow
     */
    fun getResourceByType(type: ResourceType): Flow<Resource?> {
        return resourceDao.getResourceByType(type)
    }
    
    /**
     * 插入资源
     * 
     * @param resource 要插入的资源
     */
    suspend fun insertResource(resource: Resource) {
        resourceDao.insertResource(resource)
    }
    
    /**
     * 批量插入资源
     * 
     * @param resources 要插入的资源列表
     */
    suspend fun insertResources(resources: List<Resource>) {
        resourceDao.insertResources(resources)
    }
    
    /**
     * 更新资源
     * 
     * @param resource 要更新的资源
     */
    suspend fun updateResource(resource: Resource) {
        resourceDao.updateResource(resource)
    }
    
    /**
     * 删除资源
     * 
     * @param resource 要删除的资源
     */
    suspend fun deleteResource(resource: Resource) {
        resourceDao.deleteResource(resource)
    }
    
    /**
     * 根据ID删除资源
     * 
     * @param id 资源ID
     */
    suspend fun deleteResourceById(id: String) {
        resourceDao.deleteResourceById(id)
    }
    
    /**
     * 更新资源数量
     * 
     * @param id 资源ID
     * @param amount 新数量
     */
    suspend fun updateResourceAmount(id: String, amount: Double) {
        resourceDao.updateResourceAmount(id, amount)
    }
    
    /**
     * 增加资源数量
     * 
     * @param id 资源ID
     * @param amount 要增加的数量
     */
    suspend fun addResourceAmount(id: String, amount: Double) {
        resourceDao.addResourceAmount(id, amount)
    }
    
    /**
     * 减少资源数量
     * 
     * @param id 资源ID
     * @param amount 要减少的数量
     */
    suspend fun subtractResourceAmount(id: String, amount: Double) {
        resourceDao.subtractResourceAmount(id, amount)
    }
    
    /**
     * 更新资源生产速率
     * 
     * @param id 资源ID
     * @param productionRate 新生产速率
     */
    suspend fun updateProductionRate(id: String, productionRate: Double) {
        resourceDao.updateProductionRate(id, productionRate)
    }
    
    /**
     * 更新资源消耗速率
     * 
     * @param id 资源ID
     * @param consumptionRate 新消耗速率
     */
    suspend fun updateConsumptionRate(id: String, consumptionRate: Double) {
        resourceDao.updateConsumptionRate(id, consumptionRate)
    }
    
    /**
     * 更新资源最大容量
     * 
     * @param id 资源ID
     * @param maxCapacity 新最大容量
     */
    suspend fun updateMaxCapacity(id: String, maxCapacity: Double) {
        resourceDao.updateMaxCapacity(id, maxCapacity)
    }
    
    /**
     * 检查资源是否充足
     * 
     * @param type 资源类型
     * @param requiredAmount 所需数量
     * @return 是否充足
     */
    suspend fun hasEnoughResource(type: ResourceType, requiredAmount: Double): Boolean {
        val resource = getResourceByType(type).firstOrNull()
        return resource?.hasEnough(requiredAmount) ?: false
    }
    
    /**
     * 消耗资源
     * 
     * @param type 资源类型
     * @param amount 消耗数量
     * @return 是否成功消耗
     */
    suspend fun consumeResource(type: ResourceType, amount: Double): Boolean {
        val resource = getResourceByType(type).firstOrNull()
        return if (resource != null && resource.hasEnough(amount)) {
            subtractResourceAmount(resource.id, amount)
            true
        } else {
            false
        }
    }
    
    /**
     * 生产资源
     * 
     * @param type 资源类型
     * @param amount 生产数量
     */
    suspend fun produceResource(type: ResourceType, amount: Double) {
        val resource = getResourceByType(type).firstOrNull()
        if (resource != null) {
            addResourceAmount(resource.id, amount)
        }
    }
}

