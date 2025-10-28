package com.citysimulator.game.data.model

import java.util.Date

/**
 * 城市繁荣度数据模型
 * 
 * 根据四个维度评估城市繁荣程度：
 * 1. 人口增长 - 城市吸引力
 * 2. 财政收入 - 经济健康
 * 3. 市民幸福 - 生活质量
 * 4. 城市宜居 - 环境质量
 * 
 * 注意：不使用Room持久化，仅在内存中管理
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
data class CityProsperity(
    val id: String = "main_city",
    
    // 人口增长维度 (0-100)
    val populationGrowth: Float = 0f,
    
    // 财政收入维度 (0-100) 
    val financialHealth: Float = 0f,
    
    // 市民幸福维度 (0-100)
    val citizenHappiness: Float = 0f,
    
    // 城市宜居维度 (0-100)
    val cityLivability: Float = 0f,
    
    // 总体繁荣度 (四个维度的加权平均)
    val overallProsperity: Float = 0f,
    
    // 繁荣度等级
    val prosperityLevel: ProsperityLevel = ProsperityLevel.POOR,
    
    // 最后更新时间
    val lastUpdated: Date = Date(),
    
    // 人口数量
    val totalPopulation: Int = 0,
    
    // 月收入
    val monthlyIncome: Int = 0,
    
    // 月支出
    val monthlyExpense: Int = 0,
    
    // 市民满意度
    val citizenSatisfaction: Float = 0f,
    
    // 环境质量
    val environmentQuality: Float = 0f
)

/**
 * 繁荣度等级枚举
 */
enum class ProsperityLevel(val displayName: String, val minScore: Float, val color: String) {
    POOR("贫困", 0f, "#FF5722"),           // 0-20
    DEVELOPING("发展中", 20f, "#FF9800"),   // 20-40
    MODERATE("中等", 40f, "#FFC107"),       // 40-60
    PROSPEROUS("繁荣", 60f, "#4CAF50"),     // 60-80
    THRIVING("兴旺", 80f, "#2196F3"),       // 80-90
    METROPOLIS("大都市", 90f, "#9C27B0")    // 90-100
}

/**
 * 繁荣度影响因素
 */
data class ProsperityFactors(
    // 人口相关
    val housingCapacity: Int = 0,           // 住房容量
    val jobOpportunities: Int = 0,          // 就业机会
    val populationDensity: Float = 0f,      // 人口密度
    
    // 经济相关
    val commercialBuildings: Int = 0,       // 商业建筑数量
    val industrialBuildings: Int = 0,       // 工业建筑数量
    val tourismAttractions: Int = 0,        // 旅游景点数量
    
    // 生活质量相关
    val educationFacilities: Int = 0,       // 教育设施
    val healthcareFacilities: Int = 0,      // 医疗设施
    val entertainmentFacilities: Int = 0,   // 娱乐设施
    
    // 环境相关
    val greenSpaces: Int = 0,               // 绿地面积
    val pollutionLevel: Float = 0f,         // 污染水平
    val trafficCongestion: Float = 0f       // 交通拥堵程度
)
