package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlin.random.Random

/**
 * AI对话系统 - 与居民和城市AI助手互动
 */
class AIDialogueSystem {
    
    /**
     * 与NPC居民对话
     */
    fun chatWithNPC(npc: AINPC, playerMessage: String): NPCDialogueResponse {
        val sentiment = analyzeSentiment(playerMessage)
        val topic = identifyTopic(playerMessage)
        
        return when (topic) {
            DialogueTopic.GREETING -> generateGreeting(npc)
            DialogueTopic.HAPPINESS -> generateHappinessResponse(npc, sentiment)
            DialogueTopic.WORK -> generateWorkResponse(npc)
            DialogueTopic.CITY -> generateCityOpinion(npc)
            DialogueTopic.NEEDS -> generateNeedsResponse(npc)
            DialogueTopic.COMPLAINT -> generateComplaintResponse(npc)
            DialogueTopic.SUGGESTION -> generateSuggestionResponse(npc)
            else -> generateDefaultResponse(npc)
        }
    }
    
    /**
     * 城市AI助手对话
     */
    fun chatWithCityAssistant(
        playerMessage: String,
        cityData: CityData
    ): AssistantDialogueResponse {
        val intent = identifyIntent(playerMessage)
        
        return when (intent) {
            AssistantIntent.CITY_STATUS -> provideCityStatus(cityData)
            AssistantIntent.OPTIMIZATION_ADVICE -> provideOptimizationAdvice(cityData)
            AssistantIntent.BUILDING_RECOMMENDATION -> provideBuildingRecommendation(cityData)
            AssistantIntent.RESOURCE_MANAGEMENT -> provideResourceAdvice(cityData)
            AssistantIntent.POPULATION_INFO -> providePopulationInfo(cityData)
            AssistantIntent.FORECAST -> provideForecast(cityData)
            AssistantIntent.TUTORIAL -> provideTutorial(playerMessage)
            else -> provideDefaultAssistantResponse()
        }
    }
    
    /**
     * 分析玩家消息情感
     */
    private fun analyzeSentiment(message: String): Sentiment {
        val positiveWords = listOf("好", "棒", "喜欢", "满意", "开心", "不错", "很好")
        val negativeWords = listOf("差", "烂", "讨厌", "不满", "难过", "糟糕", "问题")
        
        val positiveCount = positiveWords.count { message.contains(it) }
        val negativeCount = negativeWords.count { message.contains(it) }
        
        return when {
            positiveCount > negativeCount -> Sentiment.POSITIVE
            negativeCount > positiveCount -> Sentiment.NEGATIVE
            else -> Sentiment.NEUTRAL
        }
    }
    
    /**
     * 识别对话主题
     */
    private fun identifyTopic(message: String): DialogueTopic {
        return when {
            message.contains("你好") || message.contains("嗨") -> DialogueTopic.GREETING
            message.contains("开心") || message.contains("快乐") || message.contains("满意") -> DialogueTopic.HAPPINESS
            message.contains("工作") || message.contains("上班") -> DialogueTopic.WORK
            message.contains("城市") || message.contains("这里") -> DialogueTopic.CITY
            message.contains("需要") || message.contains("想要") -> DialogueTopic.NEEDS
            message.contains("抱怨") || message.contains("不满") || message.contains("问题") -> DialogueTopic.COMPLAINT
            message.contains("建议") || message.contains("意见") -> DialogueTopic.SUGGESTION
            else -> DialogueTopic.GENERAL
        }
    }
    
    /**
     * 识别助手意图
     */
    private fun identifyIntent(message: String): AssistantIntent {
        return when {
            message.contains("状态") || message.contains("情况") || message.contains("怎么样") -> AssistantIntent.CITY_STATUS
            message.contains("优化") || message.contains("改进") || message.contains("提升") -> AssistantIntent.OPTIMIZATION_ADVICE
            message.contains("建造") || message.contains("建筑") || message.contains("建什么") -> AssistantIntent.BUILDING_RECOMMENDATION
            message.contains("资源") || message.contains("金币") -> AssistantIntent.RESOURCE_MANAGEMENT
            message.contains("人口") || message.contains("居民") -> AssistantIntent.POPULATION_INFO
            message.contains("预测") || message.contains("未来") || message.contains("发展") -> AssistantIntent.FORECAST
            message.contains("教程") || message.contains("怎么玩") || message.contains("帮助") -> AssistantIntent.TUTORIAL
            else -> AssistantIntent.GENERAL
        }
    }
    
    // ========== NPC对话生成 ==========
    
