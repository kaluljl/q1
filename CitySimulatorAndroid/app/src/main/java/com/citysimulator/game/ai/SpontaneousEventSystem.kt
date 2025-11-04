package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import java.util.Date
import java.util.UUID
import kotlin.random.Random

/**
 * 自发性群体活动系统
 * 
 * 模拟市民自发组织的各类活动（抗议、派对、快闪等）
 * 这些活动不是预设脚本，而是基于市民集体情绪和需求自然涌现
 * 
 * @author AI进化论-花生
 */
object SpontaneousEventSystem {
    
    /**
     * 检测是否可能发生自发活动
     */
    fun checkForSpontaneousEvent(
        citizens: List<Citizen>,
        cityHappiness: Float,
        cityProsperity: Float
    ): SpontaneousEvent? {
        // 计算群体情绪
        val collectiveEmotion = calculateCollectiveEmotion(citizens)
        
        // 根据群体情绪决定事件类型
        return when {
            collectiveEmotion.anger > 0.7f && cityHappiness < 0.4f -> {
                generateProtest(citizens, collectiveEmotion)
            }
            collectiveEmotion.joy > 0.7f && Random.nextFloat() < 0.3f -> {
                generateCelebration(citizens, collectiveEmotion)
            }
            collectiveEmotion.solidarity > 0.6f && Random.nextFloat() < 0.2f -> {
                generateCommunityGathering(citizens, collectiveEmotion)
            }
            collectiveEmotion.creativity > 0.6f && Random.nextFloat() < 0.15f -> {
                generateFlashMob(citizens, collectiveEmotion)
            }
            else -> null
        }
    }
    
    /**
     * 计算集体情绪
     */
    private fun calculateCollectiveEmotion(citizens: List<Citizen>): CollectiveEmotion {
        if (citizens.isEmpty()) {
            return CollectiveEmotion()
        }
        
        val unhappyCitizens = citizens.count { it.happiness < 0.4f }
        val happyCitizens = citizens.count { it.happiness > 0.7f }
        
        return CollectiveEmotion(
            anger = unhappyCitizens.toFloat() / citizens.size,
            joy = happyCitizens.toFloat() / citizens.size,
            solidarity = calculateSolidarity(citizens),
            creativity = calculateCreativity(citizens),
            frustration = calculateFrustration(citizens),
            excitement = calculateExcitement(citizens)
        )
    }
    
    private fun calculateSolidarity(citizens: List<Citizen>): Float {
        // 基于市民性格和社交活动计算团结度
        val socialCitizens = citizens.count { 
            it.personality == com.citysimulator.game.data.model.CitizenPersonality.SOCIAL ||
            it.personality == com.citysimulator.game.data.model.CitizenPersonality.FAMILY_ORIENTED
        }
        return (socialCitizens.toFloat() / citizens.size) * 0.7f + Random.nextFloat() * 0.3f
    }
    
    private fun calculateCreativity(citizens: List<Citizen>): Float {
        val creativeCitizens = citizens.count { 
            it.education == com.citysimulator.game.data.model.EducationLevel.COLLEGE ||
            it.education == com.citysimulator.game.data.model.EducationLevel.GRADUATE ||
            it.education == com.citysimulator.game.data.model.EducationLevel.PHD
        }
        return (creativeCitizens.toFloat() / citizens.size) * 0.6f + Random.nextFloat() * 0.4f
    }
    
    private fun calculateFrustration(citizens: List<Citizen>): Float {
        val frustratedCitizens = citizens.count { 
            it.happiness < 0.5f && it.wealth < 500
        }
        return frustratedCitizens.toFloat() / citizens.size
    }
    
    private fun calculateExcitement(citizens: List<Citizen>): Float {
        val excitedCitizens = citizens.count { it.happiness > 0.6f }
        return (excitedCitizens.toFloat() / citizens.size) * 0.8f + Random.nextFloat() * 0.2f
    }
    
    /**
     * 生成抗议活动
     */
    private fun generateProtest(citizens: List<Citizen>, emotion: CollectiveEmotion): SpontaneousEvent {
        val protestReasons = listOf(
            "工资太低，生活成本太高！",
            "交通拥堵严重，上班太辛苦！",
            "环境污染严重，我们需要清洁的空气！",
            "公共设施不足，我们需要更多医院和学校！",
            "房价太高，年轻人买不起房！"
        )
        
        val participants = selectParticipants(citizens, 0.3f) // 30%愤怒市民参与
        val location = findGatheringLocation(citizens)
        
        return SpontaneousEvent(
            type = EventType.PROTEST,
            title = "市民抗议",
            description = "愤怒的市民们自发聚集在${location?.let { "坐标(${it.first},${it.second})" } ?: "市中心"}进行抗议",
            demands = listOf(protestReasons.random()),
            participants = participants,
            location = location,
            intensity = emotion.anger,
            startTime = Date(),
            duration = (2..6).random() // 2-6小时
        )
    }
    
