package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.data.model.*
import com.citysimulator.game.data.repository.CityRepository
import com.citysimulator.game.data.repository.BuildingRepository
import com.citysimulator.game.data.repository.ResourceRepository
import com.citysimulator.game.data.repository.PopulationRepository
import com.citysimulator.game.data.model.WeatherType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * 主游戏视图模型
 * 
 * 管理主游戏界面的状态和业务逻辑。
 * 使用MVVM架构模式，通过Flow提供响应式数据流。
 * 
 * @property cityRepository 城市数据仓库
 * @property buildingRepository 建筑数据仓库
 * @property resourceRepository 资源数据仓库
 * @property populationRepository 人口数据仓库
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@HiltViewModel
class MainGameViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val buildingRepository: BuildingRepository,
    private val resourceRepository: ResourceRepository,
    private val populationRepository: PopulationRepository
) : ViewModel() {
    
    // UI状态数据类
    data class MainGameUiState(
        val currentCity: City? = null,
        val buildings: List<Building> = emptyList(),
        val resources: List<Resource> = emptyList(),
        val population: List<Population> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
    
    // UI状态
    private val _uiState = MutableStateFlow(MainGameUiState())
    val uiState: StateFlow<MainGameUiState> = _uiState.asStateFlow()
    
    // 当前时间
    private val _currentTime = MutableStateFlow(Date())
    val currentTime: StateFlow<Date> = _currentTime.asStateFlow()
    
    // 天气类型
    private val _weatherType = MutableStateFlow(WeatherType.SUNNY)
    val weatherType: StateFlow<WeatherType> = _weatherType.asStateFlow()
    
    // 选中的建筑
    private val _selectedBuilding = MutableStateFlow<Building?>(null)
    val selectedBuilding: StateFlow<Building?> = _selectedBuilding.asStateFlow()
    
    init {
        // 初始化默认数据，避免空数据导致的问题
        _uiState.value = _uiState.value.copy(
            currentCity = null,
            buildings = emptyList(),
            resources = emptyList(),
            population = emptyList(),
            isLoading = false,
            error = null
        )
        
        loadGameData()
        startTimeUpdate()
        startWeatherUpdate()
    }
    
    /**
     * 加载游戏数据
     */
    private fun loadGameData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 加载当前城市
                val currentCity = cityRepository.getCurrentCity().firstOrNull()
                
                // TODO: 仓库未提供 get*ByCityId，这里先置空或读取全部再过滤
                val buildings: List<Building> = emptyList()
                val resources: List<Resource> = emptyList()
                val population: List<Population> = emptyList()
                
                _uiState.value = _uiState.value.copy(
                    currentCity = currentCity,
                    buildings = buildings,
                    resources = resources,
                    population = population,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载数据失败"
                )
            }
        }
    }
    
    /**
     * 开始时间更新
     */
    private fun startTimeUpdate() {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = Date()
                kotlinx.coroutines.delay(1000) // 每秒更新一次
            }
        }
    }
    
    /**
     * 开始天气更新
     */
    private fun startWeatherUpdate() {
        viewModelScope.launch {
            while (true) {
                // 模拟天气变化（每5分钟更新一次）
                _weatherType.value = getRandomWeatherType()
                kotlinx.coroutines.delay(5 * 60 * 1000)
            }
        }
    }
    
    /**
     * 获取随机天气类型
     */
    private fun getRandomWeatherType(): WeatherType {
        val weatherTypes = WeatherType.values()
        return weatherTypes.random()
    }
    
    /**
     * 建筑点击处理
     */
    fun onBuildingClick(building: Building) {
        _selectedBuilding.value = building
        // TODO: 显示建筑详情对话框
    }
    
    /**
     * 空网格点击处理
     */
    fun onEmptyGridClick(x: Int, y: Int) {
        // TODO: 显示建筑建造菜单
        // 参数 x, y 表示网格坐标，用于后续建筑放置
    }
    
    /**
     * 建造建筑
     */
    fun buildBuilding(type: BuildingType, x: Int, y: Int) {
        viewModelScope.launch {
            try {
                // 检查是否有当前城市
                if (_uiState.value.currentCity == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "没有当前城市，无法建造建筑"
                    )
                    return@launch
                }
                
                val building = Building(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    position = BuildingPosition(x, y),
                    isUnderConstruction = true,
                    buildTime = Date()
                )
                
                buildingRepository.insertBuilding(building)
                loadGameData() // 重新加载数据
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "建造建筑失败"
                )
            }
        }
    }
    
    /**
     * 升级建筑
     */
    fun upgradeBuilding(buildingId: String) {
        viewModelScope.launch {
            try {
                val building = _uiState.value.buildings.find { it.id == buildingId }
                if (building != null) {
                    buildingRepository.updateBuilding(
                        building.copy(
                            level = building.level + 1,
                            isUpgrading = true
                        )
                    )
                    loadGameData()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "升级建筑失败"
                )
            }
        }
    }
    
    /**
     * 维护建筑
     */
    fun maintainBuilding(buildingId: String) {
        viewModelScope.launch {
            try {
                val building = _uiState.value.buildings.find { it.id == buildingId }
                if (building != null) {
                    buildingRepository.updateBuilding(
                        building.copy(
                            isMaintenanceRequired = false,
                            lastMaintenanceDate = Date()
                        )
                    )
                    loadGameData()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "维护建筑失败"
                )
            }
        }
    }
    
    /**
     * 添加已创建的建筑到数据库
     */
    fun addBuilding(building: Building) {
        viewModelScope.launch {
            try {
                println("💾 准备保存建筑到数据库: ${building.type} (${building.customName ?: building.type.getDisplayName()}) ID=${building.id}")
                buildingRepository.insertBuilding(building)
                println("✅ 建筑已保存到本地数据库: ${building.type}")
                
                // 验证保存
                val allBuildings = buildingRepository.getAllBuildings().first()
                println("📊 数据库中现有建筑总数: ${allBuildings.size}")
            } catch (e: Exception) {
                println("❌ 保存建筑到本地数据库失败: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "保存建筑失败"
                )
            }
        }
    }
    
    /**
     * 删除建筑
     */
    fun deleteBuilding(buildingId: String) {
        viewModelScope.launch {
            try {
                buildingRepository.deleteBuildingById(buildingId)
                loadGameData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "删除建筑失败"
                )
            }
        }
    }
    
    /**
     * 批量同步建筑到数据库
     */
    fun syncBuildingsToDatabase(buildings: List<Building>) {
        viewModelScope.launch {
            try {
                println("🔄 开始同步建筑到数据库，共 ${buildings.size} 座")
                buildingRepository.insertBuildings(buildings)
                println("✅ 建筑同步完成！")
                
                // 验证同步结果
                val allBuildings = buildingRepository.getAllBuildings().first()
                println("📊 数据库中现有建筑总数: ${allBuildings.size}")
            } catch (e: Exception) {
                println("❌ 同步建筑失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 删除所有建筑
     */
    fun deleteAllBuildings() {
        viewModelScope.launch {
            try {
                buildingRepository.deleteAllBuildings()
                loadGameData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "删除所有建筑失败"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 刷新数据
     */
    fun refreshData() {
        loadGameData()
    }
}
