package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import java.util.*
import kotlin.random.Random

/**
 * 完整的市民心声系统
 * 基于城市状态的智能触发机制，让城市管理更有代入感
 * 
 * @author AI进化论-花生
 * @since 1.0
 */

/**
 * 心声紧急程度
 */
enum class FeedbackUrgency {
    LOW,        // 蓝色 - 改善建议
    MEDIUM,     // 黄色 - 重要需求  
    HIGH,       // 红色 - 紧急问题
    CRITICAL    // 深红 - 危机事件
}

/**
 * 心声触发类型
 */
enum class FeedbackTriggerType {
    CONDITION_BASED,    // 条件触发（基于城市状态）
    TIME_BASED,        // 时间触发
    MILESTONE_BASED,   // 里程碑触发
    EVENT_BASED        // 事件触发
}

/**
 * 心声数据模型
 */
data class CitizenFeedbackData(
    val id: String,
    val content: String,
    val category: FeedbackType,
    val urgency: FeedbackUrgency,
    val triggerType: FeedbackTriggerType,
    val relatedData: List<String>,      // 关联的城市数据
    val affectedArea: String,           // 影响范围
    val solutions: List<String>,        // 解决方案建议
    val timeLimit: Int,                 // 处理时限（游戏天）
    val icon: String,                   // 图标
    val createdAt: String,              // 游戏时间字符串（格式：yyyy年MM月dd日 HH:mm）
    val isResolved: Boolean = false,
    val resolvedAt: String? = null,     // 游戏时间字符串
    val escalationLevel: Int = 0        // 升级等级
)

/**
 * 城市状态分析器
 */
object CityStateAnalyzer {
    
    /**
     * 分析城市状态，返回各种指标
     */
    fun analyzeCityState(
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int,
        population: Int
    ): CityStateMetrics {
        val buildingTypes = buildings.groupBy { it.type }
        
        return CityStateMetrics(
            // 基础设施指标
            waterSupply = calculateWaterSupply(buildingTypes),
            powerSupply = calculatePowerSupply(buildingTypes),
            wasteManagement = calculateWasteManagement(buildingTypes),
            
            // 就业经济指标
            unemploymentRate = calculateUnemploymentRate(buildingTypes, population),
            averageIncome = calculateAverageIncome(buildingTypes),
            businessDensity = calculateBusinessDensity(buildingTypes),
            
            // 生活环境指标
            greenSpaceRatio = calculateGreenSpaceRatio(buildingTypes),
            noiseLevel = calculateNoiseLevel(buildingTypes),
            airQuality = calculateAirQuality(buildingTypes),
            
            // 交通出行指标
            trafficCongestion = calculateTrafficCongestion(buildingTypes, population),
            publicTransportCoverage = calculatePublicTransportCoverage(buildingTypes),
            parkingAvailability = calculateParkingAvailability(buildingTypes),
            
            // 教育医疗指标
            educationCapacity = calculateEducationCapacity(buildingTypes),
            healthcareCapacity = calculateHealthcareCapacity(buildingTypes),
            elderlyCareCapacity = calculateElderlyCareCapacity(buildingTypes),
            
            // 总体指标
            populationDensity = calculatePopulationDensity(buildings, population),
            citySize = buildings.size,
            totalGold = goldAmount
        )
    }
    
