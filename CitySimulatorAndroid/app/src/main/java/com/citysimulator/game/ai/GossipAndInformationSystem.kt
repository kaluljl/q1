package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import java.util.Date
import java.util.UUID
import kotlin.random.Random

/**
 * 信息传播与闲言碎语系统
 * 
 * 模拟市民之间的信息传播、谣言扩散和舆论形成
 * 
 * @author AI进化论-花生
 */
object GossipAndInformationSystem {
    
    /**
     * 创建新消息
     */
    fun createGossip(
        topic: GossipTopic,
        content: String,
        sentiment: GossipSentiment,
        originCitizenId: String,
        location: Pair<Int, Int>? = null
    ): Gossip {
        return Gossip(
            topic = topic,
            content = content,
            sentiment = sentiment,
            originCitizenId = originCitizenId,
            location = location,
            credibility = Random.nextFloat() * 0.5f + 0.5f, // 0.5-1.0初始可信度
            spreadRate = calculateSpreadRate(topic, sentiment)
        )
    }
    
    /**
     * 计算传播速率
     */
    private fun calculateSpreadRate(topic: GossipTopic, sentiment: GossipSentiment): Float {
        val topicMultiplier = when (topic) {
            GossipTopic.SCANDAL -> 1.5f
            GossipTopic.DISASTER -> 1.8f
            GossipTopic.CELEBRATION -> 1.2f
            GossipTopic.CRIME -> 1.6f
            GossipTopic.ROMANCE -> 1.3f
            else -> 1.0f
        }
        
        val sentimentMultiplier = when (sentiment) {
            GossipSentiment.SHOCKING -> 1.5f
            GossipSentiment.OUTRAGE -> 1.4f
            GossipSentiment.EXCITING -> 1.3f
            else -> 1.0f
        }
        
        return topicMultiplier * sentimentMultiplier
    }
    
    /**
     * 市民是否会传播此消息
     */
    fun willCitizenSpread(citizen: Citizen, gossip: Gossip): Boolean {
        // 基于市民性格决定是否传播
        val personalityTraits = citizen.personality
        
        val baseChance = when (personalityTraits) {
            com.citysimulator.game.data.model.CitizenPersonality.SOCIAL -> 0.8f
            com.citysimulator.game.data.model.CitizenPersonality.DEMANDING -> 0.7f
            com.citysimulator.game.data.model.CitizenPersonality.WORKAHOLIC -> 0.3f
            else -> 0.5f
        }
        
        val spreadChance = baseChance * gossip.spreadRate * gossip.credibility
        
        return Random.nextFloat() < spreadChance
    }
    
    /**
     * 传播消息（可能产生变异）
     */
    fun spreadGossip(gossip: Gossip, fromCitizenId: String, toCitizenId: String): Gossip {
        val mutationChance = 0.2f
        val isMutated = Random.nextFloat() < mutationChance
        
        return if (isMutated) {
            // 消息在传播中被扭曲
            gossip.copy(
                content = mutateContent(gossip.content),
                credibility = (gossip.credibility * 0.9f).coerceAtLeast(0.1f),
                spreadCount = gossip.spreadCount + 1,
                lastSpreadTime = Date()
            )
        } else {
            gossip.copy(
                credibility = (gossip.credibility * 0.95f).coerceAtLeast(0.2f),
                spreadCount = gossip.spreadCount + 1,
                lastSpreadTime = Date()
            )
        }
    }
    
    /**
     * 扭曲消息内容
     */
    private fun mutateContent(original: String): String {
        val mutations = listOf(
            "听说$original",
            "我听别人说$original",
            "好像$original",
            "据说$original，不过不确定是不是真的",
            "$original（但我觉得可能夸大了）"
        )
        return mutations.random()
    }
    
    /**
     * 计算消息对市民幸福度的影响
     */
    fun calculateImpactOnHappiness(gossip: Gossip): Float {
        val sentimentImpact = when (gossip.sentiment) {
            GossipSentiment.POSITIVE -> 0.05f
            GossipSentiment.EXCITING -> 0.08f
            GossipSentiment.HOPEFUL -> 0.06f
            GossipSentiment.NEGATIVE -> -0.05f
            GossipSentiment.WORRYING -> -0.07f
            GossipSentiment.OUTRAGE -> -0.10f
            GossipSentiment.SHOCKING -> -0.08f
            else -> 0f
        }
        
        return sentimentImpact * gossip.credibility
    }
}

/**
 * 八卦/消息
 */
data class Gossip(
    val id: String = UUID.randomUUID().toString(),
    val topic: GossipTopic,
    val content: String,
    val sentiment: GossipSentiment,
    val originCitizenId: String,
    val creationTime: Date = Date(),
    val lastSpreadTime: Date = Date(),
    val location: Pair<Int, Int>? = null,
    val credibility: Float = 1.0f, // 可信度 (0.0-1.0)
    val spreadRate: Float = 1.0f, // 传播速率
    val spreadCount: Int = 0, // 已传播次数
    val reachedCitizenIds: MutableSet<String> = mutableSetOf(originCitizenId)
) {
    /**
     * 消息是否已过时
     */
    fun isStale(): Boolean {
        val ageInHours = (Date().time - creationTime.time) / (1000 * 60 * 60)
        return ageInHours > 48 || credibility < 0.2f
    }
    
    /**
     * 获取传播范围等级
     */
    fun getSpreadLevel(): String = when {
        spreadCount < 5 -> "小范围传播"
        spreadCount < 20 -> "广泛传播"
        spreadCount < 50 -> "全城皆知"
        else -> "家喻户晓"
    }
}

/**
 * 八卦话题
 */
