package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.CitizenSimulationEngine
import com.citysimulator.game.ai.CitizenGenerator
import com.citysimulator.game.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * 市民ViewModel
 * 
 * 管理市民的创建、更新和状态
 */
@HiltViewModel
class CitizenViewModel @Inject constructor() : ViewModel() {
    
    private val simulationEngine = CitizenSimulationEngine()
    
    private val _citizens = MutableStateFlow<List<Citizen>>(emptyList())
    val citizens: StateFlow<List<Citizen>> = _citizens.asStateFlow()
    
    private val _selectedCitizen = MutableStateFlow<Citizen?>(null)
    val selectedCitizen: StateFlow<Citizen?> = _selectedCitizen.asStateFlow()
    
    private val _citizenThoughts = MutableStateFlow<Map<String, List<CitizenThought>>>(emptyMap())
    val citizenThoughts: StateFlow<Map<String, List<CitizenThought>>> = _citizenThoughts.asStateFlow()
    
    private var isSimulating = false
    
    /**
     * 初始化市民（为已有建筑生成市民）
     */
    fun initializeCitizens(buildings: List<Building>) {
        viewModelScope.launch {
            val newCitizens = CitizenGenerator.generateInitialCitizens(buildings)
            _citizens.value = newCitizens
            println("🏠 为 ${buildings.size} 个建筑生成了 ${newCitizens.size} 个市民")
            
            // 启动模拟
            startSimulation(buildings)
        }
    }
    
