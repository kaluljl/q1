package com.citysimulator.game.data.model

import java.util.Date
import java.util.UUID

/**
 * 市民记忆系统
 * 
 * 记录市民一生中的重大事件，形成独特的人生叙事
 * 
 * @author AI进化论-花生
 */
data class CitizenMemory(
    val id: String = UUID.randomUUID().toString(),
    val citizenId: String,
    val timestamp: Date,
    val eventType: MemoryEventType,
    val title: String,
    val description: String,
    val emotionalImpact: Float, // 情感影响 (-1.0到1.0, 负数为负面，正数为正面)
    val importance: MemoryImportance,
    val relatedCitizenIds: List<String> = emptyList(), // 相关的其他市民
    val location: Pair<Int, Int>? = null, // 事件发生地点
    val metadata: Map<String, String> = emptyMap() // 额外元数据
) {
    /**
     * 获取记忆的情感色彩描述
     */
    fun getEmotionalTone(): String = when {
        emotionalImpact > 0.7f -> "极度快乐"
        emotionalImpact > 0.3f -> "愉快"
        emotionalImpact > -0.3f -> "平淡"
        emotionalImpact > -0.7f -> "难过"
        else -> "痛苦"
    }
    
    /**
     * 判断是否为人生重大转折点
     */
    fun isTurningPoint(): Boolean {
        return importance == MemoryImportance.LIFE_CHANGING || 
               kotlin.math.abs(emotionalImpact) > 0.8f
    }
}

/**
 * 记忆事件类型
 */
enum class MemoryEventType {
    // 人生里程碑
    BIRTH,                  // 出生
    CHILDHOOD_MEMORY,       // 童年回忆
    FIRST_DAY_SCHOOL,      // 上学第一天
    GRADUATION,            // 毕业
    FIRST_JOB,             // 第一份工作
    PROMOTION,             // 升职
    JOB_LOSS,              // 失业
    
    // 社交关系
    MADE_FRIEND,           // 交到朋友
    LOST_FRIEND,           // 失去朋友
    FIRST_LOVE,            // 初恋
    FELL_IN_LOVE,          // 坠入爱河
    HEARTBREAK,            // 心碎
    GOT_MARRIED,           // 结婚
    DIVORCE,               // 离婚
    CHILD_BORN,            // 孩子出生
    
    // 重大决定
    MOVED_HOUSE,           // 搬家
    CHANGED_JOB,           // 换工作
    MAJOR_PURCHASE,        // 重大购买（如房子）
    CAREER_CHANGE,         // 职业转变
    
    // 城市事件
    CITY_DISASTER,         // 城市灾难
    CITY_CELEBRATION,      // 城市庆典
    WITNESSED_CRIME,       // 目击犯罪
    HELPED_SOMEONE,        // 帮助他人
    WAS_HELPED,            // 被人帮助
    
    // 成就与挫折
    ACHIEVED_DREAM,        // 实现梦想
    FAILED_DREAM,          // 梦想破灭
    WON_AWARD,             // 获奖
    PUBLIC_RECOGNITION,    // 公众认可
    PUBLIC_HUMILIATION,    // 公开羞辱
    
    // 健康相关
    SERIOUS_ILLNESS,       // 重病
    RECOVERY,              // 康复
    ACCIDENT,              // 意外事故
    
    // 其他
    LIFE_REVELATION,       // 人生顿悟
    RANDOM_ENCOUNTER,      // 偶然相遇
    CUSTOM;                // 自定义事件
    
