package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlin.math.max
import kotlin.math.min

/**
 * 连锁反应引擎
 * 
 * 实现决策的多维度影响，让每个决策都会产生连锁反应
 * 例如：建造工厂 → 污染增加 → 健康下降 → 医疗需求增加 → 财政支出增加
 */
class ChainReactionEngine {
    
    /**
     * 计算决策的连锁反应
     */
    fun calculateDecisionImpact(
        decision: Decision,
        currentState: CityState
    ): DecisionImpact {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        
        when (decision.type) {
            DecisionType.BUILD_STRUCTURE -> {
                impacts.putAll(calculateBuildingImpact(decision, currentState))
            }
            DecisionType.DEMOLISH_STRUCTURE -> {
                impacts.putAll(calculateDemolishImpact(decision, currentState))
            }
            DecisionType.ADJUST_TAX -> {
                impacts.putAll(calculateTaxImpact(decision, currentState))
            }
            DecisionType.ENACT_POLICY -> {
                impacts.putAll(calculatePolicyImpact(decision, currentState))
            }
            DecisionType.UPGRADE_INFRASTRUCTURE -> {
                impacts.putAll(calculateUpgradeImpact(decision, currentState))
            }
        }
        
        return DecisionImpact(
            decision = decision,
            immediateImpacts = impacts,
            chainReactions = calculateChainReactions(impacts, currentState),
            longTermEffects = calculateLongTermEffects(impacts, currentState)
        )
    }
    
