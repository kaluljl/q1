package com.citysimulator.game.data.model

import java.util.Date

/**
 * 成就系统
 * 
 * 记录玩家的城市发展历程和重要里程碑
 */

/**
 * 成就
 * 
 * 注意：不使用Room持久化，仅在内存中管理
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val iconEmoji: String,
    val requirement: AchievementRequirement,
    val reward: AchievementReward?,
    val isUnlocked: Boolean = false,
    val unlockedAt: Date? = null,
    val progress: Float = 0f, // 0-1
    val isSecret: Boolean = false // 隐藏成就
)

/**
 * 成就分类
 */
enum class AchievementCategory {
    POPULATION,     // 人口
    ECONOMY,        // 经济
    CONSTRUCTION,   // 建设
    ENVIRONMENT,    // 环境
    SOCIAL,         // 社会
    MILESTONE,      // 里程碑
    SPECIAL         // 特殊
}

/**
 * 成就等级
 */
enum class AchievementTier {
    BRONZE,   // 青铜
    SILVER,   // 白银
    GOLD,     // 黄金
    PLATINUM, // 铂金
    DIAMOND   // 钻石
}

/**
 * 成就要求
 */
data class AchievementRequirement(
    val type: RequirementType,
    val targetValue: Float,
    val currentValue: Float = 0f,
    val additionalConditions: Map<String, Any> = emptyMap()
)

/**
 * 要求类型
 */
enum class RequirementType {
    POPULATION_REACH,           // 达到人口
    GOLD_ACCUMULATED,           // 累积金币
    BUILDINGS_BUILT,            // 建造建筑数量
    GDP_REACH,                  // GDP达到
    ZERO_UNEMPLOYMENT,          // 零失业
    PERFECT_ENVIRONMENT,        // 完美环境
    PROSPERITY_LEVEL,           // 繁荣度等级
    CITIZEN_HAPPINESS,          // 市民幸福度
    DAYS_SURVIVED,              // 生存天数
    CONTINUOUS_PROFIT,          // 连续盈利天数
    BUILDING_TYPE_COUNT,        // 特定建筑类型数量
    COMPLETE_TECH_TREE,         // 完成科技树
    ENACT_POLICIES,             // 实施政策数量
    HANDLE_CRISIS,              // 处理危机
    SPECIAL_COMBO               // 特殊组合
}

/**
 * 成就奖励
 */
data class AchievementReward(
    val gold: Int = 0,
    val specialBuilding: BuildingType? = null,
    val bonusEffect: BonusEffect? = null
)

/**
 * 奖励效果
 */
data class BonusEffect(
    val type: BonusType,
    val value: Float,
    val duration: Int = -1 // -1为永久
)

/**
 * 奖励类型
 */
enum class BonusType {
    CONSTRUCTION_SPEED,  // 建造速度
    TAX_INCOME,          // 税收收入
    RESOURCE_EFFICIENCY, // 资源效率
    CITIZEN_HAPPINESS,   // 市民幸福度
    UNLOCK_FEATURE       // 解锁功能
}

/**
 * 里程碑
 */
data class Milestone(
    val id: Long = 0,
    val timestamp: Date,
    val type: MilestoneType,
    val title: String,
    val description: String,
    val value: Float,
    val iconEmoji: String,
    val isPositive: Boolean = true
)

/**
 * 里程碑类型
 */
enum class MilestoneType {
    FIRST_BUILDING,         // 第一个建筑
    POPULATION_MILESTONE,   // 人口里程碑
    ECONOMIC_MILESTONE,     // 经济里程碑
    CITY_ESTABLISHED,       // 城市建立
    GOLDEN_AGE,             // 黄金时代
    CRISIS_SURVIVED,        // 度过危机
    MAJOR_CONSTRUCTION,     // 重大建设
    POLICY_CHANGE,          // 政策变革
    DISASTER_STRUCK,        // 遭遇灾难
    ACHIEVEMENT_UNLOCKED    // 成就解锁
}

/**
 * 统计数据
 */