    private fun generateGreeting(npc: AINPC): NPCDialogueResponse {
        val greetings = when (npc.personality) {
            Personality.EXTROVERT -> listOf(
                "你好呀！很高兴见到你！",
                "嗨！今天天气真不错！",
                "你好！有什么我可以帮忙的吗？"
            )
            Personality.INTROVERT -> listOf(
                "你好...",
                "嗯，你好。",
                "你好，有事吗？"
            )
            else -> listOf(
                "你好！",
                "您好。",
                "嗨！"
            )
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = greetings.random(),
            emotion = when {
                npc.happiness > 70 -> NPCEmotion.HAPPY
                npc.happiness > 40 -> NPCEmotion.NEUTRAL
                else -> NPCEmotion.SAD
            },
            relationshipChange = 5
        )
    }
    
    private fun generateHappinessResponse(npc: AINPC, sentiment: Sentiment): NPCDialogueResponse {
        val message = when {
            npc.happiness > 80 -> "我现在非常开心！这座城市真是太棒了！"
            npc.happiness > 60 -> "我感觉还不错，生活挺好的。"
            npc.happiness > 40 -> "还行吧，有些地方可以改进。"
            npc.happiness > 20 -> "说实话，我不太满意现在的生活..."
            else -> "我很不开心，这里的生活太糟糕了。"
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = message,
            emotion = when {
                npc.happiness > 60 -> NPCEmotion.HAPPY
                npc.happiness > 30 -> NPCEmotion.NEUTRAL
                else -> NPCEmotion.SAD
            },
            relationshipChange = if (sentiment == Sentiment.POSITIVE) 3 else 0
        )
    }
    
    private fun generateWorkResponse(npc: AINPC): NPCDialogueResponse {
        val message = when {
            npc.workLocation == null -> "我现在还没有工作，希望能找到一份好工作。"
            npc.personality == Personality.WORKAHOLIC -> "工作让我感到充实！我热爱我的工作！"
            npc.personality == Personality.LAZY -> "工作...唉，能不能少点工作时间？"
            npc.energy < 30 -> "工作太累了，我需要休息..."
            else -> "工作还不错，我对现在的工作比较满意。"
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = message,
            emotion = when {
                npc.workLocation != null && npc.energy > 50 -> NPCEmotion.HAPPY
                npc.workLocation == null -> NPCEmotion.SAD
                else -> NPCEmotion.NEUTRAL
            },
            relationshipChange = 2
        )
    }
    
    private fun generateCityOpinion(npc: AINPC): NPCDialogueResponse {
        val opinions = mutableListOf<String>()
        
        if (npc.happiness > 70) {
            opinions.add("这座城市建设得很好！")
        } else if (npc.happiness < 40) {
            opinions.add("城市还需要很多改进...")
        }
        
        if (npc.needsFood()) {
            opinions.add("希望能有更多餐厅和商店。")
        }
        
        if (npc.needsSocial()) {
            opinions.add("我们需要更多娱乐设施和公共空间。")
        }
        
        if (npc.needsRest()) {
            opinions.add("城市太吵了，我需要安静的地方休息。")
        }
        
        val message = if (opinions.isEmpty()) {
            "这座城市还可以，没什么特别的意见。"
        } else {
            opinions.joinToString(" ")
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = message,
            emotion = NPCEmotion.NEUTRAL,
            relationshipChange = 3
        )
    }
    
    private fun generateNeedsResponse(npc: AINPC): NPCDialogueResponse {
        val needs = mutableListOf<String>()
        
        if (npc.needsRest()) needs.add("我需要休息")
        if (npc.needsFood()) needs.add("我饿了")
        if (npc.needsSocial()) needs.add("我想和朋友聊聊天")
        
        val message = if (needs.isEmpty()) {
            "我现在什么都不缺，谢谢关心！"
        } else {
            "现在${needs.joinToString("，")}。"
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = message,
            emotion = if (needs.isEmpty()) NPCEmotion.HAPPY else NPCEmotion.NEUTRAL,
            relationshipChange = 5
        )
    }
    
    private fun generateComplaintResponse(npc: AINPC): NPCDialogueResponse {
        val complaints = mutableListOf<String>()
        
        if (npc.energy < 30) complaints.add("工作太累了")
        if (npc.hunger > 70) complaints.add("食物不够")
        if (npc.socialNeed > 70) complaints.add("太孤独了")
        if (npc.happiness < 40) complaints.add("生活质量不高")
        
        val message = if (complaints.isEmpty()) {
            "其实我没什么好抱怨的，一切都还好。"
        } else {
            "我想说，${complaints.joinToString("，")}。希望能改善一下。"
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = message,
            emotion = if (complaints.isEmpty()) NPCEmotion.NEUTRAL else NPCEmotion.SAD,
            relationshipChange = 4
        )
    }
    
