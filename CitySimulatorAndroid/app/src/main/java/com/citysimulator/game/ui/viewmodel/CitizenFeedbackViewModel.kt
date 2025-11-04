package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.IntelligentFeedbackGenerator
import com.citysimulator.game.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _feedbacks = MutableStateFlow<List<CitizenFeedback>>(emptyList())
    val feedbacks: StateFlow<List<CitizenFeedback>> = _feedbacks.asStateFlow()
    
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
        // 每次生成1-3条新反馈
        private const val MAX_NEW_FEEDBACKS_PER_UPDATE = 3
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
                
                // 首次初始化：将当前月份设为上次生成月份，不生成心声（仅一次，且持久化）
                if (!isInitialized) {
                    _lastGenerationMonth.value = currentMonthId
                    savedStateHandle["last_generation_month"] = currentMonthId
                    isInitialized = true
                    println("📅 心声系统已初始化：${gameYear}年${gameMonth}月（下个月开始生成心声）")
                    return@launch
                }
                
                // 检查是否是新的月份
                if (currentMonthId <= _lastGenerationMonth.value) {
                    // 还在同一个月，不生成新反馈
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
                
                // 生成新反馈
                val allGeneratedFeedbacks = IntelligentFeedbackGenerator.generateFeedbackBasedOnCity(
                    buildings = buildings,
                    resources = resources,
                    goldAmount = goldAmount,
                    population = population,
                    gameTime = gameTime
                )
                
                // 只取前1-3条新反馈
                val newFeedbacksToAdd = allGeneratedFeedbacks
                    .take(MAX_NEW_FEEDBACKS_PER_UPDATE)
                    .map { feedbackData ->
                        CitizenFeedback(
                            id = feedbackData.id,
                            type = feedbackData.category,
                            message = feedbackData.content,
                            priority = when (feedbackData.urgency) {
                                com.citysimulator.game.ai.FeedbackUrgency.LOW -> 1
                                com.citysimulator.game.ai.FeedbackUrgency.MEDIUM -> 3
                                com.citysimulator.game.ai.FeedbackUrgency.HIGH -> 4
                                com.citysimulator.game.ai.FeedbackUrgency.CRITICAL -> 5
                            },
                            source = FeedbackSource.CITIZEN,
                            createdAt = feedbackData.createdAt,
                            isResolved = feedbackData.isResolved,
                            resolvedAt = feedbackData.resolvedAt
                        )
                    }
                
                // 合并到现有反馈中（保留旧的未解决反馈）
                val existingFeedbacks = _feedbacks.value.filter { !it.isResolved }
                val combinedFeedbacks = (existingFeedbacks + newFeedbacksToAdd)
                    .distinctBy { it.id } // 去重
                    .sortedByDescending { it.priority } // 按优先级排序
                    .take(10) // 最多保留10条
                
                _feedbacks.value = combinedFeedbacks
                _lastGenerationMonth.value = currentMonthId
                savedStateHandle["last_generation_month"] = currentMonthId
                
                println("💬 ${gameYear}年${gameMonth}月生成了 ${newFeedbacksToAdd.size} 条新心声，当前共 ${combinedFeedbacks.size} 条未解决反馈")
                
            } catch (e: Exception) {
                println("❌ 生成反馈时出错: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 添加新的反馈
     */
    fun addFeedback(feedback: CitizenFeedback) {
        val currentFeedbacks = _feedbacks.value.toMutableList()
        currentFeedbacks.add(feedback)
        _feedbacks.value = currentFeedbacks.sortedByDescending { it.priority }
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
        }
    }
    
    /**
     * 删除反馈
     */
    fun deleteFeedback(feedbackId: String) {
        val currentFeedbacks = _feedbacks.value.toMutableList()
        currentFeedbacks.removeAll { it.id == feedbackId }
        _feedbacks.value = currentFeedbacks
    }
    
    /**
     * 获取未解决的反馈
     */
    fun getUnresolvedFeedbacks(): List<CitizenFeedback> {
        return _feedbacks.value.filter { !it.isResolved }
    }
    
    /**
     * 获取按优先级排序的反馈
     */
    fun getFeedbacksByPriority(): List<CitizenFeedback> {
        return _feedbacks.value.sortedByDescending { it.priority }
    }
    
    /**
     * 获取按类型分组的反馈
     */
    fun getFeedbacksByType(): Map<FeedbackType, List<CitizenFeedback>> {
        return _feedbacks.value.groupBy { it.type }
    }
    
    /**
     * 获取按来源分组的反馈
     */
    fun getFeedbacksBySource(): Map<FeedbackSource, List<CitizenFeedback>> {
        return _feedbacks.value.groupBy { it.source }
    }
    
    /**
     * 清除所有反馈
     */
    fun clearAllFeedbacks() {
        _feedbacks.value = emptyList()
    }
    
    /**
     * 获取反馈统计
     */
    fun getFeedbackStats(): FeedbackStats {
        val allFeedbacks = _feedbacks.value
        val unresolved = allFeedbacks.count { !it.isResolved }
        val highPriority = allFeedbacks.count { it.priority >= 4 }
        val emergency = allFeedbacks.count { it.priority == 5 }
        
        return FeedbackStats(
            total = allFeedbacks.size,
            unresolved = unresolved,
            highPriority = highPriority,
            emergency = emergency
        )
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
