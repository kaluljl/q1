package com.citysimulator.game.data.model

import java.util.Date
import java.util.UUID

/**
 * 社交关系系统
 * 
 * 管理市民之间复杂的社交关系网络
 * 
 * @author AI进化论-花生
 */
data class SocialRelationship(
    val id: String = UUID.randomUUID().toString(),
    val citizen1Id: String,
    val citizen2Id: String,
    val relationshipType: RelationshipType,
    val strength: Float = 0.5f, // 关系强度 (0.0-1.0)
    val affection: Float = 0.5f, // 情感度 (-1.0到1.0, 负数为敌意，正数为好感)
    val trust: Float = 0.5f, // 信任度 (0.0-1.0)
    val intimacy: Float = 0.3f, // 亲密度 (0.0-1.0)
    val startDate: Date = Date(),
    val lastInteraction: Date = Date(),
    val interactionCount: Int = 0,
    val sharedMemories: List<String> = emptyList(), // 共同记忆ID列表
    val status: RelationshipStatus = RelationshipStatus.ACTIVE,
    val metadata: Map<String, String> = emptyMap()
) {
    /**
     * 获取关系质量描述
     */
    fun getQualityDescription(): String = when {
        affection > 0.8f && strength > 0.7f -> "深厚的"
        affection > 0.6f && strength > 0.5f -> "良好的"
        affection > 0.3f -> "一般的"
        affection > -0.3f -> "冷淡的"
        affection > -0.6f -> "紧张的"
        else -> "敌对的"
    }
    
    /**
     * 判断关系是否稳定
     */
    fun isStable(): Boolean {
        return strength > 0.5f && kotlin.math.abs(affection) < 0.8f
    }
    
    /**
     * 判断关系是否处于危机中
     */
    fun isInCrisis(): Boolean {
        return (affection < -0.5f && relationshipType in listOf(
            RelationshipType.SPOUSE, RelationshipType.ROMANTIC_PARTNER, RelationshipType.FAMILY
        )) || (strength < 0.2f && status == RelationshipStatus.ACTIVE)
    }
    
    /**
     * 更新关系状态（基于互动）
     */
    fun updateAfterInteraction(
        isPositive: Boolean,
        intensityDelta: Float = 0.1f
    ): SocialRelationship {
        val affectionDelta = if (isPositive) intensityDelta else -intensityDelta
        val trustDelta = if (isPositive) intensityDelta * 0.5f else -intensityDelta * 0.7f
        
        return copy(
            affection = (affection + affectionDelta).coerceIn(-1f, 1f),
            trust = (trust + trustDelta).coerceIn(0f, 1f),
            strength = (strength + (if (isPositive) 0.05f else -0.03f)).coerceIn(0f, 1f),
            lastInteraction = Date(),
            interactionCount = interactionCount + 1
        )
    }
}

/**
 * 关系类型
 */
enum class RelationshipType {
    // 家庭关系
    FAMILY,                 // 家人（父母、兄弟姐妹）
    SPOUSE,                 // 配偶
    CHILD,                  // 子女
    PARENT,                 // 父母
    
    // 亲密关系
    ROMANTIC_PARTNER,       // 恋人
    EX_PARTNER,            // 前任
    CRUSH,                 // 暗恋对象
    
    // 友谊
    BEST_FRIEND,           // 挚友
    CLOSE_FRIEND,          // 密友
    FRIEND,                // 朋友
    ACQUAINTANCE,          // 熟人
    
    // 工作关系
    COLLEAGUE,             // 同事
    BOSS,                  // 上司
    SUBORDINATE,           // 下属
    BUSINESS_PARTNER,      // 商业伙伴
    
    // 竞争与冲突
    RIVAL,                 // 竞争对手
    ENEMY,                 // 仇人
    
    // 其他
    NEIGHBOR,              // 邻居
    MENTOR,                // 导师
    STUDENT,               // 学生
    STRANGER;              // 陌生人
    
