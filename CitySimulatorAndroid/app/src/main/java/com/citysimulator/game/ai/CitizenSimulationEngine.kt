package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 市民模拟引擎
 * 
 * 负责模拟市民的日常生活、决策和行为
 */
class CitizenSimulationEngine {
    
    /**
     * 更新市民状态
     * 
     * 根据当前时间和市民状态，更新市民的活动和位置
     */
    fun updateCitizen(
        citizen: Citizen,
        currentTime: Date,
        buildings: List<Building>
    ): Citizen {
        val calendar = Calendar.getInstance().apply { time = currentTime }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = getDayOfWeek(calendar)
        
        // 根据时间和性格决定活动
        val newActivity = determineActivity(citizen, hour, dayOfWeek)
        
        // 如果活动改变，更新目标位置
        val (destX, destY) = if (newActivity != citizen.currentActivity) {
            getDestinationForActivity(citizen, newActivity, buildings)
        } else {
            citizen.destinationX to citizen.destinationY
        }
        
        // 移动市民（简化的移动逻辑）
        val (newX, newY) = moveTowardsDestination(
            citizen.currentX, citizen.currentY,
            destX, destY
        )
        
        // 更新需求
        val updatedNeeds = updateNeeds(citizen, newActivity)
        
        // 计算幸福度
        val newHappiness = calculateHappiness(citizen, buildings)
        
        return citizen.copy(
            currentActivity = newActivity,
            currentX = newX,
            currentY = newY,
            destinationX = destX,
            destinationY = destY,
            needsFood = updatedNeeds.first,
            needsRest = updatedNeeds.second,
            needsEntertainment = updatedNeeds.third,
            happiness = newHappiness,
            lastActivityChangeTime = if (newActivity != citizen.currentActivity) currentTime else citizen.lastActivityChangeTime
        )
    }
    
    /**
     * 根据时间和市民状态决定活动
     */
    private fun determineActivity(
        citizen: Citizen,
        hour: Int,
        dayOfWeek: DayOfWeek
    ): CitizenActivity {
        // 睡眠时间
        if (hour in 23..23 || hour in 0..5) {
            return CitizenActivity.SLEEPING
        }
        
        // 工作日
        val isWeekday = dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        
        if (isWeekday && citizen.occupation != null) {
            return when (citizen.workingHours) {
                WorkingHours.STANDARD -> {
                    when (hour) {
                        in 6..8 -> CitizenActivity.AT_HOME
                        in 8..9 -> CitizenActivity.COMMUTING_TO_WORK
                        in 9..17 -> CitizenActivity.WORKING
                        in 17..18 -> CitizenActivity.COMMUTING_HOME
                        else -> getLeisureActivity(citizen, hour)
                    }
                }
                WorkingHours.MORNING -> {
                    when (hour) {
                        in 5..6 -> CitizenActivity.COMMUTING_TO_WORK
                        in 6..14 -> CitizenActivity.WORKING
                        in 14..15 -> CitizenActivity.COMMUTING_HOME
                        else -> getLeisureActivity(citizen, hour)
                    }
                }
                WorkingHours.EVENING -> {
                    when (hour) {
                        in 6..13 -> getLeisureActivity(citizen, hour)
                        in 13..14 -> CitizenActivity.COMMUTING_TO_WORK
                        in 14..22 -> CitizenActivity.WORKING
                        in 22..23 -> CitizenActivity.COMMUTING_HOME
                        else -> CitizenActivity.AT_HOME
                    }
                }
                WorkingHours.NIGHT -> {
                    when (hour) {
                        in 6..21 -> getLeisureActivity(citizen, hour)
                        in 21..22 -> CitizenActivity.COMMUTING_TO_WORK
                        else -> CitizenActivity.WORKING
                    }
                }
                WorkingHours.FLEXIBLE -> {
                    if (Random.nextFloat() < 0.7f && hour in 9..17) {
                        CitizenActivity.WORKING
                    } else {
                        getLeisureActivity(citizen, hour)
                    }
                }
            }
        }
        
        // 周末或无工作
        return getLeisureActivity(citizen, hour)
    }
    
