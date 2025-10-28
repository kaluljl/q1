package com.citysimulator.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.data.dao.CityPolicyDao
import com.citysimulator.game.data.model.CityPolicy
import com.citysimulator.game.data.model.CityPolicyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * 城市政策ViewModel
 * 
 * 管理城市政策相关的业务逻辑和状态
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@HiltViewModel
class CityPolicyViewModel @Inject constructor(
    private val cityPolicyDao: CityPolicyDao
) : ViewModel() {
    
    private val _policies = MutableStateFlow<List<CityPolicy>>(emptyList())
    val policies: StateFlow<List<CityPolicy>> = _policies.asStateFlow()
    
    private val _implementedPolicies = MutableStateFlow<List<CityPolicy>>(emptyList())
    val implementedPolicies: StateFlow<List<CityPolicy>> = _implementedPolicies.asStateFlow()
    
    private val _availablePolicies = MutableStateFlow<List<CityPolicy>>(emptyList())
    val availablePolicies: StateFlow<List<CityPolicy>> = _availablePolicies.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _currentGold = MutableStateFlow(1000)
    val currentGold: StateFlow<Int> = _currentGold.asStateFlow()
    
    // 金币消耗事件
    private val _goldSpent = MutableStateFlow<Int?>(null)
    val goldSpent: StateFlow<Int?> = _goldSpent.asStateFlow()
    
    /**
     * 更新金币数量（从主游戏界面传入）
     */
    fun updateGold(gold: Int) {
        _currentGold.value = gold
    }
    
    /**
     * 清除金币消耗事件
     */
    fun clearGoldSpentEvent() {
        _goldSpent.value = null
    }
    
    init {
        loadPolicies()
        observePolicies()
    }
    
    /**
     * 加载所有政策
     */
    private fun loadPolicies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // 检查数据库是否为空，如果为空则初始化政策
                // 使用first()获取第一个值而不是collect阻塞
                val policyList = cityPolicyDao.getAllPolicies().first()
                if (policyList.isEmpty()) {
                    initializePolicies()
                } else {
                    _policies.value = policyList
                    updateAvailablePolicies(policyList)
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载政策失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 初始化政策
     */
    private suspend fun initializePolicies() {
        try {
            val allPolicies = CityPolicyManager.getAllPolicies()
            cityPolicyDao.insertPolicies(allPolicies)
            _policies.value = allPolicies
            updateAvailablePolicies(allPolicies)
        } catch (e: Exception) {
            _errorMessage.value = "初始化政策失败: ${e.message}"
        }
    }
    
    /**
     * 观察政策变化
     */
    private fun observePolicies() {
        viewModelScope.launch {
            cityPolicyDao.getAllPolicies().collect { policyList ->
                _policies.value = policyList
                updateAvailablePolicies(policyList)
            }
        }
        
        viewModelScope.launch {
            cityPolicyDao.getImplementedPolicies().collect { policyList ->
                _implementedPolicies.value = policyList
            }
        }
    }
    
    /**
     * 更新可实施的政策
     */
    private fun updateAvailablePolicies(allPolicies: List<CityPolicy>) {
        val implementedIds = allPolicies.filter { it.isImplemented }.map { it.id }
        val available = CityPolicyManager.getAvailablePolicies(implementedIds, _currentGold.value)
        _availablePolicies.value = available
    }
    
    /**
     * 实施政策
     */
    fun implementPolicy(policy: CityPolicy) {
        viewModelScope.launch {
            try {
                if (_currentGold.value < policy.implementationCost) {
                    _errorMessage.value = "金币不足，需要 ${policy.implementationCost} 金币"
                    return@launch
                }
                
                val implementedIds = _implementedPolicies.value.map { it.id }
                if (!CityPolicyManager.canImplement(policy, implementedIds, _currentGold.value)) {
                    _errorMessage.value = "前置政策未实施或金币不足，无法实施此政策"
                    return@launch
                }
                
                val implementedAt = Date().time
                cityPolicyDao.implementPolicy(policy.id, implementedAt)
                
                // 扣除实施成本并触发事件
                _currentGold.value -= policy.implementationCost
                _goldSpent.value = policy.implementationCost
                
                // 更新本地状态
                val updatedPolicy = policy.copy(
                    isImplemented = true,
                    implementedAt = Date(implementedAt)
                )
                val updatedList = _policies.value.map { 
                    if (it.id == policy.id) updatedPolicy else it 
                }
                _policies.value = updatedList
                updateAvailablePolicies(updatedList)
                
            } catch (e: Exception) {
                _errorMessage.value = "实施政策失败: ${e.message}"
            }
        }
    }
    
    /**
     * 撤销政策
     */
    fun revokePolicy(policy: CityPolicy) {
        viewModelScope.launch {
            try {
                if (!policy.isReversible) {
                    _errorMessage.value = "此政策不可撤销"
                    return@launch
                }
                
                cityPolicyDao.revokePolicy(policy.id)
                
                // 更新本地状态
                val updatedPolicy = policy.copy(
                    isImplemented = false,
                    implementedAt = null
                )
                val updatedList = _policies.value.map { 
                    if (it.id == policy.id) updatedPolicy else it 
                }
                _policies.value = updatedList
                updateAvailablePolicies(updatedList)
                
            } catch (e: Exception) {
                _errorMessage.value = "撤销政策失败: ${e.message}"
            }
        }
    }
    
    /**
     * 获取政策效果总和
     */
    fun getTotalPolicyEffects(): com.citysimulator.game.data.model.PolicyEffect {
        return CityPolicyManager.getTotalPolicyEffects(_implementedPolicies.value)
    }
    
    /**
     * 获取每月政策维护成本
     */
    fun getMonthlyPolicyCost(): Int {
        return _implementedPolicies.value.sumOf { it.monthlyCost }
    }
    
    /**
     * 更新当前金币
     */
    fun updateCurrentGold(gold: Int) {
        _currentGold.value = gold
        updateAvailablePolicies(_policies.value)
    }
    
    /**
     * 根据类型获取政策
     */
    fun getPoliciesByType(type: com.citysimulator.game.data.model.PolicyType): List<CityPolicy> {
        return _policies.value.filter { it.type == type }
    }
    
    /**
     * 根据等级获取政策
     */
    fun getPoliciesByLevel(level: com.citysimulator.game.data.model.PolicyLevel): List<CityPolicy> {
        return _policies.value.filter { it.level == level }
    }
    
    /**
     * 获取政策实施进度
     */
    fun getImplementationProgress(): Pair<Int, Int> {
        val implemented = _implementedPolicies.value.size
        val total = _policies.value.size
        return Pair(implemented, total)
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 重置所有政策
     */
    fun resetAllPolicies() {
        viewModelScope.launch {
            try {
                cityPolicyDao.resetAllPolicies()
                loadPolicies()
            } catch (e: Exception) {
                _errorMessage.value = "重置政策失败: ${e.message}"
            }
        }
    }
}
