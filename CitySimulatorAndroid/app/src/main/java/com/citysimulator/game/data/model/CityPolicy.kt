package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 城市政策数据模型
 * 
 * 代表城市中的长期政策，影响城市发展方向
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "city_policies")
data class CityPolicy(
    @PrimaryKey
    val id: String,
    
    // 政策名称
    val name: String,
    
    // 政策描述
    val description: String,
    
    // 政策类型
    val type: PolicyType,
    
    // 政策等级
    val level: PolicyLevel,
    
    // 实施成本
    val implementationCost: Int,
    
    // 维护成本（每月）
    val monthlyCost: Int,
    
    // 政策效果
    val effects: PolicyEffect,
    
    // 前置条件
    val prerequisites: List<String>,
    
    // 是否已实施
    val isImplemented: Boolean = false,
    
    // 实施时间
    val implementedAt: Date? = null,
    
    // 政策持续时间（月）
    val duration: Int = 12,
    
    // 是否可撤销
    val isReversible: Boolean = true
)

/**
 * 政策类型枚举
 */
enum class PolicyType {
    ECONOMIC,       // 经济政策
    SOCIAL,         // 社会政策
    ENVIRONMENTAL,  // 环境政策
    INFRASTRUCTURE, // 基础设施政策
    SECURITY,       // 安全政策
    CULTURAL        // 文化政策
}

/**
 * 政策等级枚举
 */
enum class PolicyLevel {
    BASIC,          // 基础
    INTERMEDIATE,   // 中级
    ADVANCED,       // 高级
    EXPERT          // 专家级
}

/**
 * 政策效果数据类
 */
data class PolicyEffect(
    val goldMultiplier: Float = 1.0f,
    val populationMultiplier: Float = 1.0f,
    val prosperityBonus: Float = 0f,
    val buildingEfficiencyBonus: Float = 0f,
    val happinessBonus: Float = 0f,
    val pollutionReduction: Float = 0f,
    val crimeReduction: Float = 0f,
    val educationBonus: Float = 0f,
    val healthBonus: Float = 0f,
    val tourismBonus: Float = 0f,
    val specialEffects: List<String> = emptyList()
)

/**
 * 城市政策管理器
 */
object CityPolicyManager {
    
