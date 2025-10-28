package com.citysimulator.game.data.model

import java.util.Date

/**
 * 城市事件数据模型
 * 
 * 代表城市中的随机事件，包括机遇、危机和两难抉择
 * 
 * 注意：不使用Room持久化，仅在内存中管理
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
data class CityEvent(
    val id: String,
    
    // 事件类型
    val type: EventType,
    
    // 事件标题
    val title: String,
    
    // 事件描述
    val description: String,
    
    // 事件选项
    val options: List<EventOption>,
    
    // 创建时间
    val createdAt: Date = Date(),
    
    // 是否已处理
    val isResolved: Boolean = false,
    
    // 选择的选项
    val selectedOption: Int? = null,
    
    // 处理时间
    val resolvedAt: Date? = null
)

/**
 * 事件类型枚举
 */
enum class EventType {
    OPPORTUNITY,    // 机遇
    CRISIS,         // 危机
    DILEMMA         // 两难抉择
}

/**
 * 事件选项数据类
 */
data class EventOption(
    val text: String,
    val effect: EventEffect
)

/**
 * 事件效果数据类
 */
data class EventEffect(
    val goldChange: Int = 0,
    val populationChange: Int = 0,
    val prosperityChange: Float = 0f,
    val message: String = ""
)

/**
 * 城市事件生成器
 */
object CityEventGenerator {
    
    /**
     * 生成随机事件
     */
    fun generateEvent(): CityEvent {
        val eventTypes = listOf(EventType.OPPORTUNITY, EventType.CRISIS, EventType.DILEMMA)
        val type = eventTypes.random()
        
        return when (type) {
            EventType.OPPORTUNITY -> generateOpportunityEvent()
            EventType.CRISIS -> generateCrisisEvent()
            EventType.DILEMMA -> generateDilemmaEvent()
        }
    }
    
    /**
     * 生成机遇事件
     */
    private fun generateOpportunityEvent(): CityEvent {
        val opportunities = listOf(
            Triple(
                "高科技公司落户",
                "一个高科技公司希望落户，需要你提供税收减免和一块空地。",
                listOf(
                    EventOption(
                        "同意",
                        EventEffect(
                            goldChange = -500,
                            populationChange = 50,
                            prosperityChange = 10f,
                            message = "高科技公司成功落户，带来新的就业机会和税收收入"
                        )
                    ),
                    EventOption(
                        "拒绝",
                        EventEffect(
                            message = "你拒绝了高科技公司的请求"
                        )
                    )
                )
            ),
            Triple(
                "旅游推广活动",
                "旅游局计划举办大型旅游推广活动，需要资金支持。",
                listOf(
                    EventOption(
                        "资助",
                        EventEffect(
                            goldChange = -300,
                            prosperityChange = 15f,
                            message = "旅游推广活动成功举办，城市知名度大幅提升"
                        )
                    ),
                    EventOption(
                        "不资助",
                        EventEffect(
                            message = "你决定不资助旅游推广活动"
                        )
                    )
                )
            ),
            Triple(
                "国际会议",
                "国际会议组织希望在你的城市举办会议，需要提供会议中心。",
                listOf(
                    EventOption(
                        "同意",
                        EventEffect(
                            goldChange = -1000,
                            prosperityChange = 20f,
                            message = "国际会议成功举办，城市获得国际认可"
                        )
                    ),
                    EventOption(
                        "拒绝",
                        EventEffect(
                            message = "你拒绝了国际会议的请求"
                        )
                    )
                )
            )
        )
        
        val (title, description, options) = opportunities.random()
        
        return CityEvent(
            id = "event_${System.currentTimeMillis()}",
            type = EventType.OPPORTUNITY,
            title = title,
            description = description,
            options = options
        )
    }
    