    /**
     * 生成庆祝活动
     */
    private fun generateCelebration(citizens: List<Citizen>, emotion: CollectiveEmotion): SpontaneousEvent {
        val celebrationReasons = listOf(
            "庆祝新公园建成！",
            "庆祝城市繁荣发展！",
            "自发的周末派对！",
            "庆祝季节性节日！"
        )
        
        val participants = selectParticipants(citizens, 0.2f)
        val location = findGatheringLocation(citizens)
        
        return SpontaneousEvent(
            type = EventType.CELEBRATION,
            title = "自发庆典",
            description = "快乐的市民们在${location?.let { "坐标(${it.first},${it.second})" } ?: "广场"}举办庆祝活动",
            demands = emptyList(),
            participants = participants,
            location = location,
            intensity = emotion.joy,
            startTime = Date(),
            duration = (1..4).random()
        )
    }
    
    /**
     * 生成社区聚会
     */
    private fun generateCommunityGathering(citizens: List<Citizen>, emotion: CollectiveEmotion): SpontaneousEvent {
        val participants = selectParticipants(citizens, 0.15f)
        val location = findGatheringLocation(citizens)
        
        return SpontaneousEvent(
            type = EventType.COMMUNITY_GATHERING,
            title = "社区聚会",
            description = "邻里们自发组织了一场温馨的社区聚会",
            demands = emptyList(),
            participants = participants,
            location = location,
            intensity = emotion.solidarity,
            startTime = Date(),
            duration = (2..5).random()
        )
    }
    
    /**
     * 生成快闪活动
     */
    private fun generateFlashMob(citizens: List<Citizen>, emotion: CollectiveEmotion): SpontaneousEvent {
        val participants = selectParticipants(citizens, 0.1f)
        val location = findGatheringLocation(citizens)
        
        val activities = listOf(
            "即兴音乐表演",
            "集体舞蹈",
            "行为艺术展示",
            "诗歌朗诵会"
        )
        
        return SpontaneousEvent(
            type = EventType.FLASH_MOB,
            title = "创意快闪",
            description = "富有创意的市民们组织了一场${activities.random()}快闪活动",
            demands = emptyList(),
            participants = participants,
            location = location,
            intensity = emotion.creativity,
            startTime = Date(),
            duration = 1 // 快闪通常很短
        )
    }
    
    /**
     * 选择参与者
     */
    private fun selectParticipants(citizens: List<Citizen>, participationRate: Float): List<String> {
        return citizens
            .filter { Random.nextFloat() < participationRate }
            .map { it.id }
            .take(50) // 最多50人参与
    }
    
    /**
     * 寻找聚集地点（选择人口密集区域）
     */
    private fun findGatheringLocation(citizens: List<Citizen>): Pair<Int, Int>? {
        if (citizens.isEmpty()) return null
        
        // 简单实现：选择最常见的位置
        val locationCounts = citizens
            .groupingBy { it.homeX to it.homeY }
            .eachCount()
        
        return locationCounts.maxByOrNull { it.value }?.key
    }
}

/**
 * 集体情绪
 */
data class CollectiveEmotion(
    val anger: Float = 0f,          // 愤怒 (0-1)
    val joy: Float = 0f,            // 喜悦 (0-1)
    val solidarity: Float = 0f,     // 团结 (0-1)
    val creativity: Float = 0f,     // 创造力 (0-1)
    val frustration: Float = 0f,    // 挫折感 (0-1)
    val excitement: Float = 0f      // 兴奋 (0-1)
)

/**
 * 自发事件
 */
