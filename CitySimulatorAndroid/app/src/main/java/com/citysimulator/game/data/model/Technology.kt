package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 科技树数据模型
 * 
 * 代表城市中的科技研究项目
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "technologies")
data class Technology(
    @PrimaryKey
    val id: String,
    
    // 科技名称
    val name: String,
    
    // 科技描述
    val description: String,
    
    // 科技类型
    val type: TechType,
    
    // 研究成本
    val researchCost: Int,
    
    // 研究时间（分钟）
    val researchTime: Int,
    
    // 前置科技
    val prerequisites: List<String>,
    
    // 解锁的建筑类型
    val unlockedBuildings: List<BuildingType>,
    
    // 效果
    val effects: TechEffect,
    
    // 是否已研究
    val isResearched: Boolean = false,
    
    // 研究开始时间
    val researchStartTime: Date? = null,
    
    // 研究完成时间
    val researchCompleteTime: Date? = null
)

/**
 * 科技类型枚举
 */
enum class TechType {
    INFRASTRUCTURE,  // 基础设施
    INDUSTRY,        // 工业
    ENVIRONMENT,     // 环境
    SOCIAL,          // 社会
    ADVANCED         // 高级科技
}

/**
 * 科技效果数据类
 */
data class TechEffect(
    val goldMultiplier: Float = 1.0f,
    val populationMultiplier: Float = 1.0f,
    val prosperityBonus: Float = 0f,
    val buildingEfficiencyBonus: Float = 0f,
    val specialAbilities: List<String> = emptyList()
)

/**
 * 科技树管理器
 */
object TechTreeManager {
    
