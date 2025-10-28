package com.citysimulator.game.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.IntelligentPopulationSystem
import com.citysimulator.game.ai.PopulationGrowthResult
import com.citysimulator.game.ai.PopulationGrowthFactors
import com.citysimulator.game.ai.CityPopulationAnalysis
import com.citysimulator.game.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// DataStore 扩展
private val Context.populationDataStore: DataStore<Preferences> by preferencesDataStore(name = "population")

/**
 * 人口管理ViewModel
 * 
 * 管理城市人口增长和变化，并持久化保存
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@HiltViewModel
class PopulationViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private val POPULATION_KEY = intPreferencesKey("current_population")
    }
    
    private val _currentPopulation = MutableStateFlow(20) // 初始人口
    val currentPopulation: StateFlow<Int> = _currentPopulation.asStateFlow()
    
    private val _populationGrowthResult = MutableStateFlow<PopulationGrowthResult?>(null)
    val populationGrowthResult: StateFlow<PopulationGrowthResult?> = _populationGrowthResult.asStateFlow()
    
    private val _populationHistory = MutableStateFlow<List<PopulationRecord>>(emptyList())
    val populationHistory: StateFlow<List<PopulationRecord>> = _populationHistory.asStateFlow()
    
    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()
    
    private val _lastUpdateTime = MutableStateFlow(System.currentTimeMillis())
    val lastUpdateTime: StateFlow<Long> = _lastUpdateTime.asStateFlow()
    
    init {
        loadPopulation()
    }
    
    /**
     * 从DataStore加载人口数据
     */
    private fun loadPopulation() {
        viewModelScope.launch {
            try {
                val preferences = context.populationDataStore.data.first()
                val savedPopulation = preferences[POPULATION_KEY]
                
                if (savedPopulation != null) {
                    _currentPopulation.value = savedPopulation
                    println("📊 加载人口数据: $savedPopulation")
                } else {
                    println("🆕 首次启动，初始人口: 20")
                }
            } catch (e: Exception) {
                println("❌ 加载人口数据失败: ${e.message}")
            }
        }
    }
    
    /**
     * 保存人口数据到DataStore
     */
    private suspend fun savePopulation() {
        try {
            context.populationDataStore.edit { preferences ->
                preferences[POPULATION_KEY] = _currentPopulation.value
            }
            println("💾 保存人口数据: ${_currentPopulation.value}")
        } catch (e: Exception) {
            println("❌ 保存人口数据失败: ${e.message}")
        }
    }
    
    /**
     * 更新人口（基于城市状态）
     */
    fun updatePopulation(
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int,
        timeElapsed: Double = 1.0
    ) {
        viewModelScope.launch {
            try {
                _isCalculating.value = true
                
                val currentPop = _currentPopulation.value
                val result = IntelligentPopulationSystem.calculatePopulationGrowth(
                    currentPopulation = currentPop,
                    buildings = buildings,
                    resources = resources,
                    goldAmount = goldAmount,
                    timeElapsed = timeElapsed
                )
                
                _populationGrowthResult.value = result
                _currentPopulation.value = result.newPopulation
                
                // 保存人口数据
                savePopulation()
                
                // 记录人口变化历史
                val newRecord = PopulationRecord(
                    timestamp = System.currentTimeMillis(),
                    population = result.newPopulation,
                    change = result.populationChange,
                    growthRate = result.growthRate,
                    capacity = result.populationCapacity,
                    isAtCapacity = result.isAtCapacity
                )
                
                val updatedHistory = (_populationHistory.value + newRecord).takeLast(100) // 保留最近100条记录
                _populationHistory.value = updatedHistory
                
                _lastUpdateTime.value = System.currentTimeMillis()
                
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isCalculating.value = false
            }
        }
    }
    
    /**
     * 手动设置人口
     */
    fun setPopulation(population: Int) {
        val oldPopulation = _currentPopulation.value
        _currentPopulation.value = maxOf(0, population)
        println("👥 人口数量更新: $oldPopulation -> ${_currentPopulation.value}")
        viewModelScope.launch {
            savePopulation()
        }
    }
    
    /**
     * 获取人口增长率
     */
    fun getGrowthRate(): Double {
        return _populationGrowthResult.value?.growthRate ?: 0.0
    }
    
    /**
     * 获取人口容量
     */
    fun getPopulationCapacity(): Int {
        return _populationGrowthResult.value?.populationCapacity ?: 0
    }
    
    /**
     * 是否达到人口容量
     */
    fun isAtCapacity(): Boolean {
        return _populationGrowthResult.value?.isAtCapacity ?: false
    }
    
    /**
     * 获取人口增长因素
     */
    fun getGrowthFactors(): PopulationGrowthFactors? {
        return _populationGrowthResult.value?.factors
    }
    
    /**
     * 获取城市分析
     */
    fun getCityAnalysis(): CityPopulationAnalysis? {
        return _populationGrowthResult.value?.analysis
    }
    
    /**
     * 获取人口趋势（最近7天）
     */
    fun getPopulationTrend(): List<PopulationRecord> {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        return _populationHistory.value.filter { it.timestamp >= sevenDaysAgo }
    }
    
    /**
     * 获取人口增长率趋势
     */
    fun getGrowthRateTrend(): List<Double> {
        return _populationHistory.value.map { it.growthRate }
    }
    
    /**
     * 预测未来人口（基于当前增长率）
     */
    fun predictFuturePopulation(hours: Int): Int {
        val currentPop = _currentPopulation.value
        val growthRate = getGrowthRate()
        val predictedChange = (growthRate * hours * currentPop / 100).toInt()
        return maxOf(0, currentPop + predictedChange)
    }
    
    /**
     * 重置人口数据
     */
    fun resetPopulation() {
        _currentPopulation.value = 20
        _populationGrowthResult.value = null
        _populationHistory.value = emptyList()
        _lastUpdateTime.value = System.currentTimeMillis()
        viewModelScope.launch {
            savePopulation()
        }
    }
    
    /**
     * ViewModel销毁时保存数据
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            savePopulation()
            println("🔄 应用退出，保存人口数据")
        }
    }
}

/**
 * 人口记录数据类
 */
data class PopulationRecord(
    val timestamp: Long,
    val population: Int,
    val change: Int,
    val growthRate: Double,
    val capacity: Int,
    val isAtCapacity: Boolean
)