    /**
     * 根据性格和需求获取休闲活动
     */
    private fun getLeisureActivity(citizen: Citizen, hour: Int): CitizenActivity {
        // 根据需求优先级
        if (citizen.needsMedical > 0.7f) return CitizenActivity.MEDICAL
        if (citizen.needsFood > 0.8f && hour in 7..21) return CitizenActivity.DINING
        
        // 根据性格
        return when (citizen.personality) {
            CitizenPersonality.WORKAHOLIC -> {
                if (Random.nextFloat() < 0.3f) CitizenActivity.WORKING else CitizenActivity.AT_HOME
            }
            CitizenPersonality.FAMILY_ORIENTED -> CitizenActivity.AT_HOME
            CitizenPersonality.SOCIAL -> {
                if (hour in 10..22) {
                    if (Random.nextFloat() < 0.6f) CitizenActivity.ENTERTAINMENT else CitizenActivity.SOCIALIZING
                } else CitizenActivity.AT_HOME
            }
            CitizenPersonality.HEALTH_CONSCIOUS -> {
                if (hour in 6..9 || hour in 18..20) {
                    CitizenActivity.EXERCISING
                } else if (Random.nextFloat() < 0.5f) {
                    CitizenActivity.PARK
                } else {
                    CitizenActivity.AT_HOME
                }
            }
            CitizenPersonality.BALANCED -> {
                val rand = Random.nextFloat()
                when {
                    rand < 0.2f && hour in 11..21 -> CitizenActivity.SHOPPING
                    rand < 0.4f && hour in 10..22 -> CitizenActivity.ENTERTAINMENT
                    rand < 0.5f && hour in 10..20 -> CitizenActivity.PARK
                    else -> CitizenActivity.AT_HOME
                }
            }
            CitizenPersonality.DEMANDING -> {
                val rand = Random.nextFloat()
                when {
                    rand < 0.3f -> CitizenActivity.SHOPPING
                    rand < 0.6f -> CitizenActivity.DINING
                    else -> CitizenActivity.AT_HOME
                }
            }
        }
    }
    
    /**
     * 获取活动的目标位置
     */
    private fun getDestinationForActivity(
        citizen: Citizen,
        activity: CitizenActivity,
        buildings: List<Building>
    ): Pair<Int?, Int?> {
        return when (activity) {
            CitizenActivity.AT_HOME, CitizenActivity.SLEEPING -> 
                citizen.homeX to citizen.homeY
            
            CitizenActivity.WORKING -> 
                citizen.workplaceX to citizen.workplaceY
            
            CitizenActivity.SHOPPING -> 
                findNearestBuilding(citizen, BuildingType.SHOP, buildings)
            
            CitizenActivity.DINING -> 
                findNearestBuilding(citizen, BuildingType.RESTAURANT, buildings)
            
            CitizenActivity.ENTERTAINMENT -> 
                findNearestBuilding(citizen, BuildingType.HOTEL, buildings)
            
            CitizenActivity.MEDICAL -> 
                findNearestBuilding(citizen, BuildingType.HOSPITAL, buildings)
            
            CitizenActivity.SCHOOL -> 
                findNearestBuilding(citizen, BuildingType.SCHOOL, buildings)
            
            CitizenActivity.PARK, CitizenActivity.EXERCISING -> 
                findNearestBuilding(citizen, BuildingType.PARK, buildings)
            
            else -> null to null
        }
    }
    
    /**
     * 查找最近的建筑
     */
    private fun findNearestBuilding(
        citizen: Citizen,
        buildingType: BuildingType,
        buildings: List<Building>
    ): Pair<Int?, Int?> {
        val targetBuildings = buildings.filter { it.type == buildingType }
        if (targetBuildings.isEmpty()) return null to null
        
        val nearest = targetBuildings.minByOrNull { building ->
            calculateDistance(
                citizen.currentX, citizen.currentY,
                building.position.x, building.position.y
            )
        }
        
        return nearest?.let { it.position.x to it.position.y } ?: (null to null)
    }
    
