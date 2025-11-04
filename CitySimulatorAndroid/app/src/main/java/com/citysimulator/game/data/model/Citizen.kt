package com.citysimulator.game.data.model

import java.util.Date
import java.util.UUID

/**
 * 市民数据模型
 * 
 * 代表城市中的每一个居民，包含详细的个人信息和生活状态
 * 
 * 注意：不使用Room持久化，仅在内存中管理
 */
data class Citizen(
    val id: String = UUID.randomUUID().toString(),
    
    // 基本信息
    val name: String,
    val age: Int,
    val gender: Gender,
    val birthDate: Date = Date(),
    
    // 家庭信息
    val homeX: Int, // 家庭住址X坐标
    val homeY: Int, // 家庭住址Y坐标
    val familyId: String? = null, // 家庭ID
    val maritalStatus: MaritalStatus = MaritalStatus.SINGLE,
    
    // 工作信息
    val occupation: String? = null,
    val workplaceX: Int? = null, // 工作地点X坐标
    val workplaceY: Int? = null, // 工作地点Y坐标
    val salary: Int = 0,
    val workingHours: WorkingHours = WorkingHours.STANDARD,
    
    // 状态信息
    val happiness: Float = 0.5f, // 幸福度 0-1
    val health: Float = 1.0f, // 健康度 0-1
    val education: EducationLevel = EducationLevel.HIGH_SCHOOL,
    val wealth: Int = 0, // 个人财富
    
    // 需求
    val needsFood: Float = 0.5f, // 食物需求 0-1
    val needsWater: Float = 0.5f, // 水需求 0-1
    val needsRest: Float = 0.5f, // 休息需求 0-1
    val needsEntertainment: Float = 0.5f, // 娱乐需求 0-1
    val needsMedical: Float = 0.0f, // 医疗需求 0-1
    
    // 当前活动
    val currentActivity: CitizenActivity = CitizenActivity.AT_HOME,
    val currentX: Int = homeX, // 当前位置X
    val currentY: Int = homeY, // 当前位置Y
    val destinationX: Int? = null, // 目标位置X
    val destinationY: Int? = null, // 目标位置Y
    
    // 性格特征
    val personality: CitizenPersonality = CitizenPersonality.BALANCED,
    
    // 深度AI人格系统（8维人格特质）
    val personalityTraits: PersonalityTraits? = null,
    
    // 时间统计
    val lastActivityChangeTime: Date = Date(),
    val totalCommuteTime: Long = 0, // 累计通勤时间（分钟）
    val averageCommuteTime: Int = 0, // 平均通勤时间（分钟）
    
    // 生活历史
    val complaints: Int = 0, // 抱怨次数
    val movedTimes: Int = 0, // 搬家次数
    val jobChanges: Int = 0 // 换工作次数
)

/**
 * 性别
 */
enum class Gender {
    MALE,
    FEMALE
}

/**
 * 婚姻状态
 */
enum class MaritalStatus {
    SINGLE,      // 单身
    MARRIED,     // 已婚
    DIVORCED,    // 离异
    WIDOWED;     // 丧偶
    
    fun getDisplayName(): String = when (this) {
        SINGLE -> "单身"
        MARRIED -> "已婚"
        DIVORCED -> "离异"
        WIDOWED -> "丧偶"
    }
}

/**
 * 工作时间
 */
enum class WorkingHours {
    MORNING,     // 早班 (6:00-14:00)
    STANDARD,    // 正常班 (9:00-17:00)
    EVENING,     // 晚班 (14:00-22:00)
    NIGHT,       // 夜班 (22:00-6:00)
    FLEXIBLE     // 弹性工作
}

/**
 * 市民活动状态
 */
enum class CitizenActivity {
    SLEEPING,           // 睡觉
    AT_HOME,           // 在家
    COMMUTING_TO_WORK, // 通勤去上班
    WORKING,           // 工作中
    COMMUTING_HOME,    // 通勤回家
    SHOPPING,          // 购物
    ENTERTAINMENT,     // 娱乐
    DINING,            // 用餐
    MEDICAL,           // 就医
    SCHOOL,            // 上学
    PARK,              // 公园休闲
    EXERCISING,        // 锻炼
    SOCIALIZING;       // 社交
    
    fun getDisplayName(): String = when (this) {
        SLEEPING -> "睡觉"
        AT_HOME -> "在家"
        COMMUTING_TO_WORK -> "通勤上班"
        WORKING -> "工作中"
        COMMUTING_HOME -> "通勤回家"
        SHOPPING -> "购物"
        ENTERTAINMENT -> "娱乐"
        DINING -> "用餐"
        MEDICAL -> "就医"
        SCHOOL -> "上学"
        PARK -> "公园休闲"
        EXERCISING -> "锻炼"
        SOCIALIZING -> "社交"
    }
}

/**
 * 市民性格
 */
enum class CitizenPersonality {
    WORKAHOLIC,    // 工作狂 - 更关注工作和收入
    FAMILY_ORIENTED, // 家庭为重 - 更关注家庭生活
    SOCIAL,        // 社交达人 - 更关注娱乐和社交
    HEALTH_CONSCIOUS, // 健康主义 - 更关注健康和运动
    BALANCED,      // 平衡型 - 各方面都均衡
    DEMANDING      // 苛刻型 - 对城市服务要求高
}

/**
 * 市民日程计划
 */
data class CitizenSchedule(
    val citizenId: String,
    val dayOfWeek: DayOfWeek,
    val activities: List<ScheduledActivity>
)

/**
 * 计划活动
 */
data class ScheduledActivity(
    val startHour: Int, // 开始小时 (0-23)
    val duration: Int, // 持续时间（小时）
    val activity: CitizenActivity,
    val locationX: Int?,
    val locationY: Int?
)

/**
 * 星期
 */
enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

/**
 * 市民想法/心情
 */
data class CitizenThought(
    val citizenId: String,
    val timestamp: Date,
    val type: ThoughtType,
    val message: String,
    val relatedBuildingType: BuildingType? = null,
    val relatedLocation: Pair<Int, Int>? = null
)

/**
 * 想法类型
 */
enum class ThoughtType {
    HAPPY,          // 开心
    SATISFIED,      // 满意
    NEUTRAL,        // 中性
    CONCERNED,      // 担心
    ANGRY,          // 愤怒
    NEED,           // 需求
    COMPLAINT       // 抱怨
}

/**
 * 市民统计信息
 */
data class CitizenStats(
    val citizenId: String,
    val averageHappiness: Float,
    val totalCommuteTime: Long,
    val workDaysThisMonth: Int,
    val healthIssues: Int,
    val moneySpent: Int,
    val moneyEarned: Int,
    val favoriteLocations: List<Pair<Int, Int>>,
    val recentThoughts: List<CitizenThought>
)

