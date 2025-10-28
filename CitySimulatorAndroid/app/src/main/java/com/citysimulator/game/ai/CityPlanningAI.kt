package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import java.util.*

/**
 * AI城市规划和优化建议引擎
 */
class CityPlanningAI {
    
    /**
     * 分析城市布局并提供优化建议
     */
    fun analyzeCityLayout(buildings: List<Building>, npcs: List<AINPC>): List<CityOptimizationSuggestion> {
        val suggestions = mutableListOf<CityOptimizationSuggestion>()
        
        // 分析建筑密度
        val densityAnalysis = analyzeBuildingDensity(buildings)
        if (densityAnalysis.isOvercrowded) {
            suggestions.add(
                CityOptimizationSuggestion(
                    type = SuggestionType.LAYOUT,
                    priority = Priority.HIGH,
                    title = "建筑密度过高",
                    description = "当前区域建筑过于密集，建议增加绿化空间和道路",
                    impact = "提高居民生活质量，减少交通拥堵",
                    cost = 1000,
                    benefits = listOf("+20% 居民满意度", "+15% 交通效率")
                )
            )
        }
        
        // 分析交通效率
        val trafficAnalysis = analyzeTrafficEfficiency(buildings, npcs)
        if (trafficAnalysis.needsImprovement) {
            suggestions.add(
                CityOptimizationSuggestion(
                    type = SuggestionType.TRAFFIC,
                    priority = Priority.MEDIUM,
                    title = "交通效率低下",
                    description = "居民通勤时间过长，建议优化道路布局",
                    impact = "减少通勤时间，提高工作效率",
                    cost = 2000,
                    benefits = listOf("+25% 通勤效率", "+10% 工作效率")
                )
            )
        }
        
        // 分析公共服务覆盖
        val serviceAnalysis = analyzePublicServices(buildings, npcs)
        serviceAnalysis.forEach { service ->
            suggestions.add(
                CityOptimizationSuggestion(
                    type = SuggestionType.SERVICES,
                    priority = Priority.MEDIUM,
                    title = "缺少${service.name}",
                    description = "建议在${service.recommendedLocation}建造${service.name}",
                    impact = service.impact,
                    cost = service.cost,
                    benefits = service.benefits
                )
            )
        }
        
        // 分析居民满意度
        val satisfactionAnalysis = analyzeResidentSatisfaction(npcs)
        if (satisfactionAnalysis.needsAttention) {
            suggestions.add(
                CityOptimizationSuggestion(
                    type = SuggestionType.SATISFACTION,
                    priority = Priority.HIGH,
                    title = "居民满意度偏低",
                    description = "建议增加娱乐设施和绿化空间",
                    impact = "提高居民幸福感和城市吸引力",
                    cost = 1500,
                    benefits = listOf("+30% 居民满意度", "+20% 人口增长")
                )
            )
        }
        
        return suggestions.sortedByDescending { it.priority.ordinal }
    }
    
    /**
     * 分析建筑密度
     */
    private fun analyzeBuildingDensity(buildings: List<Building>): DensityAnalysis {
        val totalArea = 100 // 假设城市总面积为100
        val buildingCount = buildings.size
        val density = buildingCount.toFloat() / totalArea
        
        return DensityAnalysis(
            density = density,
            isOvercrowded = density > 0.8f,
            recommendedDensity = 0.6f
        )
    }
    
    /**
     * 分析交通效率
     */
    private fun analyzeTrafficEfficiency(buildings: List<Building>, npcs: List<AINPC>): TrafficAnalysis {
        val avgCommuteTime = npcs.mapNotNull { npc ->
            npc.workLocation?.let { work ->
                calculateCommuteTime(npc.homeLocation, work)
            }
        }.average()
        
        return TrafficAnalysis(
            avgCommuteTime = avgCommuteTime,
            needsImprovement = avgCommuteTime > 30, // 超过30分钟需要改善
            efficiency = if (avgCommuteTime > 0) ((60 - avgCommuteTime) / 60f).toFloat() else 1f
        )
    }
    
    /**
     * 分析公共服务覆盖
     */
    private fun analyzePublicServices(buildings: List<Building>, npcs: List<AINPC>): List<ServiceAnalysis> {
        val suggestions = mutableListOf<ServiceAnalysis>()
        
        val buildingTypes = buildings.map { it.type }
        val npcCount = npcs.size
        
        // 检查医院
        if (!buildingTypes.contains(BuildingType.HOSPITAL) && npcCount > 10) {
            suggestions.add(
                ServiceAnalysis(
                    name = "医院",
                    recommendedLocation = "市中心区域",
                    impact = "提供医疗服务，提高居民健康水平",
                    cost = 5000,
                    benefits = listOf("+40% 健康水平", "+15% 居民满意度")
                )
            )
        }
        
        // 检查学校
        if (!buildingTypes.contains(BuildingType.SCHOOL) && npcCount > 20) {
            suggestions.add(
                ServiceAnalysis(
                    name = "学校",
                    recommendedLocation = "居民区附近",
                    impact = "提供教育服务，提高居民素质",
                    cost = 3000,
                    benefits = listOf("+25% 教育水平", "+20% 工作效率")
                )
            )
        }
        
        // 检查娱乐设施
        val entertainmentCount = buildingTypes.count { 
            it in listOf(BuildingType.PARK, BuildingType.MALL, BuildingType.HOTEL) 
        }
        if (entertainmentCount < npcCount / 15) {
            suggestions.add(
                ServiceAnalysis(
                    name = "娱乐设施",
                    recommendedLocation = "居民聚集区",
                    impact = "提供娱乐服务，提高居民幸福感",
                    cost = 2000,
                    benefits = listOf("+35% 居民满意度", "+10% 人口增长")
                )
            )
        }
        
        return suggestions
    }
    
