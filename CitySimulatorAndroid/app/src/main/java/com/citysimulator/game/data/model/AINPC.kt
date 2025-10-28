package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * AI驱动的NPC居民数据模型
 */
@Entity(tableName = "ai_npcs")
data class AINPC(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val age: Int,
    val profession: Profession,
    val personality: Personality,
    val currentLocation: Pair<Int, Int>, // 当前位置 (x, y)
    val homeLocation: Pair<Int, Int>, // 家位置
    val workLocation: Pair<Int, Int>?, // 工作位置
    val currentActivity: NPCActivity,
    val happiness: Float, // 0-100
    val energy: Float, // 0-100
    val hunger: Float, // 0-100
    val socialNeed: Float, // 0-100
    val lastUpdateTime: Long = System.currentTimeMillis(),
    val dailySchedule: List<ScheduleItem> = emptyList(),
    val relationships: Map<String, Float> = emptyMap(), // 与其他NPC的关系
    val preferences: NPCPreferences = NPCPreferences(),
    val aiState: AIState = AIState.IDLE
)

/**
 * NPC个性类型
 */
enum class Personality {
    EXTROVERT, // 外向型 - 喜欢社交，容易快乐
    INTROVERT, // 内向型 - 喜欢独处，需要更多休息
    WORKAHOLIC, // 工作狂 - 工作时间长，效率高
    LAZY, // 懒惰型 - 工作时间短，效率低
    OPTIMISTIC, // 乐观型 - 容易满足，恢复快
    PESSIMISTIC, // 悲观型 - 容易不满，恢复慢
    CREATIVE, // 创意型 - 喜欢艺术和文化活动
    PRACTICAL // 实用型 - 注重效率和实用性
}

/**
 * NPC活动类型
 */
enum class NPCActivity {
    SLEEPING, // 睡觉
    WORKING, // 工作
    EATING, // 吃饭
    SOCIALIZING, // 社交
    ENTERTAINMENT, // 娱乐
    SHOPPING, // 购物
    EXERCISING, // 运动
    TRAVELING, // 通勤
    IDLE // 空闲
}

/**
 * AI状态
 */
enum class AIState {
    IDLE, // 空闲
    DECIDING, // 决策中
    MOVING, // 移动中
    INTERACTING, // 交互中
    RESTING // 休息中
}

/**
 * 日程安排项
 */
data class ScheduleItem(
    val startTime: Int, // 开始时间 (小时)
    val endTime: Int, // 结束时间 (小时)
    val activity: NPCActivity,
    val location: Pair<Int, Int>,
    val priority: Int = 1 // 优先级 1-5
)

/**
 * NPC偏好设置
 */
data class NPCPreferences(
    val favoriteActivities: List<NPCActivity> = emptyList(),
    val dislikedActivities: List<NPCActivity> = emptyList(),
    val preferredWorkHours: Pair<Int, Int> = Pair(9, 17), // 偏好工作时间
    val socialThreshold: Float = 50f, // 社交需求阈值
    val energyThreshold: Float = 30f, // 能量阈值
    val hungerThreshold: Float = 40f // 饥饿阈值
)

/**
 * NPC扩展函数
 */
fun AINPC.getDisplayName(): String = name

fun AINPC.getPersonalityDisplayName(): String = when (personality) {
    Personality.EXTROVERT -> "外向型"
    Personality.INTROVERT -> "内向型"
    Personality.WORKAHOLIC -> "工作狂"
    Personality.LAZY -> "懒惰型"
    Personality.OPTIMISTIC -> "乐观型"
    Personality.PESSIMISTIC -> "悲观型"
    Personality.CREATIVE -> "创意型"
    Personality.PRACTICAL -> "实用型"
}

fun AINPC.getActivityDisplayName(): String = when (currentActivity) {
    NPCActivity.SLEEPING -> "睡觉"
    NPCActivity.WORKING -> "工作"
    NPCActivity.EATING -> "吃饭"
    NPCActivity.SOCIALIZING -> "社交"
    NPCActivity.ENTERTAINMENT -> "娱乐"
    NPCActivity.SHOPPING -> "购物"
    NPCActivity.EXERCISING -> "运动"
    NPCActivity.TRAVELING -> "通勤"
    NPCActivity.IDLE -> "空闲"
}

fun AINPC.getHappinessLevel(): String = when {
    happiness >= 80 -> "非常快乐"
    happiness >= 60 -> "快乐"
    happiness >= 40 -> "一般"
    happiness >= 20 -> "不开心"
    else -> "非常不开心"
}

fun AINPC.getEnergyLevel(): String = when {
    energy >= 80 -> "精力充沛"
    energy >= 60 -> "有活力"
    energy >= 40 -> "一般"
    energy >= 20 -> "疲惫"
    else -> "非常疲惫"
}

fun AINPC.isAtHome(): Boolean = currentLocation == homeLocation

fun AINPC.isAtWork(): Boolean = workLocation?.let { currentLocation == it } ?: false

fun AINPC.needsRest(): Boolean = energy < preferences.energyThreshold

fun AINPC.needsFood(): Boolean = hunger > preferences.hungerThreshold

fun AINPC.needsSocial(): Boolean = socialNeed > preferences.socialThreshold

fun AINPC.canWork(): Boolean = energy > 30f && hunger < 80f && happiness > 20f

fun AINPC.getOverallSatisfaction(): Float {
    return (happiness + energy + (100f - hunger) + socialNeed) / 4f
}

fun AINPC.getWorkEfficiency(): Float {
    val baseEfficiency = when (personality) {
        Personality.WORKAHOLIC -> 1.2f
        Personality.LAZY -> 0.7f
        Personality.PRACTICAL -> 1.1f
        Personality.CREATIVE -> 0.9f
        else -> 1.0f
    }
    
    val energyFactor = energy / 100f
    val happinessFactor = happiness / 100f
    
    return baseEfficiency * energyFactor * happinessFactor
}
