package com.citysimulator.game.data.model

/**
 * 马斯洛需求层次系统
 * 
 * 实现五层需求理论，从底层生理需求到顶层自我实现
 * 
 * @author AI进化论-花生
 */
data class MaslowNeeds(
    // 第一层：生理需求 (0.0 - 1.0，0表示完全不满足，1表示完全满足)
    val physiological: PhysiologicalNeeds = PhysiologicalNeeds(),
    
    // 第二层：安全需求
    val safety: SafetyNeeds = SafetyNeeds(),
    
    // 第三层：社交需求
    val social: SocialNeeds = SocialNeeds(),
    
    // 第四层：尊重需求
    val esteem: EsteemNeeds = EsteemNeeds(),
    
    // 第五层：自我实现需求
    val selfActualization: SelfActualizationNeeds = SelfActualizationNeeds()
) {
    /**
     * 计算总体满足度 (0.0 - 1.0)
     * 采用加权平均，底层需求权重更高
     */
    fun getOverallSatisfaction(): Float {
        return (
            physiological.getAverage() * 0.35f +  // 生理需求权重35%
            safety.getAverage() * 0.25f +         // 安全需求权重25%
            social.getAverage() * 0.20f +         // 社交需求权重20%
            esteem.getAverage() * 0.12f +         // 尊重需求权重12%
            selfActualization.getAverage() * 0.08f // 自我实现权重8%
        )
    }
    
    /**
     * 获取最紧迫的需求
     */
    fun getMostUrgentNeed(): NeedType {
        val needLevels = listOf(
            NeedType.PHYSIOLOGICAL to physiological.getAverage(),
            NeedType.SAFETY to safety.getAverage(),
            NeedType.SOCIAL to social.getAverage(),
            NeedType.ESTEEM to esteem.getAverage(),
            NeedType.SELF_ACTUALIZATION to selfActualization.getAverage()
        )
        
        return needLevels.minByOrNull { it.second }?.first ?: NeedType.PHYSIOLOGICAL
    }
    
    /**
     * 获取需求描述
     */
    fun getUrgentNeedDescription(): String {
        return when (getMostUrgentNeed()) {
            NeedType.PHYSIOLOGICAL -> "需要基本的食物、水和休息"
            NeedType.SAFETY -> "需要稳定的住所和工作保障"
            NeedType.SOCIAL -> "渴望友谊和归属感"
            NeedType.ESTEEM -> "希望获得认可和尊重"
            NeedType.SELF_ACTUALIZATION -> "追求个人梦想和自我实现"
        }
    }
}

/**
 * 第一层：生理需求
 */
data class PhysiologicalNeeds(
    val hunger: Float = 0.8f,      // 饥饿感 (0=饥饿, 1=饱腹)
    val thirst: Float = 0.8f,      // 口渴 (0=口渴, 1=不渴)
    val fatigue: Float = 0.7f,     // 疲劳 (0=疲惫, 1=精力充沛)
    val health: Float = 0.9f       // 健康 (0=病重, 1=健康)
) {
    fun getAverage() = (hunger + thirst + fatigue + health) / 4f
    
    fun isSatisfied() = getAverage() > 0.6f
    
    fun getMostUrgent(): String = when {
        hunger < 0.3f -> "非常饿，需要立即进食！"
        thirst < 0.3f -> "非常渴，需要喝水！"
        fatigue < 0.3f -> "极度疲劳，需要休息！"
        health < 0.4f -> "感觉不舒服，需要看医生！"
        else -> "生理需求基本满足"
    }
}

/**
 * 第二层：安全需求
 */
data class SafetyNeeds(
    val shelter: Float = 0.8f,         // 住所稳定性 (0=无家可归, 1=稳定住所)
    val jobSecurity: Float = 0.7f,     // 工作保障 (0=失业, 1=稳定工作)
    val income: Float = 0.6f,          // 收入稳定性 (0=贫困, 1=富足)
    val citySafety: Float = 0.8f,      // 城市安全度 (0=危险, 1=安全)
    val healthcareAccess: Float = 0.7f // 医疗可及性 (0=无法就医, 1=医疗完善)
) {
    fun getAverage() = (shelter + jobSecurity + income + citySafety + healthcareAccess) / 5f
    
    fun isSatisfied() = getAverage() > 0.6f
    
    fun getMostUrgent(): String = when {
        shelter < 0.4f -> "住房不稳定，担心无家可归"
        jobSecurity < 0.4f -> "工作不稳定，担心失业"
        income < 0.4f -> "收入不足，经济压力巨大"
        citySafety < 0.5f -> "城市不安全，感到恐惧"
        healthcareAccess < 0.5f -> "医疗条件差，担心生病"
        else -> "安全需求基本满足"
    }
}

