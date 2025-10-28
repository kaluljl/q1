package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * AI NPC行为决策引擎
 */
class NPCBehaviorEngine {
    
    /**
     * 根据NPC状态和个性决定下一步行为
     */
    fun decideNextAction(npc: AINPC, currentTime: Int, cityEnvironment: CityEnvironment): NPCActivity {
        val personality = npc.personality
        val needs = analyzeNeeds(npc)
        
        return when {
            // 紧急需求优先
            needs.needsRest -> NPCActivity.SLEEPING
            needs.needsFood -> NPCActivity.EATING
            needs.needsSocial && personality == Personality.EXTROVERT -> NPCActivity.SOCIALIZING
            
            // 根据时间安排
            isWorkTime(currentTime, npc) && npc.canWork() -> NPCActivity.WORKING
            
            // 根据个性偏好
            else -> decideByPersonality(npc, needs, currentTime)
        }
    }
    
    /**
     * 分析NPC当前需求
     */
    private fun analyzeNeeds(npc: AINPC): NPCNeeds {
        return NPCNeeds(
            needsRest = npc.needsRest(),
            needsFood = npc.needsFood(),
            needsSocial = npc.needsSocial()
        )
    }
    
    /**
     * 根据个性决定行为
     */
    private fun decideByPersonality(npc: AINPC, needs: NPCNeeds, currentTime: Int): NPCActivity {
        val personality = npc.personality
        
        return when (personality) {
            Personality.EXTROVERT -> {
                if (Random.nextFloat() < 0.7f) NPCActivity.SOCIALIZING
                else NPCActivity.ENTERTAINMENT
            }
            Personality.INTROVERT -> {
                if (Random.nextFloat() < 0.6f) NPCActivity.IDLE
                else NPCActivity.EXERCISING
            }
            Personality.WORKAHOLIC -> {
                if (Random.nextFloat() < 0.8f) NPCActivity.WORKING
                else NPCActivity.EATING
            }
            Personality.LAZY -> {
                if (Random.nextFloat() < 0.7f) NPCActivity.IDLE
                else NPCActivity.SLEEPING
            }
            Personality.CREATIVE -> {
                val activities = listOf(NPCActivity.ENTERTAINMENT, NPCActivity.SOCIALIZING, NPCActivity.SHOPPING)
                activities.random()
            }
            Personality.PRACTICAL -> {
                if (Random.nextFloat() < 0.6f) NPCActivity.SHOPPING
                else NPCActivity.EXERCISING
            }
            else -> NPCActivity.IDLE
        }
    }
    
    /**
     * 判断是否是工作时间
     */
    private fun isWorkTime(currentTime: Int, npc: AINPC): Boolean {
        val workHours = npc.preferences.preferredWorkHours
        return currentTime in workHours.first..workHours.second
    }
    
    /**
     * 计算移动到目标位置的时间
     */
    fun calculateTravelTime(from: Pair<Int, Int>, to: Pair<Int, Int>): Int {
        val distance = kotlin.math.abs(from.first - to.first) + kotlin.math.abs(from.second - to.second)
        return distance * 5 // 每个格子5分钟
    }
    
    /**
     * 更新NPC状态
     */
    suspend fun updateNPCState(npc: AINPC, timePassed: Int): AINPC {
        val updatedNPC = npc.copy(
            energy = (npc.energy - timePassed * 0.5f).coerceAtLeast(0f),
            hunger = (npc.hunger + timePassed * 0.3f).coerceAtMost(100f),
            socialNeed = (npc.socialNeed + timePassed * 0.2f).coerceAtMost(100f),
            lastUpdateTime = System.currentTimeMillis()
        )
        
        // 根据当前活动调整状态
        return when (updatedNPC.currentActivity) {
            NPCActivity.SLEEPING -> updatedNPC.copy(
                energy = (updatedNPC.energy + timePassed * 1.0f).coerceAtMost(100f),
                happiness = (updatedNPC.happiness + timePassed * 0.1f).coerceAtMost(100f)
            )
            NPCActivity.EATING -> updatedNPC.copy(
                hunger = (updatedNPC.hunger - timePassed * 1.5f).coerceAtLeast(0f),
                happiness = (updatedNPC.happiness + timePassed * 0.2f).coerceAtMost(100f)
            )
            NPCActivity.SOCIALIZING -> updatedNPC.copy(
                socialNeed = (updatedNPC.socialNeed - timePassed * 1.0f).coerceAtLeast(0f),
                happiness = (updatedNPC.happiness + timePassed * 0.3f).coerceAtMost(100f)
            )
            NPCActivity.WORKING -> updatedNPC.copy(
                energy = (updatedNPC.energy - timePassed * 0.8f).coerceAtLeast(0f),
                happiness = when (updatedNPC.personality) {
                    Personality.WORKAHOLIC -> (updatedNPC.happiness + timePassed * 0.1f).coerceAtMost(100f)
                    Personality.LAZY -> (updatedNPC.happiness - timePassed * 0.2f).coerceAtLeast(0f)
                    else -> updatedNPC.happiness
                }
            )
            NPCActivity.ENTERTAINMENT -> updatedNPC.copy(
                happiness = (updatedNPC.happiness + timePassed * 0.4f).coerceAtMost(100f),
                energy = (updatedNPC.energy - timePassed * 0.3f).coerceAtLeast(0f)
            )
            else -> updatedNPC
        }
    }
}

/**
 * 城市环境数据
 */
data class CityEnvironment(
    val weather: WeatherType,
    val timeOfDay: Int,
    val cityHappiness: Float,
    val availableActivities: List<NPCActivity>,
    val buildingDensity: Float,
    val trafficLevel: Float
)

/**
 * NPC需求分析结果
 */
private data class NPCNeeds(
    val needsRest: Boolean,
    val needsFood: Boolean,
    val needsSocial: Boolean
)
