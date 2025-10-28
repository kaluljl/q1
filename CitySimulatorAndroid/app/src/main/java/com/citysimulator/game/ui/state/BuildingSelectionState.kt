package com.citysimulator.game.ui.state

import com.citysimulator.game.data.model.BuildingType

/**
 * 建筑选择状态管理
 * 
 * 用于在导航之间传递选择的建筑类型。
 * 使用单例模式确保状态一致性。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
object BuildingSelectionState {
    private var _selectedBuildingType: BuildingType? = null
    
    /**
     * 获取当前选择的建筑类型
     */
    val selectedBuildingType: BuildingType?
        get() = _selectedBuildingType
    
    /**
     * 设置选择的建筑类型
     * 
     * @param buildingType 选择的建筑类型
     */
    fun selectBuilding(buildingType: BuildingType) {
        _selectedBuildingType = buildingType
    }
    
    /**
     * 清除选择的建筑类型
     */
    fun clearSelection() {
        _selectedBuildingType = null
    }
}