    /**
     * 建造建筑的影响
     */
    private fun calculateBuildingImpact(
        decision: Decision,
        state: CityState
    ): Map<ImpactCategory, List<Impact>> {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        val buildingType = decision.targetBuildingType ?: return impacts
        
        // 经济影响
        val economicImpacts = mutableListOf<Impact>()
        when (buildingType) {
            BuildingType.FACTORY, BuildingType.STEEL_MILL -> {
                economicImpacts.add(Impact("就业机会", 50f, ImpactSeverity.HIGH, "创造50个就业岗位"))
                economicImpacts.add(Impact("GDP增长", 30f, ImpactSeverity.MEDIUM, "工业产值提升"))
                economicImpacts.add(Impact("财政收入", 20f, ImpactSeverity.MEDIUM, "企业税收增加"))
            }
            BuildingType.SHOP, BuildingType.MALL, BuildingType.SUPERMARKET -> {
                economicImpacts.add(Impact("就业机会", 30f, ImpactSeverity.MEDIUM, "创造商业岗位"))
                economicImpacts.add(Impact("消费便利", 40f, ImpactSeverity.HIGH, "提升市民购物便利性"))
                economicImpacts.add(Impact("商业活力", 35f, ImpactSeverity.HIGH, "增强商业氛围"))
            }
            BuildingType.HOUSE, BuildingType.APARTMENT, BuildingType.SKYSCRAPER -> {
                economicImpacts.add(Impact("人口容量", 100f, ImpactSeverity.HIGH, "增加住房供应"))
                economicImpacts.add(Impact("房地产税", 15f, ImpactSeverity.LOW, "房产税收入"))
            }
            else -> {}
        }
        impacts[ImpactCategory.ECONOMIC] = economicImpacts
        
        // 环境影响
        val environmentalImpacts = mutableListOf<Impact>()
        when (buildingType) {
            BuildingType.FACTORY, BuildingType.STEEL_MILL -> {
                environmentalImpacts.add(Impact("空气污染", -40f, ImpactSeverity.HIGH, "工业排放导致空气质量下降"))
                environmentalImpacts.add(Impact("噪音污染", -30f, ImpactSeverity.MEDIUM, "机器运转产生噪音"))
                environmentalImpacts.add(Impact("废水排放", -25f, ImpactSeverity.MEDIUM, "需要加强水处理"))
            }
            BuildingType.PARK -> {
                environmentalImpacts.add(Impact("空气质量", 30f, ImpactSeverity.MEDIUM, "绿化改善空气"))
                environmentalImpacts.add(Impact("城市美观", 40f, ImpactSeverity.HIGH, "提升城市景观"))
                environmentalImpacts.add(Impact("休闲空间", 35f, ImpactSeverity.HIGH, "提供市民休闲场所"))
            }
            BuildingType.RECYCLING_CENTER, BuildingType.WASTE_MANAGEMENT -> {
                environmentalImpacts.add(Impact("垃圾处理", 50f, ImpactSeverity.HIGH, "提升垃圾处理能力"))
                environmentalImpacts.add(Impact("环境卫生", 30f, ImpactSeverity.MEDIUM, "改善环境卫生"))
            }
            else -> {}
        }
        impacts[ImpactCategory.ENVIRONMENTAL] = environmentalImpacts
        
        // 社会影响
        val socialImpacts = mutableListOf<Impact>()
        when (buildingType) {
            BuildingType.SCHOOL, BuildingType.UNIVERSITY -> {
                socialImpacts.add(Impact("教育水平", 45f, ImpactSeverity.HIGH, "提升市民教育水平"))
                socialImpacts.add(Impact("就业质量", 25f, ImpactSeverity.MEDIUM, "培养高素质人才"))
                socialImpacts.add(Impact("社会流动性", 30f, ImpactSeverity.MEDIUM, "增加阶层流动机会"))
            }
            BuildingType.HOSPITAL -> {
                socialImpacts.add(Impact("健康水平", 50f, ImpactSeverity.HIGH, "改善医疗条件"))
                socialImpacts.add(Impact("预期寿命", 20f, ImpactSeverity.MEDIUM, "提高市民寿命"))
                socialImpacts.add(Impact("生活质量", 35f, ImpactSeverity.HIGH, "健康保障增强"))
            }
            BuildingType.POLICE_STATION -> {
                socialImpacts.add(Impact("治安水平", 40f, ImpactSeverity.HIGH, "降低犯罪率"))
                socialImpacts.add(Impact("安全感", 35f, ImpactSeverity.HIGH, "市民安全感提升"))
            }
            BuildingType.FIRE_STATION -> {
                socialImpacts.add(Impact("消防安全", 45f, ImpactSeverity.HIGH, "提升消防响应能力"))
                socialImpacts.add(Impact("建筑保险", -15f, ImpactSeverity.LOW, "保险费用降低"))
            }
            else -> {}
        }
        impacts[ImpactCategory.SOCIAL] = socialImpacts
        
        // 基础设施影响
        val infrastructureImpacts = mutableListOf<Impact>()
        when (buildingType) {
            BuildingType.POWER_PLANT -> {
                infrastructureImpacts.add(Impact("电力供应", 500f, ImpactSeverity.CRITICAL, "大幅提升电力供应"))
                infrastructureImpacts.add(Impact("工业发展", 30f, ImpactSeverity.HIGH, "支持工业扩张"))
            }
            BuildingType.WATER_TOWER, BuildingType.WATER_TREATMENT_PLANT -> {
                infrastructureImpacts.add(Impact("水力供应", 300f, ImpactSeverity.CRITICAL, "改善供水状况"))
                infrastructureImpacts.add(Impact("生活质量", 25f, ImpactSeverity.MEDIUM, "保障基本生活需求"))
            }
            BuildingType.ROAD -> {
                infrastructureImpacts.add(Impact("交通便利", 20f, ImpactSeverity.MEDIUM, "改善交通条件"))
                infrastructureImpacts.add(Impact("商业活力", 15f, ImpactSeverity.LOW, "促进商业流通"))
            }
            BuildingType.SUBWAY_STATION -> {
                infrastructureImpacts.add(Impact("交通效率", 60f, ImpactSeverity.HIGH, "大幅提升交通效率"))
                infrastructureImpacts.add(Impact("通勤时间", -40f, ImpactSeverity.HIGH, "缩短通勤时间"))
                infrastructureImpacts.add(Impact("道路拥堵", -30f, ImpactSeverity.MEDIUM, "缓解地面交通"))
            }
            else -> {}
        }
        impacts[ImpactCategory.INFRASTRUCTURE] = infrastructureImpacts
        
        return impacts
    }
    
    /**
     * 拆除建筑的影响
     */
    private fun calculateDemolishImpact(
        decision: Decision,
        state: CityState
    ): Map<ImpactCategory, List<Impact>> {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        val buildingType = decision.targetBuildingType ?: return impacts
        
        // 拆除的影响通常是建造影响的反向
        val buildImpacts = calculateBuildingImpact(
            decision.copy(type = DecisionType.BUILD_STRUCTURE),
            state
        )
        
        buildImpacts.forEach { (category, impactList) ->
            impacts[category] = impactList.map { impact ->
                impact.copy(
                    value = -impact.value,
                    description = "拆除后：${impact.description}"
                )
            }
        }
        
        // 额外的拆除影响
        val economicImpacts = impacts[ImpactCategory.ECONOMIC]?.toMutableList() ?: mutableListOf()
        economicImpacts.add(Impact("失业增加", -30f, ImpactSeverity.HIGH, "员工失业"))
        economicImpacts.add(Impact("资源回收", 20f, ImpactSeverity.LOW, "部分建材回收"))
        impacts[ImpactCategory.ECONOMIC] = economicImpacts
        
        return impacts
    }
    
