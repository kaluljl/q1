package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.data.dao.TechnologyDao
import com.citysimulator.game.data.model.Technology
import com.citysimulator.game.data.model.TechTreeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * 科技树ViewModel
 * 
 * 管理科技树相关的业务逻辑和状态
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@HiltViewModel
class TechTreeViewModel @Inject constructor(
    private val technologyDao: TechnologyDao
) : ViewModel() {
    
    private val _technologies = MutableStateFlow<List<Technology>>(emptyList())
    val technologies: StateFlow<List<Technology>> = _technologies.asStateFlow()
    
    private val _researchedTechnologies = MutableStateFlow<List<Technology>>(emptyList())
    val researchedTechnologies: StateFlow<List<Technology>> = _researchedTechnologies.asStateFlow()
    
    private val _researchingTechnologies = MutableStateFlow<List<Technology>>(emptyList())
    val researchingTechnologies: StateFlow<List<Technology>> = _researchingTechnologies.asStateFlow()
    
    private val _availableTechnologies = MutableStateFlow<List<Technology>>(emptyList())
    val availableTechnologies: StateFlow<List<Technology>> = _availableTechnologies.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadTechnologies()
        observeTechnologies()
    }
    
    /**
     * 加载所有科技
     */
    private fun loadTechnologies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 检查数据库是否为空，如果为空则初始化科技树
                val existingTechnologies = technologyDao.getAllTechnologies()
                existingTechnologies.collect { techList ->
                    if (techList.isEmpty()) {
                        initializeTechTree()
                    } else {
                        _technologies.value = techList
                        updateAvailableTechnologies(techList)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载科技树失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 初始化科技树
     */
    private suspend fun initializeTechTree() {
        try {
            val allTechnologies = TechTreeManager.getAllTechnologies()
            technologyDao.insertTechnologies(allTechnologies)
            _technologies.value = allTechnologies
            updateAvailableTechnologies(allTechnologies)
        } catch (e: Exception) {
            _errorMessage.value = "初始化科技树失败: ${e.message}"
        }
    }
    
    /**
     * 观察科技变化
     */
    private fun observeTechnologies() {
        viewModelScope.launch {
            technologyDao.getAllTechnologies().collect { techList ->
                _technologies.value = techList
                updateAvailableTechnologies(techList)
            }
        }
        
        viewModelScope.launch {
            technologyDao.getResearchedTechnologies().collect { techList ->
                _researchedTechnologies.value = techList
            }
        }
        
        viewModelScope.launch {
            technologyDao.getResearchingTechnologies().collect { techList ->
                _researchingTechnologies.value = techList
            }
        }
    }
    
    /**
     * 更新可研究的科技
     */
    private fun updateAvailableTechnologies(allTechnologies: List<Technology>) {
        val researchedIds = allTechnologies.filter { it.isResearched }.map { it.id }
        val available = TechTreeManager.getAvailableTechnologies(researchedIds)
        _availableTechnologies.value = available
    }
    
    /**
     * 开始研究科技
     */
    fun startResearch(technology: Technology, currentGold: Int) {
        viewModelScope.launch {
            try {
                if (currentGold < technology.researchCost) {
                    _errorMessage.value = "金币不足，需要 ${technology.researchCost} 金币"
                    return@launch
                }
                
                val researchedIds = _researchedTechnologies.value.map { it.id }
                if (!TechTreeManager.canResearch(technology, researchedIds)) {
                    _errorMessage.value = "前置科技未完成，无法研究此科技"
                    return@launch
                }
                
                val startTime = Date().time
                technologyDao.startResearch(technology.id, startTime)
                
                // 更新本地状态
                val updatedTech = technology.copy(
                    researchStartTime = Date(startTime)
                )
                val updatedList = _technologies.value.map { 
                    if (it.id == technology.id) updatedTech else it 
                }
                _technologies.value = updatedList
                updateAvailableTechnologies(updatedList)
                
            } catch (e: Exception) {
                _errorMessage.value = "开始研究失败: ${e.message}"
            }
        }
    }
    
    /**
     * 完成研究科技
     */
    fun completeResearch(technology: Technology) {
        viewModelScope.launch {
            try {
                val completeTime = Date().time
                technologyDao.completeResearch(technology.id, completeTime)
                
                // 更新本地状态
                val updatedTech = technology.copy(
                    isResearched = true,
                    researchCompleteTime = Date(completeTime)
                )
                val updatedList = _technologies.value.map { 
                    if (it.id == technology.id) updatedTech else it 
                }
                _technologies.value = updatedList
                updateAvailableTechnologies(updatedList)
                
            } catch (e: Exception) {
                _errorMessage.value = "完成研究失败: ${e.message}"
            }
        }
    }
    
    /**
     * 检查研究是否完成
     */
    fun checkResearchCompletion() {
        viewModelScope.launch {
            try {
                val researching = _researchingTechnologies.value
                val currentTime = Date().time
                
                researching.forEach { tech ->
                    tech.researchStartTime?.let { startTime ->
                        val researchDuration = tech.researchTime * 60 * 1000L // 转换为毫秒
                        if (currentTime - startTime.time >= researchDuration) {
                            completeResearch(tech)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "检查研究进度失败: ${e.message}"
            }
        }
    }
    
    /**
     * 获取科技效果
     */
    fun getTechEffects(): Map<String, Float> {
        val researched = _researchedTechnologies.value
        return mapOf(
            "goldMultiplier" to researched.fold(1.0f) { acc, tech -> acc * tech.effects.goldMultiplier },
            "populationMultiplier" to researched.fold(1.0f) { acc, tech -> acc * tech.effects.populationMultiplier },
            "prosperityBonus" to researched.sumOf { it.effects.prosperityBonus.toDouble() }.toFloat(),
            "buildingEfficiencyBonus" to researched.sumOf { it.effects.buildingEfficiencyBonus.toDouble() }.toFloat()
        )
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 重置科技树
     */
    fun resetTechTree() {
        viewModelScope.launch {
            try {
                technologyDao.resetAllTechnologies()
                loadTechnologies()
            } catch (e: Exception) {
                _errorMessage.value = "重置科技树失败: ${e.message}"
            }
        }
    }
}