data class SpontaneousEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: EventType,
    val title: String,
    val description: String,
    val demands: List<String>, // 诉求（仅抗议有）
    val participants: List<String>, // 参与市民ID
    val location: Pair<Int, Int>?,
    val intensity: Float, // 强度 (0-1)
    val startTime: Date,
    val duration: Int, // 持续时间（小时）
    val endTime: Date = Date(startTime.time + duration * 60 * 60 * 1000),
    val resolved: Boolean = false,
    val playerResponse: String? = null
) {
    /**
     * 事件是否仍在进行中
     */
    fun isOngoing(): Boolean {
        return Date().before(endTime) && !resolved
    }
    
    /**
     * 获取参与规模描述
     */
    fun getScaleDescription(): String = when {
        participants.size < 10 -> "小规模"
        participants.size < 30 -> "中等规模"
        participants.size < 50 -> "大规模"
        else -> "全民性"
    }
    
    /**
     * 获取强度描述
     */
    fun getIntensityDescription(): String = when {
        intensity > 0.8f -> "非常激烈"
        intensity > 0.6f -> "激烈"
        intensity > 0.4f -> "温和"
        else -> "平和"
    }
}

/**
 * 事件类型
 */
/**
 * 为指定市民生成参与的事件（动态生成，基于城市状态）
 */
fun getEventsForCitizen(
    citizenId: String,
    allCitizens: List<Citizen>,
    cityHappiness: Float
): List<SpontaneousEvent> {
    val events = mutableListOf<SpontaneousEvent>()
    val citizen = allCitizens.find { it.id == citizenId } ?: return emptyList()
    
    // 根据城市幸福度决定事件类型
    when {
        cityHappiness > 0.8f -> {
            events.add(SpontaneousEvent(
                type = EventType.CELEBRATION,
                title = "社区庆祝活动",
                location = Pair(citizen.homeX, citizen.homeY),
                description = "邻里自发组织的庆祝活动，气氛热烈",
                demands = emptyList(),
                participants = getNearbyCitizens(citizen, allCitizens, 5),
                intensity = 0.8f + Random.nextFloat() * 0.2f,
                startTime = Date(),
                duration = 120
            ))
        }
        cityHappiness < 0.3f -> {
            val demands = listOf(
                "改善城市环境",
                "降低税收",
                "增加公共服务",
                "提高工资水平"
            )
            events.add(SpontaneousEvent(
                type = EventType.PROTEST,
                title = "市民抗议活动",
                location = Pair(citizen.workplaceX ?: citizen.homeX, citizen.workplaceY ?: citizen.homeY),
                description = "市民对当前政策表示不满，要求改革",
                demands = demands.shuffled().take(2),
                participants = getDiscontentedCitizens(allCitizens, 10),
                intensity = 0.7f + Random.nextFloat() * 0.3f,
                startTime = Date(),
                duration = 180
            ))
        }
        else -> {
            events.add(SpontaneousEvent(
                type = EventType.COMMUNITY_GATHERING,
                title = "社区聚会",
                location = Pair(citizen.homeX, citizen.homeY),
                description = "邻里间的日常聚会活动",
                demands = emptyList(),
                participants = getNearbyCitizens(citizen, allCitizens, 3),
                intensity = 0.5f + Random.nextFloat() * 0.3f,
                startTime = Date(),
                duration = 90
            ))
        }
    }
    
    return events
}

private fun getNearbyCitizens(citizen: Citizen, allCitizens: List<Citizen>, count: Int): List<String> {
    return allCitizens
        .filter { it.id != citizen.id }
        .filter { 
            kotlin.math.abs(it.homeX - citizen.homeX) <= 2 &&
            kotlin.math.abs(it.homeY - citizen.homeY) <= 2
        }
        .shuffled()
        .take(count)
        .map { it.id } + listOf(citizen.id)
}

private fun getDiscontentedCitizens(allCitizens: List<Citizen>, count: Int): List<String> {
    return allCitizens
        .filter { it.happiness < 0.4f }
        .shuffled()
        .take(count)
        .map { it.id }
}

enum class EventType {
    PROTEST,                // 抗议
    CELEBRATION,            // 庆祝
    COMMUNITY_GATHERING,    // 社区聚会
    FLASH_MOB,              // 快闪
    STRIKE,                 // 罢工
    CHARITY,                // 慈善活动
    MARCH;                  // 游行
    
    fun getDisplayName(): String = when (this) {
        PROTEST -> "抗议"
        CELEBRATION -> "庆典"
        COMMUNITY_GATHERING -> "社区聚会"
        FLASH_MOB -> "快闪"
        STRIKE -> "罢工"
        CHARITY -> "慈善活动"
        MARCH -> "游行"
    }
    
    fun getIcon(): String = when (this) {
        PROTEST -> "✊"
        CELEBRATION -> "🎉"
        COMMUNITY_GATHERING -> "👥"
        FLASH_MOB -> "💃"
        STRIKE -> "🪧"
        CHARITY -> "❤️"
        MARCH -> "🚶"
    }
}

