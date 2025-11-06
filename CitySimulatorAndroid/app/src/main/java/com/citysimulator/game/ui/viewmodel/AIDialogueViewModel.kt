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
    private val resourceRepository: ResourceRepository,
    private val citizenViewModel: CitizenViewModel
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
        // 不再自动添加问候语，让对话从空白开始
        _messages.value = emptyList()
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
                
                // 获取市民的深度AI数据
                val personality = citizenViewModel.getOrGeneratePersonality(citizen.id)
                val needs = citizenViewModel.getOrGenerateNeeds(citizen.id, citizen)
                val memories = citizenViewModel.getOrGenerateMemories(citizen.id, citizen)
                val socialNetwork = citizenViewModel.getOrGenerateSocialNetwork(citizen.id)
                val gossips = citizenViewModel.getCitizenGossips(citizen.id, city?.happiness ?: 0.6f)
                val events = citizenViewModel.getCitizenEvents(citizen.id, city?.happiness ?: 0.6f)
                val citizenTrust = citizenViewModel.getCitizenTrust(citizen.id)
                
                // 构建完整的对话上下文（包含城市+市民深度信息）
                val cityContext = buildString {
                    append("【城市概况】\n")
                    append("城市名称: ${city?.name ?: "未知"}\n")
                    append("人口: ${city?.population ?: 0}人\n")
                    append("金币: ${goldResource?.amount?.toInt() ?: 0}\n")
                    append("建筑总数: ${buildings.size}座\n")
                    append("建筑详情: $buildingDetails\n")
                    
                    append("\n【你的人格特质】\n")
                    append("主导人格: ${personality.getDominantPersonalityType().getDisplayName()}\n")
                    append("外向性: ${(personality.extraversion * 100).toInt()}% ")
                    append("(${if (personality.isExtroverted()) "外向" else if (personality.isIntroverted()) "内向" else "均衡"})\n")
                    append("勤奋度: ${(personality.diligence * 100).toInt()}% ")
                    append("(${if (personality.isDiligent()) "勤奋" else if (personality.isLazy()) "懒散" else "均衡"})\n")
                    append("好奇心: ${(personality.curiosity * 100).toInt()}% ")
                    append("(${if (personality.isCurious()) "好奇" else if (personality.isConservative()) "保守" else "均衡"})\n")
                    
                    append("\n【你的需求状态】\n")
                    append("总体满足度: ${(needs.getOverallSatisfaction() * 100).toInt()}%\n")
                    append("最紧迫需求: ${needs.getMostUrgentNeed().getDisplayName()}\n")
                    append("生理需求: ${(needs.physiological.getAverage() * 100).toInt()}%\n")
                    append("安全需求: ${(needs.safety.getAverage() * 100).toInt()}%\n")
                    append("社交需求: ${(needs.social.getAverage() * 100).toInt()}%\n")
                    
                    append("\n【你的重要记忆】\n")
                    val memoryList = memories.memories.take(3)
                    if (memoryList.isNotEmpty()) {
                        for (memory in memoryList) {
                            append("- ${memory.description} (${memory.importance.getDisplayName()})\n")
                        }
                    } else {
                        append("- 暂无特殊记忆\n")
                    }
                    
                    append("\n【你的社交关系】\n")
                    val relationshipList = socialNetwork.relationships
                    if (relationshipList.isNotEmpty()) {
                        val relationshipSummary = relationshipList.groupBy { relation -> relation.relationshipType }
                        for ((relType, relations) in relationshipSummary) {
                            append("- ${relType.getDisplayName()}: ${relations.size}人\n")
                        }
                    } else {
                        append("- 暂无社交关系\n")
                    }
                    
                    append("\n【最近听到的八卦】\n")
                    val gossipList = gossips.take(2)
                    if (gossipList.isNotEmpty()) {
                        for (gossip in gossipList) {
                            append("- ${gossip.content} (${gossip.sentiment.getDisplayName()})\n")
                        }
                    } else {
                        append("- 暂无八卦\n")
                    }
                    
                    append("\n【最近的城市事件】\n")
                    val eventList = events.take(2)
                    if (eventList.isNotEmpty()) {
                        for (event in eventList) {
                            append("- ${event.title}: ${event.description}\n")
                        }
                    } else {
                        append("- 暂无特殊事件\n")
                    }
                    
                    append("\n【对市长的信任度】\n")
                    append("信任度: ${(citizenTrust * 100).toInt()}% ")
                    append(when {
                        citizenTrust >= 0.8f -> "(非常信任)"
                        citizenTrust >= 0.6f -> "(比较信任)"
                        citizenTrust >= 0.4f -> "(一般)"
                        citizenTrust >= 0.2f -> "(不太信任)"
                        else -> "(很不信任)"
                    })
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
     * 清除对话历史
     */
    fun clearMessages() {
        _messages.value = emptyList()
        currentCitizen = null
    }
}

