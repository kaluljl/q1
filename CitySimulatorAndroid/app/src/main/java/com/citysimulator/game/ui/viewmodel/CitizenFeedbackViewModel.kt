package com.citysimulator.game.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.IntelligentFeedbackGenerator
import com.citysimulator.game.ai.CitizenFeedbackData
import com.citysimulator.game.ai.FeedbackUrgency
import com.citysimulator.game.ai.FeedbackTriggerType
import com.citysimulator.game.ai.deepseek.DeepSeekClient
import com.citysimulator.game.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * 市民反馈ViewModel
 * 
 * 管理市民反馈的生成和更新
 * 每个游戏月生成一次心声
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@HiltViewModel
class CitizenFeedbackViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deepSeekClient: DeepSeekClient,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("citizen_feedback_prefs", Context.MODE_PRIVATE)
    
    private val _feedbacks = MutableStateFlow<List<CitizenFeedbackData>>(emptyList())
    val feedbacks: StateFlow<List<CitizenFeedbackData>> = _feedbacks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 记录上次生成反馈的游戏月份（年*12 + 月）
    // 使用 SavedStateHandle 持久化，避免因重建导致每次都当作首次
    private val _lastGenerationMonth = MutableStateFlow(
        savedStateHandle.get<Int>("last_generation_month") ?: -1
    )
    val lastGenerationMonth: StateFlow<Int> = _lastGenerationMonth.asStateFlow()
    
    // 是否已初始化（持久化）
    private var isInitialized: Boolean
        get() = savedStateHandle.get<Boolean>("feedback_initialized") ?: false
        set(value) { savedStateHandle["feedback_initialized"] = value }
    
    companion object {
        // 每次生成1条新反馈
        private const val MAX_NEW_FEEDBACKS_PER_UPDATE = 1
        private const val PREFS_KEY_FEEDBACKS = "feedbacks_json"
    }
    
    init {
        // 初始化时加载保存的反馈
        loadFeedbacksFromPrefs()
    }
    
    /**
     * 从SharedPreferences加载反馈
     */
    private fun loadFeedbacksFromPrefs() {
        try {
            val jsonString = prefs.getString(PREFS_KEY_FEEDBACKS, null)
            if (jsonString != null) {
                val jsonArray = JSONArray(jsonString)
                val feedbackList = mutableListOf<CitizenFeedbackData>()
                
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    val feedback = CitizenFeedbackData(
                        id = json.getString("id"),
                        content = json.getString("content"),
                        category = FeedbackType.valueOf(json.getString("category")),
                        urgency = FeedbackUrgency.valueOf(json.getString("urgency")),
                        triggerType = FeedbackTriggerType.valueOf(json.getString("triggerType")),
                        relatedData = JSONArray(json.getString("relatedData")).let { arr ->
                            List(arr.length()) { idx -> arr.getString(idx) }
                        },
                        affectedArea = json.getString("affectedArea"),
                        solutions = JSONArray(json.getString("solutions")).let { arr ->
                            List(arr.length()) { idx -> arr.getString(idx) }
                        },
                        timeLimit = json.getInt("timeLimit"),
                        icon = json.getString("icon"),
                        createdAt = json.getString("createdAt"),
                        isResolved = json.getBoolean("isResolved"),
                        resolvedAt = if (json.has("resolvedAt") && !json.isNull("resolvedAt")) json.getString("resolvedAt") else null,
                        escalationLevel = json.getInt("escalationLevel")
                    )
                    feedbackList.add(feedback)
                }
                
                _feedbacks.value = feedbackList
                println("📦 从缓存加载了 ${feedbackList.size} 条市民心声")
            }
        } catch (e: Exception) {
            println("❌ 加载反馈失败: ${e.message}")
        }
    }
    
    /**
     * 保存反馈到SharedPreferences
     */
    private fun saveFeedbacksToPrefs() {
        try {
            val jsonArray = JSONArray()
            _feedbacks.value.forEach { feedback ->
                val json = JSONObject().apply {
                    put("id", feedback.id)
                    put("content", feedback.content)
                    put("category", feedback.category.name)
                    put("urgency", feedback.urgency.name)
                    put("triggerType", feedback.triggerType.name)
                    put("relatedData", JSONArray(feedback.relatedData))
                    put("affectedArea", feedback.affectedArea)
                    put("solutions", JSONArray(feedback.solutions))
                    put("timeLimit", feedback.timeLimit)
                    put("icon", feedback.icon)
                    put("createdAt", feedback.createdAt)
                    put("isResolved", feedback.isResolved)
                    put("resolvedAt", feedback.resolvedAt ?: "")
                    put("escalationLevel", feedback.escalationLevel)
                }
                jsonArray.put(json)
            }
            
            prefs.edit().putString(PREFS_KEY_FEEDBACKS, jsonArray.toString()).apply()
            println("💾 保存了 ${_feedbacks.value.size} 条市民心声到缓存")
        } catch (e: Exception) {
            println("❌ 保存反馈失败: ${e.message}")
        }
    }
    
    /**
     * 根据城市状态生成反馈（每个游戏月生成一次）
     * @param gameYear 当前游戏年份
     * @param gameMonth 当前游戏月份（1-12）
     * @param gameTime 游戏时间字符串（格式：yyyy年MM月dd日 HH:mm）
     */
    fun generateFeedback(
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int,
        population: Int,
        gameYear: Int,
        gameMonth: Int,
        gameTime: String
    ) {
        viewModelScope.launch {
            try {
                // 计算当前游戏月份ID（年*12 + 月）
                val currentMonthId = gameYear * 12 + gameMonth
                
                // 首次初始化：生成初始心声，然后设置为已初始化
                if (!isInitialized) {
                    println("📅 心声系统首次初始化：${gameYear}年${gameMonth}月，生成初始心声...")
                    
                    // 如果人口为0，不生成心声
                    if (population == 0) {
                        println("📅 城市尚无居民，跳过初始心声生成")
                        _lastGenerationMonth.value = currentMonthId
                        savedStateHandle["last_generation_month"] = currentMonthId
                        isInitialized = true
                        return@launch
                    }
                    
                    // 生成初始心声（使用AI）
                    _isLoading.value = true
                    val initialFeedback = generateAIFeedback(
                        buildings = buildings,
                        goldAmount = goldAmount,
                        population = population,
                        gameTime = gameTime
                    )
                    
                    if (initialFeedback != null) {
                        _feedbacks.value = listOf(initialFeedback)
                        saveFeedbacksToPrefs() // 保存到缓存
                        println("💬 首次初始化生成了 1 条AI心声: ${initialFeedback.content}")
                    } else {
                        println("⚠️ 首次初始化AI心声生成失败")
                    }
                    
                    _lastGenerationMonth.value = currentMonthId
                    savedStateHandle["last_generation_month"] = currentMonthId
                    isInitialized = true
                    _isLoading.value = false
                    return@launch
                }
                
                // 检查是否是新的月份
                if (currentMonthId <= _lastGenerationMonth.value) {
                    // 还在同一个月，不生成新反馈
                    // 每10次调用输出一次日志（避免日志刷屏）
                    if (currentMonthId % 10 == 0) {
                        println("📅 当前月份 ${gameYear}年${gameMonth}月 (ID:$currentMonthId)，上次生成月份 ID:${_lastGenerationMonth.value}，无需生成新心声")
                    }
                    return@launch
                }
                
                // 如果人口为0，不生成心声（城市还未发展）
                if (population == 0) {
                    println("📅 ${gameYear}年${gameMonth}月：城市尚无居民，跳过心声生成")
                    _lastGenerationMonth.value = currentMonthId
                    savedStateHandle["last_generation_month"] = currentMonthId
                    return@launch
                }
                
                println("📅 新的游戏月份：${gameYear}年${gameMonth}月，生成市民心声...")
                
                _isLoading.value = true
                
                // 生成新反馈（使用AI）
                val newFeedback = generateAIFeedback(
                    buildings = buildings,
                    goldAmount = goldAmount,
                    population = population,
                    gameTime = gameTime
                )
                
                if (newFeedback != null) {
                    // 合并到现有反馈中（保留旧的未解决反馈）
                    val existingFeedbacks = _feedbacks.value.filter { !it.isResolved }
                    val combinedFeedbacks = (existingFeedbacks + newFeedback)
                        .distinctBy { it.id } // 去重
                        .sortedByDescending { 
                            when (it.urgency) {
                                FeedbackUrgency.LOW -> 1
                                FeedbackUrgency.MEDIUM -> 3
                                FeedbackUrgency.HIGH -> 4
                                FeedbackUrgency.CRITICAL -> 5
                            }
                        } // 按优先级排序
                        .take(10) // 最多保留10条
                    
                    _feedbacks.value = combinedFeedbacks
                    saveFeedbacksToPrefs() // 保存到缓存
                    println("💬 ${gameYear}年${gameMonth}月生成了 1 条新AI心声: ${newFeedback.content}，当前共 ${combinedFeedbacks.size} 条未解决反馈")
                } else {
                    println("⚠️ ${gameYear}年${gameMonth}月AI心声生成失败")
                }
                
                _lastGenerationMonth.value = currentMonthId
                savedStateHandle["last_generation_month"] = currentMonthId
                
            } catch (e: Exception) {
                println("❌ 生成反馈时出错: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 使用AI生成市民心声
     */
    private suspend fun generateAIFeedback(
        buildings: List<Building>,
        goldAmount: Int,
        population: Int,
        gameTime: String
    ): CitizenFeedbackData? {
        try {
            // 构建城市状态描述
            val cityStatus = buildCityStatusDescription(buildings, goldAmount, population, gameTime)
            
            // 调用DeepSeek API生成心声
            val aiMessage = deepSeekClient.generateCitizenFeedback(cityStatus)
            
            if (aiMessage.isBlank()) {
                println("⚠️ AI返回空心声，使用fallback")
                return generateFallbackFeedback(buildings, goldAmount, population)
            }
            
            // 创建CitizenFeedbackData对象
            return CitizenFeedbackData(
                id = UUID.randomUUID().toString(),
                content = aiMessage,
                category = FeedbackType.ENTERTAINMENT_NEED, // 使用通用类型
                urgency = FeedbackUrgency.MEDIUM,
                triggerType = FeedbackTriggerType.CONDITION_BASED,
                relatedData = listOf("AI生成"),
                affectedArea = "全城",
                solutions = listOf(),
                timeLimit = 30,
                icon = "💬",
                createdAt = gameTime,
                isResolved = false,
                resolvedAt = null,
                escalationLevel = 0
            )
        } catch (e: Exception) {
            println("❌ AI生成心声失败: ${e.message}")
            return generateFallbackFeedback(buildings, goldAmount, population)
        }
    }
    
    /**
     * 构建城市状态描述
     */
    private fun buildCityStatusDescription(
        buildings: List<Building>,
        goldAmount: Int,
        population: Int,
        gameTime: String
    ): String {
        val buildingTypes = buildings.groupBy { it.type }.mapValues { it.value.size }
        val residentialCount = buildingTypes.filter { 
            it.key.name.contains("HOUSE") || it.key.name.contains("APARTMENT") || 
            it.key.name.contains("VILLA") || it.key.name.contains("RESIDENCE")
        }.values.sum()
        val commercialCount = buildingTypes.filter { 
            it.key.name.contains("SHOP") || it.key.name.contains("STORE") || 
            it.key.name.contains("MALL")
        }.values.sum()
        val industrialCount = buildingTypes.filter { 
            it.key.name.contains("FACTORY") || it.key.name.contains("PLANT")
        }.values.sum()
        
        return """
游戏时间：$gameTime
人口：${population}人
建筑总数：${buildings.size}座
- 住宅：${residentialCount}座
- 商业：${commercialCount}座
- 工业：${industrialCount}座
城市金币：${goldAmount}
        """.trimIndent()
    }
    
    /**
     * Fallback心声生成（当AI失败时使用）
     */
    private fun generateFallbackFeedback(
        buildings: List<Building>,
        goldAmount: Int,
        population: Int
    ): CitizenFeedbackData {
        val messages = listOf(
            "希望城市能发展得更好。",
            "我们需要更多的基础设施。",
            "城市的环境还不错。",
            "希望能有更多的就业机会。",
            "生活成本有点高。"
        )
        
        return CitizenFeedbackData(
            id = UUID.randomUUID().toString(),
            content = messages.random(),
            category = FeedbackType.ENTERTAINMENT_NEED,
            urgency = FeedbackUrgency.LOW,
            triggerType = FeedbackTriggerType.CONDITION_BASED,
            relatedData = listOf("Fallback"),
            affectedArea = "全城",
            solutions = listOf(),
            timeLimit = 30,
            icon = "💬",
            createdAt = Date().toString(),
            isResolved = false,
            resolvedAt = null,
            escalationLevel = 0
        )
    }
    
    /**
     * 添加新的反馈
     */
    fun addFeedback(feedback: CitizenFeedbackData) {
        val currentFeedbacks = _feedbacks.value.toMutableList()
        currentFeedbacks.add(feedback)
        _feedbacks.value = currentFeedbacks.sortedByDescending { 
            when (it.urgency) {
                FeedbackUrgency.LOW -> 1
                FeedbackUrgency.MEDIUM -> 3
                FeedbackUrgency.HIGH -> 4
                FeedbackUrgency.CRITICAL -> 5
            }
        }
        saveFeedbacksToPrefs() // 保存到缓存
    }
    
    /**
     * 标记反馈为已解决
     * @param gameTime 游戏时间字符串（格式：yyyy年MM月dd日 HH:mm）
     */
    fun resolveFeedback(feedbackId: String, gameTime: String) {
        val currentFeedbacks = _feedbacks.value.toMutableList()
        val index = currentFeedbacks.indexOfFirst { it.id == feedbackId }
        if (index != -1) {
            val feedback = currentFeedbacks[index]
            val updatedFeedback = feedback.copy(
                isResolved = true,
                resolvedAt = gameTime
            )
            currentFeedbacks[index] = updatedFeedback
            _feedbacks.value = currentFeedbacks
            saveFeedbacksToPrefs() // 保存到缓存
            println("✅ 已解决市民心声: ${feedback.content}")
        }
    }
    
    /**
     * 删除反馈
     */
    fun deleteFeedback(feedbackId: String) {
        val currentFeedbacks = _feedbacks.value.toMutableList()
        currentFeedbacks.removeAll { it.id == feedbackId }
        _feedbacks.value = currentFeedbacks
        saveFeedbacksToPrefs() // 保存到缓存
        println("🗑️ 已删除市民心声")
    }
    
    /**
     * 获取未解决的反馈
     */
    fun getUnresolvedFeedbacks(): List<CitizenFeedbackData> {
        return _feedbacks.value.filter { !it.isResolved }
    }
    
    /**
     * 获取按优先级排序的反馈
     */
    fun getFeedbacksByPriority(): List<CitizenFeedbackData> {
        return _feedbacks.value.sortedByDescending { 
            when (it.urgency) {
                FeedbackUrgency.LOW -> 1
                FeedbackUrgency.MEDIUM -> 3
                FeedbackUrgency.HIGH -> 4
                FeedbackUrgency.CRITICAL -> 5
            }
        }
    }
    
    /**
     * 获取按类型分组的反馈
     */
    fun getFeedbacksByType(): Map<FeedbackType, List<CitizenFeedbackData>> {
        return _feedbacks.value.groupBy { it.category }
    }
    
    /**
     * 清除所有反馈
     */
    fun clearAllFeedbacks() {
        _feedbacks.value = emptyList()
        saveFeedbacksToPrefs() // 保存到缓存
        println("🧹 已清除所有市民心声")
    }
}

/**
 * 反馈统计数据类
 */
data class FeedbackStats(
    val total: Int,
    val unresolved: Int,
    val highPriority: Int,
    val emergency: Int
)