    fun getDisplayName(): String = when (this) {
        FAMILY -> "家人"
        SPOUSE -> "配偶"
        CHILD -> "子女"
        PARENT -> "父母"
        ROMANTIC_PARTNER -> "恋人"
        EX_PARTNER -> "前任"
        CRUSH -> "暗恋"
        BEST_FRIEND -> "挚友"
        CLOSE_FRIEND -> "密友"
        FRIEND -> "朋友"
        ACQUAINTANCE -> "熟人"
        COLLEAGUE -> "同事"
        BOSS -> "上司"
        SUBORDINATE -> "下属"
        BUSINESS_PARTNER -> "商业伙伴"
        RIVAL -> "竞争对手"
        ENEMY -> "仇人"
        NEIGHBOR -> "邻居"
        MENTOR -> "导师"
        STUDENT -> "学生"
        STRANGER -> "陌生人"
    }
    
    /**
     * 判断是否为亲密关系
     */
    fun isIntimate(): Boolean = this in listOf(
        SPOUSE, ROMANTIC_PARTNER, BEST_FRIEND, FAMILY, PARENT, CHILD
    )
}

/**
 * 关系状态
 */
enum class RelationshipStatus {
    ACTIVE,         // 活跃
    INACTIVE,       // 不活跃（长时间未联系）
    BROKEN,         // 破裂
    ENDED;          // 结束
    
    fun getDisplayName(): String = when (this) {
        ACTIVE -> "活跃"
        INACTIVE -> "疏远"
        BROKEN -> "破裂"
        ENDED -> "结束"
    }
}

/**
 * 社交网络
 */
data class SocialNetwork(
    val citizenId: String,
    val relationships: MutableList<SocialRelationship> = mutableListOf()
) {
    /**
     * 添加关系
     */
    fun addRelationship(relationship: SocialRelationship) {
        // 检查是否已存在关系
        val existing = relationships.find { 
            (it.citizen1Id == relationship.citizen1Id && it.citizen2Id == relationship.citizen2Id) ||
            (it.citizen1Id == relationship.citizen2Id && it.citizen2Id == relationship.citizen1Id)
        }
        
        if (existing == null) {
            relationships.add(relationship)
        }
    }
    
    /**
     * 获取特定类型的关系
     */
    fun getRelationshipsByType(type: RelationshipType): List<SocialRelationship> {
        return relationships.filter { it.relationshipType == type && it.status == RelationshipStatus.ACTIVE }
    }
    
    /**
     * 获取所有朋友
     */
    fun getFriends(): List<SocialRelationship> {
        return relationships.filter { 
            it.relationshipType in listOf(
                RelationshipType.BEST_FRIEND,
                RelationshipType.CLOSE_FRIEND,
                RelationshipType.FRIEND
            ) && it.status == RelationshipStatus.ACTIVE
        }
    }
    
    /**
     * 获取所有敌人
     */
    fun getEnemies(): List<SocialRelationship> {
        return relationships.filter { 
            it.relationshipType in listOf(RelationshipType.RIVAL, RelationshipType.ENEMY) &&
            it.status == RelationshipStatus.ACTIVE
        }
    }
    
    /**
     * 获取最亲密的人
     */
    fun getClosestPeople(count: Int = 5): List<SocialRelationship> {
        return relationships
            .filter { it.status == RelationshipStatus.ACTIVE }
            .sortedByDescending { it.intimacy * it.affection * it.strength }
            .take(count)
    }
    
    /**
     * 计算社交满意度
     */
    fun getSocialSatisfaction(): Float {
        if (relationships.isEmpty()) return 0.3f
        
        val friendCount = getFriends().size
        val intimateRelations = relationships.count { it.relationshipType.isIntimate() }
        val averageAffection = relationships
            .filter { it.status == RelationshipStatus.ACTIVE }
            .map { it.affection }
            .average()
            .toFloat()
        
        return ((friendCount * 0.1f + intimateRelations * 0.2f) * 0.5f + 
                (averageAffection + 1f) * 0.25f)
            .coerceIn(0f, 1f)
    }
    
    /**
     * 判断是否孤独
     */
    fun isLonely(): Boolean {
        val activeRelationships = relationships.count { it.status == RelationshipStatus.ACTIVE }
        val intimateRelationships = relationships.count { 
            it.relationshipType.isIntimate() && it.status == RelationshipStatus.ACTIVE 
        }
        return activeRelationships < 3 && intimateRelationships == 0
    }
    
    /**
     * 获取与指定市民的关系
     */
    fun getRelationshipWith(otherCitizenId: String): SocialRelationship? {
        return relationships.find { 
            (it.citizen1Id == citizenId && it.citizen2Id == otherCitizenId) ||
            (it.citizen1Id == otherCitizenId && it.citizen2Id == citizenId)
        }
    }
}

