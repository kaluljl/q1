package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.CitizenActivity
import com.citysimulator.game.data.model.EducationLevel
import com.citysimulator.game.data.model.MaritalStatus
import com.citysimulator.game.ai.deepseek.DeepSeekClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * 市民文本生成系统 - 基于AI的对话与叙事生成
 * 
 * 功能：
 * - 动态生成市民之间的对话
 * - 生成市民的日记内容
 * - 生成多样化的抱怨内容
 * - 生成市民的自言自语
 * 
 * @author AI进化论-花生
 */
object CitizenTextGenerator {
    
    // DeepSeek API密钥（从配置读取）
    private const val API_KEY = "sk-95ccc807b0c34d3e96bb3c47d8c48c98"
    private val deepSeekClient = DeepSeekClient(API_KEY)
    
    /**
     * 生成市民对市长建议的回复（同步版本，用于UI）
     */
    fun generateResponseToMayorAdvice(
        citizen: Citizen,
        playerAdvice: String,
        accepted: Boolean,
        mayorTrust: Float,
        personality: com.citysimulator.game.data.model.PersonalityTraits
    ): String {
        // 构建AI提示词
        val prompt = buildString {
            appendLine("你是一个城市模拟游戏中的市民，请根据以下信息生成对市长建议的真实回复：")
            appendLine()
            appendLine("【市民信息】")
            appendLine("姓名: ${citizen.name}")
            appendLine("年龄: ${citizen.age}岁")
            appendLine("性别: ${if (citizen.gender == com.citysimulator.game.data.model.Gender.MALE) "男" else "女"}")
            appendLine("职业: ${citizen.occupation ?: "无业"}")
            appendLine("幸福度: ${(citizen.happiness * 100).toInt()}%")
            appendLine("健康: ${(citizen.health * 100).toInt()}%")
            appendLine("财富: ${citizen.wealth}金币")
            appendLine()
            appendLine("【性格特质】")
            appendLine("外向性: ${(personality.extraversion * 100).toInt()}% ${if (personality.extraversion > 0.6f) "(外向)" else "(内向)"}")
            appendLine("勤奋度: ${(personality.diligence * 100).toInt()}% ${if (personality.diligence > 0.6f) "(勤奋)" else "(懒散)"}")
            appendLine("好奇心: ${(personality.curiosity * 100).toInt()}% ${if (personality.curiosity > 0.6f) "(好奇)" else "(保守)"}")
            appendLine("友善度: ${(personality.kindness * 100).toInt()}% ${if (personality.kindness > 0.6f) "(友善)" else "(冷漠)"}")
            appendLine("稳定性: ${(personality.stability * 100).toInt()}% ${if (personality.stability > 0.6f) "(稳定)" else "(情绪化)"}")
            appendLine("创造力: ${(personality.creativity * 100).toInt()}% ${if (personality.creativity > 0.6f) "(有创意)" else "(务实)"}")
            appendLine("野心: ${(personality.ambition * 100).toInt()}% ${if (personality.ambition > 0.6f) "(有野心)" else "(知足)"}")
            appendLine("叛逆性: ${(personality.rebelliousness * 100).toInt()}% ${if (personality.rebelliousness > 0.6f) "(叛逆)" else "(顺从)"}")
            appendLine()
            appendLine("【对市长的信任度】")
            appendLine("${(mayorTrust * 100).toInt()}% ${when {
                mayorTrust > 0.7f -> "(非常信任)"
                mayorTrust > 0.5f -> "(比较信任)"
                mayorTrust > 0.3f -> "(一般信任)"
                else -> "(不太信任)"
            }}")
            appendLine()
            appendLine("【市长的建议】")
            appendLine("\"$playerAdvice\"")
            appendLine()
            appendLine("【你的决定】")
            appendLine(if (accepted) "接受了这个建议" else "拒绝了这个建议")
            appendLine()
            appendLine("【要求】")
            appendLine("1. 请以第一人称生成${citizen.name}对市长建议的真实回复（30-60字）")
            appendLine("2. 回复要符合${citizen.name}的性格特质")
            appendLine("3. 回复要体现对市长的信任度")
            appendLine("4. 如果接受，要表达感谢和积极态度")
            appendLine("5. 如果拒绝，要给出合理的理由，但态度要礼貌")
            appendLine("6. 不要使用引号，直接输出回复内容")
            appendLine()
            appendLine("回复:")
        }
        
