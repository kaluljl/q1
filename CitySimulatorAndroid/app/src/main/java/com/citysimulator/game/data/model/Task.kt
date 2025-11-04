package com.citysimulator.game.data.model

import java.util.Date

/**
 * 任务类型枚举
 * 
 * 分为三大类：新手引导、发展导向、随机事件
 */
enum class TaskType {
    TUTORIAL,           // 新手引导型任务
    DEVELOPMENT,        // 发展导向型任务
    RANDOM_EVENT        // 随机事件型任务
}

/**
 * 任务状态枚举
 */
enum class TaskStatus {
    AVAILABLE,          // 可接受
    IN_PROGRESS,        // 进行中
    COMPLETED,          // 已完成
    FAILED,             // 已失败
    REJECTED            // 已拒绝（仅用于可选任务）
}

/**
 * 任务触发方式枚举
 */
enum class TaskTrigger {
    MANUAL,             // 手动/初始
    POPULATION_MILESTONE,   // 人口里程碑
    BUILDING_BUILT,     // 建筑建造后
    DATA_THRESHOLD,     // 数据阈值
    TIME_BASED,         // 时间触发
    RANDOM              // 随机触发
}

/**
 * 任务奖励类型枚举
 */
enum class RewardType {
    GOLD,               // 金币
    POPULATION,         // 人口
    BUILDING_UNLOCK,    // 建筑解锁
    SPECIAL_ITEM,       // 特殊道具
    REPUTATION,         // 声望/满意度
    BOOST               // 临时增益效果
}

/**
 * 任务奖励数据类
 */
data class TaskReward(
    val type: RewardType,
    val amount: Int = 0,
    val description: String = "",
    val buildingType: SimplifiedBuildingType? = null
)

/**
 * 任务数据模型
 * 
 * 表示游戏中的一个任务
 * 
 * 注意：使用SharedPreferences持久化，不使用Room
 */
data class Task(
    val id: String,
    
    // 基本信息
    val title: String,
    val description: String,
    val type: TaskType,
    val status: TaskStatus = TaskStatus.AVAILABLE,
    
    // 任务目标
    val targetType: String,             // 目标类型：如 "BUILD", "POPULATION", "INCOME", "SOLVE_PROBLEM"
    val targetValue: Int,               // 目标值
    val currentValue: Int = 0,          // 当前进度
    val targetBuildingType: SimplifiedBuildingType? = null,  // 如果是建造任务，指定建筑类型
    
    // 任务属性
    val isOptional: Boolean = false,    // 是否可选（可拒绝）
    val isMainTask: Boolean = false,    // 是否主线任务
    val priority: Int = 0,              // 优先级（数字越大越重要）
    val timeLimit: Long? = null,        // 时间限制（毫秒），null表示无限制
    
    // 奖励
    val rewards: List<TaskReward> = emptyList(),
    
    // 触发信息
    val trigger: TaskTrigger = TaskTrigger.MANUAL,
    val triggerValue: String? = null,   // 触发条件的具体值
    
    // 时间戳
    val createdAt: Date = Date(),
    val startedAt: Date? = null,
    val completedAt: Date? = null,
    val expiresAt: Date? = null
) {
    /**
     * 获取任务进度百分比
     */
    fun getProgressPercentage(): Int {
        if (targetValue == 0) return 0
        return ((currentValue.toFloat() / targetValue.toFloat()) * 100).toInt().coerceIn(0, 100)
    }
    
    /**
     * 检查任务是否已完成
     */
    fun isComplete(): Boolean {
        return currentValue >= targetValue
    }
    
    /**
     * 检查任务是否已过期
     */
    fun isExpired(): Boolean {
        return expiresAt != null && Date().after(expiresAt)
    }
    
    /**
     * 获取任务图标emoji
     */
    fun getIcon(): String {
        return when (type) {
            TaskType.TUTORIAL -> "🎓"
            TaskType.DEVELOPMENT -> "🎯"
            TaskType.RANDOM_EVENT -> "✦"
        }
    }
    
    /**
     * 获取状态图标emoji
     */
    fun getStatusIcon(): String {
        return when (status) {
            TaskStatus.AVAILABLE -> "📌"
            TaskStatus.IN_PROGRESS -> "⚠️"
            TaskStatus.COMPLETED -> "✅"
            TaskStatus.FAILED -> "❌"
            TaskStatus.REJECTED -> "🚫"
        }
    }
    
    /**
     * 获取奖励描述文本
     */
    fun getRewardDescription(): String {
        if (rewards.isEmpty()) return "无奖励"
        return rewards.joinToString(", ") { reward ->
            when (reward.type) {
                RewardType.GOLD -> "${reward.amount}💰金币"
                RewardType.POPULATION -> "${reward.amount}👥人口"
                RewardType.BUILDING_UNLOCK -> "解锁${reward.description}"
                RewardType.SPECIAL_ITEM -> reward.description
                RewardType.REPUTATION -> "+${reward.amount}满意度"
                RewardType.BOOST -> reward.description
            }
        }
    }
}

