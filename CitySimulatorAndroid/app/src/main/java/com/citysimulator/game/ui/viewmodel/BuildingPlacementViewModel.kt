package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.citysimulator.game.data.model.SimplifiedBuildingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 建筑放置ViewModel
 * 管理建筑选择和放置状态
 */
@HiltViewModel
class BuildingPlacementViewModel @Inject constructor() : ViewModel() {
    
    // 当前选中的建筑类型（null表示未选中）
    private val _selectedBuildingType = MutableStateFlow<SimplifiedBuildingType?>(null)
    val selectedBuildingType: StateFlow<SimplifiedBuildingType?> = _selectedBuildingType.asStateFlow()
    
    // 是否处于放置模式
    private val _isPlacementMode = MutableStateFlow(false)
    val isPlacementMode: StateFlow<Boolean> = _isPlacementMode.asStateFlow()
    
    /**
     * 选择要建造的建筑
     */
    fun selectBuilding(buildingType: SimplifiedBuildingType) {
        _selectedBuildingType.value = buildingType
        _isPlacementMode.value = true
    }
    
    /**
     * 取消建造
     */
    fun cancelPlacement() {
        _selectedBuildingType.value = null
        _isPlacementMode.value = false
    }
    
    /**
     * 完成放置（建造一次后自动取消）
     */
    fun completePlacement() {
        _selectedBuildingType.value = null
        _isPlacementMode.value = false
    }
}