enum class GossipTopic {
    CITY_DEVELOPMENT,   // 城市发展
    CITY_PROBLEM,       // 城市问题
    NEW_BUILDING,       // 新建筑
    POLLUTION,          // 污染
    TRAFFIC,            // 交通
    CRIME,              // 犯罪
    CELEBRATION,        // 庆典
    DISASTER,           // 灾难
    SCANDAL,            // 丑闻
    ROMANCE,            // 恋爱八卦
    WORK,               // 工作相关
    NEIGHBORHOOD,       // 邻里琐事
    POLITICS,           // 政策政治
    OTHER;              // 其他
    
    fun getDisplayName(): String = when (this) {
        CITY_DEVELOPMENT -> "城市发展"
        CITY_PROBLEM -> "城市问题"
        NEW_BUILDING -> "新建筑"
        POLLUTION -> "环境污染"
        TRAFFIC -> "交通状况"
        CRIME -> "治安问题"
        CELEBRATION -> "庆祝活动"
        DISASTER -> "灾难事件"
        SCANDAL -> "丑闻"
        ROMANCE -> "恋爱八卦"
        WORK -> "工作"
        NEIGHBORHOOD -> "邻里"
        POLITICS -> "政策"
        OTHER -> "其他"
    }
}

/**
 * 八卦情感色彩
 */
enum class GossipSentiment {
    POSITIVE,       // 正面
    NEGATIVE,       // 负面
    NEUTRAL,        // 中性
    EXCITING,       // 令人兴奋
    WORRYING,       // 令人担忧
    SHOCKING,       // 令人震惊
    HOPEFUL,        // 充满希望
    OUTRAGE;        // 愤怒
    
    fun getDisplayName(): String = when (this) {
        POSITIVE -> "正面"
        NEGATIVE -> "负面"
        NEUTRAL -> "中性"
        EXCITING -> "令人兴奋"
        WORRYING -> "令人担忧"
        SHOCKING -> "令人震惊"
        HOPEFUL -> "充满希望"
        OUTRAGE -> "愤怒"
    }
}

/**
 * 公众舆论
 */
data class PublicOpinion(
    val topic: String,
    val overallSentiment: Float, // -1.0 (完全负面) 到 1.0 (完全正面)
    val intensity: Float, // 0.0-1.0 讨论强度
    val relatedGossips: List<String> = emptyList(), // 相关八卦ID
    val participatingCitizens: Int = 0
) {
    fun getSentimentDescription(): String = when {
        overallSentiment > 0.6f -> "非常正面"
        overallSentiment > 0.2f -> "偏正面"
        overallSentiment > -0.2f -> "中立"
        overallSentiment > -0.6f -> "偏负面"
        else -> "非常负面"
    }
    
    fun getIntensityDescription(): String = when {
        intensity > 0.8f -> "热烈讨论中"
        intensity > 0.5f -> "广泛关注"
        intensity > 0.2f -> "有所讨论"
        else -> "少数人提及"
    }
}

/**
 * 为指定市民生成相关的八卦（动态生成，基于城市状态）
 */
fun generateGossipsForCitizen(
    citizenId: String,
    allCitizens: List<Citizen>,
    cityHappiness: Float
): List<Gossip> {
    val gossips = mutableListOf<Gossip>()
    val citizen = allCitizens.find { it.id == citizenId } ?: return emptyList()
    
    // 根据城市幸福度生成不同的八卦
    when {
        cityHappiness > 0.7f -> {
            gossips.add(Gossip(
                topic = GossipTopic.CITY_DEVELOPMENT,
                content = "听说城市要建新的公园和娱乐设施！",
                sentiment = GossipSentiment.EXCITING,
                originCitizenId = citizenId,
                credibility = 0.7f + Random.nextFloat() * 0.2f,
                spreadRate = 1.2f
            ))
            gossips.add(Gossip(
                topic = GossipTopic.WORK,
                content = "最近城市经济发展不错，工资可能会涨",
                sentiment = GossipSentiment.HOPEFUL,
                originCitizenId = citizenId,
                credibility = 0.6f + Random.nextFloat() * 0.3f,
                spreadRate = 1.0f
            ))
        }
        cityHappiness < 0.4f -> {
            gossips.add(Gossip(
                topic = GossipTopic.POLITICS,
                content = "市政府最近的政策让人失望",
                sentiment = GossipSentiment.OUTRAGE,
                originCitizenId = citizenId,
                credibility = 0.8f + Random.nextFloat() * 0.2f,
                spreadRate = 1.5f
            ))
            gossips.add(Gossip(
                topic = GossipTopic.POLLUTION,
                content = "城市环境越来越差，空气质量堪忧",
                sentiment = GossipSentiment.WORRYING,
                originCitizenId = citizenId,
                credibility = 0.7f + Random.nextFloat() * 0.2f,
                spreadRate = 1.3f
            ))
        }
        else -> {
            gossips.add(Gossip(
                topic = GossipTopic.NEIGHBORHOOD,
                content = "邻居家最近发生了一些有趣的事",
                sentiment = GossipSentiment.NEUTRAL,
                originCitizenId = citizenId,
                credibility = 0.5f + Random.nextFloat() * 0.3f,
                spreadRate = 0.8f
            ))
        }
    }
    
    // 根据市民职业添加职业相关八卦
    if (citizen.occupation != null) {
        gossips.add(Gossip(
            topic = GossipTopic.WORK,
            content = "听说${citizen.occupation}行业要有大变动",
            sentiment = GossipSentiment.NEUTRAL,
            originCitizenId = citizenId,
            credibility = 0.4f + Random.nextFloat() * 0.4f,
            spreadRate = 0.9f
        ))
    }
    
    return gossips.take(3) // 最多返回3条
}