    fun getDisplayName(): String = when (this) {
        BIRTH -> "出生"
        CHILDHOOD_MEMORY -> "童年回忆"
        FIRST_DAY_SCHOOL -> "上学第一天"
        GRADUATION -> "毕业典礼"
        FIRST_JOB -> "第一份工作"
        PROMOTION -> "获得升职"
        JOB_LOSS -> "失去工作"
        MADE_FRIEND -> "结交新朋友"
        LOST_FRIEND -> "失去朋友"
        FIRST_LOVE -> "初恋"
        FELL_IN_LOVE -> "坠入爱河"
        HEARTBREAK -> "失恋"
        GOT_MARRIED -> "结婚"
        DIVORCE -> "离婚"
        CHILD_BORN -> "孩子出生"
        MOVED_HOUSE -> "搬家"
        CHANGED_JOB -> "换工作"
        MAJOR_PURCHASE -> "重大购买"
        CAREER_CHANGE -> "职业转变"
        CITY_DISASTER -> "经历灾难"
        CITY_CELEBRATION -> "参加庆典"
        WITNESSED_CRIME -> "目击犯罪"
        HELPED_SOMEONE -> "帮助他人"
        WAS_HELPED -> "得到帮助"
        ACHIEVED_DREAM -> "实现梦想"
        FAILED_DREAM -> "梦想破灭"
        WON_AWARD -> "获得奖项"
        PUBLIC_RECOGNITION -> "获得认可"
        PUBLIC_HUMILIATION -> "遭受羞辱"
        SERIOUS_ILLNESS -> "重病"
        RECOVERY -> "康复"
        ACCIDENT -> "意外事故"
        LIFE_REVELATION -> "人生顿悟"
        RANDOM_ENCOUNTER -> "偶然相遇"
        CUSTOM -> "特殊事件"
    }
}

/**
 * 记忆重要性
 */
enum class MemoryImportance {
    TRIVIAL,        // 琐碎的
    MINOR,          // 次要的
    MODERATE,       // 中等的
    SIGNIFICANT,    // 重要的
    MAJOR,          // 重大的
    LIFE_CHANGING;  // 改变人生的
    
    fun getDisplayName(): String = when (this) {
        TRIVIAL -> "琐碎"
        MINOR -> "次要"
        MODERATE -> "一般"
        SIGNIFICANT -> "重要"
        MAJOR -> "重大"
        LIFE_CHANGING -> "人生转折"
    }
}

/**
 * 市民记忆集合
 */
data class CitizenMemoryCollection(
    val citizenId: String,
    val memories: MutableList<CitizenMemory> = mutableListOf()
) {
    /**
     * 添加记忆
     */
    fun addMemory(memory: CitizenMemory) {
        memories.add(memory)
        // 保持记忆数量在合理范围内（最多保留100条）
        if (memories.size > 100) {
            // 移除最不重要且最久远的记忆
            memories.sortedWith(
                compareBy<CitizenMemory> { it.importance.ordinal }
                    .thenBy { it.timestamp }
            ).firstOrNull()?.let { memories.remove(it) }
        }
    }
    
    /**
     * 获取最重要的记忆
     */
    fun getMostImportantMemories(count: Int = 5): List<CitizenMemory> {
        return memories.sortedByDescending { it.importance.ordinal }
            .take(count)
    }
    
    /**
     * 获取最近的记忆
     */
    fun getRecentMemories(count: Int = 10): List<CitizenMemory> {
        return memories.sortedByDescending { it.timestamp }
            .take(count)
    }
    
    /**
     * 获取人生转折点
     */
    fun getTurningPoints(): List<CitizenMemory> {
        return memories.filter { it.isTurningPoint() }
            .sortedBy { it.timestamp }
    }
    
    /**
     * 生成人生故事
     */
    fun generateLifeStory(): String {
        val turningPoints = getTurningPoints()
        if (turningPoints.isEmpty()) {
            return "这是一个平凡但充实的人生..."
        }
        
        val story = StringBuilder()
        story.append("【人生故事】\n\n")
        
        turningPoints.forEachIndexed { index, memory ->
            val date = java.text.SimpleDateFormat("yyyy年MM月", java.util.Locale.CHINA)
                .format(memory.timestamp)
            story.append("${index + 1}. $date - ${memory.title}\n")
            story.append("   ${memory.description}\n")
            story.append("   情感: ${memory.getEmotionalTone()}\n\n")
        }
        
        return story.toString()
    }
    
    /**
     * 统计情感倾向
     */
    fun getEmotionalBalance(): Float {
        if (memories.isEmpty()) return 0f
        return memories.map { it.emotionalImpact }.average().toFloat()
    }
    
    /**
     * 判断是否经历过特定类型事件
     */
    fun hasExperienced(eventType: MemoryEventType): Boolean {
        return memories.any { it.eventType == eventType }
    }
}