    /**
     * 分析居民满意度
     */
    private fun analyzeResidentSatisfaction(npcs: List<AINPC>): SatisfactionAnalysis {
        val avgHappiness = npcs.map { it.happiness }.average()
        val avgEnergy = npcs.map { it.energy }.average()
        val avgSocial = npcs.map { it.socialNeed }.average()
        
        val overallSatisfaction = (avgHappiness + avgEnergy + (100 - avgSocial)) / 3
        
        return SatisfactionAnalysis(
            overallSatisfaction = overallSatisfaction,
            needsAttention = overallSatisfaction < 60,
            happiness = avgHappiness,
            energy = avgEnergy,
            socialNeed = avgSocial
        )
    }
    
    /**
     * 计算通勤时间
     */
    private fun calculateCommuteTime(from: Pair<Int, Int>, to: Pair<Int, Int>): Int {
        val distance = kotlin.math.abs(from.first - to.first) + kotlin.math.abs(from.second - to.second)
        return distance * 3 // 每个格子3分钟
    }
    
    /**
     * 生成城市发展预测
     */
    fun generateCityForecast(buildings: List<Building>, npcs: List<AINPC>): CityForecast {
        val currentPopulation = npcs.size
        val buildingCount = buildings.size
        val avgSatisfaction = npcs.map { it.getOverallSatisfaction() }.average()
        
        // 基于当前状态预测未来6个月
        val populationGrowth = calculatePopulationGrowth(avgSatisfaction, buildingCount)
        val economicGrowth = calculateEconomicGrowth(buildings, npcs)
        val infrastructureNeeds = calculateInfrastructureNeeds(populationGrowth, buildingCount)
        
        return CityForecast(
            timeHorizon = 6, // 6个月
            predictedPopulation = currentPopulation + populationGrowth,
            predictedEconomicGrowth = economicGrowth,
            infrastructureNeeds = infrastructureNeeds,
            risks = identifyRisks(avgSatisfaction, populationGrowth),
            opportunities = identifyOpportunities(buildings, npcs)
        )
    }
    
    private fun calculatePopulationGrowth(satisfaction: Double, buildingCount: Int): Int {
        val baseGrowth = (satisfaction - 50) / 10 // 满意度影响增长率
        val capacityFactor = buildingCount * 0.1 // 建筑容量影响
        return (baseGrowth + capacityFactor).toInt().coerceAtLeast(0)
    }
    
    private fun calculateEconomicGrowth(buildings: List<Building>, npcs: List<AINPC>): Float {
        val workBuildings = buildings.count { it.type in listOf(BuildingType.SHOP, BuildingType.SUPERMARKET, BuildingType.MALL) }
        val workingNPCs = npcs.count { it.currentActivity == NPCActivity.WORKING }
        return (workBuildings * workingNPCs * 0.1f).coerceAtMost(50f)
    }
    
    private fun calculateInfrastructureNeeds(populationGrowth: Int, currentBuildings: Int): List<String> {
        val needs = mutableListOf<String>()
        if (populationGrowth > 10) needs.add("需要更多住宅")
        if (populationGrowth > 5) needs.add("需要更多商业设施")
        if (populationGrowth > 15) needs.add("需要更多公共服务")
        return needs
    }
    
    private fun identifyRisks(satisfaction: Double, populationGrowth: Int): List<String> {
        val risks = mutableListOf<String>()
        if (satisfaction < 40) risks.add("居民满意度过低可能导致人口流失")
        if (populationGrowth > 20) risks.add("人口增长过快可能导致基础设施不足")
        return risks
    }
    
    private fun identifyOpportunities(buildings: List<Building>, npcs: List<AINPC>): List<String> {
        val opportunities = mutableListOf<String>()
        val avgHappiness = npcs.map { it.happiness }.average()
        if (avgHappiness > 70) opportunities.add("高居民满意度，适合发展旅游业")
        val workBuildings = buildings.count { it.type in listOf(BuildingType.SHOP, BuildingType.SUPERMARKET, BuildingType.MALL) }
        if (workBuildings > 5) opportunities.add("商业基础良好，适合招商引资")
        return opportunities
    }
}

/**
 * 城市优化建议
 */
data class CityOptimizationSuggestion(
    val type: SuggestionType,
    val priority: Priority,
    val title: String,
    val description: String,
    val impact: String,
    val cost: Int,
    val benefits: List<String>
)

enum class SuggestionType {
    LAYOUT, TRAFFIC, SERVICES, SATISFACTION, ECONOMIC, ENVIRONMENTAL
}

enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * 分析结果数据类
 */
private data class DensityAnalysis(
    val density: Float,
    val isOvercrowded: Boolean,
    val recommendedDensity: Float
)

private data class TrafficAnalysis(
    val avgCommuteTime: Double,
    val needsImprovement: Boolean,
    val efficiency: Float
)

private data class ServiceAnalysis(
    val name: String,
    val recommendedLocation: String,
    val impact: String,
    val cost: Int,
    val benefits: List<String>
)

private data class SatisfactionAnalysis(
    val overallSatisfaction: Double,
    val needsAttention: Boolean,
    val happiness: Double,
    val energy: Double,
    val socialNeed: Double
)

/**
 * 城市发展预测
 */
data class CityForecast(
    val timeHorizon: Int, // 预测时间范围（月）
    val predictedPopulation: Int,
    val predictedEconomicGrowth: Float,
    val infrastructureNeeds: List<String>,
    val risks: List<String>,
    val opportunities: List<String>
)
