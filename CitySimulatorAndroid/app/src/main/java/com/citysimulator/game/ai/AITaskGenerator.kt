package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.random.Random

/**
 * AI任务生成器
 * 
 * 根据城市状态动态生成多样化的任务
 */
object AITaskGenerator {
    
    /**
     * 根据城市状态生成AI任务
     */
    suspend fun generateAITask(
        population: Int,
        goldAmount: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>,
        monthlyIncome: Int,
        cityHappiness: Float,
        currentMonth: Int
    ): Task? = withContext(Dispatchers.IO) {
        try {
            // 只有在人口达到一定规模后才生成AI任务
            if (population < 20) return@withContext null
            
            // 30%概率生成AI任务
            if (Random.nextFloat() > 0.3f) return@withContext null
            
            // 根据城市状态选择任务类型
            val taskType = selectTaskType(population, goldAmount, cityHappiness)
            
            // 生成任务
            generateTaskByType(
                taskType,
                population,
                goldAmount,
                buildingCount,
                monthlyIncome,
                cityHappiness,
                currentMonth
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 选择任务类型
     */
    private fun selectTaskType(
        population: Int,
        goldAmount: Int,
        cityHappiness: Float
    ): AITaskType {
        return when {
            // 人口少，优先建造住宅
            population < 50 -> AITaskType.BUILD_HOUSING
            
            // 金币少，优先发展经济
            goldAmount < 1000 -> AITaskType.DEVELOP_ECONOMY
            
            // 幸福度低，优先改善民生
            cityHappiness < 0.4f -> AITaskType.IMPROVE_HAPPINESS
            
            // 人口多，发展公共服务
            population > 100 -> AITaskType.BUILD_PUBLIC_SERVICE
            
            // 随机选择
            else -> AITaskType.values().random()
        }
    }
    
    /**
     * 根据类型生成任务
     */
    private fun generateTaskByType(
        type: AITaskType,
        population: Int,
        goldAmount: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>,
        monthlyIncome: Int,
        cityHappiness: Float,
        currentMonth: Int
    ): Task {
        val taskId = "ai_task_${System.currentTimeMillis()}"
        
        return when (type) {
            AITaskType.BUILD_HOUSING -> generateHousingTask(taskId, population, buildingCount)
            AITaskType.DEVELOP_ECONOMY -> generateEconomyTask(taskId, goldAmount, monthlyIncome, buildingCount)
            AITaskType.IMPROVE_HAPPINESS -> generateHappinessTask(taskId, cityHappiness, buildingCount)
            AITaskType.BUILD_PUBLIC_SERVICE -> generatePublicServiceTask(taskId, population, buildingCount)
            AITaskType.EXPAND_CITY -> generateExpansionTask(taskId, population, buildingCount)
            AITaskType.SOLVE_CRISIS -> generateCrisisTask(taskId, cityHappiness, goldAmount)
        }
    }
    
    /**
     * 生成住房任务
     */
    private fun generateHousingTask(
        taskId: String,
        population: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>
    ): Task {
        val housingTypes = listOf(
            SimplifiedBuildingType.SMALL_HOUSE,
            SimplifiedBuildingType.APARTMENT,
            SimplifiedBuildingType.MODERN_RESIDENCE
        )
        
        // 根据人口选择合适的住宅类型
        val buildingType = when {
            population < 30 -> SimplifiedBuildingType.SMALL_HOUSE
            population < 80 -> SimplifiedBuildingType.APARTMENT
            else -> housingTypes.random()
        }
        
        val currentCount = buildingCount[buildingType] ?: 0
        val targetCount = currentCount + Random.nextInt(1, 4)
        
        val titles = listOf(
            "扩建住宅区",
            "解决住房问题",
            "增加居住空间",
            "建设宜居社区",
            "改善居住条件"
        )
        
        val descriptions = listOf(
            "市民反映住房紧张，请建造${targetCount - currentCount}栋${buildingType.getDisplayName()}来缓解压力。",
            "城市人口增长迅速，急需更多住宅。请建造${targetCount - currentCount}栋${buildingType.getDisplayName()}。",
            "为了吸引更多居民，我们需要建造${targetCount - currentCount}栋${buildingType.getDisplayName()}。",
            "住房短缺影响了城市发展，请尽快建造${targetCount - currentCount}栋${buildingType.getDisplayName()}。"
        )
        
        val goldReward = (targetCount - currentCount) * Random.nextInt(200, 400)
        val reputationReward = (targetCount - currentCount) * Random.nextInt(5, 15)
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.DEVELOPMENT,
            targetType = "BUILD",
            targetValue = targetCount,
            currentValue = currentCount,
            targetBuildingType = buildingType,
            isOptional = true,
            priority = 2,
            rewards = listOf(
                TaskReward(RewardType.GOLD, goldReward, "金币奖励"),
                TaskReward(RewardType.REPUTATION, reputationReward, "声望提升")
            ),
            trigger = TaskTrigger.RANDOM,
            createdAt = Date()
        )
    }
    
    /**
     * 生成经济任务
     */
    private fun generateEconomyTask(
        taskId: String,
        goldAmount: Int,
        monthlyIncome: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>
    ): Task {
        val economicBuildings = listOf(
            SimplifiedBuildingType.LEMONADE_STAND,
            SimplifiedBuildingType.CONVENIENCE_STORE,
            SimplifiedBuildingType.FOOD_FACTORY,
            SimplifiedBuildingType.SHOPPING_MALL,
            SimplifiedBuildingType.TECH_PARK
        )
        
        // 根据当前收入选择建筑
        val buildingType = when {
            monthlyIncome < 200 -> SimplifiedBuildingType.LEMONADE_STAND
            monthlyIncome < 500 -> SimplifiedBuildingType.CONVENIENCE_STORE
            monthlyIncome < 1000 -> SimplifiedBuildingType.FOOD_FACTORY
            else -> economicBuildings.random()
        }
        
        val currentCount = buildingCount[buildingType] ?: 0
        val targetCount = currentCount + Random.nextInt(1, 3)
        
        val titles = listOf(
            "振兴经济",
            "增加财政收入",
            "发展商业",
            "提升经济活力",
            "打造商业中心"
        )
        
        val descriptions = listOf(
            "城市财政紧张，请建造${targetCount - currentCount}个${buildingType.getDisplayName()}来增加收入。",
            "为了提升经济实力，我们需要${targetCount - currentCount}个${buildingType.getDisplayName()}。",
            "商业发展滞后，请建造${targetCount - currentCount}个${buildingType.getDisplayName()}来改善。",
            "市民呼吁更多商业设施，请建造${targetCount - currentCount}个${buildingType.getDisplayName()}。"
        )
        
        val goldReward = (targetCount - currentCount) * Random.nextInt(300, 600)
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.DEVELOPMENT,
            targetType = "BUILD",
            targetValue = targetCount,
            currentValue = currentCount,
            targetBuildingType = buildingType,
            isOptional = true,
            priority = 3,
            rewards = listOf(
                TaskReward(RewardType.GOLD, goldReward, "金币奖励"),
                TaskReward(RewardType.BOOST, 0, "经济增益：收入+10%（7天）")
            ),
            trigger = TaskTrigger.RANDOM,
            createdAt = Date()
        )
    }
    
    /**
     * 生成幸福度任务
     */
    private fun generateHappinessTask(
        taskId: String,
        cityHappiness: Float,
        buildingCount: Map<SimplifiedBuildingType, Int>
    ): Task {
        val happinessBuildings = listOf(
            SimplifiedBuildingType.SMALL_PARK,
            SimplifiedBuildingType.PLAZA,
            SimplifiedBuildingType.SMALL_CLINIC,
            SimplifiedBuildingType.HOSPITAL
        )
        
        val buildingType = happinessBuildings.random()
        val currentCount = buildingCount[buildingType] ?: 0
        val targetCount = currentCount + Random.nextInt(1, 3)
        
        val titles = listOf(
            "提升市民幸福感",
            "改善生活质量",
            "关注民生",
            "建设幸福城市",
            "回应市民诉求"
        )
        
        val descriptions = listOf(
            "市民满意度下降，请建造${targetCount - currentCount}个${buildingType.getDisplayName()}来改善。",
            "为了让市民更幸福，我们需要${targetCount - currentCount}个${buildingType.getDisplayName()}。",
            "市民抱怨生活设施不足，请建造${targetCount - currentCount}个${buildingType.getDisplayName()}。",
            "幸福指数告急！请尽快建造${targetCount - currentCount}个${buildingType.getDisplayName()}。"
        )
        
        val goldReward = (targetCount - currentCount) * Random.nextInt(250, 500)
        val reputationReward = (targetCount - currentCount) * Random.nextInt(10, 20)
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.RANDOM_EVENT,
            targetType = "BUILD",
            targetValue = targetCount,
            currentValue = currentCount,
            targetBuildingType = buildingType,
            isOptional = true,
            priority = 4,
            rewards = listOf(
                TaskReward(RewardType.GOLD, goldReward, "金币奖励"),
                TaskReward(RewardType.REPUTATION, reputationReward, "声望大幅提升")
            ),
            trigger = TaskTrigger.RANDOM,
            timeLimit = 7 * 24 * 60 * 60 * 1000L, // 7天
            createdAt = Date()
        )
    }
    