/**
 * 预定义的新手任务列表
 */
object TutorialTasks {
    fun getAll(): List<Task> {
        return listOf(
            Task(
                id = "tutorial_road",
                title = "建造第一条道路",
                description = "道路是城市的血脉，让我们先建造一条土路吧！",
                type = TaskType.TUTORIAL,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.DIRT_ROAD,
                isMainTask = true,
                priority = 100,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 50, "新手奖励")
                )
            ),
            Task(
                id = "tutorial_house",
                title = "规划住宅区",
                description = "市民需要住所！建造一座小木屋吧。",
                type = TaskType.TUTORIAL,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SMALL_HOUSE,
                isMainTask = true,
                priority = 99,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 100),
                    TaskReward(RewardType.POPULATION, 5, "新市民入住")
                )
            ),
            Task(
                id = "tutorial_power",
                title = "建造发电设施",
                description = "城市需要电力！建造一座风车吧。",
                type = TaskType.TUTORIAL,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.WINDMILL,
                isMainTask = true,
                priority = 98,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 150)
                )
            ),
            Task(
                id = "tutorial_water",
                title = "保障水源供应",
                description = "市民需要饮用水！建造一口水井。",
                type = TaskType.TUTORIAL,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.WATER_WELL,
                isMainTask = true,
                priority = 97,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 150),
                    TaskReward(RewardType.REPUTATION, 10, "市民感谢")
                )
            )
        )
    }
}

/**
 * 预定义的发展任务模板
 */
object DevelopmentTaskTemplates {
    fun getPopulationMilestones(): List<Task> {
        return listOf(
            Task(
                id = "dev_pop_50",
                title = "达到50人口",
                description = "让你的城市繁荣起来！达到50人口。",
                type = TaskType.DEVELOPMENT,
                targetType = "POPULATION",
                targetValue = 50,
                isMainTask = true,
                priority = 80,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "25",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 300),
                    TaskReward(RewardType.BUILDING_UNLOCK, description = "公寓楼")
                )
            ),
            Task(
                id = "dev_pop_100",
                title = "达到100人口",
                description = "城市规模不断扩大！达到100人口。",
                type = TaskType.DEVELOPMENT,
                targetType = "POPULATION",
                targetValue = 100,
                isMainTask = true,
                priority = 75,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "50",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 500),
                    TaskReward(RewardType.BUILDING_UNLOCK, description = "便利店")
                )
            ),
            Task(
                id = "dev_pop_200",
                title = "达到200人口",
                description = "你的城市已经小有名气！达到200人口。",
                type = TaskType.DEVELOPMENT,
                targetType = "POPULATION",
                targetValue = 200,
                isMainTask = true,
                priority = 70,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "100",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1000),
                    TaskReward(RewardType.BUILDING_UNLOCK, description = "现代化住宅")
                )
            )
        )
    }
    
    fun getIncomeMilestones(): List<Task> {
        return listOf(
            Task(
                id = "dev_income_500",
                title = "月收入达到500金币",
                description = "让城市经济运转起来！",
                type = TaskType.DEVELOPMENT,
                targetType = "INCOME",
                targetValue = 500,
                priority = 60,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 500),
                    TaskReward(RewardType.BOOST, description = "24小时建造速度+20%")
                )
            )
        )
    }
    
    fun getBuildingTasks(): List<Task> {
        return listOf(
            Task(
                id = "dev_build_school",
                title = "建造教育设施",
                description = "提升市民教育水平，建造一所小诊所或警察局。",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SMALL_CLINIC,
                priority = 50,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "50",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 400),
                    TaskReward(RewardType.REPUTATION, 20)
                )
            ),
            Task(
                id = "dev_build_park",
                title = "建造休闲设施",
                description = "市民需要放松！建造一个小公园。",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SMALL_PARK,
                priority = 45,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 300),
                    TaskReward(RewardType.REPUTATION, 15, "市民满意度提升")
                )
            ),
            Task(
                id = "dev_build_factory",
                title = "工业发展",
                description = "建造工厂，提升城市生产力！",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.FOOD_FACTORY,
                priority = 55,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "75",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 600),
                    TaskReward(RewardType.BOOST, description = "收入+10%持续12小时")
                )
            ),
            Task(
                id = "dev_build_store",
                title = "商业设施",
                description = "建造商店，满足市民购物需求！",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.CONVENIENCE_STORE,
                priority = 40,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 250),
                    TaskReward(RewardType.REPUTATION, 10)
                )
            ),
            Task(
                id = "dev_build_shopping_mall",
                title = "商业中心",
                description = "建造购物中心，让城市更加繁华！",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SHOPPING_MALL,
                priority = 65,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "120",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1000),
                    TaskReward(RewardType.REPUTATION, 25)
                )
            ),
            Task(
                id = "dev_build_hospital",
                title = "医疗保障",
                description = "建造大医院，保障市民健康！",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.HOSPITAL,
                priority = 70,
                trigger = TaskTrigger.POPULATION_MILESTONE,
                triggerValue = "150",
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1200),
                    TaskReward(RewardType.REPUTATION, 30, "健康城市称号")
                )
            ),
            Task(
                id = "dev_build_solar_plant",
                title = "绿色能源",
                description = "建造太阳能电站，走可持续发展道路！",
                type = TaskType.DEVELOPMENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SOLAR_PLANT,
                priority = 60,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 800),
                    TaskReward(RewardType.REPUTATION, 20, "环保城市")
                )
            )
        )
    }
}

