package com.citysimulator.game.data.model

import java.util.Date

/**
 * 市民心声数据模型
 * 
 * 表示市民对城市的需求和反馈
 * 
 * 注意：不使用Room持久化，仅在内存中管理
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
data class CitizenFeedback(
    val id: String,
    
    // 反馈类型
    val type: FeedbackType,
    
    // 反馈内容
    val message: String,
    
    // 优先级 (1-5, 5最高)
    val priority: Int,
    
    // 反馈来源
    val source: FeedbackSource,
    
    // 创建时间
    val createdAt: Date = Date(),
    
    // 是否已处理
    val isResolved: Boolean = false,
    
    // 处理时间
    val resolvedAt: Date? = null
)

/**
 * 反馈类型枚举
 */
enum class FeedbackType {
    NEED_PARK,              // 需要公园
    TRAFFIC_CONGESTION,     // 交通拥堵
    LACK_JOBS,              // 缺少工作岗位
    NEED_EDUCATION,         // 需要教育设施
    NEED_HEALTHCARE,        // 需要医疗设施
    POLLUTION_COMPLAINT,    // 污染投诉
    HOUSING_SHORTAGE,       // 住房短缺
    ENTERTAINMENT_NEED      // 需要娱乐设施
}

/**
 * 反馈来源枚举
 */
enum class FeedbackSource {
    CITIZEN,                // 普通市民
    BUSINESS,               // 商业组织
    ENVIRONMENTAL_GROUP,    // 环保组织
    EDUCATION_GROUP,        // 教育组织
    HEALTH_GROUP            // 健康组织
}

/**
 * 反馈生成器
 * 
 * 根据城市状态生成市民反馈
 */
object CitizenFeedbackGenerator {
    
    /**
     * 生成反馈消息
     */
    fun generateFeedback(type: FeedbackType, source: FeedbackSource): String {
        return when (type) {
            FeedbackType.NEED_PARK -> when (source) {
                FeedbackSource.CITIZEN -> "市民希望有一个公园，可以放松和娱乐！"
                FeedbackSource.BUSINESS -> "商业组织建议建设公园以提升周边商业价值"
                FeedbackSource.ENVIRONMENTAL_GROUP -> "环保组织呼吁建设更多绿地改善空气质量"
                else -> "需要建设公园设施"
            }
            FeedbackType.TRAFFIC_CONGESTION -> when (source) {
                FeedbackSource.CITIZEN -> "交通拥堵严重，通勤时间变长了！"
                FeedbackSource.BUSINESS -> "交通拥堵影响商业活动，建议改善交通"
                else -> "交通拥堵问题需要解决"
            }
            FeedbackType.LACK_JOBS -> when (source) {
                FeedbackSource.CITIZEN -> "我们缺少高级工作岗位"
                FeedbackSource.BUSINESS -> "缺少合适的人才，建议增加工作岗位"
                else -> "需要更多就业机会"
            }
            FeedbackType.NEED_EDUCATION -> when (source) {
                FeedbackSource.CITIZEN -> "我们的孩子需要更好的教育设施"
                FeedbackSource.EDUCATION_GROUP -> "教育组织建议增加学校以提升教育水平"
                else -> "需要更多教育设施"
            }
            FeedbackType.NEED_HEALTHCARE -> when (source) {
                FeedbackSource.CITIZEN -> "社区需要医疗设施"
                FeedbackSource.HEALTH_GROUP -> "健康组织建议增加医院以提升医疗服务"
                else -> "需要医疗设施"
            }
            FeedbackType.POLLUTION_COMPLAINT -> when (source) {
                FeedbackSource.CITIZEN -> "工业污染严重影响了我们的生活质量"
                FeedbackSource.ENVIRONMENTAL_GROUP -> "环保组织抗议工业污染，要求采取措施"
                else -> "环境污染问题需要解决"
            }
            FeedbackType.HOUSING_SHORTAGE -> when (source) {
                FeedbackSource.CITIZEN -> "住房短缺，很多人找不到住处"
                FeedbackSource.BUSINESS -> "住房短缺影响了人才引进"
                else -> "需要更多住房"
            }
            FeedbackType.ENTERTAINMENT_NEED -> when (source) {
                FeedbackSource.CITIZEN -> "我们需要更多娱乐设施"
                FeedbackSource.BUSINESS -> "娱乐设施可以提升城市吸引力"
                else -> "需要娱乐设施"
            }
        }
    }
    
    /**
     * 根据城市状态生成反馈
     */
    fun generateFeedbackFromCityState(
        buildings: List<Building>,
        resources: List<Resource>,
        prosperity: CityProsperity
    ): List<CitizenFeedback> {
        val feedbacks = mutableListOf<CitizenFeedback>()
        
        // 检查是否需要公园
        val parkCount = buildings.count { it.type == BuildingType.PARK }
        if (parkCount == 0) {
            feedbacks.add(CitizenFeedback(
                id = "feedback_${System.currentTimeMillis()}_park",
                type = FeedbackType.NEED_PARK,
                message = generateFeedback(FeedbackType.NEED_PARK, FeedbackSource.CITIZEN),
                priority = 3,
                source = FeedbackSource.CITIZEN
            ))
        }
        
        // 检查交通拥堵
        if (prosperity.cityLivability < 30f) {
            feedbacks.add(CitizenFeedback(
                id = "feedback_${System.currentTimeMillis()}_traffic",
                type = FeedbackType.TRAFFIC_CONGESTION,
                message = generateFeedback(FeedbackType.TRAFFIC_CONGESTION, FeedbackSource.CITIZEN),
                priority = 4,
                source = FeedbackSource.CITIZEN
            ))
        }
        
        // 检查工作岗位
        val jobBuildings = buildings.filter { 
            it.type in listOf(BuildingType.SHOP, BuildingType.SUPERMARKET, BuildingType.MALL, 
                             BuildingType.RESTAURANT, BuildingType.HOTEL)
        }
        if (jobBuildings.size < 2) {
            feedbacks.add(CitizenFeedback(
                id = "feedback_${System.currentTimeMillis()}_jobs",
                type = FeedbackType.LACK_JOBS,
                message = generateFeedback(FeedbackType.LACK_JOBS, FeedbackSource.CITIZEN),
                priority = 4,
                source = FeedbackSource.CITIZEN
            ))
        }
        
        // 检查住房
        val housingBuildings = buildings.filter { 
            it.type in listOf(BuildingType.HOUSE, BuildingType.APARTMENT, BuildingType.VILLA, BuildingType.SKYSCRAPER)
        }
        if (housingBuildings.size < 3) {
            feedbacks.add(CitizenFeedback(
                id = "feedback_${System.currentTimeMillis()}_housing",
                type = FeedbackType.HOUSING_SHORTAGE,
                message = generateFeedback(FeedbackType.HOUSING_SHORTAGE, FeedbackSource.CITIZEN),
                priority = 5,
                source = FeedbackSource.CITIZEN
            ))
        }
        
        return feedbacks
    }
}

