package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.data.model.PersonalityTraits
import java.util.Date
import java.util.UUID
import kotlin.random.Random

/**
 * 玩家干预与建议系统
 * 
 * 玩家可以对市民提出建议，但市民是否接受取决于多种因素
 * 
 * @author AI进化论-花生
 */
object PlayerInterventionSystem {
    
    /**
     * 玩家给市民提建议
     */
    fun suggestTogetizen(
        citizen: Citizen,
        suggestion: Suggestion,
        trustInMayor: Float // 市民对市长的信任度 (0-1)
    ): SuggestionResponse {
        // 计算接受建议的概率
        val acceptanceProbability = calculateAcceptanceProbability(
            citizen,
            suggestion,
            trustInMayor
        )
        
        val isAccepted = Random.nextFloat() < acceptanceProbability
        
        return SuggestionResponse(
            suggestion = suggestion,
            citizenId = citizen.id,
            isAccepted = isAccepted,
            reason = getResponseReason(citizen, suggestion, isAccepted, trustInMayor),
            trustChange = if (isAccepted) 0.02f else -0.01f,
            timestamp = Date()
        )
    }
    
    /**
     * 计算市民接受建议的概率
     */
    private fun calculateAcceptanceProbability(
        citizen: Citizen,
        suggestion: Suggestion,
        trustInMayor: Float
    ): Float {
        var probability = 0.5f
        
        // 信任度影响 (30%权重)
        probability += (trustInMayor - 0.5f) * 0.3f
        
        // 性格影响
        // 注意：这里需要从Citizen获取PersonalityTraits，暂时用旧的personality
        when (citizen.personality) {
            com.citysimulator.game.data.model.CitizenPersonality.WORKAHOLIC -> {
                if (suggestion.type == MayorSuggestionType.CAREER_ADVICE) probability += 0.2f
            }
            com.citysimulator.game.data.model.CitizenPersonality.SOCIAL -> {
                if (suggestion.type == MayorSuggestionType.SOCIAL_ADVICE) probability += 0.2f
            }
            com.citysimulator.game.data.model.CitizenPersonality.DEMANDING -> {
                probability -= 0.1f // 苛刻型更难接受建议
            }
            else -> {}
        }
        
        // 当前需求状态影响
        if (citizen.happiness < 0.3f && suggestion.type == MayorSuggestionType.LIFE_IMPROVEMENT) {
            probability += 0.15f // 不开心时更愿意听改善建议
        }
        
        // 建议质量影响
        probability += suggestion.quality * 0.2f
        
        return probability.coerceIn(0.1f, 0.9f)
    }
    
    /**
     * 获取回应理由
     */
    private fun getResponseReason(
        citizen: Citizen,
        suggestion: Suggestion,
        isAccepted: Boolean,
        trustInMayor: Float
    ): String {
        return if (isAccepted) {
            when {
                trustInMayor > 0.7f -> "您说得对，市长大人，我会试试看的！"
                trustInMayor > 0.4f -> "嗯...听起来不错，我考虑一下。"
                else -> "好吧，我可以试试。"
            }
        } else {
            when {
                trustInMayor < 0.3f -> "抱歉，我不太相信您的建议。"
                citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.DEMANDING ->
                    "这个建议不太适合我。"
                else -> "谢谢您的关心，但我有自己的计划。"
            }
        }
    }
    
    /**
     * 计算市民对市长的信任度
     */
    fun calculateTrustInMayor(
        citizen: Citizen,
        cityHappiness: Float,
        cityProsperity: Float,
        pastInteractions: List<SuggestionResponse>
    ): Float {
        var trust = 0.5f
        
        // 基于城市整体状况
        trust += (cityHappiness - 0.5f) * 0.3f
        trust += (cityProsperity - 0.5f) * 0.2f
        
        // 基于个人幸福度
        trust += (citizen.happiness - 0.5f) * 0.2f
        
        // 基于过去互动历史
        val successfulSuggestions = pastInteractions.count { it.isAccepted }
        val totalSuggestions = pastInteractions.size
        if (totalSuggestions > 0) {
            val successRate = successfulSuggestions.toFloat() / totalSuggestions
            trust += (successRate - 0.5f) * 0.3f
        }
        
        return trust.coerceIn(0f, 1f)
    }
    