    private fun calculateWaterSupply(buildingTypes: Map<BuildingType, List<Building>>): Double {
        // 使用现有的建筑类型，暂时用发电厂代表基础设施
        val waterFacilities = buildingTypes[BuildingType.POWER_PLANT]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (waterFacilities.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculatePowerSupply(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val powerFacilities = buildingTypes[BuildingType.POWER_PLANT]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (powerFacilities.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateWasteManagement(buildingTypes: Map<BuildingType, List<Building>>): Double {
        // 使用医院代表垃圾处理设施
        val wasteFacilities = buildingTypes[BuildingType.HOSPITAL]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (wasteFacilities.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateUnemploymentRate(buildingTypes: Map<BuildingType, List<Building>>, population: Int): Double {
        val jobCapacity = buildingTypes.values.sumOf { buildings ->
            buildings.sumOf { it.capacity }
        }
        return if (population > 0) ((population - jobCapacity).toDouble() / population) * 100 else 0.0
    }
    
    private fun calculateAverageIncome(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val totalIncome = buildingTypes.values.sumOf { buildings ->
            buildings.sumOf { it.income }
        }
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) totalIncome.toDouble() / totalBuildings else 0.0
    }
    
    private fun calculateBusinessDensity(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val businessBuildings = (buildingTypes[BuildingType.SHOP]?.size ?: 0) + 
                               (buildingTypes[BuildingType.SUPERMARKET]?.size ?: 0) +
                               (buildingTypes[BuildingType.FACTORY]?.size ?: 0)
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (businessBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateGreenSpaceRatio(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val greenBuildings = buildingTypes[BuildingType.PARK]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (greenBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateNoiseLevel(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val noisyBuildings = (buildingTypes[BuildingType.STEEL_MILL]?.size ?: 0) * 2 +
                            (buildingTypes[BuildingType.POWER_PLANT]?.size ?: 0) * 3
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (noisyBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateAirQuality(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val pollutingBuildings = (buildingTypes[BuildingType.STEEL_MILL]?.size ?: 0) * 2 +
                                (buildingTypes[BuildingType.POWER_PLANT]?.size ?: 0)
        val greenBuildings = buildingTypes[BuildingType.PARK]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) {
            val pollutionRatio = pollutingBuildings.toDouble() / totalBuildings
            val greenRatio = greenBuildings.toDouble() / totalBuildings
            maxOf(0.0, 100.0 - (pollutionRatio * 50) + (greenRatio * 30))
        } else 100.0
    }
    
    private fun calculateTrafficCongestion(buildingTypes: Map<BuildingType, List<Building>>, population: Int): Double {
        val roadBuildings = buildingTypes[BuildingType.ROAD]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        val roadRatio = if (totalBuildings > 0) roadBuildings.toDouble() / totalBuildings else 0.0
        val populationDensity = if (totalBuildings > 0) population.toDouble() / totalBuildings else 0.0
        
        return maxOf(0.0, 100.0 - (roadRatio * 200) + (populationDensity * 10))
    }
    
    private fun calculatePublicTransportCoverage(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val transportBuildings = (buildingTypes[BuildingType.POLICE_STATION]?.size ?: 0) +
                                 (buildingTypes[BuildingType.FIRE_STATION]?.size ?: 0)
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (transportBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateParkingAvailability(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val parkingBuildings = buildingTypes[BuildingType.HOTEL]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (parkingBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateEducationCapacity(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val educationBuildings = buildingTypes[BuildingType.SCHOOL]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (educationBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateHealthcareCapacity(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val healthcareBuildings = buildingTypes[BuildingType.HOSPITAL]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (healthcareBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculateElderlyCareCapacity(buildingTypes: Map<BuildingType, List<Building>>): Double {
        val elderlyBuildings = buildingTypes[BuildingType.HOSPITAL]?.size ?: 0
        val totalBuildings = buildingTypes.values.sumOf { it.size }
        return if (totalBuildings > 0) (elderlyBuildings.toDouble() / totalBuildings) * 100 else 0.0
    }
    
    private fun calculatePopulationDensity(buildings: List<Building>, population: Int): Double {
        val totalCapacity = buildings.sumOf { it.capacity }
        return if (totalCapacity > 0) (population.toDouble() / totalCapacity) * 100 else 0.0
    }
}

/**
 * 城市状态指标
 */
data class CityStateMetrics(
    // 基础设施
    val waterSupply: Double,
    val powerSupply: Double,
    val wasteManagement: Double,
    
    // 就业经济
    val unemploymentRate: Double,
    val averageIncome: Double,
    val businessDensity: Double,
    
    // 生活环境
    val greenSpaceRatio: Double,
    val noiseLevel: Double,
    val airQuality: Double,
    
    // 交通出行
    val trafficCongestion: Double,
    val publicTransportCoverage: Double,
    val parkingAvailability: Double,
    
    // 教育医疗
    val educationCapacity: Double,
    val healthcareCapacity: Double,
    val elderlyCareCapacity: Double,
    
    // 总体
    val populationDensity: Double,
    val citySize: Int,
    val totalGold: Int
)

/**
 * 心声内容库
 */
object FeedbackContentLibrary {
    
    /**
     * 基础设施类心声
     */
    private val infrastructureFeedbacks = mapOf(
        "water_supply_low" to listOf(
            "经常停水停电，什么时候能解决？",
            "水压太小了，洗澡都不舒服",
            "希望供水系统更稳定一些"
        ),
        "power_supply_low" to listOf(
            "又停电了！我的工作都白做了",
            "电力供应不稳定，电器经常坏",
            "希望电力系统能更可靠"
        ),
        "waste_management_poor" to listOf(
            "垃圾堆了三天没人收，要臭死了！",
            "垃圾桶都满了，环境太差了",
            "希望垃圾处理能更及时"
        )
    )
    
    /**
     * 就业经济类心声
     */
    private val economicFeedbacks = mapOf(
        "unemployment_high" to listOf(
            "找工作好难，公司太少了",
            "已经失业3个月了，什么时候有新工作？",
            "希望有更多就业机会"
        ),
        "income_low" to listOf(
            "工资太低，物价太高，活不下去了",
            "生活成本太高，压力好大",
            "希望收入能跟上物价"
        ),
        "business_low" to listOf(
            "希望有更多高科技企业",
            "商业机会太少了",
            "需要更多投资和创业支持"
        )
    )
    
    /**
     * 生活环境类心声
     */
    private val livingEnvironmentFeedbacks = mapOf(
        "green_space_low" to listOf(
            "公园太少了，孩子没地方玩",
            "希望多建几个公园，带孩子散步都没地方去",
            "城市太缺乏绿色了"
        ),
        "noise_high" to listOf(
            "邻居工厂噪音太大，睡不着觉",
            "噪音污染严重，影响休息",
            "希望噪音能控制一下"
        ),
        "air_quality_poor" to listOf(
            "空气好难闻，我的哮喘又犯了！",
            "空气质量太差了，不敢出门",
            "希望空气能更清新一些"
        )
    )
    
    /**
     * 交通出行类心声
     */
    private val transportationFeedbacks = mapOf(
        "traffic_congestion_high" to listOf(
            "每天堵车2小时，我要迟到了！",
            "交通太堵了，上班总是迟到",
            "道路太窄，天天堵车"
        ),
        "public_transport_poor" to listOf(
            "公交班次太少，等车要半小时",
            "地铁什么时候能通到城东？",
            "公共交通太不方便了"
        ),
        "parking_low" to listOf(
            "停车位严重不足",
            "找停车位要绕半小时",
            "希望有更多停车场"
        )
    )
    
    /**
     * 教育医疗类心声
     */
    private val educationHealthcareFeedbacks = mapOf(
        "education_capacity_low" to listOf(
            "学校名额满了，孩子上不了学",
            "教育资源太紧张了",
            "希望有更多好学校"
        ),
        "healthcare_capacity_low" to listOf(
            "医院排队太长，看个病要一天",
            "夜间急诊排队太长",
            "医疗资源太紧张了"
        ),
        "elderly_care_low" to listOf(
            "需要更多养老设施",
            "老人照顾服务太少了",
            "希望有更好的养老环境"
        )
    )
    
    /**
     * 获取心声内容
     */
    fun getFeedbackContent(category: String, issue: String): String {
        val contentMap = when (category) {
            "infrastructure" -> infrastructureFeedbacks
            "economic" -> economicFeedbacks
            "living_environment" -> livingEnvironmentFeedbacks
            "transportation" -> transportationFeedbacks
            "education_healthcare" -> educationHealthcareFeedbacks
            else -> emptyMap()
        }
        
        val contents = contentMap[issue] ?: return "希望城市能发展得更好"
        return contents.random()
    }
}

/**
 * 智能心声生成器
 */
object IntelligentFeedbackGenerator {
    
    /**
     * 基于城市状态生成心声
     * @param gameTime 游戏时间字符串（格式：yyyy年MM月dd日 HH:mm）
     */
    fun generateFeedbackBasedOnCity(
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int,
        population: Int,
        gameTime: String
    ): List<CitizenFeedbackData> {
        val cityMetrics = CityStateAnalyzer.analyzeCityState(buildings, resources, goldAmount, population)
        val feedbacks = mutableListOf<CitizenFeedbackData>()
        
        // 条件触发 - 基于城市状态
        feedbacks.addAll(generateConditionBasedFeedbacks(cityMetrics, gameTime))
        
        // 里程碑触发
        feedbacks.addAll(generateMilestoneBasedFeedbacks(cityMetrics, gameTime))
        
        // 时间触发（模拟）
        feedbacks.addAll(generateTimeBasedFeedbacks(cityMetrics, gameTime))
        
        return feedbacks.take(5) // 最多显示5条心声
    }
    
    /**
     * 生成条件触发的心声
     */
    private fun generateConditionBasedFeedbacks(metrics: CityStateMetrics, gameTime: String): List<CitizenFeedbackData> {
        val feedbacks = mutableListOf<CitizenFeedbackData>()
        
        // 基础设施问题
        if (metrics.waterSupply < 20) {
            feedbacks.add(createFeedback(
                "water_supply_critical",
                FeedbackContentLibrary.getFeedbackContent("infrastructure", "water_supply_low"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("供水系统", "基础设施"),
                "全市",
                listOf("建设水厂", "升级供水系统"),
                15,
                "💧"
            ))
        }
        
        if (metrics.powerSupply < 20) {
            feedbacks.add(createFeedback(
                "power_supply_critical",
                FeedbackContentLibrary.getFeedbackContent("infrastructure", "power_supply_low"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("电力系统", "基础设施"),
                "全市",
                listOf("建设发电厂", "升级电网"),
                15,
                "⚡"
            ))
        }
        
        if (metrics.wasteManagement < 10) {
            feedbacks.add(createFeedback(
                "waste_management_critical",
                FeedbackContentLibrary.getFeedbackContent("infrastructure", "waste_management_poor"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("垃圾处理", "环境卫生"),
                "全市",
                listOf("建设垃圾处理厂", "增加垃圾收集点"),
                20,
                "🗑️"
            ))
        }
        
        // 就业经济问题
        if (metrics.unemploymentRate > 15) {
            feedbacks.add(createFeedback(
                "unemployment_high",
                FeedbackContentLibrary.getFeedbackContent("economic", "unemployment_high"),
                FeedbackType.LACK_JOBS,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("就业率", "经济发展"),
                "全市",
                listOf("建设工厂", "发展商业", "招商引资"),
                30,
                "💼"
            ))
        }
        
        if (metrics.averageIncome < 20) {
            feedbacks.add(createFeedback(
                "income_low",
                FeedbackContentLibrary.getFeedbackContent("economic", "income_low"),
                FeedbackType.LACK_JOBS,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("平均收入", "生活成本"),
                "全市",
                listOf("提高工资水平", "控制物价", "发展高薪产业"),
                25,
                "💰"
            ))
        }
        
        // 生活环境问题
        if (metrics.greenSpaceRatio < 10) {
            feedbacks.add(createFeedback(
                "green_space_low",
                FeedbackContentLibrary.getFeedbackContent("living_environment", "green_space_low"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("绿化率", "公园建设"),
                "全市",
                listOf("建设公园", "增加绿地", "改善环境"),
                20,
                "🌳"
            ))
        }
        
        if (metrics.noiseLevel > 60) {
            feedbacks.add(createFeedback(
                "noise_high",
                FeedbackContentLibrary.getFeedbackContent("living_environment", "noise_high"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("噪音水平", "环境质量"),
                "工业区周边",
                listOf("控制工业噪音", "建设隔音设施", "调整工业布局"),
                25,
                "🔇"
            ))
        }
        
        if (metrics.airQuality < 40) {
            feedbacks.add(createFeedback(
                "air_quality_poor",
                FeedbackContentLibrary.getFeedbackContent("living_environment", "air_quality_poor"),
                FeedbackType.POLLUTION_COMPLAINT,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("空气质量", "环境污染"),
                "全市",
                listOf("控制工业污染", "增加绿化", "发展清洁能源"),
                20,
                "🌬️"
            ))
        }
        
        // 交通出行问题
        if (metrics.trafficCongestion > 70) {
            feedbacks.add(createFeedback(
                "traffic_congestion_high",
                FeedbackContentLibrary.getFeedbackContent("transportation", "traffic_congestion_high"),
                FeedbackType.TRAFFIC_CONGESTION,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("交通拥堵", "道路建设"),
                "全市",
                listOf("扩建道路", "建设立交桥", "发展公共交通"),
                30,
                "🚗"
            ))
        }
        
        if (metrics.publicTransportCoverage < 15) {
            feedbacks.add(createFeedback(
                "public_transport_poor",
                FeedbackContentLibrary.getFeedbackContent("transportation", "public_transport_poor"),
                FeedbackType.TRAFFIC_CONGESTION,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("公共交通", "交通便利性"),
                "全市",
                listOf("建设公交站", "发展地铁", "增加公交线路"),
                25,
                "🚌"
            ))
        }
        
        if (metrics.parkingAvailability < 10) {
            feedbacks.add(createFeedback(
                "parking_low",
                FeedbackContentLibrary.getFeedbackContent("transportation", "parking_low"),
                FeedbackType.TRAFFIC_CONGESTION,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("停车位", "停车设施"),
                "商业区",
                listOf("建设停车场", "增加路边停车", "发展智能停车"),
                20,
                "🅿️"
            ))
        }
        
        // 教育医疗问题
        if (metrics.educationCapacity < 15) {
            feedbacks.add(createFeedback(
                "education_capacity_low",
                FeedbackContentLibrary.getFeedbackContent("education_healthcare", "education_capacity_low"),
                FeedbackType.NEED_EDUCATION,
                FeedbackUrgency.MEDIUM,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("教育容量", "教育资源"),
                "全市",
                listOf("建设学校", "扩建现有学校", "提高教育质量"),
                30,
                "🎓"
            ))
        }
        
        if (metrics.healthcareCapacity < 10) {
            feedbacks.add(createFeedback(
                "healthcare_capacity_low",
                FeedbackContentLibrary.getFeedbackContent("education_healthcare", "healthcare_capacity_low"),
                FeedbackType.NEED_HEALTHCARE,
                FeedbackUrgency.HIGH,
                FeedbackTriggerType.CONDITION_BASED,
                listOf("医疗容量", "医疗资源"),
                "全市",
                listOf("建设医院", "增加诊所", "提高医疗水平"),
                25,
                "🏥"
            ))
        }
        
        return feedbacks
    }
    
    /**
     * 生成里程碑触发的心声
     */
    private fun generateMilestoneBasedFeedbacks(metrics: CityStateMetrics, gameTime: String): List<CitizenFeedbackData> {
        val feedbacks = mutableListOf<CitizenFeedbackData>()
        
        // 人口里程碑
        when {
            metrics.citySize >= 50 && metrics.citySize < 100 -> {
                feedbacks.add(createFeedback(
                    "city_growing",
                    "城市发展得不错！希望继续保持这个势头",
                    FeedbackType.NEED_PARK,
                    FeedbackUrgency.LOW,
                    FeedbackTriggerType.MILESTONE_BASED,
                    listOf("城市规模", "发展水平"),
                    "全市",
                    listOf("继续发展", "保持增长"),
                    0,
                    "🏙️"
                ))
            }
            metrics.citySize >= 100 -> {
                feedbacks.add(createFeedback(
                    "city_major",
                    "我们的大城市！希望有更多国际化的设施",
                    FeedbackType.NEED_PARK,
                    FeedbackUrgency.LOW,
                    FeedbackTriggerType.MILESTONE_BASED,
                    listOf("城市规模", "国际化水平"),
                    "全市",
                    listOf("建设国际设施", "提升城市形象"),
                    0,
                    "🌆"
                ))
            }
        }
        
        return feedbacks
    }
    
    /**
     * 生成时间触发的心声
     */
    private fun generateTimeBasedFeedbacks(metrics: CityStateMetrics, gameTime: String): List<CitizenFeedbackData> {
        val feedbacks = mutableListOf<CitizenFeedbackData>()
        
        // 模拟时间触发的心声
        if (Random.nextBoolean()) {
            feedbacks.add(createFeedback(
                "general_suggestion",
                "希望城市能发展得更好，我们都很期待！",
                FeedbackType.NEED_PARK,
                FeedbackUrgency.LOW,
                FeedbackTriggerType.TIME_BASED,
                listOf("城市发展", "市民期待"),
                "全市",
                listOf("听取建议", "持续改进"),
                0,
                "💭"
            ))
        }
        
        return feedbacks
    }
    
    /**
     * 创建心声数据
     */
    private fun createFeedback(
        id: String,
        content: String,
        category: FeedbackType,
        urgency: FeedbackUrgency,
        triggerType: FeedbackTriggerType,
        relatedData: List<String>,
        affectedArea: String,
        solutions: List<String>,
        timeLimit: Int,
        icon: String,
        gameTime: String? = null
    ): CitizenFeedbackData {
        return CitizenFeedbackData(
            id = id,
            content = content,
            category = category,
            urgency = urgency,
            triggerType = triggerType,
            relatedData = relatedData,
            affectedArea = affectedArea,
            solutions = solutions,
            timeLimit = timeLimit,
            icon = icon,
            createdAt = gameTime ?: "游戏时间未知"
        )
    }
}