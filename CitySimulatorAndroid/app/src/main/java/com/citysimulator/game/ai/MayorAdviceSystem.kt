package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.PersonalityTraits
import kotlin.random.Random

/**
 * 市长建议系统 - 微观干预
 * 
 * 玩家可以点击市民给出建议，但市民是否采纳取决于：
 * 1. 市民的性格特质（外向性、好奇心、友善性等）
 * 2. 市民对市长的信任度
 * 3. 建议的类型和市民当前状态的匹配度
 * 
 * @author AI进化论-花生
 */

/**
 * 建议类型
 */
enum class MayorAdviceType {
    CAREER_CHANGE,      // 职业建议（"试试去那家新公司面试"）
    EDUCATION,          // 教育建议（"去学习新技能提升自己"）
    SOCIAL,             // 社交建议（"多认识一些朋友"）
    HEALTH,             // 健康建议（"注意休息，别太累了"）
    FINANCIAL,          // 财务建议（"考虑存点钱以备不时之需"）
    RELATIONSHIP,       // 感情建议（"也许该找个伴侣了"）
    RELOCATION          // 搬家建议（"搬到更好的社区"）
}

/**
 * 市长建议数据类
 */
data class MayorAdvice(
    val type: MayorAdviceType,
    val title: String,              // 建议标题
    val content: String,            // 建议内容
    val expectedBenefit: String,    // 预期收益
    val difficulty: Float,          // 实施难度 (0-1)
    val requiredTrust: Float        // 需要的信任度阈值 (0-1)
)

/**
 * 建议采纳结果
 */
data class AdviceAcceptanceResult(
    val accepted: Boolean,                  // 是否接受
    val acceptanceProbability: Float,       // 接受概率
    val reason: String,                     // 接受/拒绝原因
    val trustChange: Float,                 // 信任度变化
    val moodChange: Float                   // 心情变化
)

/**
 * 市长建议系统
 */
object MayorAdviceSystem {
    
