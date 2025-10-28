package com.citysimulator.game.data.repository

import com.citysimulator.game.data.dao.PopulationDao
import com.citysimulator.game.data.model.Population
import com.citysimulator.game.data.model.Profession
import com.citysimulator.game.data.model.EducationLevel
import com.citysimulator.game.data.model.HealthStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 人口数据仓库
 * 
 * 提供人口数据的访问接口，封装数据访问逻辑。
 * 
 * @property populationDao 人口数据访问对象
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Singleton
class PopulationRepository @Inject constructor(
    private val populationDao: PopulationDao
) {
    
    /**
     * 获取所有人口
     * 
     * @return 人口列表的Flow
     */
    fun getAllPopulation(): Flow<List<Population>> {
        return populationDao.getAllPopulation()
    }
    
    /**
     * 根据ID获取人口
     * 
     * @param id 人口ID
     * @return 人口对象的Flow
     */
    fun getPopulationById(id: String): Flow<Population?> {
        return populationDao.getPopulationById(id)
    }
    
    /**
     * 根据职业获取人口
     * 
     * @param profession 职业
     * @return 人口列表的Flow
     */
    fun getPopulationByProfession(profession: Profession): Flow<List<Population>> {
        return populationDao.getPopulationByProfession(profession)
    }
    
    /**
     * 根据教育水平获取人口
     * 
     * @param educationLevel 教育水平
     * @return 人口列表的Flow
     */
    fun getPopulationByEducation(educationLevel: EducationLevel): Flow<List<Population>> {
        return populationDao.getPopulationByEducation(educationLevel)
    }
    
    /**
     * 根据健康状况获取人口
     * 
     * @param healthStatus 健康状况
     * @return 人口列表的Flow
     */
    fun getPopulationByHealth(healthStatus: HealthStatus): Flow<List<Population>> {
        return populationDao.getPopulationByHealth(healthStatus)
    }
    
    /**
     * 获取已就业人口
     * 
     * @return 已就业人口列表的Flow
     */
    fun getEmployedPopulation(): Flow<List<Population>> {
        return populationDao.getEmployedPopulation()
    }
    
    /**
     * 获取失业人口
     * 
     * @return 失业人口列表的Flow
     */
    fun getUnemployedPopulation(): Flow<List<Population>> {
        return populationDao.getUnemployedPopulation()
    }
    
    /**
     * 获取有住所的人口
     * 
     * @return 有住所人口列表的Flow
     */
    fun getPopulationWithResidence(): Flow<List<Population>> {
        return populationDao.getPopulationWithResidence()
    }
    
    /**
     * 获取无住所的人口
     * 
     * @return 无住所人口列表的Flow
     */
    fun getPopulationWithoutResidence(): Flow<List<Population>> {
        return populationDao.getPopulationWithoutResidence()
    }
    
    /**
     * 获取需要医疗服务的人口
     * 
     * @return 需要医疗服务人口列表的Flow
     */
    fun getPopulationNeedingMedicalCare(): Flow<List<Population>> {
        return populationDao.getPopulationNeedingMedicalCare()
    }
    
    /**
     * 插入人口
     * 
     * @param population 要插入的人口
     */
    suspend fun insertPopulation(population: Population) {
        populationDao.insertPopulation(population)
    }
    
    /**
     * 批量插入人口
     * 
     * @param population 要插入的人口列表
     */
    suspend fun insertPopulationList(population: List<Population>) {
        populationDao.insertPopulationList(population)
    }
    
    /**
     * 更新人口
     * 
     * @param population 要更新的人口
     */
    suspend fun updatePopulation(population: Population) {
        populationDao.updatePopulation(population)
    }
    
    /**
     * 删除人口
     * 
     * @param population 要删除的人口
     */
    suspend fun deletePopulation(population: Population) {
        populationDao.deletePopulation(population)
    }
    
    /**
     * 根据ID删除人口
     * 
     * @param id 人口ID
     */
    suspend fun deletePopulationById(id: String) {
        populationDao.deletePopulationById(id)
    }
    
    /**
     * 更新人口幸福指数
     * 
     * @param id 人口ID
     * @param happiness 新幸福指数
     */
    suspend fun updateHappiness(id: String, happiness: Float) {
        populationDao.updateHappiness(id, happiness)
    }
    
    /**
     * 更新人口收入
     * 
     * @param id 人口ID
     * @param income 新收入
     */
    suspend fun updateIncome(id: String, income: Int) {
        populationDao.updateIncome(id, income)
    }
    
    /**
     * 更新人口职业
     * 
     * @param id 人口ID
     * @param profession 新职业
     */
    suspend fun updateProfession(id: String, profession: Profession) {
        populationDao.updateProfession(id, profession)
    }
    
    /**
     * 更新人口教育水平
     * 
     * @param id 人口ID
     * @param educationLevel 新教育水平
     */
    suspend fun updateEducationLevel(id: String, educationLevel: EducationLevel) {
        populationDao.updateEducationLevel(id, educationLevel)
    }
    
    /**
     * 更新人口健康状况
     * 
     * @param id 人口ID
     * @param healthStatus 新健康状况
     */
    suspend fun updateHealthStatus(id: String, healthStatus: HealthStatus) {
        populationDao.updateHealthStatus(id, healthStatus)
    }
    
    /**
     * 更新人口居住地
     * 
     * @param id 人口ID
     * @param residenceId 新居住地ID
     */
    suspend fun updateResidence(id: String, residenceId: String?) {
        populationDao.updateResidence(id, residenceId)
    }
    
    /**
     * 更新人口工作地
     * 
     * @param id 人口ID
     * @param workplaceId 新工作地ID
     */
    suspend fun updateWorkplace(id: String, workplaceId: String?) {
        populationDao.updateWorkplace(id, workplaceId)
    }
    
    data class PopulationStatistics(
        val total: Int,
        val employed: Int,
        val unemployed: Int
    )

    /**
     * 获取人口统计信息
     */
    suspend fun getPopulationStatistics(): PopulationStatistics {
        val s = populationDao.getPopulationStatistics()
        return PopulationStatistics(s.total, s.employed, s.unemployed)
    }
}