    /**
     * 生成公共服务任务
     */
    private fun generatePublicServiceTask(
        taskId: String,
        population: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>
    ): Task {
        val serviceBuildings = listOf(
            SimplifiedBuildingType.POLICE_STATION,
            SimplifiedBuildingType.HOSPITAL,
            SimplifiedBuildingType.RECYCLING_CENTER,
            SimplifiedBuildingType.COAL_PLANT,
            SimplifiedBuildingType.WATER_PUMP
        )
        
        val buildingType = serviceBuildings.random()
        val currentCount = buildingCount[buildingType] ?: 0
        val targetCount = currentCount + 1
        
        val titles = listOf(
            "完善公共服务",
            "提升城市功能",
            "建设基础设施",
            "优化公共设施",
            "强化城市管理"
        )
        
        val descriptions = listOf(
            "随着城市规模扩大，我们需要${buildingType.getDisplayName()}来保障公共服务。",
            "市民呼吁增加${buildingType.getDisplayName()}，请尽快建造。",
            "为了城市长远发展，请建造${buildingType.getDisplayName()}。",
            "公共服务不足影响市民生活，请建造${buildingType.getDisplayName()}。"
        )
        
        val goldReward = Random.nextInt(400, 800)
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.DEVELOPMENT,
            targetType = "BUILD",
            targetValue = targetCount,
            currentValue = currentCount,
            targetBuildingType = buildingType,
            isOptional = true,
            priority = 2,
            rewards = listOf(
                TaskReward(RewardType.GOLD, goldReward, "金币奖励"),
                TaskReward(RewardType.REPUTATION, 15, "声望提升")
            ),
            trigger = TaskTrigger.RANDOM,
            createdAt = Date()
        )
    }
    
    /**
     * 生成城市扩张任务
     */
    private fun generateExpansionTask(
        taskId: String,
        population: Int,
        buildingCount: Map<SimplifiedBuildingType, Int>
    ): Task {
        val allBuildingTypes = SimplifiedBuildingType.values()
        val randomBuilding = allBuildingTypes.random()
        val currentCount = buildingCount[randomBuilding] ?: 0
        val targetCount = currentCount + Random.nextInt(2, 5)
        
        val titles = listOf(
            "城市扩张计划",
            "大规模建设",
            "打造现代化城市",
            "城市升级工程",
            "全面发展战略"
        )
        
        val descriptions = listOf(
            "城市进入快速发展期，请建造${targetCount - currentCount}个${randomBuilding.getDisplayName()}。",
            "为了实现城市现代化，我们需要${targetCount - currentCount}个${randomBuilding.getDisplayName()}。",
            "扩张计划启动！请建造${targetCount - currentCount}个${randomBuilding.getDisplayName()}。"
        )
        
        val goldReward = (targetCount - currentCount) * Random.nextInt(300, 600)
        val reputationReward = (targetCount - currentCount) * Random.nextInt(10, 25)
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.DEVELOPMENT,
            targetType = "BUILD",
            targetValue = targetCount,
            currentValue = currentCount,
            targetBuildingType = randomBuilding,
            isOptional = true,
            priority = 1,
            rewards = listOf(
                TaskReward(RewardType.GOLD, goldReward, "丰厚金币奖励"),
                TaskReward(RewardType.REPUTATION, reputationReward, "声望大幅提升"),
                TaskReward(RewardType.BOOST, 0, "建设加速：建造时间-20%（5天）")
            ),
            trigger = TaskTrigger.RANDOM,
            createdAt = Date()
        )
    }
    
    /**
     * 生成危机任务
     */
    private fun generateCrisisTask(
        taskId: String,
        cityHappiness: Float,
        goldAmount: Int
    ): Task {
        val crisisTypes = listOf(
            "经济危机" to "城市财政告急！",
            "民心危机" to "市民满意度暴跌！",
            "发展危机" to "城市发展停滞不前！"
        )
        
        val (crisisType, crisisDesc) = crisisTypes.random()
        
        val urgentBuildings = listOf(
            SimplifiedBuildingType.SMALL_PARK,
            SimplifiedBuildingType.CONVENIENCE_STORE,
            SimplifiedBuildingType.SMALL_CLINIC
        )
        
        val buildingType = urgentBuildings.random()
        
        val titles = listOf(
            "紧急：$crisisType",
            "危机应对：$crisisType",
            "市长！$crisisType",
            "警报：$crisisType"
        )
        
        val descriptions = listOf(
            "$crisisDesc 请立即建造3个${buildingType.getDisplayName()}来稳定局势！",
            "$crisisDesc 市民要求立即行动，建造3个${buildingType.getDisplayName()}！",
            "$crisisDesc 时间紧迫，请尽快建造3个${buildingType.getDisplayName()}！"
        )
        
        return Task(
            id = taskId,
            title = titles.random(),
            description = descriptions.random(),
            type = TaskType.RANDOM_EVENT,
            targetType = "BUILD",
            targetValue = 3,
            currentValue = 0,
            targetBuildingType = buildingType,
            isOptional = false, // 危机任务不可拒绝
            isMainTask = true,
            priority = 5,
            rewards = listOf(
                TaskReward(RewardType.GOLD, 1000, "危机奖励"),
                TaskReward(RewardType.REPUTATION, 50, "声望恢复"),
                TaskReward(RewardType.BOOST, 0, "市民信心恢复")
            ),
            trigger = TaskTrigger.RANDOM,
            timeLimit = 3 * 24 * 60 * 60 * 1000L, // 3天限时
            createdAt = Date()
        )
    }
}