    /**
     * 为市民生成适合的建议列表
     */
    fun generateAvailableAdvice(citizen: Citizen): List<MayorAdvice> {
        val advice = mutableListOf<MayorAdvice>()
        
        // 根据市民状态生成相应建议
        // 职业建议
        if (citizen.happiness < 0.5f || citizen.occupation == "失业") {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.CAREER_CHANGE,
                    title = "寻找新机会",
                    content = "我注意到附近有一些不错的工作机会，要不要试试？",
                    expectedBenefit = "改善收入和生活质量",
                    difficulty = 0.6f,
                    requiredTrust = 0.3f
                )
            )
        }
        
        // 教育建议
        if (citizen.age < 40 && citizen.happiness < 0.7f) {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.EDUCATION,
                    title = "学习提升",
                    content = "学点新技能会让你更有竞争力哦！",
                    expectedBenefit = "提升职业发展前景",
                    difficulty = 0.7f,
                    requiredTrust = 0.4f
                )
            )
        }
        
        // 社交建议
        if (citizen.happiness < 0.6f) {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.SOCIAL,
                    title = "社交活动",
                    content = "多认识一些朋友，生活会更精彩！",
                    expectedBenefit = "提升幸福感",
                    difficulty = 0.4f,
                    requiredTrust = 0.2f
                )
            )
        }
        
        // 健康建议
        if (citizen.health < 0.7f) {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.HEALTH,
                    title = "注意健康",
                    content = "工作虽然重要，但也要注意休息啊！",
                    expectedBenefit = "改善健康状况",
                    difficulty = 0.3f,
                    requiredTrust = 0.2f
                )
            )
        }
        
        // 财务建议
        if (citizen.happiness < 0.5f) {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.FINANCIAL,
                    title = "财务规划",
                    content = "存点钱以备不时之需，会让生活更安心。",
                    expectedBenefit = "增加安全感",
                    difficulty = 0.5f,
                    requiredTrust = 0.3f
                )
            )
        }
        
        // 感情建议 (未婚)
        if (citizen.maritalStatus == com.citysimulator.game.data.model.MaritalStatus.SINGLE && citizen.age > 20) {
            advice.add(
                MayorAdvice(
                    type = MayorAdviceType.RELATIONSHIP,
                    title = "寻找伴侣",
                    content = "也许该找个伴侣了，一起生活会更温暖哦！",
                    expectedBenefit = "提升生活幸福感",
                    difficulty = 0.8f,
                    requiredTrust = 0.5f
                )
            )
        }
        
        return advice
    }
    
    /**
     * 计算市民采纳建议的概率
     */
    fun calculateAcceptanceProbability(
        citizen: Citizen,
        advice: MayorAdvice,
        personality: PersonalityTraits,
        mayorTrust: Float
    ): Float {
        var probability = 0.3f // 基础概率
        
        // 信任度影响 (最高+40%)
        probability += mayorTrust * 0.4f
        
        // 性格特质影响
        when (advice.type) {
            MayorAdviceType.CAREER_CHANGE -> {
                probability += personality.curiosity * 0.2f // 好奇心+20%
                probability += personality.ambition * 0.15f // 雄心+15%
                probability -= (1 - personality.stability) * 0.1f // 稳定性低-10%
            }
            MayorAdviceType.EDUCATION -> {
                probability += personality.curiosity * 0.3f
                probability += personality.diligence * 0.2f
            }
            MayorAdviceType.SOCIAL -> {
                probability += personality.extraversion * 0.25f
                probability += personality.kindness * 0.2f
            }
            MayorAdviceType.HEALTH -> {
                probability += personality.stability * 0.2f
                probability -= personality.rebelliousness * 0.1f
            }
            MayorAdviceType.FINANCIAL -> {
                probability += personality.stability * 0.25f
                probability += (1 - personality.extraversion) * 0.15f // 内向者更谨慎
            }
            MayorAdviceType.RELATIONSHIP -> {
                probability += personality.kindness * 0.2f
                probability += personality.extraversion * 0.2f
                probability -= (1 - personality.curiosity) * 0.1f
            }
            MayorAdviceType.RELOCATION -> {
                probability += personality.curiosity * 0.25f
                probability -= personality.stability * 0.15f // 稳定者不喜欢搬家
            }
        }
        
        // 当前状态影响
        when (advice.type) {
            MayorAdviceType.CAREER_CHANGE -> {
                if (citizen.happiness < 0.3f) probability += 0.2f // 很不开心，更愿意改变
                if (citizen.occupation == "失业") probability += 0.3f
            }
            MayorAdviceType.HEALTH -> {
                if (citizen.health < 0.5f) probability += 0.25f
            }
            MayorAdviceType.SOCIAL -> {
                if (citizen.happiness < 0.4f) probability += 0.15f
            }
            else -> {}
        }
        
        // 难度影响 (建议越难，接受概率越低)
        probability -= advice.difficulty * 0.2f
        
        // 信任度门槛
        if (mayorTrust < advice.requiredTrust) {
            probability *= 0.5f // 信任度不足，概率减半
        }
        
        return probability.coerceIn(0f, 1f)
    }
    
    /**
     * 模拟市民对建议的反应
     */
    fun simulateAdviceResponse(
        citizen: Citizen,
        advice: MayorAdvice,
        personality: PersonalityTraits,
        mayorTrust: Float
    ): AdviceAcceptanceResult {
        val probability = calculateAcceptanceProbability(citizen, advice, personality, mayorTrust)
        val accepted = Random.nextFloat() < probability
        
        val trustChange = if (accepted) {
            // 接受建议，信任度小幅提升
            Random.nextFloat() * 0.05f + 0.02f
        } else {
            // 拒绝建议，信任度轻微下降（但不严重）
            -Random.nextFloat() * 0.03f
        }
        
        val moodChange = if (accepted) {
            // 接受建议后的心情变化取决于难度
            if (advice.difficulty > 0.7f) {
                -0.05f // 难度高的建议可能短期降低心情
            } else {
                0.03f // 容易的建议让人感觉积极
            }
        } else {
            0f // 拒绝建议不影响心情
        }
        
        val reason = if (accepted) {
            when {
                mayorTrust > 0.7f -> "我很信任市长，我会试试看的！"
                probability > 0.6f -> "这个建议听起来不错，我可以考虑一下。"
                citizen.happiness < 0.3f -> "我现在的状况确实需要改变，我愿意尝试。"
                else -> "好吧，我试试看..."
            }
        } else {
            when {
                mayorTrust < advice.requiredTrust -> "抱歉，我现在还不太能相信这个建议..."
                advice.difficulty > 0.7f -> "这对我来说太难了，我暂时做不到。"
                personality.rebelliousness > 0.7f -> "我有我自己的想法，不需要别人告诉我该怎么做。"
                else -> "谢谢您的建议，但我暂时不打算这么做。"
            }
        }
        
        return AdviceAcceptanceResult(
            accepted = accepted,
            acceptanceProbability = probability,
            reason = reason,
            trustChange = trustChange,
            moodChange = moodChange
        )
    }
}

/**
 * 市民对市长的信任度数据
 */
data class CitizenTrust(
    val citizenId: String,
    val trustLevel: Float,         // 信任等级 (0-1)
    val adviceHistory: List<AdviceRecord> = emptyList()
)

/**
 * 建议记录
 */
data class AdviceRecord(
    val timestamp: java.util.Date,
    val advice: MayorAdvice,
    val accepted: Boolean,
    val outcome: String  // 建议的结果（如果接受）
)
