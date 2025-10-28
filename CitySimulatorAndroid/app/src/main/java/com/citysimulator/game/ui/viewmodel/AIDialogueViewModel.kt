package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.deepseek.DeepSeekClient
import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.repository.BuildingRepository
import com.citysimulator.game.data.repository.CityRepository
import com.citysimulator.game.data.repository.ResourceRepository
import com.citysimulator.game.ui.screen.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AI对话ViewModel
 * 
 * 管理与市民的AI对话状态和消息
 * 对话会参考城市实际环境
 */
@HiltViewModel
class AIDialogueViewModel @Inject constructor(
    private val deepSeekClient: DeepSeekClient,
    private val cityRepository: CityRepository,
    private val buildingRepository: BuildingRepository,
    private val resourceRepository: ResourceRepository
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var currentCitizen: Citizen? = null
    
    /**
     * 开始与市民的对话
     */
    fun startConversationWithCitizen(citizen: Citizen) {
        currentCitizen = citizen
        _messages.value = listOf(
            ChatMessage(
                content = generateGreeting(citizen),
                isUser = false
            )
        )
    }
    
    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        val citizen = currentCitizen ?: return
        
        // 添加用户消息
        val userMessage = ChatMessage(content = content, isUser = true)
        _messages.value = _messages.value + userMessage
        
        // 调用AI生成回复
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 获取城市环境信息
                val city = cityRepository.getCityById("default_city").first()
                val buildings = buildingRepository.getAllBuildings().first()
                val resources = resourceRepository.getAllResources().first()
                val goldResource = resources.find { it.type.name == "GOLD" }
                
                // 调试：打印数据库中的建筑
                println("🏗️ AI对话 - 数据库中的建筑总数: ${buildings.size}")
                println("📋 建筑详细列表：")
                buildings.forEachIndexed { index, building ->
                    println("   ${index + 1}. ${building.type.name} | 自定义名称:${building.customName} | 位置:(${building.position.x}, ${building.position.y}) | ID:${building.id.take(8)}")
                }
                
                // 统计建筑类型（按照简化建筑的显示名称）
                val buildingCounts = mutableMapOf<String, Int>()
                buildings.forEach { building ->
                    val displayName = building.customName ?: building.type.getDisplayName()
                    buildingCounts[displayName] = buildingCounts.getOrDefault(displayName, 0) + 1
                }
                
                // 构建详细的建筑列表描述
                val buildingDetails = buildString {
                    if (buildingCounts.isNotEmpty()) {
                        buildingCounts.entries.sortedByDescending { it.value }.forEach { (name, count) ->
                            if (isNotEmpty()) append(", ")
                            append("${name}×${count}")
                        }
                    } else {
                        append("暂无建筑")
                    }
                }
                
                // 构建城市环境描述
                val cityContext = buildString {
                    append("【城市概况】\n")
                    append("城市名称: ${city?.name ?: "未知"}\n")
                    append("人口: ${city?.population ?: 0}人\n")
                    append("金币: ${goldResource?.amount?.toInt() ?: 0}\n")
                    append("建筑总数: ${buildings.size}座\n")
                    append("\n【建筑详情】\n")
                    append(buildingDetails)
                }
                
                // 调试：打印发送给AI的城市环境信息
                println("🤖 发送给AI的城市环境信息：")
                println(cityContext)
                println("=" .repeat(50))
                
                // 获取对话历史（增加到最近5轮对话，让AI更好理解上下文）
                val history = _messages.value
                    .takeLast(10) // 最近5轮对话
                    .map { it.content }
                
                // 调用真实AI对话
                val response = deepSeekClient.chatWithCitizen(
                    citizenName = citizen.name,
                    citizenAge = citizen.age,
                    citizenOccupation = citizen.occupation ?: "市民",
                    citizenHappiness = citizen.happiness.toInt(),
                    citizenHealth = citizen.health.toInt(),
                    citizenPersonality = citizen.personality.name,
                    maritalStatus = citizen.maritalStatus.name,
                    hasChildren = false,
                    playerMessage = content,
                    conversationHistory = history,
                    cityContext = cityContext
                )
                
                // 添加AI回复
                val aiMessage = ChatMessage(
                    content = response,
                    isUser = false
                )
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                // 错误处理
                val errorMessage = ChatMessage(
                    content = "抱歉,我现在有点累了,稍后再聊吧...(${e.message})",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 生成问候语
     */
    private fun generateGreeting(citizen: Citizen): String {
        return when {
            citizen.happiness > 80 -> "你好！很高兴见到你！我最近过得很开心，有什么想聊的吗？"
            citizen.happiness > 50 -> "嗨，你好。有什么事吗？"
            else -> "...你好。（看起来有些疲惫）"
        }
    }
    
    
    /**
     * 清除对话历史
     */
    fun clearMessages() {
        _messages.value = emptyList()
        currentCitizen = null
    }
}

