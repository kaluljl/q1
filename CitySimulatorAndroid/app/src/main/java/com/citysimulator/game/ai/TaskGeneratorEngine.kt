package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlin.random.Random

/**
 * 任务生成引擎
 * 
 * 负责根据城市状态生成合适的任务
 */
class TaskGeneratorEngine {
    
    /**
     * 生成初始新手任务
     */
    fun generateTutorialTasks(): List<Task> {
        return TutorialTasks.getAll()
    }
    
    /**
     * 根据人口里程碑生成发展任务
     */
    fun generatePopulationMilestoneTasks(currentPopulation: Int): List<Task> {
        val tasks = mutableListOf<Task>()
        
        // 根据当前人口，生成下一个里程碑任务
        val milestones = DevelopmentTaskTemplates.getPopulationMilestones()
        for (milestone in milestones) {
            if (currentPopulation < milestone.targetValue) {
                tasks.add(milestone.copy(
                    currentValue = currentPopulation,
                    status = if (currentPopulation >= milestone.targetValue) 
                        TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
                ))
                break // 只生成下一个里程碑
            }
        }
        
        return tasks
    }
    
    /**
     * 根据城市状态生成发展任务
     */
    fun generateDevelopmentTasks(
        population: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>,
        monthlyIncome: Int
    ): List<Task> {
        val tasks = mutableListOf<Task>()
        
        // 人口里程碑任务
        tasks.addAll(generatePopulationMilestoneTasks(population))
        
        // 收入里程碑任务
        if (monthlyIncome < 500) {
            tasks.add(DevelopmentTaskTemplates.getIncomeMilestones()[0].copy(
                currentValue = monthlyIncome
            ))
        }
        
        // 建筑任务（基于人口解锁）
        if (population >= 50) {
            val buildingTasks = DevelopmentTaskTemplates.getBuildingTasks()
            for (task in buildingTasks) {
                val targetType = task.targetBuildingType
                if (targetType != null) {
                    val built = buildingCount[targetType] ?: 0
                    if (built < task.targetValue) {
                        tasks.add(task.copy(currentValue = built))
                    }
                }
            }
        }
        
        return tasks
    }
    
    /**
     * 随机生成事件任务
     */
    fun generateRandomEventTask(
        population: Int,
        goldAmount: Int,
        trafficCongestionRate: Float = 0f
    ): Task? {
        // 只有在人口达到一定规模后才触发随机事件
        if (population < 30) return null
        
        // 20%概率触发随机事件
        if (Random.nextFloat() > 0.2f) return null
        
        val eventTemplates = RandomEventTaskTemplates.getAll()
        
        // 根据城市状态筛选合适的事件
        val availableEvents = eventTemplates.filter { event ->
            when (event.id) {
                "event_park_request" -> population >= 50
                "event_investor_mall" -> population >= 100 && goldAmount < 2000
                "event_traffic_congestion" -> trafficCongestionRate > 60f
                else -> true
            }
        }
        
        if (availableEvents.isEmpty()) return null
        
        // 随机选择一个事件
        return availableEvents.random().copy(
            createdAt = java.util.Date(),
            expiresAt = if (availableEvents.random().timeLimit != null) {
                java.util.Date(System.currentTimeMillis() + (availableEvents.random().timeLimit ?: 0))
            } else null
        )
    }
    
    /**
     * 更新任务进度
     */
    fun updateTaskProgress(
        task: Task,
        currentValue: Int
    ): Task {
        val updatedTask = task.copy(currentValue = currentValue)
        
        // 如果任务完成，更新状态
        return if (updatedTask.isComplete() && updatedTask.status == TaskStatus.IN_PROGRESS) {
            updatedTask.copy(
                status = TaskStatus.COMPLETED,
                completedAt = java.util.Date()
            )
        } else {
            updatedTask
        }
    }
    
    /**
     * 接受任务
     */
    fun acceptTask(task: Task): Task {
        return task.copy(
            status = TaskStatus.IN_PROGRESS,
            startedAt = java.util.Date()
        )
    }
    
    /**
     * 拒绝任务
     */
    fun rejectTask(task: Task): Task {
        return task.copy(
            status = TaskStatus.REJECTED,
            completedAt = java.util.Date()
        )
    }
    
    /**
     * 检查任务是否过期
     */
    fun checkTaskExpiration(task: Task): Task {
        return if (task.isExpired() && task.status == TaskStatus.IN_PROGRESS) {
            task.copy(
                status = TaskStatus.FAILED,
                completedAt = java.util.Date()
            )
        } else {
            task
        }
    }
    
    /**
     * 分发任务奖励
     */
    fun distributeRewards(
        task: Task,
        onGoldReward: (Int) -> Unit,
        onPopulationReward: (Int) -> Unit,
        onReputationReward: (Int) -> Unit,
        onUnlockBuilding: (String) -> Unit,
        onSpecialReward: (String) -> Unit
    ) {
        if (task.status != TaskStatus.COMPLETED) return
        
        task.rewards.forEach { reward ->
            when (reward.type) {
                RewardType.GOLD -> onGoldReward(reward.amount)
                RewardType.POPULATION -> onPopulationReward(reward.amount)
                RewardType.REPUTATION -> onReputationReward(reward.amount)
                RewardType.BUILDING_UNLOCK -> onUnlockBuilding(reward.description)
                RewardType.SPECIAL_ITEM, RewardType.BOOST -> onSpecialReward(reward.description)
            }
        }
    }
}