    /**
     * 获取所有政策
     */
    fun getAllPolicies(): List<CityPolicy> {
        return listOf(
            // 经济政策
            CityPolicy(
                id = "tax_incentive",
                name = "税收优惠政策",
                description = "为特定行业提供税收减免，促进经济发展",
                type = PolicyType.ECONOMIC,
                level = PolicyLevel.BASIC,
                implementationCost = 1000,
                monthlyCost = 200,
                effects = PolicyEffect(
                    goldMultiplier = 1.2f,
                    prosperityBonus = 5f,
                    specialEffects = listOf("工业建筑收入+20%")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "free_trade_zone",
                name = "自由贸易区",
                description = "建立自由贸易区，吸引外资投资",
                type = PolicyType.ECONOMIC,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 3000,
                monthlyCost = 500,
                effects = PolicyEffect(
                    goldMultiplier = 1.5f,
                    prosperityBonus = 15f,
                    tourismBonus = 10f,
                    specialEffects = listOf("商业建筑收入+50%", "吸引外资")
                ),
                prerequisites = listOf("tax_incentive")
            ),
            CityPolicy(
                id = "innovation_hub",
                name = "创新中心",
                description = "建设科技创新中心，推动高新技术发展",
                type = PolicyType.ECONOMIC,
                level = PolicyLevel.ADVANCED,
                implementationCost = 5000,
                monthlyCost = 800,
                effects = PolicyEffect(
                    goldMultiplier = 1.8f,
                    prosperityBonus = 25f,
                    buildingEfficiencyBonus = 0.2f,
                    specialEffects = listOf("科技建筑效率+20%", "解锁高级科技")
                ),
                prerequisites = listOf("free_trade_zone")
            ),
            
            // 社会政策
            CityPolicy(
                id = "universal_healthcare",
                name = "全民医疗",
                description = "建立全民医疗保障体系，提升市民健康水平",
                type = PolicyType.SOCIAL,
                level = PolicyLevel.BASIC,
                implementationCost = 2000,
                monthlyCost = 400,
                effects = PolicyEffect(
                    happinessBonus = 15f,
                    healthBonus = 20f,
                    prosperityBonus = 10f,
                    specialEffects = listOf("减少疾病", "提升寿命")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "education_reform",
                name = "教育改革",
                description = "全面改革教育体系，提升教育质量",
                type = PolicyType.SOCIAL,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 2500,
                monthlyCost = 300,
                effects = PolicyEffect(
                    educationBonus = 25f,
                    happinessBonus = 10f,
                    prosperityBonus = 15f,
                    specialEffects = listOf("提升人才质量", "减少犯罪")
                ),
                prerequisites = listOf("universal_healthcare")
            ),
            CityPolicy(
                id = "social_welfare",
                name = "社会福利",
                description = "建立完善的社会福利体系，保障市民基本生活",
                type = PolicyType.SOCIAL,
                level = PolicyLevel.ADVANCED,
                implementationCost = 4000,
                monthlyCost = 600,
                effects = PolicyEffect(
                    happinessBonus = 30f,
                    prosperityBonus = 20f,
                    crimeReduction = 15f,
                    specialEffects = listOf("减少贫困", "社会稳定")
                ),
                prerequisites = listOf("education_reform")
            ),
            
            // 环境政策
            CityPolicy(
                id = "green_initiative",
                name = "绿色倡议",
                description = "推广绿色建筑和清洁能源，保护环境",
                type = PolicyType.ENVIRONMENTAL,
                level = PolicyLevel.BASIC,
                implementationCost = 1500,
                monthlyCost = 200,
                effects = PolicyEffect(
                    pollutionReduction = 20f,
                    prosperityBonus = 8f,
                    happinessBonus = 5f,
                    specialEffects = listOf("减少污染", "提升环境质量")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "carbon_neutral",
                name = "碳中和计划",
                description = "实施碳中和计划，实现零碳排放",
                type = PolicyType.ENVIRONMENTAL,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 3500,
                monthlyCost = 400,
                effects = PolicyEffect(
                    pollutionReduction = 40f,
                    prosperityBonus = 20f,
                    happinessBonus = 15f,
                    specialEffects = listOf("零碳排放", "国际声誉")
                ),
                prerequisites = listOf("green_initiative")
            ),
            CityPolicy(
                id = "sustainable_city",
                name = "可持续发展城市",
                description = "建设可持续发展的智慧城市",
                type = PolicyType.ENVIRONMENTAL,
                level = PolicyLevel.ADVANCED,
                implementationCost = 6000,
                monthlyCost = 700,
                effects = PolicyEffect(
                    pollutionReduction = 60f,
                    prosperityBonus = 30f,
                    happinessBonus = 25f,
                    buildingEfficiencyBonus = 0.15f,
                    specialEffects = listOf("可持续发展", "智慧管理")
                ),
                prerequisites = listOf("carbon_neutral")
            ),
            
            // 基础设施政策
            CityPolicy(
                id = "smart_transport",
                name = "智能交通",
                description = "建设智能交通系统，提升交通效率",
                type = PolicyType.INFRASTRUCTURE,
                level = PolicyLevel.BASIC,
                implementationCost = 2000,
                monthlyCost = 300,
                effects = PolicyEffect(
                    buildingEfficiencyBonus = 0.1f,
                    prosperityBonus = 10f,
                    happinessBonus = 8f,
                    specialEffects = listOf("减少拥堵", "提升效率")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "digital_city",
                name = "数字城市",
                description = "建设数字化城市管理系统",
                type = PolicyType.INFRASTRUCTURE,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 4000,
                monthlyCost = 500,
                effects = PolicyEffect(
                    buildingEfficiencyBonus = 0.2f,
                    prosperityBonus = 20f,
                    happinessBonus = 12f,
                    specialEffects = listOf("智能管理", "数据驱动")
                ),
                prerequisites = listOf("smart_transport")
            ),
            CityPolicy(
                id = "future_city",
                name = "未来城市",
                description = "建设面向未来的智慧城市",
                type = PolicyType.INFRASTRUCTURE,
                level = PolicyLevel.ADVANCED,
                implementationCost = 8000,
                monthlyCost = 1000,
                effects = PolicyEffect(
                    buildingEfficiencyBonus = 0.3f,
                    prosperityBonus = 35f,
                    happinessBonus = 20f,
                    specialEffects = listOf("未来科技", "全球领先")
                ),
                prerequisites = listOf("digital_city")
            ),
            
            // 安全政策
            CityPolicy(
                id = "community_police",
                name = "社区警务",
                description = "建立社区警务体系，提升治安水平",
                type = PolicyType.SECURITY,
                level = PolicyLevel.BASIC,
                implementationCost = 1500,
                monthlyCost = 250,
                effects = PolicyEffect(
                    crimeReduction = 20f,
                    happinessBonus = 10f,
                    prosperityBonus = 8f,
                    specialEffects = listOf("减少犯罪", "社区和谐")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "smart_security",
                name = "智能安防",
                description = "建设智能安防系统，提升城市安全",
                type = PolicyType.SECURITY,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 3000,
                monthlyCost = 400,
                effects = PolicyEffect(
                    crimeReduction = 35f,
                    happinessBonus = 15f,
                    prosperityBonus = 15f,
                    specialEffects = listOf("智能监控", "预防犯罪")
                ),
                prerequisites = listOf("community_police")
            ),
            CityPolicy(
                id = "safe_city",
                name = "安全城市",
                description = "建设全方位安全防护体系",
                type = PolicyType.SECURITY,
                level = PolicyLevel.ADVANCED,
                implementationCost = 5000,
                monthlyCost = 600,
                effects = PolicyEffect(
                    crimeReduction = 50f,
                    happinessBonus = 25f,
                    prosperityBonus = 25f,
                    specialEffects = listOf("零犯罪", "国际安全")
                ),
                prerequisites = listOf("smart_security")
            ),
            
            // 文化政策
            CityPolicy(
                id = "cultural_heritage",
                name = "文化遗产保护",
                description = "保护城市文化遗产，传承历史文化",
                type = PolicyType.CULTURAL,
                level = PolicyLevel.BASIC,
                implementationCost = 1000,
                monthlyCost = 150,
                effects = PolicyEffect(
                    happinessBonus = 8f,
                    tourismBonus = 15f,
                    prosperityBonus = 5f,
                    specialEffects = listOf("文化传承", "吸引游客")
                ),
                prerequisites = emptyList()
            ),
            CityPolicy(
                id = "creative_city",
                name = "创意城市",
                description = "建设创意文化产业，提升城市文化软实力",
                type = PolicyType.CULTURAL,
                level = PolicyLevel.INTERMEDIATE,
                implementationCost = 2500,
                monthlyCost = 350,
                effects = PolicyEffect(
                    happinessBonus = 15f,
                    tourismBonus = 25f,
                    prosperityBonus = 18f,
                    specialEffects = listOf("创意产业", "文化输出")
                ),
                prerequisites = listOf("cultural_heritage")
            ),
            CityPolicy(
                id = "global_cultural_hub",
                name = "全球文化中心",
                description = "建设具有国际影响力的文化中心",
                type = PolicyType.CULTURAL,
                level = PolicyLevel.ADVANCED,
                implementationCost = 4500,
                monthlyCost = 550,
                effects = PolicyEffect(
                    happinessBonus = 25f,
                    tourismBonus = 40f,
                    prosperityBonus = 30f,
                    specialEffects = listOf("国际影响", "文化输出")
                ),
                prerequisites = listOf("creative_city")
            )
        )
    }
    
    /**
     * 检查政策是否可以实施
     */
    fun canImplement(policy: CityPolicy, implementedPolicies: List<String>, currentGold: Int): Boolean {
        return policy.prerequisites.all { it in implementedPolicies } && 
               currentGold >= policy.implementationCost
    }
    
    /**
     * 获取可实施的政策
     */
    fun getAvailablePolicies(implementedPolicies: List<String>, currentGold: Int): List<CityPolicy> {
        return getAllPolicies().filter { policy ->
            // 检查政策ID是否已在已实施列表中
            policy.id !in implementedPolicies && canImplement(policy, implementedPolicies, currentGold) 
        }
    }
    
    /**
     * 获取政策效果总和
     */
    fun getTotalPolicyEffects(implementedPolicies: List<CityPolicy>): PolicyEffect {
        return implementedPolicies.fold(PolicyEffect()) { acc, policy ->
            PolicyEffect(
                goldMultiplier = acc.goldMultiplier * policy.effects.goldMultiplier,
                populationMultiplier = acc.populationMultiplier * policy.effects.populationMultiplier,
                prosperityBonus = acc.prosperityBonus + policy.effects.prosperityBonus,
                buildingEfficiencyBonus = acc.buildingEfficiencyBonus + policy.effects.buildingEfficiencyBonus,
                happinessBonus = acc.happinessBonus + policy.effects.happinessBonus,
                pollutionReduction = acc.pollutionReduction + policy.effects.pollutionReduction,
                crimeReduction = acc.crimeReduction + policy.effects.crimeReduction,
                educationBonus = acc.educationBonus + policy.effects.educationBonus,
                healthBonus = acc.healthBonus + policy.effects.healthBonus,
                tourismBonus = acc.tourismBonus + policy.effects.tourismBonus,
                specialEffects = acc.specialEffects + policy.effects.specialEffects
            )
        }
    }
}