    /**
     * 生成危机事件
     */
    private fun generateCrisisEvent(): CityEvent {
        val crises = listOf(
            Triple(
                "风暴袭击",
                "一场风暴袭击了城市，部分建筑受损，需要立即修复！",
                listOf(
                    EventOption(
                        "立即修复",
                        EventEffect(
                            goldChange = -800,
                            prosperityChange = 5f,
                            message = "迅速修复了受损建筑，城市恢复正常"
                        )
                    ),
                    EventOption(
                        "暂时搁置",
                        EventEffect(
                            goldChange = -200,
                            prosperityChange = -10f,
                            message = "延迟修复导致市民不满，城市繁荣度下降"
                        )
                    )
                )
            ),
            Triple(
                "公共卫生危机",
                "城市爆发公共卫生危机，需要采取措施。",
                listOf(
                    EventOption(
                        "紧急应对",
                        EventEffect(
                            goldChange = -600,
                            prosperityChange = 5f,
                            message = "迅速应对危机，城市安全得到保障"
                        )
                    ),
                    EventOption(
                        "等待观察",
                        EventEffect(
                            prosperityChange = -15f,
                            message = "延迟应对导致危机扩大，市民不满"
                        )
                    )
                )
            ),
            Triple(
                "经济衰退",
                "全球经济衰退影响城市经济，需要采取措施。",
                listOf(
                    EventOption(
                        "刺激经济",
                        EventEffect(
                            goldChange = -1000,
                            prosperityChange = 8f,
                            message = "经济刺激措施缓解了衰退影响"
                        )
                    ),
                    EventOption(
                        "削减开支",
                        EventEffect(
                            goldChange = 500,
                            prosperityChange = -10f,
                            message = "削减开支导致公共服务质量下降"
                        )
                    )
                )
            )
        )
        
        val (title, description, options) = crises.random()
        
        return CityEvent(
            id = "event_${System.currentTimeMillis()}",
            type = EventType.CRISIS,
            title = title,
            description = description,
            options = options
        )
    }
    
    /**
     * 生成两难抉择事件
     */
    private fun generateDilemmaEvent(): CityEvent {
        val dilemmas = listOf(
            Triple(
                "环保与工业",
                "环保组织抗议工业区的污染，要求你关闭工厂，但这会减少收入。",
                listOf(
                    EventOption(
                        "关闭工厂",
                        EventEffect(
                            goldChange = -500,
                            prosperityChange = 10f,
                            message = "关闭工厂改善了环境，但减少了收入"
                        )
                    ),
                    EventOption(
                        "继续运营",
                        EventEffect(
                            goldChange = 300,
                            prosperityChange = -8f,
                            message = "继续运营工厂带来收入，但污染加剧"
                        )
                    )
                )
            ),
            Triple(
                "住房与绿地",
                "开发商希望将绿地改建为住宅区，这能解决住房问题但减少绿地。",
                listOf(
                    EventOption(
                        "改建住宅",
                        EventEffect(
                            populationChange = 100,
                            prosperityChange = 8f,
                            message = "建造住宅解决了住房问题"
                        )
                    ),
                    EventOption(
                        "保持绿地",
                        EventEffect(
                            prosperityChange = 5f,
                            message = "保持绿地改善了环境"
                        )
                    )
                )
            ),
            Triple(
                "教育投资",
                "教育部门要求增加教育预算，这会提升教育水平但增加开支。",
                listOf(
                    EventOption(
                        "增加预算",
                        EventEffect(
                            goldChange = -700,
                            prosperityChange = 12f,
                            message = "增加教育预算提升了教育水平"
                        )
                    ),
                    EventOption(
                        "保持现状",
                        EventEffect(
                            prosperityChange = -3f,
                            message = "保持现状导致教育水平停滞"
                        )
                    )
                )
            )
        )
        
        val (title, description, options) = dilemmas.random()
        
        return CityEvent(
            id = "event_${System.currentTimeMillis()}",
            type = EventType.DILEMMA,
            title = title,
            description = description,
            options = options
        )
    }
}

