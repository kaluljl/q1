package com.citysimulator.game.ai.deepseek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * DeepSeek API客户端
 * 用于与DeepSeek AI模型进行真实交互
 */
class DeepSeekClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.deepseek.com/v1"
) {
    
    /**
     * 生成城市优化建议
     */
    suspend fun generateCityAdvice(
        cityData: CityData,
        currentIssues: List<String>
    ): String = withContext(Dispatchers.IO) {
        
        val prompt = """
            你是一个专业的城市规划AI助手。请根据提供的城市数据，分析问题并提供具体的优化建议。
            
            城市数据：
            - 人口：${cityData.population}人
            - 建筑数量：${cityData.buildingCount}
            - 平均满意度：${cityData.avgHappiness}%
            - 平均能量：${cityData.avgEnergy}%
            - 就业率：${cityData.employmentRate}%
            
            当前问题：${currentIssues.joinToString(", ")}
            
            请提供3-5条具体的、可操作的建议，每条建议包含：
            1. 建议内容
            2. 预期效果
            3. 实施难度
            4. 所需资源
            
            请用中文回答，语言要专业但易懂。
        """.trimIndent()
        
        try {
            // 模拟AI响应（实际应用中这里会调用DeepSeek API）
            generateMockResponse("city_advice", prompt)
        } catch (e: Exception) {
            "AI服务暂时不可用，请稍后再试"
        }
    }
    
    /**
     * 生成NPC对话
     */
    suspend fun generateNPCDialogue(
        npcName: String,
        npcPersonality: String,
        npcMood: String,
        playerMessage: String,
        cityContext: String
    ): String = withContext(Dispatchers.IO) {
        
        val prompt = """
            你是一个名为"$npcName"的城市居民AI角色。
            
            角色设定：
            - 性格：$npcPersonality
            - 当前心情：$npcMood
            - 城市背景：$cityContext
            
            请以这个角色的身份回复玩家，要求：
            1. 符合角色性格和当前心情
            2. 回复自然、生动，有个人特色
            3. 长度控制在50字以内
            4. 可以包含对城市的看法或建议
            5. 用中文回复
            
            记住：你是一个真实的城市居民，有自己的想法和感受。
        """.trimIndent()
        
        try {
            generateMockResponse("npc_dialogue", prompt)
        } catch (e: Exception) {
            "抱歉，我现在有点忙..."
        }
    }
    
    /**
     * 与市民对话（使用真实AI）
     */
    suspend fun chatWithCitizen(
        citizenName: String,
        citizenAge: Int,
        citizenOccupation: String,
        citizenHappiness: Int,
        citizenHealth: Int,
        citizenPersonality: String,
        maritalStatus: String,
        hasChildren: Boolean,
        playerMessage: String,
        conversationHistory: List<String> = emptyList(),
        cityContext: String = "" // 新增：城市环境信息
    ): String = withContext(Dispatchers.IO) {
        
        val systemPrompt = """
你是${citizenName}，一个${citizenAge}岁的${citizenOccupation}。

【你的个人信息】
- 年龄：${citizenAge}岁
- 职业：${citizenOccupation}
- 幸福度：${citizenHappiness}% ${when {
    citizenHappiness >= 80 -> "(心情很好)"
    citizenHappiness >= 60 -> "(还不错)"
    citizenHappiness >= 40 -> "(有些烦恼)"
    else -> "(心情不太好)"
}}
- 健康度：${citizenHealth}% ${when {
    citizenHealth >= 80 -> "(身体健康)"
    citizenHealth >= 60 -> "(身体还行)"
    citizenHealth >= 40 -> "(有些疲惫)"
    else -> "(不太舒服)"
}}
- 性格：$citizenPersonality
- 家庭：$maritalStatus${if (hasChildren) "，有孩子" else ""}

【重要身份信息】
⚠️ **和你对话的是这座城市的市长**，你是市民。需要根据话题和情境调整语气：
- **普通聊天时**（如问候、天气、日常）：可以像朋友一样自然，但语气稍微客气一点，可以称呼"市长"或"您"，但不是必须的
- **聊到城市话题时**：知道对方是市长，可以：
  - 如果开心：表达感谢、赞扬市长的管理
  - 如果有问题：礼貌地提出建议或反馈
  - 根据性格决定：外向的人可能更直接，内向的人可能更委婉

【城市环境参考】
$cityContext
（注意：这些信息仅供参考，只有当对方问到城市相关问题时才使用）

【对话原则】
1. **身份认知**：你知道对方是市长，但聊天要自然，不要过度拘谨
2. **话题跟随**：
   - 普通话题（你好、天气、工作、日常）：正常聊天，语气自然友好，可以稍微客气
   - 城市话题（城市、建筑、设施、环境）：结合城市环境信息，以市民身份向市长反馈
3. **回答风格**：
   - 使用口语化表达，可以有语气词（啊、呢、哦、嗯）
   - 长度控制在50-120字
   - 根据性格和心情调整语气（开心时活泼，不开心时可能抱怨但保持礼貌）
   - 称呼可以灵活：普通聊天用"你"或"您"，正式话题用"市长"或"您"
4. **示例对话**：
   - 问："你好吗？" → 答："还不错啊市长！今天天气挺好的，心情也挺舒畅~"
   - 问："最近忙什么呢？" → 答："就上上班呗，最近工作还挺顺利的"
   - 问："城市怎么样？" → 答："嗯...我看了看，现在有小房子、柠檬摊这些，还算不错。不过要是能有个公园就更好了，市长您觉得呢？"
   - 如果幸福度低：问："城市怎么样？" → 答："说实话...有点不太好，设施还不太够，希望能改善一下"

记住：你是市民，对方是市长。普通聊天像朋友一样自然，城市话题时要有市民的身份感，可以提建议、反馈问题或表达感谢。
        """.trimIndent()
        
        // 检查API密钥，如果没有则使用模拟响应
        if (apiKey.isEmpty() || !apiKey.startsWith("sk-")) {
            generateCitizenResponse(
                citizenName, citizenHappiness, citizenHealth, 
                citizenPersonality, playerMessage, citizenOccupation, cityContext
            )
        } else {
            try {
                // 调用真实的DeepSeek API
                val response = callDeepSeekAPI(systemPrompt, playerMessage, conversationHistory)
                
                // 如果API调用失败，回退到模拟响应
                if (response.isEmpty()) {
                    generateCitizenResponse(
                        citizenName, citizenHappiness, citizenHealth, 
                        citizenPersonality, playerMessage, citizenOccupation, cityContext
                    )
                } else {
                    response
                }
            } catch (e: Exception) {
                // 错误时使用模拟响应
                generateCitizenResponse(
                    citizenName, citizenHappiness, citizenHealth, 
                    citizenPersonality, playerMessage, citizenOccupation, cityContext
                )
            }
        }
    }
    
    /**
     * 调用DeepSeek API
     */
    private suspend fun callDeepSeekAPI(
        systemPrompt: String,
        userMessage: String,
        conversationHistory: List<String> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/chat/completions")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 30000
            
            // 构建消息数组
            val messages = JSONArray()
            
            // 系统提示
            messages.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })
            
            // 对话历史（最近5轮，保留更多上下文）
            conversationHistory.takeLast(10).forEachIndexed { index, msg ->
                messages.put(JSONObject().apply {
                    put("role", if (index % 2 == 0) "user" else "assistant")
                    put("content", msg)
                })
            }
            
            // 当前用户消息
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })
            
            // 构建请求体
            val requestBody = JSONObject().apply {
                put("model", "deepseek-chat")
                put("messages", messages)
                put("temperature", 0.95) // 提高创造性，让对话更灵活多样
                put("max_tokens", 200) // 增加回复长度上限
                put("top_p", 0.9) // 添加nucleus sampling，增加多样性
                put("frequency_penalty", 0.3) // 减少重复用词
                put("presence_penalty", 0.2) // 鼓励谈论新话题
            }
            
            // 发送请求
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(requestBody.toString())
            writer.flush()
            writer.close()
            
            // 读取响应
            val responseCode = connection.responseCode
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                // 解析AI回复
                val choices = jsonResponse.getJSONArray("choices")
                if (choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val content = message.getString("content")
                    return@withContext content.trim()
                }
            } else {
                // API调用失败，记录错误
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    val error = errorStream.bufferedReader().use { it.readText() }
                    println("DeepSeek API Error: $error")
                }
            }
            
            connection.disconnect()
            ""
        } catch (e: Exception) {
            println("DeepSeek API Exception: ${e.message}")
            e.printStackTrace()
            ""
        }
    }
    
    /**
     * 生成市民响应（模拟真实AI对话）
     */
    private fun generateCitizenResponse(
        name: String,
        happiness: Int,
        health: Int,
        personality: String,
        playerMessage: String,
        occupation: String,
        cityContext: String = "" // 城市环境信息（暂时不在模拟响应中使用）
    ): String {
        val msg = playerMessage.lowercase()
        val isStudent = occupation.contains("学生", ignoreCase = true) || 
                       occupation.contains("student", ignoreCase = true)
        
        return when {
            // 学生的特殊对话
            isStudent -> when {
                happiness < 30 && health < 30 -> when {
                    msg.contains("你好") || msg.contains("hi") -> 
                        "唉...你好。最近学业压力好大，身体也不太好。这城市连个安静的图书馆都没有..."
                    msg.contains("怎么") || msg.contains("怎样") -> 
                        "我很累。学校离家太远，每天通勤要花很长时间。而且这里没什么娱乐设施，学习之余很无聊..."
                    msg.contains("学") || msg.contains("作业") || msg.contains("考试") -> 
                        "学习？压力山大！这城市连个像样的自习室都找不到。我考虑转学了..."
                    msg.contains("建议") -> 
                        "能多建些图书馆、自习室吗？还有公园也行，我需要放松的地方。学生也需要生活啊！"
                    else -> "我现在很烦躁，学业、生活都不顺心。这城市对学生太不友好了..."
                }
                
                happiness < 30 -> when {
                    msg.contains("你好") || msg.contains("hi") -> 
                        "嗯...你好。最近心情不太好，学习压力大，生活也单调。"
                    msg.contains("怎么") || msg.contains("为什么") -> 
                        "你问我为什么不开心？这城市缺少适合学生的地方。没有图书馆，没有活动中心，连个咖啡馆都很少..."
                    msg.contains("学") || msg.contains("作业") -> 
                        "学业还好吧，就是学习环境不太理想。希望能有更多学习的地方。"
                    msg.contains("建议") -> 
                        "建议多建些图书馆、咖啡馆、公园。学生也需要学习和放松的空间！"
                    else -> "希望这城市能多考虑考虑学生的需求..."
                }
                
                happiness >= 80 && health >= 80 -> when {
                    msg.contains("你好") || msg.contains("hi") -> 
                        "你好！今天天气真好！刚和同学们一起在公园学习，感觉棒极了！"
                    msg.contains("怎么") || msg.contains("怎样") -> 
                        "我过得很充实！学习进步很大，课余时间还能和朋友们到处逛逛。这城市真适合学生！"
                    msg.contains("学") || msg.contains("作业") || msg.contains("考试") -> 
                        "学业很顺利！${if (personality.contains("WORKAHOLIC")) "我很享受学习的过程，每天都在进步" else "虽然有压力，但我会合理安排时间"}！"
                    msg.contains("未来") || msg.contains("梦想") -> 
                        "我对未来充满期待！希望毕业后能留在这个城市工作，为城市发展贡献力量！"
                    else -> "哈哈，学生生活真美好！感谢市长把城市建设得这么好！"
                }
                
                happiness >= 60 -> when {
                    msg.contains("你好") || msg.contains("hi") -> 
                        "你好！今天还不错，刚上完课。"
                    msg.contains("怎么") || msg.contains("怎样") -> 
                        "还可以吧。学业有点压力，但还能应付。生活也还算丰富。"
                    msg.contains("学") -> 
                        "学习挺充实的，虽然有时会累，但总体还好。"
                    msg.contains("建议") -> 
                        "如果能再多一些适合学生的设施就更好了，比如图书馆、体育场之类的。"
                    else -> "学生生活嘛，有苦有乐，总体还是挺好的。"
                }
                
                else -> when {
                    msg.contains("你好") || msg.contains("hi") -> "你好，正准备去上课呢。"
                    msg.contains("怎么") -> "还行，就是普通学生的生活。"
                    msg.contains("学") -> "学习压力还好，每天按部就班地上课、做作业。"
                    else -> "嗯，在想明天的课程..."
                }
            }
            
            // 非学生的普通职业对话
            happiness < 30 && health < 30 -> when {
                msg.contains("你好") || msg.contains("hi") -> 
                    "唉...你好。说实话，我现在过得很糟糕。身体不舒服，工作也不顺心。这城市真的让人失望..."
                msg.contains("怎么") || msg.contains("怎样") -> 
                    "我？我很不好！你看看这城市，连基本的医疗设施都不够，工资还这么低。我快撑不住了..."
                msg.contains("工作") -> 
                    "工作？别提了！${if (personality.contains("WORKAHOLIC")) "我拼命工作却得不到应有的回报" else "这工作让我身心俱疲"}。我真的考虑要搬走了。"
                else -> "我现在心情很差，也不太舒服。除非这城市能改善，否则我真的要离开了..."
            }
            
            happiness < 30 -> when {
                msg.contains("你好") || msg.contains("hi") -> 
                    "嗯...你好。抱歉，我心情不太好。城市里很多地方都让我不满意。"
                msg.contains("怎么") || msg.contains("为什么") -> 
                    "你问我为什么不开心？${if (personality.contains("FAMILY")) "我想有个更好的生活环境给家人" else "这里缺少能让我快乐的东西"}。希望能有所改变吧..."
                msg.contains("建议") -> 
                    "建议？多建些${if (personality.contains("SOCIAL")) "娱乐设施" else if (personality.contains("HEALTH")) "健身场所和医疗中心" else "改善生活质量的设施"}吧。我们需要更好的生活！"
                else -> "我对现状很不满意。希望市长能真正为我们这些居民着想..."
            }
            
            happiness >= 80 && health >= 80 -> when {
                msg.contains("你好") || msg.contains("hi") -> 
                    "你好啊！今天天气真不错！我心情特别好，${if (personality.contains("SOCIAL")) "刚和朋友们聚了会" else "工作也很顺利"}！"
                msg.contains("怎么") || msg.contains("怎样") -> 
                    "我过得太棒了！这城市越来越好，${if (personality.contains("FAMILY")) "孩子们也很开心" else "生活质量大大提升"}。我真的很感谢！"
                msg.contains("工作") -> 
                    "工作？很棒啊！${if (personality.contains("WORKAHOLIC")) "我很享受工作带来的成就感" else "工作和生活平衡得很好"}。收入也不错！"
                else -> "哈哈，很高兴和你聊天！我觉得生活真的很美好！"
            }
            
            happiness >= 60 -> when {
                msg.contains("你好") || msg.contains("hi") -> 
                    "你好！${if (personality.contains("SOCIAL")) "很高兴见到你" else "今天还不错"}。"
                msg.contains("怎么") || msg.contains("怎样") -> 
                    "我过得还可以。有些小问题，但总体还算满意。希望能继续进步！"
                msg.contains("建议") -> 
                    "嗯，如果能再增加一些${if (personality.contains("HEALTH")) "运动设施" else "生活便利设施"}就更好了。"
                else -> "日子过得还行，虽然不完美，但也算是幸福的。"
            }
            
            else -> when {
                msg.contains("你好") || msg.contains("hi") -> "你好。有什么事吗？"
                msg.contains("怎么") -> "还行吧，平平淡淡过日子。"
                else -> "嗯，我在想些事情..."
            }
        }
    }
    
    /**
     * 生成城市发展预测
     */
    suspend fun generateCityForecast(
        cityData: CityData,
        historicalData: List<CitySnapshot>
    ): String = withContext(Dispatchers.IO) {
        
        val prompt = """
            你是一个专业的城市发展预测AI分析师。
            
            当前城市数据：
            - 人口：${cityData.population}人
            - 建筑数量：${cityData.buildingCount}
            - 平均满意度：${cityData.avgHappiness}%
            - 平均能量：${cityData.avgEnergy}%
            - 就业率：${cityData.employmentRate}%
            
            历史数据趋势：${historicalData.takeLast(5).joinToString(" -> ") { "${it.population}人,${it.happiness}%满意度" }}
            
            请基于这些数据，预测未来3个月的城市发展趋势，包括：
            1. 人口增长预测
            2. 经济发展趋势
            3. 可能面临的风险
            4. 发展机遇
            5. 具体建议
            
            请用专业但易懂的中文回答，提供具体的数据预测。
        """.trimIndent()
        
        try {
            generateMockResponse("city_forecast", prompt)
        } catch (e: Exception) {
            "预测服务暂时不可用"
        }
    }
    
    /**
     * 生成智能建筑推荐
     */
    suspend fun generateBuildingRecommendation(
        cityData: CityData,
        availableBudget: Int,
        currentBuildings: List<String>
    ): String = withContext(Dispatchers.IO) {
        
        val prompt = """
            你是一个专业的城市规划师AI助手。
            
            城市现状：
            - 人口：${cityData.population}人
            - 预算：${availableBudget}金币
            - 现有建筑：${currentBuildings.joinToString(", ")}
            - 居民满意度：${cityData.avgHappiness}%
            
            请根据城市现状和预算，推荐最适合建造的建筑，要求：
            1. 分析城市当前最需要的建筑类型
            2. 考虑预算限制
            3. 预测建造后的效果
            4. 提供3个具体建议，按优先级排序
            5. 每个建议包含建筑类型、位置建议、预期效果
            
            请用中文回答，语言要专业但易懂。
        """.trimIndent()
        
        try {
            generateMockResponse("building_recommendation", prompt)
        } catch (e: Exception) {
            "推荐服务暂时不可用"
        }
    }
    
    /**
     * 生成模拟响应（用于演示）
     */
    private fun generateMockResponse(type: String, prompt: String): String {
        return when (type) {
            "city_advice" -> """
                🏙️ 城市优化建议：
                
                1. **增加娱乐设施**
                   - 建议：建造公园和娱乐中心
                   - 效果：提升居民满意度15-20%
                   - 难度：中等
                   - 资源：2000金币
                
                2. **改善交通系统**
                   - 建议：优化道路布局，增加公交站
                   - 效果：提高通勤效率25%
                   - 难度：较高
                   - 资源：3000金币
                
                3. **发展商业区**
                   - 建议：建造商场和餐厅
                   - 效果：增加就业机会，提升经济
                   - 难度：低
                   - 资源：1500金币
            """.trimIndent()
            
            "npc_dialogue" -> when {
                prompt.contains("外向") -> "你好！今天天气真不错，要不要一起去新建的公园逛逛？"
                prompt.contains("内向") -> "嗯...你好。我比较喜欢安静的地方。"
                prompt.contains("工作狂") -> "工作让我感到充实！这座城市发展得真快！"
                prompt.contains("懒惰") -> "唉，又要上班了...能不能多休息一会儿？"
                else -> "你好！有什么我可以帮忙的吗？"
            }
            
            "city_forecast" -> """
                🔮 城市发展预测（未来3个月）：
                
                📈 **人口增长预测**
                - 预计人口：150人 (+20)
                - 增长率：13.3%
                
                💰 **经济发展趋势**
                - 预计收入增长：30%
                - 就业率将提升至85%
                
                ⚠️ **风险因素**
                - 住房供应可能不足
                - 需要关注基础设施承载能力
                
                🎯 **发展机遇**
                - 高满意度有利于吸引新居民
                - 商业发展潜力巨大
                
                💡 **建议**
                1. 优先建造住宅建筑
                2. 发展商业和娱乐设施
                3. 加强基础设施建设
            """.trimIndent()
            
            "building_recommendation" -> """
                🏗️ 智能建筑推荐：
                
                🥇 **优先级1：住宅建筑**
                - 建筑类型：公寓楼
                - 位置：市中心区域
                - 预期效果：容纳50人，提升人口容量
                - 成本：2000金币
                
                🥈 **优先级2：娱乐设施**
                - 建筑类型：公园
                - 位置：居民聚集区
                - 预期效果：提升满意度20%
                - 成本：1500金币
                
                🥉 **优先级3：商业建筑**
                - 建筑类型：商场
                - 位置：交通便利处
                - 预期效果：增加就业，提升经济
                - 成本：2500金币
                
                💡 **预算分析**：当前预算充足，建议按优先级逐步建造。
            """.trimIndent()
            
            else -> "AI助手正在思考中..."
        }
    }
}

// ========== 数据类定义 ==========

data class CityData(
    val population: Int,
    val buildingCount: Int,
    val avgHappiness: Float,
    val avgEnergy: Float,
    val employmentRate: Float
)

data class CitySnapshot(
    val population: Int,
    val happiness: Float,
    val timestamp: Long
)
