package com.citysimulator.game.data.model

import kotlin.random.Random

/**
 * 多维人格特质系统
 * 
 * 每个市民拥有独特的人格特质组合，影响他们的行为和决策
 * 
 * @author AI进化论-花生
 */
data class PersonalityTraits(
    // 核心人格维度 (0.0 - 1.0)
    val extraversion: Float = 0.5f,      // 外向性 (0=内向, 1=外向)
    val diligence: Float = 0.5f,         // 勤奋度 (0=懒惰, 1=勤奋)
    val curiosity: Float = 0.5f,         // 好奇心 (0=保守, 1=好奇)
    val kindness: Float = 0.5f,          // 友善度 (0=自私, 1=友善)
    val stability: Float = 0.5f,         // 情绪稳定性 (0=敏感, 1=稳定)
    val creativity: Float = 0.5f,        // 创造力 (0=务实, 1=创新)
    val ambition: Float = 0.5f,          // 野心 (0=知足, 1=野心勃勃)
    val rebelliousness: Float = 0.5f     // 叛逆性 (0=顺从, 1=叛逆)
) {
    /**
     * 判断是否为外向型
     */
    fun isExtroverted(): Boolean = extraversion > 0.6f
    
    /**
     * 判断是否为内向型
     */
    fun isIntroverted(): Boolean = extraversion < 0.4f
    
    /**
     * 判断是否勤奋
     */
    fun isDiligent(): Boolean = diligence > 0.6f
    
    /**
     * 判断是否懒惰
     */
    fun isLazy(): Boolean = diligence < 0.4f
    
    /**
     * 判断是否好奇
     */
    fun isCurious(): Boolean = curiosity > 0.6f
    
    /**
     * 判断是否保守
     */
    fun isConservative(): Boolean = curiosity < 0.4f
    
    /**
     * 判断是否友善
     */
    fun isKind(): Boolean = kindness > 0.6f
    
    /**
     * 判断是否自私
     */
    fun isSelfish(): Boolean = kindness < 0.4f
    
    /**
     * 判断是否情绪稳定
     */
    fun isStable(): Boolean = stability > 0.6f
    
    /**
     * 判断是否情绪敏感
     */
    fun isSensitive(): Boolean = stability < 0.4f
    
    /**
     * 判断是否具有创造力
     */
    fun isCreative(): Boolean = creativity > 0.6f
    
    /**
     * 判断是否野心勃勃
     */
    fun isAmbitious(): Boolean = ambition > 0.6f
    
    /**
     * 判断是否叛逆
     */
    fun isRebellious(): Boolean = rebelliousness > 0.6f
    
    /**
     * 获取人格描述
     */
    fun getPersonalityDescription(): String {
        val traits = mutableListOf<String>()
        
        if (isExtroverted()) traits.add("外向的")
        else if (isIntroverted()) traits.add("内向的")
        
        if (isDiligent()) traits.add("勤奋的")
        else if (isLazy()) traits.add("慵懒的")
        
        if (isCurious()) traits.add("好奇的")
        else if (isConservative()) traits.add("保守的")
        
        if (isKind()) traits.add("友善的")
        else if (isSelfish()) traits.add("自私的")
        
        if (isCreative()) traits.add("富有创意的")
        if (isAmbitious()) traits.add("有野心的")
        if (isRebellious()) traits.add("叛逆的")
        if (isSensitive()) traits.add("敏感的")
        
        return if (traits.isEmpty()) "平衡的" else traits.joinToString("、")
    }
    
    /**
     * 获取主导人格类型
     */
    fun getDominantPersonalityType(): PersonalityType {
        return when {
            diligence > 0.7f && ambition > 0.7f -> PersonalityType.WORKAHOLIC
            kindness > 0.7f && extraversion > 0.6f -> PersonalityType.SOCIAL_BUTTERFLY
            stability < 0.3f && creativity > 0.7f -> PersonalityType.ARTIST
            curiosity > 0.7f && creativity > 0.6f -> PersonalityType.EXPLORER
            rebelliousness > 0.7f -> PersonalityType.REBEL
            diligence < 0.3f && ambition < 0.3f -> PersonalityType.LAZY
            kindness < 0.3f && extraversion < 0.4f -> PersonalityType.LONER
            else -> PersonalityType.BALANCED
        }
    }
    
    companion object {
        /**
         * 生成随机人格特质
         */
        fun generateRandom(): PersonalityTraits {
            return PersonalityTraits(
                extraversion = Random.nextFloat(),
                diligence = Random.nextFloat(),
                curiosity = Random.nextFloat(),
                kindness = Random.nextFloat(),
                stability = Random.nextFloat(),
                creativity = Random.nextFloat(),
                ambition = Random.nextFloat(),
                rebelliousness = Random.nextFloat()
            )
        }
        
        /**
         * 基于父母特质生成子女特质（遗传+变异）
         */
        fun inherit(parent1: PersonalityTraits, parent2: PersonalityTraits): PersonalityTraits {
            fun inherit(trait1: Float, trait2: Float): Float {
                val inherited = (trait1 + trait2) / 2f
                val mutation = (Random.nextFloat() - 0.5f) * 0.3f // ±15%变异
                return (inherited + mutation).coerceIn(0f, 1f)
            }
            
            return PersonalityTraits(
                extraversion = inherit(parent1.extraversion, parent2.extraversion),
                diligence = inherit(parent1.diligence, parent2.diligence),
                curiosity = inherit(parent1.curiosity, parent2.curiosity),
                kindness = inherit(parent1.kindness, parent2.kindness),
                stability = inherit(parent1.stability, parent2.stability),
                creativity = inherit(parent1.creativity, parent2.creativity),
                ambition = inherit(parent1.ambition, parent2.ambition),
                rebelliousness = inherit(parent1.rebelliousness, parent2.rebelliousness)
            )
        }
    }
}

/**
 * 人格类型（主导类型）
 */
enum class PersonalityType {
    WORKAHOLIC,         // 工作狂
    SOCIAL_BUTTERFLY,   // 社交达人
    ARTIST,             // 艺术家
    EXPLORER,           // 探索者
    REBEL,              // 叛逆者
    LAZY,               // 懒人
    LONER,              // 独行者
    BALANCED;           // 平衡型
    
    fun getDisplayName(): String = when (this) {
        WORKAHOLIC -> "工作狂"
        SOCIAL_BUTTERFLY -> "社交达人"
        ARTIST -> "艺术家"
        EXPLORER -> "探索者"
        REBEL -> "叛逆者"
        LAZY -> "懒散者"
        LONER -> "独行者"
        BALANCED -> "平衡者"
    }
    
    fun getDescription(): String = when (this) {
        WORKAHOLIC -> "热爱工作，追求事业成功，工作效率高但容易忽视生活"
        SOCIAL_BUTTERFLY -> "善于交际，朋友众多，喜欢参加社交活动"
        ARTIST -> "富有创造力，情感丰富，追求艺术与美"
        EXPLORER -> "充满好奇心，喜欢尝试新事物，勇于冒险"
        REBEL -> "不喜欢被束缚，经常挑战权威，追求自由"
        LAZY -> "不太积极主动，喜欢悠闲的生活节奏"
        LONER -> "喜欢独处，不太在意他人看法，有自己的世界"
        BALANCED -> "各方面都比较均衡，能够适应不同情况"
    }
}