    /**
     * 获取所有科技
     */
    fun getAllTechnologies(): List<Technology> {
        return listOf(
            // 基础设施科技
            Technology(
                id = "basic_roads",
                name = "基础道路",
                description = "改善城市交通，提升建筑效率",
                type = TechType.INFRASTRUCTURE,
                researchCost = 500,
                researchTime = 5,
                prerequisites = emptyList(),
                unlockedBuildings = emptyList(),
                effects = TechEffect(
                    buildingEfficiencyBonus = 0.1f,
                    prosperityBonus = 5f
                )
            ),
            Technology(
                id = "advanced_roads",
                name = "高级道路",
                description = "建设高速公路网络，大幅提升交通效率",
                type = TechType.INFRASTRUCTURE,
                researchCost = 1500,
                researchTime = 10,
                prerequisites = listOf("basic_roads"),
                unlockedBuildings = emptyList(),
                effects = TechEffect(
                    buildingEfficiencyBonus = 0.2f,
                    prosperityBonus = 10f
                )
            ),
            Technology(
                id = "public_transport",
                name = "公共交通",
                description = "建设公交和地铁系统，减少交通拥堵",
                type = TechType.INFRASTRUCTURE,
                researchCost = 2000,
                researchTime = 15,
                prerequisites = listOf("advanced_roads"),
                unlockedBuildings = listOf(BuildingType.SUBWAY_STATION),
                effects = TechEffect(
                    populationMultiplier = 1.2f,
                    prosperityBonus = 15f
                )
            ),
            
            // 工业科技
            Technology(
                id = "basic_manufacturing",
                name = "基础制造业",
                description = "提升工厂生产效率，增加收入",
                type = TechType.INDUSTRY,
                researchCost = 800,
                researchTime = 8,
                prerequisites = emptyList(),
                unlockedBuildings = listOf(BuildingType.FACTORY),
                effects = TechEffect(
                    goldMultiplier = 1.3f,
                    buildingEfficiencyBonus = 0.15f
                )
            ),
            Technology(
                id = "automation",
                name = "自动化技术",
                description = "引入自动化设备，大幅提升生产效率",
                type = TechType.INDUSTRY,
                researchCost = 2500,
                researchTime = 20,
                prerequisites = listOf("basic_manufacturing"),
                unlockedBuildings = emptyList(),
                effects = TechEffect(
                    goldMultiplier = 1.5f,
                    buildingEfficiencyBonus = 0.3f
                )
            ),
            Technology(
                id = "renewable_energy",
                name = "可再生能源",
                description = "发展太阳能和风能，减少污染",
                type = TechType.INDUSTRY,
                researchCost = 3000,
                researchTime = 25,
                prerequisites = listOf("automation"),
                unlockedBuildings = listOf(BuildingType.SOLAR_PLANT, BuildingType.WIND_FARM),
                effects = TechEffect(
                    prosperityBonus = 20f,
                    specialAbilities = listOf("减少污染", "降低维护成本")
                )
            ),
            
            // 环境科技
            Technology(
                id = "waste_management",
                name = "废物管理",
                description = "建立完善的废物处理系统",
                type = TechType.ENVIRONMENT,
                researchCost = 1000,
                researchTime = 12,
                prerequisites = emptyList(),
                unlockedBuildings = listOf(BuildingType.RECYCLING_CENTER),
                effects = TechEffect(
                    prosperityBonus = 8f,
                    specialAbilities = listOf("减少污染")
                )
            ),
            Technology(
                id = "green_building",
                name = "绿色建筑",
                description = "推广环保建筑技术，提升城市环境",
                type = TechType.ENVIRONMENT,
                researchCost = 1800,
                researchTime = 18,
                prerequisites = listOf("waste_management"),
                unlockedBuildings = emptyList(),
                effects = TechEffect(
                    prosperityBonus = 12f,
                    buildingEfficiencyBonus = 0.1f,
                    specialAbilities = listOf("减少污染", "降低能耗")
                )
            ),
            Technology(
                id = "smart_city",
                name = "智慧城市",
                description = "建设智能城市管理系统，提升整体效率",
                type = TechType.ENVIRONMENT,
                researchCost = 4000,
                researchTime = 30,
                prerequisites = listOf("green_building", "renewable_energy"),
                unlockedBuildings = listOf(BuildingType.SMART_CENTER),
                effects = TechEffect(
                    goldMultiplier = 1.4f,
                    populationMultiplier = 1.3f,
                    prosperityBonus = 25f,
                    buildingEfficiencyBonus = 0.25f,
                    specialAbilities = listOf("智能管理", "自动优化")
                )
            ),
            
            // 社会科技
            Technology(
                id = "education_system",
                name = "教育体系",
                description = "建立完善的教育系统，提升市民素质",
                type = TechType.SOCIAL,
                researchCost = 1200,
                researchTime = 15,
                prerequisites = emptyList(),
                unlockedBuildings = listOf(BuildingType.UNIVERSITY),
                effects = TechEffect(
                    populationMultiplier = 1.2f,
                    prosperityBonus = 10f
                )
            ),
            Technology(
                id = "healthcare_system",
                name = "医疗体系",
                description = "建设现代化医疗系统，提升市民健康",
                type = TechType.SOCIAL,
                researchCost = 1500,
                researchTime = 18,
                prerequisites = listOf("education_system"),
                unlockedBuildings = listOf(BuildingType.HOSPITAL),
                effects = TechEffect(
                    populationMultiplier = 1.15f,
                    prosperityBonus = 12f,
                    specialAbilities = listOf("提升健康", "减少疾病")
                )
            ),
            Technology(
                id = "cultural_center",
                name = "文化中心",
                description = "建设文化设施，丰富市民精神生活",
                type = TechType.SOCIAL,
                researchCost = 2000,
                researchTime = 20,
                prerequisites = listOf("healthcare_system"),
                unlockedBuildings = listOf(BuildingType.MUSEUM, BuildingType.THEATER),
                effects = TechEffect(
                    prosperityBonus = 15f,
                    specialAbilities = listOf("提升文化", "增加旅游")
                )
            ),
            
            // 高级科技
            Technology(
                id = "space_program",
                name = "太空计划",
                description = "发展太空技术，提升城市科技水平",
                type = TechType.ADVANCED,
                researchCost = 5000,
                researchTime = 40,
                prerequisites = listOf("smart_city", "cultural_center"),
                unlockedBuildings = listOf(BuildingType.SPACE_CENTER),
                effects = TechEffect(
                    goldMultiplier = 1.6f,
                    populationMultiplier = 1.4f,
                    prosperityBonus = 30f,
                    buildingEfficiencyBonus = 0.3f,
                    specialAbilities = listOf("太空技术", "国际声誉")
                )
            ),
            Technology(
                id = "ai_governance",
                name = "AI治理",
                description = "引入人工智能进行城市管理",
                type = TechType.ADVANCED,
                researchCost = 6000,
                researchTime = 45,
                prerequisites = listOf("space_program"),
                unlockedBuildings = listOf(BuildingType.AI_CENTER),
                effects = TechEffect(
                    goldMultiplier = 1.8f,
                    populationMultiplier = 1.5f,
                    prosperityBonus = 35f,
                    buildingEfficiencyBonus = 0.4f,
                    specialAbilities = listOf("AI管理", "预测分析", "自动优化")
                )
            )
        )
    }
    
    /**
     * 检查科技是否可以研究
     */
    fun canResearch(technology: Technology, researchedTechs: List<String>): Boolean {
        return technology.prerequisites.all { it in researchedTechs }
    }
    
    /**
     * 获取可研究的科技
     */
    fun getAvailableTechnologies(researchedTechs: List<String>): List<Technology> {
        return getAllTechnologies().filter { 
            !it.isResearched && canResearch(it, researchedTechs) 
        }
    }
    
    /**
     * 获取科技树层级
     */
    fun getTechTreeLevels(): Map<Int, List<Technology>> {
        val technologies = getAllTechnologies()
        val levels = mutableMapOf<Int, MutableList<Technology>>()
        
        technologies.forEach { tech ->
            val level = tech.prerequisites.size
            levels.getOrPut(level) { mutableListOf() }.add(tech)
        }
        
        return levels
    }
}
