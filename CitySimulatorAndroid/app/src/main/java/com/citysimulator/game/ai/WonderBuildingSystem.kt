package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import kotlin.random.Random

/**
 * 奇观建筑系统 - 宏观影响
 * 
 * 世界奇观不仅提供数值加成，更会永久改变所有市民的"集体意识"：
 * - 激发雄心与自豪感
 * - 改变市民的价值观
 * - 影响市民的行为模式
 * - 创造城市独特文化
 * 
 * @author AI进化论-花生
 */

/**
 * 奇观类型
 */
enum class WonderType {
    SPACE_ELEVATOR,      // 太空电梯 - 激发探索精神
    GREAT_LIBRARY,       // 大图书馆 - 激发求知欲
    COLOSSUS,            // 巨像 - 激发自豪感
    TECH_TOWER,          // 科技塔 - 激发创新精神
    HARMONY_TEMPLE,      // 和谐神殿 - 激发友善与和平
    GOLDEN_PALACE,       // 黄金宫殿 - 激发财富追求
    ETERNAL_MONUMENT     // 永恒纪念碑 - 激发历史感和使命感
}

/**
 * 集体意识效果
 */
data class CollectiveConsciousness(
    val wonderType: WonderType,
    val inspirationLevel: Float,        // 激励水平 (0-1)
    val culturalImpact: String,         // 文化影响描述
    val valueShift: Map<String, Float>, // 价值观变化
    val behaviorModifiers: BehaviorModifiers
)

/**
 * 行为修正器
 */
data class BehaviorModifiers(
    val ambitionBoost: Float = 0f,      // 野心提升
    val curiosityBoost: Float = 0f,     // 好奇心提升
    val creativityBoost: Float = 0f,    // 创造力提升
    val friendlinessBoost: Float = 0f,  // 友善度提升
    val prideBoost: Float = 0f,         // 自豪感提升
    val diligenceBoost: Float = 0f      // 勤奋度提升
)

/**
 * 奇观建筑数据
 */
data class WonderBuilding(
    val type: WonderType,
    val name: String,
    val description: String,
    val buildCost: Int,
    val buildTime: Int,  // 建造时间（游戏天数）
    val collectiveEffect: CollectiveConsciousness,
    val numericBonuses: WonderBonuses
)

/**
 * 奇观数值加成
 */
data class WonderBonuses(
    val goldPerMonth: Int = 0,
    val happinessBonus: Float = 0f,
    val productivityBonus: Float = 0f,
    val culturalOutput: Int = 0,
    val tourismIncome: Int = 0
)

/**
 * 奇观事件
 */
data class WonderEvent(
    val wonderType: WonderType,
    val eventTitle: String,
    val eventDescription: String,
    val citizenReactions: List<String>,
    val longTermEffects: String
)

/**
 * 奇观建筑系统
 */
object WonderBuildingSystem {
    