    /**
     * 税收调整的影响
     */
    private fun calculateTaxImpact(
        decision: Decision,
        state: CityState
    ): Map<ImpactCategory, List<Impact>> {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        val taxChange = decision.taxChange ?: 0f
        
        val economicImpacts = mutableListOf<Impact>()
        if (taxChange > 0) {
            // 增税
            economicImpacts.add(Impact("财政收入", (taxChange * 100).toFloat(), ImpactSeverity.HIGH, "税收增加"))
            economicImpacts.add(Impact("企业负担", -(taxChange * 80).toFloat(), ImpactSeverity.HIGH, "企业成本上升"))
            economicImpacts.add(Impact("投资意愿", -(taxChange * 60).toFloat(), ImpactSeverity.MEDIUM, "企业投资减少"))
            
            val socialImpacts = mutableListOf<Impact>()
            socialImpacts.add(Impact("市民满意度", -(taxChange * 50).toFloat(), ImpactSeverity.MEDIUM, "税负增加引发不满"))
            socialImpacts.add(Impact("消费能力", -(taxChange * 40).toFloat(), ImpactSeverity.MEDIUM, "可支配收入减少"))
            impacts[ImpactCategory.SOCIAL] = socialImpacts
        } else {
            // 减税
            economicImpacts.add(Impact("财政收入", (taxChange * 100).toFloat(), ImpactSeverity.HIGH, "税收减少"))
            economicImpacts.add(Impact("企业活力", (-taxChange * 90).toFloat(), ImpactSeverity.HIGH, "企业负担减轻"))
            economicImpacts.add(Impact("投资意愿", (-taxChange * 70).toFloat(), ImpactSeverity.HIGH, "吸引更多投资"))
            
            val socialImpacts = mutableListOf<Impact>()
            socialImpacts.add(Impact("市民满意度", (-taxChange * 60).toFloat(), ImpactSeverity.MEDIUM, "减税受到欢迎"))
            socialImpacts.add(Impact("消费能力", (-taxChange * 50).toFloat(), ImpactSeverity.MEDIUM, "可支配收入增加"))
            impacts[ImpactCategory.SOCIAL] = socialImpacts
        }
        
        impacts[ImpactCategory.ECONOMIC] = economicImpacts
        return impacts
    }
    
    /**
     * 政策实施的影响
     */
    private fun calculatePolicyImpact(
        decision: Decision,
        state: CityState
    ): Map<ImpactCategory, List<Impact>> {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        val policyType = decision.policyType ?: return impacts
        
        when (policyType) {
            PolicyType.ECONOMIC -> {
                val economicImpacts = mutableListOf(
                    Impact("经济增长", 40f, ImpactSeverity.HIGH, "刺激经济发展"),
                    Impact("就业率", 30f, ImpactSeverity.MEDIUM, "创造就业机会"),
                    Impact("财政支出", -25f, ImpactSeverity.MEDIUM, "政策补贴成本")
                )
                impacts[ImpactCategory.ECONOMIC] = economicImpacts
            }
            PolicyType.ENVIRONMENTAL -> {
                val environmentalImpacts = mutableListOf(
                    Impact("环境质量", 50f, ImpactSeverity.HIGH, "改善环境"),
                    Impact("可持续发展", 45f, ImpactSeverity.HIGH, "长期环保效益")
                )
                val economicImpacts = mutableListOf(
                    Impact("企业成本", -30f, ImpactSeverity.MEDIUM, "环保投入增加"),
                    Impact("长期收益", 25f, ImpactSeverity.LOW, "绿色经济发展")
                )
                impacts[ImpactCategory.ENVIRONMENTAL] = environmentalImpacts
                impacts[ImpactCategory.ECONOMIC] = economicImpacts
            }
            PolicyType.SOCIAL -> {
                val socialImpacts = mutableListOf(
                    Impact("社会福利", 55f, ImpactSeverity.HIGH, "提升福利水平"),
                    Impact("幸福指数", 40f, ImpactSeverity.HIGH, "市民更幸福"),
                    Impact("社会稳定", 35f, ImpactSeverity.MEDIUM, "减少社会矛盾")
                )
                val economicImpacts = mutableListOf(
                    Impact("财政支出", -40f, ImpactSeverity.HIGH, "福利支出增加")
                )
                impacts[ImpactCategory.SOCIAL] = socialImpacts
                impacts[ImpactCategory.ECONOMIC] = economicImpacts
            }
            else -> {}
        }
        
        return impacts
    }
    