        // 生成回复（使用fallback机制）
        return generateFallbackResponseToAdvice(
            citizen, playerAdvice, accepted, mayorTrust, personality
        )
    }
    
    /**
     * 生成市民之间的对话
     */
    suspend fun generateDialogue(
        citizen1: Citizen,
        citizen2: Citizen,
        context: String = "日常相遇"
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildDialoguePrompt(citizen1, citizen2, context)
            // 暂时使用降级方案，未来可集成DeepSeek API
            val response: String? = null
            response ?: generateFallbackDialogue(citizen1, citizen2, context)
        } catch (e: Exception) {
            generateFallbackDialogue(citizen1, citizen2, context)
        }
    }
    
    /**
     * 生成市民的日记
     */
    suspend fun generateDiary(
        citizen: Citizen,
        recentEvents: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildDiaryPrompt(citizen, recentEvents)
            // 暂时使用降级方案，未来可集成DeepSeek API
            val response: String? = null
            response ?: generateFallbackDiary(citizen)
        } catch (e: Exception) {
            generateFallbackDiary(citizen)
        }
    }
    
    /**
     * 生成市民的抱怨
     */
    suspend fun generateComplaint(
        citizen: Citizen,
        cityProblems: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildComplaintPrompt(citizen, cityProblems)
            // 暂时使用降级方案，未来可集成DeepSeek API
            val response: String? = null
            response ?: generateFallbackComplaint(citizen, cityProblems)
        } catch (e: Exception) {
            generateFallbackComplaint(citizen, cityProblems)
        }
    }
    
    /**
     * 生成市民的自言自语
     */
    suspend fun generateMonologue(citizen: Citizen): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildMonologuePrompt(citizen)
            // 暂时使用降级方案，未来可集成DeepSeek API
            val response: String? = null
            response ?: generateFallbackMonologue(citizen)
        } catch (e: Exception) {
            generateFallbackMonologue(citizen)
        }
    }
    
    // ========== AI Prompt构建 ==========
    
    private fun buildDialoguePrompt(
        citizen1: Citizen,
        citizen2: Citizen,
        context: String
    ): String {
        val relationship = analyzeRelationship(citizen1, citizen2)
        
        return """
你是一个城市模拟游戏的对话生成器。请为两个市民生成一段简短的对话（2-4句）。

市民A信息：
- 职业：${citizen1.occupation ?: "无业"}
- 教育：${citizen1.education.getDisplayName()}
- 幸福度：${(citizen1.happiness * 100).toInt()}%
- 婚姻：${citizen1.maritalStatus.getDisplayName()}

市民B信息：
- 职业：${citizen2.occupation ?: "无业"}
- 教育：${citizen2.education.getDisplayName()}
- 幸福度：${(citizen2.happiness * 100).toInt()}%
- 婚姻：${citizen2.maritalStatus.getDisplayName()}

场景：$context
关系：$relationship

要求：
1. 对话要自然、生活化
2. 反映他们的职业、教育、心情
3. 不要超过4句话
4. 不需要对话标签，直接输出内容

输出格式示例：
A: 最近工作怎么样？
B: 还不错，公司刚发了奖金。
A: 真羡慕，我们单位最近在裁员呢。
        """.trimIndent()
    }
    
    private fun buildDiaryPrompt(citizen: Citizen, recentEvents: List<String>): String {
        return """
你是一个城市模拟游戏的日记生成器。请为一个市民生成一篇简短的日记（50-100字）。

市民信息：
- 职业：${citizen.occupation ?: "目前待业"}
- 教育：${citizen.education.getDisplayName()}
- 幸福度：${(citizen.happiness * 100).toInt()}%
- 健康度：${(citizen.health * 100).toInt()}%
- 婚姻：${citizen.maritalStatus.getDisplayName()}
- 财富：${citizen.wealth}金币

近期事件：
${if (recentEvents.isEmpty()) "平淡的一天" else recentEvents.joinToString("\n")}

要求：
1. 第一人称叙述
2. 符合市民的身份和心情
3. 真实、自然的日常感受
4. 50-100字

示例：
今天又是平凡的一天。早上去公司加班到很晚，回家路上看到街边新开的咖啡店，真想进去坐坐，可惜钱包有点紧。希望下个月工资能涨一点。
        """.trimIndent()
    }
    
    private fun buildComplaintPrompt(citizen: Citizen, cityProblems: List<String>): String {
        return """
你是一个城市模拟游戏的抱怨生成器。请为一个不满的市民生成一条抱怨（20-40字）。

市民信息：
- 职业：${citizen.occupation ?: "无业"}
- 幸福度：${(citizen.happiness * 100).toInt()}%（偏低）
- 抱怨次数：${citizen.complaints}次

城市问题：
${if (cityProblems.isEmpty()) "整体环境欠佳" else cityProblems.joinToString("、")}

要求：
1. 要有具体指向，不要泛泛而谈
2. 符合市民身份和处境
3. 真实、接地气的抱怨
4. 20-40字

示例：
城市的公共交通太差了，每天上班要挤两个小时的公交车，政府什么时候能修地铁啊？
        """.trimIndent()
    }
    
    private fun buildMonologuePrompt(citizen: Citizen): String {
        return """
你是一个城市模拟游戏的自言自语生成器。请为市民生成一句内心独白（15-30字）。

市民状态：
- 当前状态：工作中
- 幸福度：${(citizen.happiness * 100).toInt()}%
- 健康度：${(citizen.health * 100).toInt()}%

要求：
1. 符合当前活动场景
2. 反映心情状态
3. 自然、口语化
4. 15-30字

示例：
真累啊，今天的工作量好像特别大，要是能早点下班就好了...
        """.trimIndent()
    }
    
    // ========== 降级方案（AI调用失败时） ==========
    
    /**
     * Fallback: 生成市民对建议的回复（无AI时）
     */
    private fun generateFallbackResponseToAdvice(
        citizen: Citizen,
        playerAdvice: String,
        accepted: Boolean,
        mayorTrust: Float,
        personality: com.citysimulator.game.data.model.PersonalityTraits
    ): String {
        return if (accepted) {
            when {
                mayorTrust > 0.7f && personality.kindness > 0.6f -> 
                    "市长，感谢您一直关心我们！您的建议我一定会认真执行的。"
                mayorTrust > 0.7f -> 
                    "市长一直为我们着想，我相信您的判断！"
                personality.curiosity > 0.7f -> 
                    "这个建议听起来很有趣，正好我也想尝试些新东西！"
                personality.ambition > 0.7f -> 
                    "好的，这确实是个改变现状的好机会！"
                personality.kindness > 0.7f -> 
                    "谢谢您的关心，我会认真考虑并照做的。"
                citizen.happiness < 0.4f -> 
                    "反正现在也没什么可失去的，试试吧。"
                citizen.occupation == null -> 
                    "我正需要这样的建议，感谢市长！"
                else -> 
                    "我会试着按照您的建议去做。"
            }
        } else {
            when {
                mayorTrust < 0.3f && personality.rebelliousness > 0.5f -> 
                    "抱歉，我不太认同这个建议，我有自己的想法。"
                mayorTrust < 0.3f -> 
                    "对不起市长，我现在还不太信任这个建议..."
                personality.rebelliousness > 0.7f -> 
                    "我有自己的想法，不需要别人告诉我怎么做。"
                personality.stability > 0.7f -> 
                    "我现在的生活还算稳定，暂时不想改变。"
                citizen.happiness < 0.3f -> 
                    "我现在心情不太好，实在没精力考虑这些..."
                citizen.age > 50 -> 
                    "我这个年纪了，习惯了现在的生活方式，不太想折腾了。"
                personality.diligence < 0.3f -> 
                    "这听起来有点麻烦，我可能做不到..."
                else -> 
                    "谢谢您的建议，但我觉得现在不太合适。"
            }
        }
    }
    
    private fun generateFallbackDialogue(
        citizen1: Citizen,
        citizen2: Citizen,
        context: String
    ): String {
        val templates = listOf(
            "A: 嗨，最近怎么样？\nB: 还不错，你呢？",
            "A: 天气真好！\nB: 是啊，适合出来走走。",
            "A: 你在忙什么？\nB: 刚下班，正准备回家。"
        )
        return templates.random()
    }
    
    private fun generateFallbackDiary(citizen: Citizen): String {
        return when {
            citizen.happiness > 0.7f -> "今天心情不错，工作顺利，生活充实。希望每天都这样！"
            citizen.happiness < 0.4f -> "最近压力有点大，感觉生活不太如意。需要找时间放松一下。"
            else -> "普通的一天，按部就班地工作和生活。平凡但也还算稳定。"
        }
    }
    
    private fun generateFallbackComplaint(
        citizen: Citizen,
        cityProblems: List<String>
    ): String {
        val problemTemplates = listOf(
            "城市的公共服务太差了，希望政府能改进！",
            "税收太高了，生活成本压力很大。",
            "环境污染越来越严重，空气质量堪忧。",
            "公共设施太少，连个公园都找不到。",
            "交通拥堵太严重，每天上班都要浪费很多时间。"
        )
        
        return if (cityProblems.isNotEmpty()) {
            "对${cityProblems.random()}感到不满，希望市政府重视！"
        } else {
            problemTemplates.random()
        }
    }
    
    private fun generateFallbackMonologue(citizen: Citizen): String {
        return when {
            citizen.happiness > 0.7f -> "心情不错，继续加油！"
            citizen.happiness < 0.4f -> "感觉有点累了..."
            else -> "普通的一天..."
        }
    }
    
    // ========== 辅助函数 ==========
    
    /**
     * 分析两个市民的关系
     */
    private fun analyzeRelationship(citizen1: Citizen, citizen2: Citizen): String {
        return when {
            citizen1.familyId == citizen2.familyId && 
            citizen1.maritalStatus == MaritalStatus.MARRIED -> "配偶"
            
            citizen1.workplaceX == citizen2.workplaceX && 
            citizen1.workplaceY == citizen2.workplaceY -> "同事"
            
            kotlin.math.abs(citizen1.homeX - citizen2.homeX) <= 1 && 
            kotlin.math.abs(citizen1.homeY - citizen2.homeY) <= 1 -> "邻居"
            
            citizen1.occupation == citizen2.occupation -> "同行"
            
            else -> "陌生人"
        }
    }
}

// ========== 扩展函数 ==========

private fun EducationLevel.getDisplayName(): String = when (this) {
    EducationLevel.PRIMARY -> "小学"
    EducationLevel.SECONDARY -> "初中"
    EducationLevel.HIGH_SCHOOL -> "高中"
    EducationLevel.COLLEGE -> "大学"
    EducationLevel.GRADUATE -> "研究生"
    EducationLevel.PHD -> "博士"
}

private fun MaritalStatus.getDisplayName(): String = when (this) {
    MaritalStatus.SINGLE -> "单身"
    MaritalStatus.MARRIED -> "已婚"
    MaritalStatus.DIVORCED -> "离异"
    MaritalStatus.WIDOWED -> "丧偶"
}

// 移除了CitizenActivity的扩展函数

