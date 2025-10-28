package com.citysimulator.game.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citysimulator.game.ai.TaskGeneratorEngine
import com.citysimulator.game.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 任务系统ViewModel
 * 
 * 管理所有任务的状态和生命周期，使用SharedPreferences持久化
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val taskGenerator = TaskGeneratorEngine()
    private val gson = Gson()
    
    // SharedPreferences for task persistence
    private val prefs: SharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TASKS = "tasks_json"
        private const val KEY_COMPLETED_COUNT = "completed_count"
    }
    
    // 所有任务列表
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    // 当前进行中的主任务
    private val _currentMainTask = MutableStateFlow<Task?>(null)
    val currentMainTask: StateFlow<Task?> = _currentMainTask.asStateFlow()
    
    // 近期任务（显示在UI上的）
    private val _recentTasks = MutableStateFlow<List<Task>>(emptyList())
    val recentTasks: StateFlow<List<Task>> = _recentTasks.asStateFlow()
    
    // 已完成任务数量
    private val _completedTaskCount = MutableStateFlow(0)
    val completedTaskCount: StateFlow<Int> = _completedTaskCount.asStateFlow()
    
    // 是否显示新任务通知
    private val _showNewTaskNotification = MutableStateFlow(false)
    val showNewTaskNotification: StateFlow<Boolean> = _showNewTaskNotification.asStateFlow()
    
    // 刚完成的任务（用于显示完成对话框）
    private val _justCompletedTask = MutableStateFlow<Task?>(null)
    val justCompletedTask: StateFlow<Task?> = _justCompletedTask.asStateFlow()
    
    init {
        // 从SharedPreferences加载任务
        loadTasksFromPreferences()
    }
    
    /**
     * 从SharedPreferences加载任务
     */
    private fun loadTasksFromPreferences() {
        viewModelScope.launch {
            val tasksJson = prefs.getString(KEY_TASKS, null)
            
            if (tasksJson.isNullOrEmpty()) {
                // 如果没有保存的任务，初始化新手任务
                initializeTutorialTasks()
            } else {
                // 从JSON反序列化任务列表
                try {
                    val type = object : TypeToken<List<Task>>() {}.type
                    val savedTasks: List<Task> = gson.fromJson(tasksJson, type)
                    _tasks.value = savedTasks
                    updateCurrentMainTask()
                    updateRecentTasks()
                    _completedTaskCount.value = prefs.getInt(KEY_COMPLETED_COUNT, 0)
                } catch (e: Exception) {
                    // JSON解析失败，重新初始化
                    initializeTutorialTasks()
                }
            }
        }
    }
    
    /**
     * 保存任务到SharedPreferences
     */
    private fun saveTasksToPreferences(tasks: List<Task>) {
        val tasksJson = gson.toJson(tasks)
        prefs.edit()
            .putString(KEY_TASKS, tasksJson)
            .putInt(KEY_COMPLETED_COUNT, tasks.count { it.status == TaskStatus.COMPLETED })
            .apply()
    }
    
    /**
     * 初始化新手任务
     */
    private suspend fun initializeTutorialTasks() {
        val tutorialTasks = taskGenerator.generateTutorialTasks()
        _tasks.value = tutorialTasks
        saveTasksToPreferences(tutorialTasks)
        updateCurrentMainTask()
        updateRecentTasks()
    }
    
    /**
     * 根据城市状态更新任务
     */
    fun updateTasksBasedOnCityState(
        population: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>,
        monthlyIncome: Int,
        goldAmount: Int
    ) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            
            // 更新人口相关任务进度（只更新进行中的任务）
            currentTasks.forEachIndexed { index, task ->
                // 只更新已接受且进行中的任务，不更新可接受、已完成、已拒绝或失败的任务
                if (task.status == TaskStatus.IN_PROGRESS) {
                    val updatedTask = if (task.targetType == "POPULATION") {
                        taskGenerator.updateTaskProgress(task, population)
                    } else if (task.targetType == "INCOME") {
                        taskGenerator.updateTaskProgress(task, monthlyIncome)
                    } else if (task.targetType == "BUILD" && task.targetBuildingType != null) {
                        val built = buildingCount[task.targetBuildingType] ?: 0
                        taskGenerator.updateTaskProgress(task, built)
                    } else {
                        task
                    }
                    
                    // 检查任务是否刚刚完成
                    if (task.status == TaskStatus.IN_PROGRESS && updatedTask.status == TaskStatus.COMPLETED) {
                        _justCompletedTask.value = updatedTask
                        println("🎉 任务完成: ${updatedTask.title}")
                    }
                    
                    currentTasks[index] = updatedTask
                }
            }
            
            // 检查任务过期（只检查进行中和可接受的任务）
            currentTasks.forEachIndexed { index, task ->
                if (task.status == TaskStatus.IN_PROGRESS || task.status == TaskStatus.AVAILABLE) {
                    currentTasks[index] = taskGenerator.checkTaskExpiration(task)
                }
            }
            
            // 生成新的发展任务（仅在任务列表为空或需要新任务时）
            // 只为不存在的任务ID生成新任务，避免覆盖已接受的任务
            val developmentTasks = taskGenerator.generateDevelopmentTasks(
                population, buildingCount, monthlyIncome
            )
            
            // 添加新任务（严格避免重复，保护已接受的任务）
            for (newTask in developmentTasks) {
                val existingTask = currentTasks.find { it.id == newTask.id }
                if (existingTask == null) {
                    // 任务不存在，添加新任务
                    currentTasks.add(newTask)
                    _showNewTaskNotification.value = true
                    println("✨ 新任务生成: ${newTask.title}")
                }
                // 如果任务已存在，不做任何操作，保持原有状态（已接受/进行中/已完成）
            }
            
            _tasks.value = currentTasks
            
            // 保存到SharedPreferences
            saveTasksToPreferences(currentTasks)
            
            updateCurrentMainTask()
            updateRecentTasks()
            
            // 更新完成任务计数
            _completedTaskCount.value = currentTasks.count { it.status == TaskStatus.COMPLETED }
        }
    }
    
    /**
     * 尝试生成随机事件任务
     */
    fun tryGenerateRandomEvent(
        population: Int,
        goldAmount: Int,
        trafficCongestionRate: Float = 0f
    ) {
        viewModelScope.launch {
            val eventTask = taskGenerator.generateRandomEventTask(
                population, goldAmount, trafficCongestionRate
            )
            
            if (eventTask != null) {
                val currentTasks = _tasks.value.toMutableList()
                // 检查是否已存在相同的事件
                if (!currentTasks.any { it.id == eventTask.id && it.status == TaskStatus.AVAILABLE }) {
                    currentTasks.add(eventTask)
                    _tasks.value = currentTasks
                    // 保存到SharedPreferences
                    saveTasksToPreferences(currentTasks)
                    _showNewTaskNotification.value = true
                    updateRecentTasks()
                }
            }
        }
    }
    
    /**
     * 接受任务
     */
    fun acceptTask(taskId: String) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val taskIndex = currentTasks.indexOfFirst { it.id == taskId }
            
            if (taskIndex != -1) {
                currentTasks[taskIndex] = taskGenerator.acceptTask(currentTasks[taskIndex])
                _tasks.value = currentTasks
                // 保存到SharedPreferences
                saveTasksToPreferences(currentTasks)
                updateCurrentMainTask()
                updateRecentTasks()
            }
        }
    }
    
    /**
     * 拒绝任务
     */
    fun rejectTask(taskId: String) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val taskIndex = currentTasks.indexOfFirst { it.id == taskId }
            
            if (taskIndex != -1) {
                currentTasks[taskIndex] = taskGenerator.rejectTask(currentTasks[taskIndex])
                _tasks.value = currentTasks
                // 保存到SharedPreferences
                saveTasksToPreferences(currentTasks)
                updateRecentTasks()
            }
        }
    }
    
    /**
     * 完成任务并分发奖励
     */
    fun completeTask(
        taskId: String,
        onGoldReward: (Int) -> Unit,
        onPopulationReward: (Int) -> Unit,
        onReputationReward: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val currentTasks = _tasks.value.toMutableList()
            val taskIndex = currentTasks.indexOfFirst { it.id == taskId }
            
            if (taskIndex != -1) {
                val task = currentTasks[taskIndex]
                
                // 分发奖励
                taskGenerator.distributeRewards(
                    task = task,
                    onGoldReward = onGoldReward,
                    onPopulationReward = onPopulationReward,
                    onReputationReward = onReputationReward,
                    onUnlockBuilding = { building ->
                        println("🔓 解锁建筑: $building")
                    },
                    onSpecialReward = { reward ->
                        println("🎁 特殊奖励: $reward")
                    }
                )
                
                _tasks.value = currentTasks
                // 保存到SharedPreferences
                saveTasksToPreferences(currentTasks)
                _completedTaskCount.value++
                updateCurrentMainTask()
                updateRecentTasks()
            }
        }
    }
    
    /**
     * 更新当前主任务
     */
    private fun updateCurrentMainTask() {
        val mainTask = _tasks.value
            .filter { it.isMainTask && it.status == TaskStatus.IN_PROGRESS }
            .maxByOrNull { it.priority }
        _currentMainTask.value = mainTask
    }
    
    /**
     * 更新近期任务列表（显示在UI上的）
     */
    private fun updateRecentTasks() {
        val recent = _tasks.value
            .filter { 
                it.status == TaskStatus.AVAILABLE || 
                it.status == TaskStatus.IN_PROGRESS 
            }
            .sortedByDescending { it.priority }
            .take(5) // 只显示最多5个任务
        _recentTasks.value = recent
    }
    
    /**
     * 清除新任务通知
     */
    fun clearNewTaskNotification() {
        _showNewTaskNotification.value = false
    }
    
    /**
     * 清除刚完成的任务提示
     */
    fun clearCompletedTaskNotification() {
        _justCompletedTask.value = null
    }
    
    /**
     * 获取任务分类
     */
    fun getTasksByType(type: TaskType): List<Task> {
        return _tasks.value.filter { it.type == type }
    }
    
    /**
     * 获取特定状态的任务
     */
    fun getTasksByStatus(status: TaskStatus): List<Task> {
        return _tasks.value.filter { it.status == status }
    }
}