/**
 * 随机事件任务模板
 */
object RandomEventTaskTemplates {
    fun getAll(): List<Task> {
        return listOf(
            Task(
                id = "event_park_request",
                title = "市民请求：建造公园",
                description = "市民代表希望在市中心建造一个公园，提升生活品质。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SMALL_PARK,
                isOptional = true,
                priority = 30,
                timeLimit = 120000, // 2分钟
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.REPUTATION, 25, "社区领袖称号")
                )
            ),
            Task(
                id = "event_investor_mall",
                title = "投资者提议：建造购物中心",
                description = "投资者愿意资助$1500，但购物中心会增加交通压力。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.SHOPPING_MALL,
                isOptional = true,
                priority = 35,
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1500, "投资资助")
                )
            ),
            Task(
                id = "event_traffic_congestion",
                title = "紧急：交通拥堵",
                description = "主干道拥堵率超过60%！建造更多道路缓解压力。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 2,
                targetBuildingType = SimplifiedBuildingType.PAVED_ROAD,
                priority = 40,
                timeLimit = 90000, // 1.5分钟
                trigger = TaskTrigger.DATA_THRESHOLD,
                triggerValue = "traffic_congestion_60",
                rewards = listOf(
                    TaskReward(RewardType.REPUTATION, 15, "市民感激")
                )
            ),
            Task(
                id = "event_festival",
                title = "🎉 城市节日庆典",
                description = "一年一度的城市节日即将到来！建造一个广场庆祝吧！",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.PLAZA,
                isOptional = true,
                priority = 50,
                timeLimit = 180000, // 3分钟
                trigger = TaskTrigger.TIME_BASED,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 800),
                    TaskReward(RewardType.REPUTATION, 40, "节日组织者")
                )
            ),
            Task(
                id = "event_power_shortage",
                title = "⚡ 电力短缺警告",
                description = "用电高峰期到来，城市缺电！快速建造发电设施。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.COAL_PLANT,
                priority = 60,
                timeLimit = 120000, // 2分钟
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 500),
                    TaskReward(RewardType.REPUTATION, 20)
                )
            ),
            Task(
                id = "event_homeless_crisis",
                title = "🏠 住房危机",
                description = "大量新移民涌入！紧急建造住宅满足需求。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 3,
                targetBuildingType = SimplifiedBuildingType.APARTMENT,
                priority = 55,
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 600),
                    TaskReward(RewardType.POPULATION, 15, "新市民感激入住")
                )
            ),
            Task(
                id = "event_entrepreneur",
                title = "💼 创业者入驻",
                description = "科技创业者想在你的城市开公司！建造科技园区吸引他们。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.TECH_PARK,
                isOptional = true,
                priority = 45,
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1000),
                    TaskReward(RewardType.BOOST, description = "税收+15%持续24小时")
                )
            ),
            Task(
                id = "event_pollution_alert",
                title = "🌿 环保行动",
                description = "环保组织呼吁减少污染！建造绿化设施改善环境。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 2,
                targetBuildingType = SimplifiedBuildingType.SMALL_PARK,
                isOptional = true,
                priority = 40,
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.REPUTATION, 30, "绿色城市勋章")
                )
            ),
            Task(
                id = "event_crime_wave",
                title = "🚨 犯罪率上升",
                description = "市民反映治安问题！建造警察局维护秩序。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.POLICE_STATION,
                priority = 65,
                timeLimit = 150000, // 2.5分钟
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 700),
                    TaskReward(RewardType.REPUTATION, 35, "安全城市")
                )
            ),
            Task(
                id = "event_tech_boom",
                title = "💻 科技浪潮",
                description = "抓住科技革命的机会！建造食品加工厂提升生产力。",
                type = TaskType.RANDOM_EVENT,
                targetType = "BUILD",
                targetValue = 1,
                targetBuildingType = SimplifiedBuildingType.FOOD_FACTORY,
                isOptional = true,
                priority = 50,
                trigger = TaskTrigger.RANDOM,
                rewards = listOf(
                    TaskReward(RewardType.GOLD, 1500),
                    TaskReward(RewardType.BOOST, description = "生产力+25%持续48小时")
                )
            )
        )
    }
}

