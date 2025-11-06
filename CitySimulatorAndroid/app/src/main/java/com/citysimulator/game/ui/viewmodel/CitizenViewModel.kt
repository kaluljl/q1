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
    
    // 市民人格数据缓存（确保人格固定）
    private val _citizenPersonalities = MutableStateFlow<Map<String, PersonalityTraits>>(emptyMap())
    val citizenPersonalities: StateFlow<Map<String, PersonalityTraits>> = _citizenPersonalities.asStateFlow()
    
    // 市民需求缓存
    private val _citizenNeeds = MutableStateFlow<Map<String, MaslowNeeds>>(emptyMap())
    val citizenNeeds: StateFlow<Map<String, MaslowNeeds>> = _citizenNeeds.asStateFlow()
    
    // 市民记忆缓存
    private val _citizenMemories = MutableStateFlow<Map<String, CitizenMemoryCollection>>(emptyMap())
    val citizenMemories: StateFlow<Map<String, CitizenMemoryCollection>> = _citizenMemories.asStateFlow()
    
    // 社交网络缓存
    private val _socialNetworks = MutableStateFlow<Map<String, SocialNetwork>>(emptyMap())
    val socialNetworks: StateFlow<Map<String, SocialNetwork>> = _socialNetworks.asStateFlow()
    
    private var isSimulating = false
    
    /**
     * 初始化市民（为已有建筑生成市民）
     * 智能初始化：如果已有市民，只为新建筑添加市民；如果没有市民，全新生成
     */
    fun initializeCitizens(buildings: List<Building>) {
        viewModelScope.launch {
            println("👥 [CitizenViewModel] 开始初始化市民，建筑数量: ${buildings.size}，当前市民数: ${_citizens.value.size}")
            
            // 如果已经有市民，说明不是首次初始化，保留现有市民
            if (_citizens.value.isNotEmpty()) {
                println("✅ 已有市民 ${_citizens.value.size} 个，保持不变")
                
                // 检查是否有新建筑需要添加市民
                val existingBuildingIds = _citizens.value.map { "${it.homeX},${it.homeY}" }.toSet()
                val newBuildings = buildings.filter { building ->
                    "${building.position.x},${building.position.y}" !in existingBuildingIds
                }
                
                if (newBuildings.isNotEmpty()) {
                    println("🏗️ 发现 ${newBuildings.size} 个新建筑，为其生成市民")
                    val additionalCitizens = newBuildings.flatMap { building ->
                        CitizenGenerator.generateCitizensForBuilding(building)
                    }
                    if (additionalCitizens.isNotEmpty()) {
                        _citizens.value = _citizens.value + additionalCitizens
                        println("👥 新增 ${additionalCitizens.size} 个市民，总数: ${_citizens.value.size}")
                    }
                }
                return@launch
            }
            
            // 首次初始化：生成全新市民
            println("🆕 首次初始化，生成全新市民")
            val newCitizens = CitizenGenerator.generateInitialCitizens(buildings)
            _citizens.value = newCitizens
            println("🏠 为 ${buildings.size} 个建筑生成了 ${newCitizens.size} 个市民")
            
            // 启动模拟
            if (newCitizens.isNotEmpty()) {
                println("🎬 启动市民模拟系统...")
                startSimulation(buildings)
            } else {
                println("⚠️ 没有市民，无法启动模拟")
            }
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
        if (isSimulating) {
            println("⚠️ 模拟已在运行中")
            return
        }
        isSimulating = true
        println("✅ 市民模拟系统已启动！建筑数量: ${buildings.size}")
        
        viewModelScope.launch {
            var updateCount = 0
            var buildingList = buildings
            
            while (isSimulating) {
                // 优化：市民模拟频率 3 秒（让市民有明显移动）
                delay(3000)
                
                updateCount++
                val currentTime = Date()
                
                // 优化：更新50%的市民（让城市看起来更有活力）
                val updatePercentage = 0.5f
                val citizensToUpdate = _citizens.value.shuffled().take((_citizens.value.size * updatePercentage).toInt().coerceAtLeast(5))
                
                if (updateCount % 10 == 0) {
                    println("🔄 [更新 #$updateCount] 正在更新 ${citizensToUpdate.size}/${_citizens.value.size} 个市民...")
                }
                
                val updatedCitizens = _citizens.value.map { citizen ->
                    if (citizen in citizensToUpdate) {
                        val updated = simulationEngine.updateCitizen(citizen, currentTime, buildingList)
                        // 每100次更新输出一次移动日志
                        if (updateCount % 100 == 0 && citizen.id == _citizens.value.first().id) {
                            println("👤 市民 ${citizen.name}: (${citizen.currentX},${citizen.currentY}) -> (${updated.currentX},${updated.currentY}), 活动: ${updated.currentActivity}")
                        }
                        updated
                    } else {
                        citizen
                    }
                }
                _citizens.value = updatedCitizens
                
                // 优化：完全禁用想法生成（性能瓶颈）
                // _citizenThoughts.value = emptyMap()
                
                // 更新选中的市民
                _selectedCitizen.value?.let { selected ->
                    _selectedCitizen.value = updatedCitizens.find { it.id == selected.id }
                }
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
     * 获取或生成市民的固定人格
     * 如果市民已有人格则返回，否则生成新的并缓存
     */
    fun getOrGeneratePersonality(citizenId: String): PersonalityTraits {
        // 先检查缓存
        _citizenPersonalities.value[citizenId]?.let { return it }
        
        // 生成新人格并缓存
        val newPersonality = PersonalityTraits.generateRandom()
        _citizenPersonalities.value = _citizenPersonalities.value + (citizenId to newPersonality)
        
        println("🎭 为市民 $citizenId 生成新人格: ${newPersonality.getDominantPersonalityType().getDisplayName()}")
        return newPersonality
    }
    
    /**
     * 更新市民人格（用于重大事件影响）
     */
    fun updatePersonality(citizenId: String, newPersonality: PersonalityTraits) {
        _citizenPersonalities.value = _citizenPersonalities.value + (citizenId to newPersonality)
        println("🎭 市民 $citizenId 的人格发生了变化")
    }
    
    /**
     * 获取或生成市民的需求数据
     */
    fun getOrGenerateNeeds(citizenId: String, citizen: Citizen): MaslowNeeds {
        _citizenNeeds.value[citizenId]?.let { return it }
        
        // 使用默认构造函数暂时简化（未来可以扩展）
        val needs = MaslowNeeds()
        
        _citizenNeeds.value = _citizenNeeds.value + (citizenId to needs)
        println("🎯 为市民 $citizenId 生成需求数据")
        return needs
    }
    
    /**
     * 获取或生成市民的记忆
     */
    fun getOrGenerateMemories(citizenId: String, citizen: Citizen): CitizenMemoryCollection {
        _citizenMemories.value[citizenId]?.let { return it }
        
        val collection = CitizenMemoryCollection(citizenId)
        
        // 出生记忆
        collection.addMemory(CitizenMemory(
            citizenId = citizenId,
            timestamp = citizen.birthDate,
            eventType = MemoryEventType.BIRTH,
            title = "来到这个世界",
            description = "在这座城市中诞生，开始了人生旅程",
            emotionalImpact = 0.8f,
            importance = MemoryImportance.MAJOR
        ))
        
        // 工作记忆
        if (citizen.occupation != null) {
            collection.addMemory(CitizenMemory(
                citizenId = citizenId,
                timestamp = java.util.Date(java.util.Date().time - 180L * 24 * 60 * 60 * 1000),
                eventType = MemoryEventType.FIRST_JOB,
                title = "开始职业生涯",
                description = "成为了${citizen.occupation}，踏上职业道路",
                emotionalImpact = 0.7f,
                importance = MemoryImportance.SIGNIFICANT
            ))
        }
        
        // 婚姻记忆
        if (citizen.maritalStatus == MaritalStatus.MARRIED) {
            collection.addMemory(CitizenMemory(
                citizenId = citizenId,
                timestamp = java.util.Date(java.util.Date().time - 365L * 24 * 60 * 60 * 1000),
                eventType = MemoryEventType.FELL_IN_LOVE,
                title = "步入婚姻殿堂",
                description = "与心爱的人结为伴侣，开始新的生活",
                emotionalImpact = 0.9f,
                importance = MemoryImportance.MAJOR
            ))
        }
        
        // 负面记忆（如果抱怨多）
        if (citizen.complaints > 3) {
            collection.addMemory(CitizenMemory(
                citizenId = citizenId,
                timestamp = java.util.Date(java.util.Date().time - 30L * 24 * 60 * 60 * 1000),
                eventType = MemoryEventType.LOST_FRIEND,
                title = "对城市管理感到失望",
                description = "多次反映问题但未得到满意的答复",
                emotionalImpact = -0.6f,
                importance = MemoryImportance.SIGNIFICANT
            ))
        }
        
        _citizenMemories.value = _citizenMemories.value + (citizenId to collection)
        println("📖 为市民 $citizenId 生成${collection.memories.size}条记忆")
        return collection
    }
    
    // ================== 市长建议系统 ==================
    
    /**
     * 市民对市长的信任度缓存
     */
    private val _citizenTrust = MutableStateFlow<Map<String, Float>>(emptyMap())
    val citizenTrust: StateFlow<Map<String, Float>> = _citizenTrust.asStateFlow()
    
    /**
     * 获取市民对市长的信任度
     */
    fun getCitizenTrust(citizenId: String): Float {
        return _citizenTrust.value[citizenId] ?: 0.5f // 默认50%信任度
    }
    
    /**
     * 更新市民对市长的信任度
     */
    fun updateCitizenTrust(citizenId: String, newTrust: Float) {
        _citizenTrust.value = _citizenTrust.value + (citizenId to newTrust.coerceIn(0f, 1f))
    }
    
    // ================== 社交网络系统 ==================
    
    /**
     * 获取或生成市民的社交网络
     */
    fun getOrGenerateSocialNetwork(citizenId: String): SocialNetwork {
        _socialNetworks.value[citizenId]?.let { return it }
        
        val network = SocialNetwork(citizenId)
        val citizen = _citizens.value.find { it.id == citizenId } ?: return network
        val allCitizens = _citizens.value
        
        // 找同事（工作地点相同）
        if (citizen.workplaceX != null && citizen.workplaceY != null) {
            val colleagues = allCitizens.filter { 
                it.id != citizenId && 
                it.workplaceX == citizen.workplaceX && 
                it.workplaceY == citizen.workplaceY 
            }.take(2)
            
            colleagues.forEach { colleague ->
                network.addRelationship(SocialRelationship(
                    citizen1Id = citizenId,
                    citizen2Id = colleague.id,
                    relationshipType = RelationshipType.COLLEAGUE,
                    intimacy = 0.4f + kotlin.random.Random.nextFloat() * 0.3f,
                    trust = 0.5f + kotlin.random.Random.nextFloat() * 0.3f,
                    status = RelationshipStatus.ACTIVE
                ))
            }
        }
        
        // 找邻居（住址相近）
        val neighbors = allCitizens.filter {
            it.id != citizenId &&
            kotlin.math.abs(it.homeX - citizen.homeX) <= 1 &&
            kotlin.math.abs(it.homeY - citizen.homeY) <= 1
        }.take(2)
        
        neighbors.forEach { neighbor ->
            network.addRelationship(SocialRelationship(
                citizen1Id = citizenId,
                citizen2Id = neighbor.id,
                relationshipType = RelationshipType.NEIGHBOR,
                intimacy = 0.3f + kotlin.random.Random.nextFloat() * 0.4f,
                trust = 0.4f + kotlin.random.Random.nextFloat() * 0.4f,
                status = RelationshipStatus.ACTIVE
            ))
        }
        
        // 如果已婚，尝试找配偶
        if (citizen.maritalStatus == MaritalStatus.MARRIED && citizen.familyId != null) {
            val spouse = allCitizens.find { 
                it.id != citizenId && 
                it.familyId == citizen.familyId && 
                it.maritalStatus == MaritalStatus.MARRIED 
            }
            
            if (spouse != null) {
                network.addRelationship(SocialRelationship(
                    citizen1Id = citizenId,
                    citizen2Id = spouse.id,
                    relationshipType = RelationshipType.SPOUSE,
                    intimacy = 0.9f + kotlin.random.Random.nextFloat() * 0.1f,
                    trust = 0.85f + kotlin.random.Random.nextFloat() * 0.15f,
                    status = RelationshipStatus.ACTIVE
                ))
            }
        }
        
        _socialNetworks.value = _socialNetworks.value + (citizenId to network)
        println("👥 为市民 $citizenId 生成${network.relationships.size}个社交关系")
        return network
    }
    
    /**
     * 获取市民相关的八卦（动态生成，反映城市状态）
     */
    fun getCitizenGossips(citizenId: String, cityHappiness: Float): List<com.citysimulator.game.ai.Gossip> {
        val citizen = _citizens.value.find { it.id == citizenId } ?: return emptyList()
        return com.citysimulator.game.ai.generateGossipsForCitizen(
            citizenId, _citizens.value, cityHappiness
        )
    }
    
    /**
     * 获取市民参与的事件（动态生成，反映城市状态）
     */
    fun getCitizenEvents(citizenId: String, cityHappiness: Float): List<com.citysimulator.game.ai.SpontaneousEvent> {
        val citizen = _citizens.value.find { it.id == citizenId } ?: return emptyList()
        return com.citysimulator.game.ai.getEventsForCitizen(
            citizenId, _citizens.value, cityHappiness
        )
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