/**
 * AI任务类型枚举
 */
enum class AITaskType {
    BUILD_HOUSING,          // 建造住宅
    DEVELOP_ECONOMY,        // 发展经济
    IMPROVE_HAPPINESS,      // 提升幸福度
    BUILD_PUBLIC_SERVICE,   // 建造公共服务
    EXPAND_CITY,            // 城市扩张
    SOLVE_CRISIS            // 解决危机
}

/**
 * 扩展函数：获取建筑显示名称
 */
private fun SimplifiedBuildingType.getDisplayName(): String = when (this) {
    // 住宅
    SimplifiedBuildingType.SMALL_HOUSE -> "小木屋"
    SimplifiedBuildingType.APARTMENT -> "公寓楼"
    SimplifiedBuildingType.MODERN_RESIDENCE -> "现代化住宅"
    // 经济
    SimplifiedBuildingType.LEMONADE_STAND -> "柠檬水摊"
    SimplifiedBuildingType.SMALL_FARM -> "小农场"
    SimplifiedBuildingType.CONVENIENCE_STORE -> "便利店"
    SimplifiedBuildingType.FOOD_FACTORY -> "食品工厂"
    SimplifiedBuildingType.SHOPPING_MALL -> "购物中心"
    SimplifiedBuildingType.TECH_PARK -> "科技园区"
    // 电力
    SimplifiedBuildingType.WINDMILL -> "风车"
    SimplifiedBuildingType.COAL_PLANT -> "火电厂"
    SimplifiedBuildingType.SOLAR_PLANT -> "太阳能电站"
    // 水源
    SimplifiedBuildingType.WATER_WELL -> "水井"
    SimplifiedBuildingType.WATER_PUMP -> "水泵站"
    SimplifiedBuildingType.WATER_PURIFIER -> "水净化厂"
    // 垃圾
    SimplifiedBuildingType.GARBAGE_DUMP -> "垃圾堆"
    SimplifiedBuildingType.RECYCLING_CENTER -> "回收中心"
    SimplifiedBuildingType.ECO_FACILITY -> "环保处理厂"
    // 安全与健康
    SimplifiedBuildingType.SMALL_CLINIC -> "小诊所"
    SimplifiedBuildingType.HOSPITAL -> "医院"
    SimplifiedBuildingType.POLICE_STATION -> "警察局"
    // 道路与装饰
    SimplifiedBuildingType.DIRT_ROAD -> "土路"
    SimplifiedBuildingType.PAVED_ROAD -> "柏油路"
    SimplifiedBuildingType.TREE_LINED_ROAD -> "林荫大道"
    SimplifiedBuildingType.SMALL_PARK -> "小公园"
    SimplifiedBuildingType.PLAZA -> "广场"
    SimplifiedBuildingType.FOUNTAIN -> "喷泉"
}