    /**
     * 计算距离
     */
    private fun calculateDistance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val dx = (x2 - x1).toFloat()
        val dy = (y2 - y1).toFloat()
        return sqrt(dx * dx + dy * dy)
    }
    
    /**
     * 移动到目标位置
     */
    private fun moveTowardsDestination(
        currentX: Int,
        currentY: Int,
        destX: Int?,
        destY: Int?
    ): Pair<Int, Int> {
        if (destX == null || destY == null) return currentX to currentY
        
        // 简单的直线移动
        val dx = destX - currentX
        val dy = destY - currentY
        
        if (abs(dx) == 0 && abs(dy) == 0) return currentX to currentY
        
        // 每次更新移动1格
        val newX = when {
            dx > 0 -> currentX + 1
            dx < 0 -> currentX - 1
            else -> currentX
        }
        
        val newY = when {
            dy > 0 -> currentY + 1
            dy < 0 -> currentY - 1
            else -> currentY
        }
        
        return newX to newY
    }
    
    /**
     * 更新需求
     */
    private fun updateNeeds(
        citizen: Citizen,
        activity: CitizenActivity
    ): Triple<Float, Float, Float> {
        var food = citizen.needsFood
        var rest = citizen.needsRest
        var entertainment = citizen.needsEntertainment
        
        // 随时间增加需求
        food = (food + 0.05f).coerceIn(0f, 1f)
        rest = (rest + 0.03f).coerceIn(0f, 1f)
        entertainment = (entertainment + 0.02f).coerceIn(0f, 1f)
        
        // 活动减少需求
        when (activity) {
            CitizenActivity.SLEEPING -> rest = (rest - 0.3f).coerceIn(0f, 1f)
            CitizenActivity.DINING -> food = (food - 0.5f).coerceIn(0f, 1f)
            CitizenActivity.ENTERTAINMENT, CitizenActivity.SOCIALIZING, CitizenActivity.PARK -> 
                entertainment = (entertainment - 0.2f).coerceIn(0f, 1f)
            else -> {}
        }
        
        return Triple(food, rest, entertainment)
    }
    
    /**
     * 计算幸福度
     */
    private fun calculateHappiness(
        citizen: Citizen,
        buildings: List<Building>
    ): Float {
        var happiness = 0.5f
        
        // 基础需求满足度
        happiness += (1f - citizen.needsFood) * 0.15f
        happiness += (1f - citizen.needsRest) * 0.15f
        happiness += (1f - citizen.needsEntertainment) * 0.1f
        
        // 健康状态
        happiness += citizen.health * 0.15f
        
        // 工作状态
        if (citizen.occupation != null && citizen.salary > 0) {
            happiness += 0.1f
        } else {
            happiness -= 0.15f // 失业惩罚
        }
        
        // 通勤时间
        if (citizen.averageCommuteTime > 60) {
            happiness -= 0.1f
        } else if (citizen.averageCommuteTime < 20) {
            happiness += 0.05f
        }
        
        // 周边设施
        val nearbyPark = buildings.any { 
            it.type == BuildingType.PARK && 
            calculateDistance(citizen.homeX, citizen.homeY, it.position.x, it.position.y) < 5f
        }
        if (nearbyPark) happiness += 0.05f
        
        val nearbyShop = buildings.any { 
            it.type == BuildingType.SHOP && 
            calculateDistance(citizen.homeX, citizen.homeY, it.position.x, it.position.y) < 5f
        }
        if (nearbyShop) happiness += 0.05f
        
        // 性格调整
        when (citizen.personality) {
            CitizenPersonality.DEMANDING -> happiness -= 0.1f // 苛刻型基础不满
            CitizenPersonality.BALANCED -> happiness += 0.05f // 平衡型更容易满足
            else -> {}
        }
        
        return happiness.coerceIn(0f, 1f)
    }
    
    /**
     * 生成市民想法
     */
    fun generateThought(citizen: Citizen, buildings: List<Building>): CitizenThought? {
        // 根据状态生成想法
        val (type, message) = when {
            citizen.happiness > 0.8f -> ThoughtType.HAPPY to getHappyThought(citizen)
            citizen.happiness > 0.6f -> ThoughtType.SATISFIED to getSatisfiedThought(citizen)
            citizen.happiness < 0.3f -> ThoughtType.ANGRY to getAngryThought(citizen)
            citizen.happiness < 0.5f -> ThoughtType.CONCERNED to getConcernedThought(citizen)
            citizen.needsFood > 0.8f -> ThoughtType.NEED to "我饿了，需要找个地方吃饭"
            citizen.needsRest > 0.8f -> ThoughtType.NEED to "我好累，需要好好休息"
            citizen.needsMedical > 0.7f -> ThoughtType.NEED to "我感觉不舒服，需要看医生"
            else -> return null
        }
        
        return CitizenThought(
            citizenId = citizen.id,
            timestamp = Date(),
            type = type,
            message = message
        )
    }
    
    private fun getHappyThought(citizen: Citizen): String {
        val thoughts = listOf(
            "这座城市真不错！",
            "今天心情很好！",
            "生活在这里很幸福！",
            "我爱这座城市！",
            "一切都很完美！"
        )
        return thoughts.random()
    }
    
    private fun getSatisfiedThought(citizen: Citizen): String {
        val thoughts = listOf(
            "生活还算不错",
            "整体还挺满意的",
            "这里的生活还可以",
            "工作生活都还好"
        )
        return thoughts.random()
    }
    
    private fun getAngryThought(citizen: Citizen): String {
        val thoughts = listOf(
            "这座城市太糟糕了！",
            "我受够了这里的生活！",
            "市长根本不关心我们！",
            "这里什么都缺！",
            "我要搬走了！"
        )
        return thoughts.random()
    }
    
    private fun getConcernedThought(citizen: Citizen): String {
        val thoughts = listOf(
            "希望城市能改善一下...",
            "有些问题需要解决",
            "生活有点困难",
            "希望情况会好转"
        )
        return thoughts.random()
    }
    
    private fun getDayOfWeek(calendar: Calendar): DayOfWeek {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            else -> DayOfWeek.SUNDAY
        }
    }
}