    /**
     * 基础设施升级的影响
     */
    private fun calculateUpgradeImpact(
        decision: Decision,
        state: CityState
    ): Map<ImpactCategory, List<Impact>> {
        val impacts = mutableMapOf<ImpactCategory, List<Impact>>()
        
        val infrastructureImpacts = mutableListOf(
            Impact("设施效率", 60f, ImpactSeverity.HIGH, "升级提升效率"),
            Impact("维护成本", -20f, ImpactSeverity.LOW, "新设备维护成本增加"),
            Impact("使用寿命", 40f, ImpactSeverity.MEDIUM, "延长设施寿命")
        )
        
        val economicImpacts = mutableListOf(
            Impact("初期投入", -50f, ImpactSeverity.HIGH, "升级资金投入"),
            Impact("长期节约", 30f, ImpactSeverity.MEDIUM, "运营效率提升")
        )
        
        impacts[ImpactCategory.INFRASTRUCTURE] = infrastructureImpacts
        impacts[ImpactCategory.ECONOMIC] = economicImpacts
        
        return impacts
    }
    
    /**
     * 计算连锁反应
     */
    private fun calculateChainReactions(
        immediateImpacts: Map<ImpactCategory, List<Impact>>,
        state: CityState
    ): List<ChainReaction> {
        val reactions = mutableListOf<ChainReaction>()
        
        // 环境影响 → 健康影响
        immediateImpacts[ImpactCategory.ENVIRONMENTAL]?.forEach { impact ->
            if (impact.name.contains("污染") && impact.value < 0) {
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("健康问题", impact.value / 2, ImpactSeverity.MEDIUM, "污染导致健康问题"),
                    delay = 2,
                    category = ImpactCategory.SOCIAL
                ))
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("医疗支出", -impact.value / 3, ImpactSeverity.LOW, "医疗需求增加"),
                    delay = 3,
                    category = ImpactCategory.ECONOMIC
                ))
            }
        }
        
        // 就业影响 → 消费影响
        immediateImpacts[ImpactCategory.ECONOMIC]?.forEach { impact ->
            if (impact.name.contains("就业") && impact.value > 0) {
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("消费增长", impact.value * 0.6f, ImpactSeverity.MEDIUM, "就业带动消费"),
                    delay = 1,
                    category = ImpactCategory.ECONOMIC
                ))
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("商业繁荣", impact.value * 0.4f, ImpactSeverity.LOW, "消费促进商业"),
                    delay = 2,
                    category = ImpactCategory.ECONOMIC
                ))
            }
        }
        
        // 教育影响 → 就业质量影响
        immediateImpacts[ImpactCategory.SOCIAL]?.forEach { impact ->
            if (impact.name.contains("教育") && impact.value > 0) {
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("高薪就业", impact.value * 0.5f, ImpactSeverity.MEDIUM, "教育提升就业质量"),
                    delay = 5,
                    category = ImpactCategory.ECONOMIC
                ))
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("创新能力", impact.value * 0.4f, ImpactSeverity.MEDIUM, "培养创新人才"),
                    delay = 6,
                    category = ImpactCategory.ECONOMIC
                ))
            }
        }
        
        // 交通改善 → 经济活力
        immediateImpacts[ImpactCategory.INFRASTRUCTURE]?.forEach { impact ->
            if (impact.name.contains("交通") && impact.value > 0) {
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("商业效率", impact.value * 0.6f, ImpactSeverity.MEDIUM, "交通促进商业"),
                    delay = 1,
                    category = ImpactCategory.ECONOMIC
                ))
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("生活便利", impact.value * 0.5f, ImpactSeverity.LOW, "出行更方便"),
                    delay = 1,
                    category = ImpactCategory.SOCIAL
                ))
            }
        }
        
        // 安全改善 → 投资吸引
        immediateImpacts[ImpactCategory.SOCIAL]?.forEach { impact ->
            if (impact.name.contains("治安") && impact.value > 0) {
                reactions.add(ChainReaction(
                    trigger = impact,
                    effect = Impact("投资环境", impact.value * 0.7f, ImpactSeverity.MEDIUM, "安全吸引投资"),
                    delay = 2,
                    category = ImpactCategory.ECONOMIC
                ))
            }
        }
        
        return reactions
    }
    
    /**
     * 计算长期效应
     */
    private fun calculateLongTermEffects(
        immediateImpacts: Map<ImpactCategory, List<Impact>>,
        state: CityState
    ): List<LongTermEffect> {
        val effects = mutableListOf<LongTermEffect>()
        
        // 累积环境效应
        val environmentalScore = immediateImpacts[ImpactCategory.ENVIRONMENTAL]
            ?.sumOf { it.value.toDouble() }?.toFloat() ?: 0f
        
        if (environmentalScore < -50) {
            effects.add(LongTermEffect(
                name = "环境恶化",
                description = "长期污染导致生态系统受损",
                duration = 20,
                cumulativeImpact = Impact("生态损害", -10f, ImpactSeverity.CRITICAL, "需要长期治理")
            ))
        } else if (environmentalScore > 50) {
            effects.add(LongTermEffect(
                name = "生态改善",
                description = "持续环保带来生态红利",
                duration = 15,
                cumulativeImpact = Impact("生态红利", 8f, ImpactSeverity.MEDIUM, "宜居城市")
            ))
        }
        
        // 教育投资的长期回报
        val educationScore = immediateImpacts[ImpactCategory.SOCIAL]
            ?.filter { it.name.contains("教育") }
            ?.sumOf { it.value.toDouble() }?.toFloat() ?: 0f
        
        if (educationScore > 30) {
            effects.add(LongTermEffect(
                name = "人才红利",
                description = "教育投资在未来产生回报",
                duration = 10,
                cumulativeImpact = Impact("人才竞争力", 15f, ImpactSeverity.HIGH, "高素质劳动力")
            ))
        }
        
        // 基础设施的持续效益
        val infrastructureScore = immediateImpacts[ImpactCategory.INFRASTRUCTURE]
            ?.sumOf { it.value.toDouble() }?.toFloat() ?: 0f
        
        if (infrastructureScore > 100) {
            effects.add(LongTermEffect(
                name = "基建优势",
                description = "完善的基础设施持续发挥作用",
                duration = 25,
                cumulativeImpact = Impact("城市竞争力", 12f, ImpactSeverity.HIGH, "吸引人口和企业")
            ))
        }
        
        return effects
    }
}