/**
 * 第三层：社交需求
 */
data class SocialNeeds(
    val friendship: Float = 0.5f,      // 友谊 (0=孤独, 1=朋友众多)
    val romance: Float = 0.5f,         // 爱情 (0=单身孤独, 1=幸福恋爱)
    val family: Float = 0.7f,          // 家庭 (0=家庭破裂, 1=家庭和睦)
    val community: Float = 0.6f,       // 社区归属感 (0=格格不入, 1=融入社区)
    val socialActivity: Float = 0.5f   // 社交活动 (0=无社交, 1=社交丰富)
) {
    fun getAverage() = (friendship + romance + family + community + socialActivity) / 5f
    
    fun isSatisfied() = getAverage() > 0.6f
    
    fun getMostUrgent(): String = when {
        friendship < 0.3f -> "感到孤独，渴望交到朋友"
        romance < 0.3f -> "渴望爱情和陪伴"
        family < 0.4f -> "家庭关系紧张，感到痛苦"
        community < 0.4f -> "感觉自己不被社区接纳"
        socialActivity < 0.4f -> "社交生活匮乏，感到无聊"
        else -> "社交需求基本满足"
    }
}

/**
 * 第四层：尊重需求
 */
data class EsteemNeeds(
    val achievement: Float = 0.5f,     // 成就感 (0=一事无成, 1=成就卓越)
    val recognition: Float = 0.5f,     // 他人认可 (0=被忽视, 1=受尊敬)
    val status: Float = 0.5f,          // 社会地位 (0=底层, 1=精英)
    val competence: Float = 0.6f,      // 能力感 (0=无能, 1=能力出众)
    val reputation: Float = 0.5f       // 名声 (0=默默无闻, 1=名声显赫)
) {
    fun getAverage() = (achievement + recognition + status + competence + reputation) / 5f
    
    fun isSatisfied() = getAverage() > 0.6f
    
    fun getMostUrgent(): String = when {
        achievement < 0.4f -> "感觉一事无成，缺乏成就感"
        recognition < 0.4f -> "渴望得到他人的认可"
        status < 0.4f -> "对自己的社会地位不满"
        competence < 0.4f -> "觉得自己能力不足"
        reputation < 0.4f -> "希望提升自己的名声"
        else -> "尊重需求基本满足"
    }
}

/**
 * 第五层：自我实现需求
 */
data class SelfActualizationNeeds(
    val dreamPursuit: Float = 0.4f,    // 梦想追求 (0=放弃梦想, 1=实现梦想)
    val creativity: Float = 0.5f,      // 创造力发挥 (0=被压抑, 1=自由创造)
    val meaningfulness: Float = 0.5f,  // 人生意义感 (0=迷茫, 1=有意义)
    val growth: Float = 0.5f,          // 个人成长 (0=停滞, 1=持续成长)
    val contribution: Float = 0.4f     // 社会贡献 (0=无价值, 1=贡献巨大)
) {
    fun getAverage() = (dreamPursuit + creativity + meaningfulness + growth + contribution) / 5f
    
    fun isSatisfied() = getAverage() > 0.6f
    
    fun getMostUrgent(): String = when {
        dreamPursuit < 0.4f -> "渴望追求自己的梦想"
        creativity < 0.4f -> "希望发挥自己的创造力"
        meaningfulness < 0.4f -> "对人生感到迷茫，寻找意义"
        growth < 0.4f -> "感觉自己停滞不前"
        contribution < 0.4f -> "想要为社会做出贡献"
        else -> "自我实现需求基本满足"
    }
}

/**
 * 需求类型
 */
enum class NeedType {
    PHYSIOLOGICAL,      // 生理需求
    SAFETY,             // 安全需求
    SOCIAL,             // 社交需求
    ESTEEM,             // 尊重需求
    SELF_ACTUALIZATION; // 自我实现需求
    
    fun getDisplayName(): String = when (this) {
        PHYSIOLOGICAL -> "生理需求"
        SAFETY -> "安全需求"
        SOCIAL -> "社交需求"
        ESTEEM -> "尊重需求"
        SELF_ACTUALIZATION -> "自我实现"
    }
    
    fun getLevel(): Int = ordinal + 1
}