    /**
     * 获取所有可用奇观
     */
    fun getAllWonders(): List<WonderBuilding> {
        return listOf(
            WonderBuilding(
                type = WonderType.SPACE_ELEVATOR,
                name = "太空电梯",
                description = "连接地球与星空的壮丽建筑，象征人类对未知的无尽探索",
                buildCost = 50000,
                buildTime = 12,
                collectiveEffect = CollectiveConsciousness(
                    wonderType = WonderType.SPACE_ELEVATOR,
                    inspirationLevel = 0.9f,
                    culturalImpact = "城市成为全球科技创新中心，市民充满探索精神和未来憧憬",
                    valueShift = mapOf(
                        "科技崇拜" to 0.8f,
                        "探索欲望" to 0.7f,
                        "未来主义" to 0.9f
                    ),
                    behaviorModifiers = BehaviorModifiers(
                        ambitionBoost = 0.3f,
                        curiosityBoost = 0.4f,
                        creativityBoost = 0.3f
                    )
                ),
                numericBonuses = WonderBonuses(
                    goldPerMonth = 500,
                    happinessBonus = 0.15f,
                    productivityBonus = 0.2f,
                    culturalOutput = 100,
                    tourismIncome = 800
                )
            ),
            WonderBuilding(
                type = WonderType.GREAT_LIBRARY,
                name = "大图书馆",
                description = "汇聚人类智慧的殿堂，每一本书都是通往另一个世界的门",
                buildCost = 30000,
                buildTime = 6,
                collectiveEffect = CollectiveConsciousness(
                    wonderType = WonderType.GREAT_LIBRARY,
                    inspirationLevel = 0.7f,
                    culturalImpact = "城市成为知识与文化的圣地，市民普遍追求智慧和自我提升",
                    valueShift = mapOf(
                        "知识追求" to 0.9f,
                        "教育重视" to 0.8f,
                        "文化传承" to 0.7f
                    ),
                    behaviorModifiers = BehaviorModifiers(
                        curiosityBoost = 0.5f,
                        diligenceBoost = 0.3f,
                        creativityBoost = 0.2f
                    )
                ),
                numericBonuses = WonderBonuses(
                    goldPerMonth = 200,
                    happinessBonus = 0.1f,
                    productivityBonus = 0.15f,
                    culturalOutput = 150,
                    tourismIncome = 400
                )
            ),
            WonderBuilding(
                type = WonderType.COLOSSUS,
                name = "城市巨像",
                description = "高耸入云的雕像，象征着城市的力量、荣耀与不屈精神",
                buildCost = 40000,
                buildTime = 8,
                collectiveEffect = CollectiveConsciousness(
                    wonderType = WonderType.COLOSSUS,
                    inspirationLevel = 0.8f,
                    culturalImpact = "市民对城市充满自豪感，集体凝聚力和归属感空前提升",
                    valueShift = mapOf(
                        "城市自豪" to 1.0f,
                        "集体主义" to 0.7f,
                        "荣誉感" to 0.8f
                    ),
                    behaviorModifiers = BehaviorModifiers(
                        prideBoost = 0.5f,
                        ambitionBoost = 0.2f,
                        friendlinessBoost = 0.2f
                    )
                ),
                numericBonuses = WonderBonuses(
                    goldPerMonth = 300,
                    happinessBonus = 0.2f,
                    productivityBonus = 0.1f,
                    culturalOutput = 120,
                    tourismIncome = 600
                )
            )
        )
    }
    
    /**
     * 应用奇观对市民的集体意识影响
     */
    fun applyWonderEffectToCitizen(
        citizen: Citizen,
        wonder: WonderBuilding
    ): Citizen {
        val personality = citizen.personalityTraits ?: com.citysimulator.game.data.model.PersonalityTraits.generateRandom()
        val modifiers = wonder.collectiveEffect.behaviorModifiers
        
        val newPersonality = com.citysimulator.game.data.model.PersonalityTraits(
            extraversion = personality.extraversion,
            diligence = (personality.diligence + modifiers.diligenceBoost).coerceIn(0f, 1f),
            curiosity = (personality.curiosity + modifiers.curiosityBoost).coerceIn(0f, 1f),
            kindness = (personality.kindness + modifiers.friendlinessBoost).coerceIn(0f, 1f),
            stability = personality.stability,
            creativity = (personality.creativity + modifiers.creativityBoost).coerceIn(0f, 1f),
            ambition = (personality.ambition + modifiers.ambitionBoost).coerceIn(0f, 1f),
            rebelliousness = personality.rebelliousness
        )
        
        val newHappiness = (citizen.happiness + wonder.numericBonuses.happinessBonus).coerceIn(0f, 1f)
        
        return citizen.copy(
            personalityTraits = newPersonality,
            happiness = newHappiness
        )
    }
    
    /**
     * 生成奇观建造事件描述
     */
    fun generateWonderCompletionEvent(wonder: WonderBuilding): WonderEvent {
        return WonderEvent(
            wonderType = wonder.type,
            eventTitle = "${wonder.name}落成典礼",
            eventDescription = buildString {
                append("经过${wonder.buildTime}个月的建设，${wonder.name}终于落成！\n\n")
                append(wonder.description)
                append("\n\n这座伟大建筑将永远改变这座城市。")
            },
            citizenReactions = listOf(
                "太震撼了！",
                "我为我们的城市感到骄傲！"
            ),
            longTermEffects = buildString {
                append("永久效果：\n")
                append("• 每月收入：+${wonder.numericBonuses.goldPerMonth} 金币\n")
                append("• 市民幸福度：+${(wonder.numericBonuses.happinessBonus * 100).toInt()}%\n")
            }
        )
    }
}