data class CityStatistics(
    val id: String = "main",
    val foundedDate: Date,
    val totalPlayTime: Long = 0, // 总游戏时间（秒）
    val totalBuildings: Int = 0,
    val totalDemolished: Int = 0,
    val highestPopulation: Int = 0,
    val highestGDP: Int = 0,
    val highestProsperity: Float = 0f,
    val totalGoldEarned: Long = 0,
    val totalGoldSpent: Long = 0,
    val totalTaxCollected: Long = 0,
    val citizensBorn: Int = 0,
    val citizensDied: Int = 0,
    val businessesCreated: Int = 0,
    val businessesBankrupt: Int = 0,
    val policiesEnacted: Int = 0,
    val technologiesResearched: Int = 0,
    val crisisHandled: Int = 0,
    val achievementsUnlocked: Int = 0,
    val longestProfitStreak: Int = 0,
    val longestDeficitStreak: Int = 0,
    val perfectDays: Int = 0, // 完美运转天数
    val lastUpdateTime: Date = Date()
)

/**
 * 预定义成就列表
 */
object PredefinedAchievements {
    val ALL_ACHIEVEMENTS = listOf(
        // 人口成就
        Achievement(
            id = "pop_100",
            name = "初具规模",
            description = "城市人口达到100人",
            category = AchievementCategory.POPULATION,
            tier = AchievementTier.BRONZE,
            iconEmoji = "👥",
            requirement = AchievementRequirement(RequirementType.POPULATION_REACH, 100f),
            reward = AchievementReward(gold = 1000)
        ),
        Achievement(
            id = "pop_500",
            name = "小镇",
            description = "城市人口达到500人",
            category = AchievementCategory.POPULATION,
            tier = AchievementTier.SILVER,
            iconEmoji = "🏘️",
            requirement = AchievementRequirement(RequirementType.POPULATION_REACH, 500f),
            reward = AchievementReward(gold = 5000)
        ),
        Achievement(
            id = "pop_1000",
            name = "繁荣之城",
            description = "城市人口达到1000人",
            category = AchievementCategory.POPULATION,
            tier = AchievementTier.GOLD,
            iconEmoji = "🏙️",
            requirement = AchievementRequirement(RequirementType.POPULATION_REACH, 1000f),
            reward = AchievementReward(gold = 10000)
        ),
        Achievement(
            id = "pop_5000",
            name = "大都会",
            description = "城市人口达到5000人",
            category = AchievementCategory.POPULATION,
            tier = AchievementTier.PLATINUM,
            iconEmoji = "🌆",
            requirement = AchievementRequirement(RequirementType.POPULATION_REACH, 5000f),
            reward = AchievementReward(
                gold = 50000,
                specialBuilding = BuildingType.SPACE_CENTER
            )
        ),
        
        // 经济成就
        Achievement(
            id = "gold_10k",
            name = "小康生活",
            description = "累积获得10,000金币",
            category = AchievementCategory.ECONOMY,
            tier = AchievementTier.BRONZE,
            iconEmoji = "💰",
            requirement = AchievementRequirement(RequirementType.GOLD_ACCUMULATED, 10000f),
            reward = AchievementReward(gold = 2000)
        ),
        Achievement(
            id = "gold_100k",
            name = "财富自由",
            description = "累积获得100,000金币",
            category = AchievementCategory.ECONOMY,
            tier = AchievementTier.GOLD,
            iconEmoji = "💎",
            requirement = AchievementRequirement(RequirementType.GOLD_ACCUMULATED, 100000f),
            reward = AchievementReward(
                gold = 20000,
                bonusEffect = BonusEffect(BonusType.TAX_INCOME, 1.1f, -1)
            )
        ),
        Achievement(
            id = "zero_unemployment",
            name = "充分就业",
            description = "达到零失业率",
            category = AchievementCategory.ECONOMY,
            tier = AchievementTier.GOLD,
            iconEmoji = "👷",
            requirement = AchievementRequirement(RequirementType.ZERO_UNEMPLOYMENT, 1f),
            reward = AchievementReward(gold = 15000)
        ),
        
        // 建设成就
        Achievement(
            id = "build_10",
            name = "建筑师学徒",
            description = "建造10座建筑",
            category = AchievementCategory.CONSTRUCTION,
            tier = AchievementTier.BRONZE,
            iconEmoji = "🏗️",
            requirement = AchievementRequirement(RequirementType.BUILDINGS_BUILT, 10f),
            reward = AchievementReward(gold = 1000)
        ),
        Achievement(
            id = "build_50",
            name = "城市规划师",
            description = "建造50座建筑",
            category = AchievementCategory.CONSTRUCTION,
            tier = AchievementTier.SILVER,
            iconEmoji = "🏛️",
            requirement = AchievementRequirement(RequirementType.BUILDINGS_BUILT, 50f),
            reward = AchievementReward(
                gold = 5000,
                bonusEffect = BonusEffect(BonusType.CONSTRUCTION_SPEED, 1.2f, -1)
            )
        ),
        Achievement(
            id = "build_100",
            name = "都市建造师",
            description = "建造100座建筑",
            category = AchievementCategory.CONSTRUCTION,
            tier = AchievementTier.GOLD,
            iconEmoji = "🏰",
            requirement = AchievementRequirement(RequirementType.BUILDINGS_BUILT, 100f),
            reward = AchievementReward(gold = 10000)
        ),
        
        // 环境成就
        Achievement(
            id = "green_city",
            name = "绿色城市",
            description = "建造10个公园",
            category = AchievementCategory.ENVIRONMENT,
            tier = AchievementTier.SILVER,
            iconEmoji = "🌳",
            requirement = AchievementRequirement(
                RequirementType.BUILDING_TYPE_COUNT,
                10f,
                additionalConditions = mapOf("buildingType" to BuildingType.PARK)
            ),
            reward = AchievementReward(
                gold = 5000,
                bonusEffect = BonusEffect(BonusType.CITIZEN_HAPPINESS, 1.1f, -1)
            )
        ),
        Achievement(
            id = "eco_warrior",
            name = "生态卫士",
            description = "建造5个回收中心",
            category = AchievementCategory.ENVIRONMENT,
            tier = AchievementTier.GOLD,
            iconEmoji = "♻️",
            requirement = AchievementRequirement(
                RequirementType.BUILDING_TYPE_COUNT,
                5f,
                additionalConditions = mapOf("buildingType" to BuildingType.RECYCLING_CENTER)
            ),
            reward = AchievementReward(
                gold = 8000,
                bonusEffect = BonusEffect(BonusType.RESOURCE_EFFICIENCY, 1.15f, -1)
            )
        ),
        
        // 社会成就
        Achievement(
            id = "happy_city",
            name = "幸福之城",
            description = "市民平均幸福度达到80%",
            category = AchievementCategory.SOCIAL,
            tier = AchievementTier.GOLD,
            iconEmoji = "😊",
            requirement = AchievementRequirement(RequirementType.CITIZEN_HAPPINESS, 80f),
            reward = AchievementReward(gold = 12000)
        ),
        
        // 里程碑成就
        Achievement(
            id = "first_step",
            name = "起步",
            description = "建造第一座建筑",
            category = AchievementCategory.MILESTONE,
            tier = AchievementTier.BRONZE,
            iconEmoji = "🎯",
            requirement = AchievementRequirement(RequirementType.BUILDINGS_BUILT, 1f),
            reward = AchievementReward(gold = 500)
        ),
        Achievement(
            id = "survive_30_days",
            name = "稳步发展",
            description = "生存30天",
            category = AchievementCategory.MILESTONE,
            tier = AchievementTier.SILVER,
            iconEmoji = "📅",
            requirement = AchievementRequirement(RequirementType.DAYS_SURVIVED, 30f),
            reward = AchievementReward(gold = 3000)
        ),
        Achievement(
            id = "survive_100_days",
            name = "百日筑城",
            description = "生存100天",
            category = AchievementCategory.MILESTONE,
            tier = AchievementTier.GOLD,
            iconEmoji = "🏆",
            requirement = AchievementRequirement(RequirementType.DAYS_SURVIVED, 100f),
            reward = AchievementReward(gold = 10000)
        ),
        
        // 特殊成就
        Achievement(
            id = "night_owl",
            name = "夜猫子",
            description = "在凌晨2-4点游玩超过1小时",
            category = AchievementCategory.SPECIAL,
            tier = AchievementTier.BRONZE,
            iconEmoji = "🦉",
            requirement = AchievementRequirement(RequirementType.SPECIAL_COMBO, 1f),
            reward = AchievementReward(gold = 1000),
            isSecret = true
        ),
        Achievement(
            id = "perfectionist",
            name = "完美主义者",
            description = "连续10天城市运转完美",
            category = AchievementCategory.SPECIAL,
            tier = AchievementTier.DIAMOND,
            iconEmoji = "✨",
            requirement = AchievementRequirement(RequirementType.SPECIAL_COMBO, 10f),
            reward = AchievementReward(
                gold = 50000,
                specialBuilding = BuildingType.AI_CENTER
            ),
            isSecret = true
        )
    )
}

