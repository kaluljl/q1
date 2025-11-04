package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Supabase游戏视图模型
 * 负责管理游戏数据与Supabase的同步
 */
@HiltViewModel
class SupabaseGameViewModel @Inject constructor(
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {
    
    private val _buildings = MutableStateFlow<List<Building>>(emptyList())
    val buildings: StateFlow<List<Building>> = _buildings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _goldAmount = MutableStateFlow(1500)
    val goldAmount: StateFlow<Int> = _goldAmount.asStateFlow()
    
    // 已建造的奇观列表 (存储奇观类型名称)
    private val _builtWonders = MutableStateFlow<Set<String>>(emptySet())
    val builtWonders: StateFlow<Set<String>> = _builtWonders.asStateFlow()
    
    // 正在建造的奇观 (奇观类型名称 -> 剩余月数)
    private val _wondersInProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val wondersInProgress: StateFlow<Map<String, Int>> = _wondersInProgress.asStateFlow()
    
    init {
        loadBuildings()
    }
    
    /**
     * 更新金币数量
     */
    fun updateGoldAmount(amount: Int) {
        _goldAmount.value = amount
    }
    
    /**
     * 开始建造奇观
     */
    fun startBuildingWonder(wonder: com.citysimulator.game.ai.WonderBuilding): Boolean {
        // 检查是否已建造
        if (_builtWonders.value.contains(wonder.type.name)) {
            println("⚠️ 奇观 ${wonder.name} 已经建造过了")
            return false
        }
        
        // 检查是否正在建造
        if (_wondersInProgress.value.containsKey(wonder.type.name)) {
            println("⚠️ 奇观 ${wonder.name} 正在建造中")
            return false
        }
        
        // 检查金币是否足够
        if (_goldAmount.value < wonder.buildCost) {
            println("⚠️ 金币不足，需要 ${wonder.buildCost}，当前 ${_goldAmount.value}")
            return false
        }
        
        // 扣除金币
        _goldAmount.value -= wonder.buildCost
        
        // 添加到建造队列
        val currentProgress = _wondersInProgress.value.toMutableMap()
        currentProgress[wonder.type.name] = wonder.buildTime
        _wondersInProgress.value = currentProgress
        
        println("✅ 开始建造奇观: ${wonder.name}，需要 ${wonder.buildTime} 个月")
        return true
    }
    
    /**
     * 更新奇观建造进度 (每个游戏月调用一次)
     * @param onWonderCompleted 奇观建造完成时的回调，传入奇观类型名称
     */
    fun updateWonderProgress(onWonderCompleted: ((String) -> Unit)? = null) {
        val currentProgress = _wondersInProgress.value.toMutableMap()
        val builtWonders = _builtWonders.value.toMutableSet()
        val completedWonders = mutableListOf<String>()
        
        currentProgress.forEach { (wonderTypeName, remainingMonths) ->
            val newRemaining = remainingMonths - 1
            if (newRemaining <= 0) {
                // 建造完成
                completedWonders.add(wonderTypeName)
                builtWonders.add(wonderTypeName)
                println("🎉 奇观建造完成: $wonderTypeName")
                
                // 触发完成回调
                onWonderCompleted?.invoke(wonderTypeName)
            } else {
                currentProgress[wonderTypeName] = newRemaining
            }
        }
        
        // 从进度中移除已完成的
        completedWonders.forEach { currentProgress.remove(it) }
        
        _wondersInProgress.value = currentProgress
        _builtWonders.value = builtWonders
    }
    
    /**
     * 获取所有已建造奇观的效果列表
     */
    fun getBuiltWondersEffects(): List<com.citysimulator.game.ai.WonderBuilding> {
        return com.citysimulator.game.ai.WonderBuildingSystem.getAllWonders()
            .filter { _builtWonders.value.contains(it.type.name) }
    }
    
    /**
     * 检查奇观是否已建造
     */
    fun isWonderBuilt(wonderTypeName: String): Boolean {
        return _builtWonders.value.contains(wonderTypeName)
    }
    
    /**
     * 检查奇观是否正在建造
     */
    fun isWonderInProgress(wonderTypeName: String): Boolean {
        return _wondersInProgress.value.containsKey(wonderTypeName)
    }
    
    /**
     * 获取奇观剩余建造时间
     */
    fun getWonderRemainingTime(wonderTypeName: String): Int? {
        return _wondersInProgress.value[wonderTypeName]
    }
    
    /**
     * 加载建筑数据
     */
    fun loadBuildings() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val buildingsList = supabaseRepository.getAllBuildings()
                _buildings.value = buildingsList
                println("从Supabase加载了 ${buildingsList.size} 个建筑")
            } catch (e: Exception) {
                _errorMessage.value = "加载建筑数据失败: ${e.message}"
                println("加载建筑数据失败: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 保存建筑到Supabase
     */
    fun saveBuilding(building: Building) {
        viewModelScope.launch {
            try {
                val success = supabaseRepository.insertBuilding(building)
                if (success) {
                    // 更新本地状态
                    val currentBuildings = _buildings.value.toMutableList()
                    currentBuildings.add(building)
                    _buildings.value = currentBuildings
                    println("建筑保存到Supabase成功: ${building.id}")
                } else {
                    _errorMessage.value = "保存建筑失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "保存建筑失败: ${e.message}"
                println("保存建筑失败: ${e.message}")
            }
        }
    }
    
    /**
     * 更新建筑
     */
    fun updateBuilding(building: Building) {
        viewModelScope.launch {
            try {
                val success = supabaseRepository.updateBuilding(building)
                if (success) {
                    // 更新本地状态
                    val currentBuildings = _buildings.value.toMutableList()
                    val index = currentBuildings.indexOfFirst { it.id == building.id }
                    if (index != -1) {
                        currentBuildings[index] = building
                        _buildings.value = currentBuildings
                    }
                    println("建筑更新成功: ${building.id}")
                } else {
                    _errorMessage.value = "更新建筑失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新建筑失败: ${e.message}"
                println("更新建筑失败: ${e.message}")
            }
        }
    }
    
    /**
     * 删除建筑
     */
    fun deleteBuilding(buildingId: String) {
        viewModelScope.launch {
            try {
                val success = supabaseRepository.deleteBuilding(buildingId)
                if (success) {
                    // 更新本地状态
                    val currentBuildings = _buildings.value.toMutableList()
                    currentBuildings.removeAll { it.id == buildingId }
                    _buildings.value = currentBuildings
                    println("建筑删除成功: $buildingId")
                } else {
                    _errorMessage.value = "删除建筑失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "删除建筑失败: ${e.message}"
                println("删除建筑失败: ${e.message}")
            }
        }
    }
    
    /**
     * 测试Supabase连接
     */
    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val success = supabaseRepository.testConnection()
                if (success) {
                    // 连接成功，重新加载数据
                    loadBuildings()
                } else {
                    _errorMessage.value = "Supabase连接失败，请检查数据库表是否已创建"
                }
            } catch (e: Exception) {
                _errorMessage.value = "连接测试失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