    /**
     * 玩家发布城市级指令/请求
     */
    fun issuePublicRequest(
        request: PublicRequest,
        citizens: List<Citizen>,
        averageTrustInMayor: Float
    ): PublicRequestResponse {
        val supporters = mutableListOf<String>()
        val opponents = mutableListOf<String>()
        val neutral = mutableListOf<String>()
        
        citizens.forEach { citizen ->
            val supportProbability = calculateSupportProbability(
                citizen,
                request,
                averageTrustInMayor
            )
            
            when {
                supportProbability > 0.6f -> supporters.add(citizen.id)
                supportProbability < 0.4f -> opponents.add(citizen.id)
                else -> neutral.add(citizen.id)
            }
        }
        
        return PublicRequestResponse(
            request = request,
            supporters = supporters,
            opponents = opponents,
            neutral = neutral,
            overallSupport = supporters.size.toFloat() / citizens.size,
            timestamp = Date()
        )
    }
    
    /**
     * 计算市民对公共请求的支持概率
     */
    private fun calculateSupportProbability(
        citizen: Citizen,
        request: PublicRequest,
        averageTrustInMayor: Float
    ): Float {
        var probability = averageTrustInMayor
        
        // 根据请求类型和市民性格调整
        when (request.type) {
            PublicRequestType.WORK_HARDER -> {
                if (citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.WORKAHOLIC) {
                    probability += 0.3f
                } else if (citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.BALANCED) {
                    probability -= 0.2f
                }
            }
            PublicRequestType.CELEBRATE_TOGETHER -> {
                if (citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.SOCIAL) {
                    probability += 0.3f
                }
            }
            PublicRequestType.BE_PATIENT -> {
                probability += (citizen.happiness - 0.5f) * 0.4f
            }
            PublicRequestType.SAVE_RESOURCES -> {
                probability += 0.1f // 大多数人会轻微支持
            }
            PublicRequestType.VOLUNTEER -> {
                if (citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.FAMILY_ORIENTED ||
                    citizen.personality == com.citysimulator.game.data.model.CitizenPersonality.SOCIAL) {
                    probability += 0.2f
                }
            }
        }
        
        return probability.coerceIn(0f, 1f)
    }
}

/**
 * 建议
 */
data class Suggestion(
    val id: String = UUID.randomUUID().toString(),
    val type: MayorSuggestionType,
    val content: String,
    val quality: Float = 0.7f, // 建议质量 (0-1)
    val timestamp: Date = Date()
)

/**
 * 建议类型
 */
enum class MayorSuggestionType {
    CAREER_ADVICE,      // 职业建议
    HOUSING_ADVICE,     // 住房建议
    SOCIAL_ADVICE,      // 社交建议
    HEALTH_ADVICE,      // 健康建议
    LIFE_IMPROVEMENT,   // 生活改善
    FINANCIAL_ADVICE;   // 财务建议
    
    fun getDisplayName(): String = when (this) {
        CAREER_ADVICE -> "职业建议"
        HOUSING_ADVICE -> "住房建议"
        SOCIAL_ADVICE -> "社交建议"
        HEALTH_ADVICE -> "健康建议"
        LIFE_IMPROVEMENT -> "生活改善"
        FINANCIAL_ADVICE -> "财务建议"
    }
}

/**
 * 建议回应
 */
data class SuggestionResponse(
    val suggestion: Suggestion,
    val citizenId: String,
    val isAccepted: Boolean,
    val reason: String,
    val trustChange: Float, // 对市长信任度的变化
    val timestamp: Date
)

/**
 * 公共请求
 */
data class PublicRequest(
    val id: String = UUID.randomUUID().toString(),
    val type: PublicRequestType,
    val title: String,
    val description: String,
    val timestamp: Date = Date()
)

/**
 * 公共请求类型
 */
enum class PublicRequestType {
    WORK_HARDER,        // 请求市民更努力工作
    CELEBRATE_TOGETHER, // 邀请市民一起庆祝
    BE_PATIENT,         // 请求市民耐心等待
    SAVE_RESOURCES,     // 请求节约资源
    VOLUNTEER;          // 请求志愿服务
    
    fun getDisplayName(): String = when (this) {
        WORK_HARDER -> "努力工作"
        CELEBRATE_TOGETHER -> "共同庆祝"
        BE_PATIENT -> "保持耐心"
        SAVE_RESOURCES -> "节约资源"
        VOLUNTEER -> "志愿服务"
    }
}

/**
 * 公共请求回应
 */
data class PublicRequestResponse(
    val request: PublicRequest,
    val supporters: List<String>, // 支持者市民ID
    val opponents: List<String>, // 反对者市民ID
    val neutral: List<String>, // 中立者市民ID
    val overallSupport: Float, // 总体支持率 (0-1)
    val timestamp: Date
) {
    fun getSupportDescription(): String = when {
        overallSupport > 0.8f -> "压倒性支持"
        overallSupport > 0.6f -> "广泛支持"
        overallSupport > 0.4f -> "分歧明显"
        overallSupport > 0.2f -> "多数反对"
        else -> "强烈反对"
    }
}