    /**
     * 为新建筑添加市民
     * @param building 新建筑
     * @param onPopulationUpdate 人口更新回调
     */
    fun addCitizensForBuilding(building: Building, onPopulationUpdate: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val newCitizens = CitizenGenerator.generateCitizensForBuilding(building)
            if (newCitizens.isNotEmpty()) {
                _citizens.value = _citizens.value + newCitizens
                println("👥 新建筑 ${building.getDisplayName()} 添加了 ${newCitizens.size} 个市民")
                
                // 通知人口更新
                onPopulationUpdate?.invoke(_citizens.value.size)
            }
        }
    }
    
    /**
     * 移除建筑相关的市民
     * @param building 被拆除的建筑
     * @param onPopulationUpdate 人口更新回调
     */
    fun removeCitizensForBuilding(building: Building, onPopulationUpdate: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val beforeCount = _citizens.value.size
            val buildingX = building.position.x
            val buildingY = building.position.y
            
            // 移除住在该建筑或在该建筑工作的市民（通过坐标匹配）
            _citizens.value = _citizens.value.filter { citizen ->
                val livesHere = citizen.homeX == buildingX && citizen.homeY == buildingY
                val worksHere = citizen.workplaceX == buildingX && citizen.workplaceY == buildingY
                !livesHere && !worksHere
            }
            
            val afterCount = _citizens.value.size
            val removedCount = beforeCount - afterCount
            
            if (removedCount > 0) {
                println("👋 拆除建筑 ${building.getDisplayName()} 后移除了 $removedCount 个市民（剩余 $afterCount 人）")
                
                // 通知人口更新
                onPopulationUpdate?.invoke(afterCount)
            }
        }
    }
    
    /**
     * 获取市民总数
     */
    fun getCitizenCount(): Int = _citizens.value.size
    
    /**
     * 生成市民
     */
    private fun generateCitizens(count: Int, buildings: List<Building>): List<Citizen> {
        val residentialBuildings = buildings.filter { 
            it.type == BuildingType.HOUSE || 
            it.type == BuildingType.APARTMENT ||
            it.type == BuildingType.VILLA ||
            it.type == BuildingType.SKYSCRAPER
        }
        
        val workplaces = buildings.filter { 
            it.type == BuildingType.FACTORY ||
            it.type == BuildingType.SHOP ||
            it.type == BuildingType.RESTAURANT ||
            it.type == BuildingType.HOTEL
        }
        
        if (residentialBuildings.isEmpty()) return emptyList()
        
        return List(count) { index ->
            val home = residentialBuildings.random()
            val hasJob = Random.nextFloat() < 0.7f // 70%就业率
            val workplace = if (hasJob && workplaces.isNotEmpty()) workplaces.random() else null
            
            Citizen(
                id = UUID.randomUUID().toString(),
                name = generateName(),
                age = Random.nextInt(18, 65),
                gender = if (Random.nextBoolean()) Gender.MALE else Gender.FEMALE,
                homeX = home.position.x,
                homeY = home.position.y,
                occupation = if (hasJob) generateOccupation() else null,
                workplaceX = workplace?.position?.x,
                workplaceY = workplace?.position?.y,
                salary = if (hasJob) Random.nextInt(3000, 15000) else 0,
                happiness = Random.nextFloat() * 0.4f + 0.4f, // 0.4-0.8
                health = Random.nextFloat() * 0.3f + 0.7f, // 0.7-1.0
                education = EducationLevel.values().random(),
                wealth = Random.nextInt(0, 50000),
                personality = CitizenPersonality.values().random(),
                workingHours = WorkingHours.values().random(),
                currentActivity = CitizenActivity.AT_HOME
            )
        }
    }
    
    /**
     * 启动模拟
     */
    private fun startSimulation(buildings: List<Building>) {
        if (isSimulating) return
        isSimulating = true
        
        viewModelScope.launch {
            while (isSimulating) {
                val currentTime = Date()
                
                // 更新所有市民
                val updatedCitizens = _citizens.value.map { citizen ->
                    simulationEngine.updateCitizen(citizen, currentTime, buildings)
                }
                _citizens.value = updatedCitizens
                
                // 生成部分市民的想法（随机选择10%）
                val thoughtsMap = _citizenThoughts.value.toMutableMap()
                updatedCitizens.filter { Random.nextFloat() < 0.1f }.forEach { citizen ->
                    simulationEngine.generateThought(citizen, buildings)?.let { thought ->
                        val existingThoughts = thoughtsMap[citizen.id] ?: emptyList()
                        thoughtsMap[citizen.id] = (existingThoughts + thought).takeLast(10)
                    }
                }
                _citizenThoughts.value = thoughtsMap
                
                // 更新选中的市民
                _selectedCitizen.value?.let { selected ->
                    _selectedCitizen.value = updatedCitizens.find { it.id == selected.id }
                }
                
                // 每5秒更新一次
                delay(5000)
            }
        }
    }
    
    /**
     * 停止模拟
     */
    fun stopSimulation() {
        isSimulating = false
    }
    
    /**
     * 选择市民
     */
    fun selectCitizen(citizen: Citizen) {
        _selectedCitizen.value = citizen
    }
    
    /**
     * 取消选择
     */
    fun deselectCitizen() {
        _selectedCitizen.value = null
    }
    
    /**
     * 根据位置查找市民
     */
    fun getCitizensAtPosition(x: Int, y: Int): List<Citizen> {
        return _citizens.value.filter { it.currentX == x && it.currentY == y }
    }
    
    /**
     * 获取市民想法
     */
    fun getCitizenThoughts(citizenId: String): List<CitizenThought> {
        return _citizenThoughts.value[citizenId] ?: emptyList()
    }
    
    /**
     * 获取统计信息
     */
    fun getCitizenStats(): CitizenStatsSummary {
        val citizens = _citizens.value
        return CitizenStatsSummary(
            totalPopulation = citizens.size,
            employmentRate = citizens.count { it.occupation != null }.toFloat() / citizens.size.coerceAtLeast(1),
            averageHappiness = citizens.map { it.happiness }.average().toFloat(),
            averageHealth = citizens.map { it.health }.average().toFloat(),
            activitiesBreakdown = citizens.groupBy { it.currentActivity }
                .mapValues { it.value.size }
        )
    }
    
    // 辅助函数
    private fun generateName(): String {
        val firstNames = listOf(
            "张伟", "王芳", "李娜", "刘洋", "陈静",
            "杨帆", "赵敏", "黄强", "周杰", "吴磊",
            "徐婷", "孙浩", "朱丽", "马超", "胡军",
            "郭敏", "何平", "高峰", "林涛", "罗霞"
        )
        return firstNames.random() + Random.nextInt(1, 100)
    }
    
    private fun generateOccupation(): String {
        val occupations = listOf(
            "软件工程师", "教师", "医生", "销售员", "会计",
            "设计师", "厨师", "司机", "护士", "程序员",
            "经理", "工人", "服务员", "店员", "技术员",
            "律师", "记者", "艺术家", "运动员", "作家"
        )
        return occupations.random()
    }
}

/**
 * 市民统计摘要
 */
data class CitizenStatsSummary(
    val totalPopulation: Int,
    val employmentRate: Float,
    val averageHappiness: Float,
    val averageHealth: Float,
    val activitiesBreakdown: Map<CitizenActivity, Int>
)