/**
 * 决策类型
 */
enum class DecisionType {
    BUILD_STRUCTURE,        // 建造建筑
    DEMOLISH_STRUCTURE,     // 拆除建筑
    ADJUST_TAX,            // 调整税率
    ENACT_POLICY,          // 实施政策
    UPGRADE_INFRASTRUCTURE  // 升级基础设施
}

/**
 * 决策
 */
data class Decision(
    val id: String,
    val type: DecisionType,
    val name: String,
    val description: String,
    val cost: Int,
    val targetBuildingType: BuildingType? = null,
    val taxChange: Float? = null,
    val policyType: PolicyType? = null
)

/**
 * 影响分类
 */
enum class ImpactCategory {
    ECONOMIC,           // 经济影响
    ENVIRONMENTAL,      // 环境影响
    SOCIAL,            // 社会影响
    INFRASTRUCTURE     // 基础设施影响
}

/**
 * 影响严重程度
 */
enum class ImpactSeverity {
    LOW,       // 轻微
    MEDIUM,    // 中等
    HIGH,      // 重大
    CRITICAL   // 关键
}

/**
 * 影响
 */
data class Impact(
    val name: String,
    val value: Float,          // 正值为正面影响，负值为负面影响
    val severity: ImpactSeverity,
    val description: String
)

/**
 * 连锁反应
 */
data class ChainReaction(
    val trigger: Impact,       // 触发影响
    val effect: Impact,        // 连锁效应
    val delay: Int,           // 延迟回合数
    val category: ImpactCategory
)

/**
 * 长期效应
 */
data class LongTermEffect(
    val name: String,
    val description: String,
    val duration: Int,        // 持续回合数
    val cumulativeImpact: Impact
)

/**
 * 决策影响
 */
data class DecisionImpact(
    val decision: Decision,
    val immediateImpacts: Map<ImpactCategory, List<Impact>>,
    val chainReactions: List<ChainReaction>,
    val longTermEffects: List<LongTermEffect>
) {
    /**
     * 获取总体评分
     */
    fun getOverallScore(): Float {
        return immediateImpacts.values.flatten().sumOf { it.value.toDouble() }.toFloat()
    }
    
    /**
     * 获取主要正面影响
     */
    fun getPositiveImpacts(): List<Impact> {
        return immediateImpacts.values.flatten().filter { it.value > 0 }.sortedByDescending { it.value }
    }
    
    /**
     * 获取主要负面影响
     */
    fun getNegativeImpacts(): List<Impact> {
        return immediateImpacts.values.flatten().filter { it.value < 0 }.sortedBy { it.value }
    }
}

/**
 * 城市状态（简化版，用于计算影响）
 */
data class CityState(
    val population: Int,
    val gdp: Int,
    val environmentalQuality: Float,
    val averageHappiness: Float,
    val infrastructureLevel: Float
)