    private fun generateSuggestionResponse(npc: AINPC): NPCDialogueResponse {
        val suggestions = when (npc.personality) {
            Personality.CREATIVE -> "建议增加更多艺术和文化设施！"
            Personality.PRACTICAL -> "应该优化城市布局，提高效率。"
            Personality.EXTROVERT -> "多建一些社交场所会更好！"
            Personality.INTROVERT -> "希望有更多安静的休息区域。"
            else -> "我觉得城市发展得不错，继续保持！"
        }
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = suggestions,
            emotion = NPCEmotion.HAPPY,
            relationshipChange = 8
        )
    }
    
    private fun generateDefaultResponse(npc: AINPC): NPCDialogueResponse {
        val responses = listOf(
            "嗯，我明白了。",
            "是的，你说得对。",
            "好的，谢谢。",
            "我会考虑的。"
        )
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = responses.random(),
            emotion = NPCEmotion.NEUTRAL,
            relationshipChange = 1
        )
    }
    
    // ========== 城市助手对话生成 ==========
    
    private fun provideCityStatus(cityData: CityData): AssistantDialogueResponse {
        val status = """
            📊 城市状态报告：
            
            🏙️ 人口：${cityData.population}人
            💰 金币：${cityData.gold}
            🏢 建筑数量：${cityData.buildingCount}
            😊 平均满意度：${String.format("%.1f", cityData.avgHappiness)}%
            ⚡ 平均能量：${String.format("%.1f", cityData.avgEnergy)}%
            
            ${if (cityData.avgHappiness > 70) "✅ 城市运行良好！" else "⚠️ 需要关注居民满意度"}
        """.trimIndent()
        
        return AssistantDialogueResponse(
            message = status,
            suggestions = listOf("查看优化建议", "查看人口详情", "查看发展预测"),
            actionButtons = listOf("优化", "详情", "预测")
        )
    }
    
    private fun provideOptimizationAdvice(cityData: CityData): AssistantDialogueResponse {
        val advice = mutableListOf<String>()
        
        if (cityData.avgHappiness < 60) {
            advice.add("🎭 建议增加娱乐设施提升居民满意度")
        }
        
        if (cityData.avgEnergy < 50) {
            advice.add("🏥 建议增加医疗和休息设施")
        }
        
        if (cityData.buildingCount < cityData.population / 5) {
            advice.add("🏗️ 建筑数量不足，建议扩建")
        }
        
        val message = if (advice.isEmpty()) {
            "✅ 您的城市运行得很好！暂时没有紧急的优化建议。"
        } else {
            "💡 优化建议：\n\n" + advice.joinToString("\n")
        }
        
        return AssistantDialogueResponse(
            message = message,
            suggestions = listOf("查看详细分析", "开始建造", "查看预算"),
            actionButtons = listOf("分析", "建造", "预算")
        )
    }
    
    private fun provideBuildingRecommendation(cityData: CityData): AssistantDialogueResponse {
        val recommendations = mutableListOf<String>()
        
        if (cityData.avgHappiness < 60) {
            recommendations.add("🎪 娱乐设施（公园、电影院）- 提升居民满意度")
        }
        
        if (cityData.population > cityData.buildingCount * 3) {
            recommendations.add("🏠 住宅建筑 - 容纳更多居民")
        }
        
        if (cityData.gold > 5000) {
            recommendations.add("🏢 商业建筑 - 增加收入来源")
        }
        
        val message = "🏗️ 建造推荐：\n\n" + 
            if (recommendations.isEmpty()) {
                "当前城市发展均衡，可以根据个人喜好建造。"
            } else {
                recommendations.joinToString("\n\n")
            }
        
        return AssistantDialogueResponse(
            message = message,
            suggestions = listOf("打开建筑菜单", "查看预算", "查看地图"),
            actionButtons = listOf("建造", "预算", "地图")
        )
    }
    
    private fun provideResourceAdvice(cityData: CityData): AssistantDialogueResponse {
        val message = """
            💰 资源管理建议：
            
            当前金币：${cityData.gold}
            每日收入：约 ${cityData.buildingCount * 10} 金币
            
            ${when {
                cityData.gold < 1000 -> "⚠️ 资源紧张，建议优先建造收入型建筑"
                cityData.gold < 5000 -> "✅ 资源充足，可以稳步发展"
                else -> "💎 资源丰富，可以大胆投资高级建筑"
            }}
        """.trimIndent()
        
        return AssistantDialogueResponse(
            message = message,
            suggestions = listOf("查看收入详情", "优化支出", "投资建议"),
            actionButtons = listOf("收入", "支出", "投资")
        )
    }
    
    private fun providePopulationInfo(cityData: CityData): AssistantDialogueResponse {
        val message = """
            👥 人口信息：
            
            总人口：${cityData.population}人
            平均满意度：${String.format("%.1f", cityData.avgHappiness)}%
            就业率：${String.format("%.1f", cityData.employmentRate)}%
            
            ${when {
                cityData.avgHappiness > 70 -> "😊 居民整体满意，城市吸引力高"
                cityData.avgHappiness > 40 -> "😐 居民满意度一般，需要改善"
                else -> "😟 居民不满意，需要紧急关注"
            }}
        """.trimIndent()
        
        return AssistantDialogueResponse(
            message = message,
            suggestions = listOf("查看居民详情", "提升满意度", "增加就业"),
            actionButtons = listOf("详情", "满意度", "就业")
        )
    }
    
    private fun provideForecast(cityData: CityData): AssistantDialogueResponse {
        val growthRate = if (cityData.avgHappiness > 60) 10 else 5
        val predictedPopulation = cityData.population + growthRate
        
        val message = """
            🔮 城市发展预测（未来1个月）：
            
            预计人口：${predictedPopulation}人 (+${growthRate})
            预计收入：${cityData.buildingCount * 300} 金币
            
            发展趋势：${if (cityData.avgHappiness > 60) "📈 稳步增长" else "📉 需要改善"}
            
            建议：${if (cityData.avgHappiness > 60) 
                "继续保持当前发展策略" 
            else 
                "优先提升居民满意度以促进增长"}
        """.trimIndent()
        
        return AssistantDialogueResponse(
            message = message,
            suggestions = listOf("查看详细预测", "制定计划", "风险分析"),
            actionButtons = listOf("预测", "计划", "风险")
        )
    }
    
    private fun provideTutorial(message: String): AssistantDialogueResponse {
        val tutorial = """
            📚 城市模拟器教程：
            
            1️⃣ 建造建筑：点击底部"建筑"按钮，选择要建造的建筑类型
            2️⃣ 管理资源：关注顶部的金币和资源显示
            3️⃣ 关注居民：居民会自动生活，注意他们的需求
            4️⃣ 优化城市：根据AI建议优化城市布局
            
            💡 提示：保持居民满意度高可以吸引更多人口！
        """.trimIndent()
        
        return AssistantDialogueResponse(
            message = tutorial,
            suggestions = listOf("开始建造", "查看示例", "更多教程"),
            actionButtons = listOf("开始", "示例", "教程")
        )
    }
    
    private fun provideDefaultAssistantResponse(): AssistantDialogueResponse {
        return AssistantDialogueResponse(
            message = "我是您的城市AI助手，可以帮您分析城市状态、提供优化建议、回答问题。请问有什么可以帮您的吗？",
            suggestions = listOf("查看城市状态", "优化建议", "建造推荐", "教程"),
            actionButtons = listOf("状态", "优化", "建造", "帮助")
        )
    }
}

// ========== 数据类定义 ==========

enum class DialogueTopic {
    GREETING, HAPPINESS, WORK, CITY, NEEDS, COMPLAINT, SUGGESTION, GENERAL
}

enum class AssistantIntent {
    CITY_STATUS, OPTIMIZATION_ADVICE, BUILDING_RECOMMENDATION,
    RESOURCE_MANAGEMENT, POPULATION_INFO, FORECAST, TUTORIAL, GENERAL
}

enum class Sentiment {
    POSITIVE, NEUTRAL, NEGATIVE
}

enum class NPCEmotion {
    HAPPY, NEUTRAL, SAD, ANGRY, EXCITED
}

data class NPCDialogueResponse(
    val npcName: String,
    val message: String,
    val emotion: NPCEmotion,
    val relationshipChange: Int
)

data class AssistantDialogueResponse(
    val message: String,
    val suggestions: List<String>,
    val actionButtons: List<String>
)

data class CityData(
    val population: Int,
    val gold: Int,
    val buildingCount: Int,
    val avgHappiness: Float,
    val avgEnergy: Float,
    val employmentRate: Float = 0f
)

