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
    
    private val _goldAmount = MutableStateFlow(1000)
    val goldAmount: StateFlow<Int> = _goldAmount.asStateFlow()
    
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
