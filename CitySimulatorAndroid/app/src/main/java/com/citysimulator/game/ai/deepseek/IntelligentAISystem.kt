package com.citysimulator.game.ai.deepseek

import com.citysimulator.game.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 智能AI系统 - 集成DeepSeek API
 * 提供真正的AI功能，包括智能对话、城市分析和预测
 */
@Singleton
class IntelligentAISystem @Inject constructor(
    private val deepSeekClient: DeepSeekClient
) {
    
    /**
     * 智能NPC对话系统
     */
    suspend fun intelligentNPCChat(
        npc: AINPC,
        playerMessage: String,
        cityContext: CityContext
    ): NPCDialogueResponse {
        
        val npcPersonality = npc.getPersonalityDisplayName()
        val npcMood = when {
            npc.happiness > 80 -> "非常开心"
            npc.happiness > 60 -> "心情不错"
            npc.happiness > 40 -> "一般般"
            npc.happiness > 20 -> "有点不开心"
            else -> "很不开心"
        }
        
        val cityContextStr = """
            城市概况：人口${cityContext.population}人，建筑${cityContext.buildingCount}个
            当前天气：${cityContext.weather}
            城市满意度：${cityContext.avgHappiness}%
            最近事件：${cityContext.recentEvents.joinToString(", ")}
        """.trimIndent()
        
        val aiResponse = deepSeekClient.generateNPCDialogue(
            npcName = npc.name,
            npcPersonality = npcPersonality,
            npcMood = npcMood,
            playerMessage = playerMessage,
            cityContext = cityContextStr
        )
        
        return NPCDialogueResponse(
            npcName = npc.name,
            message = aiResponse,
            emotion = determineEmotionFromResponse(aiResponse, npc.happiness),
            relationshipChange = calculateRelationshipChange(aiResponse, npc.personality)
        )
    }
    
    /**
     * 智能城市分析系统
     */
    suspend fun intelligentCityAnalysis(
        buildings: List<Building>,
        npcs: List<AINPC>,
        resources: List<Resource>
    ): IntelligentCityAnalysis {
        
        val cityData = CityData(
            population = npcs.size,
            buildingCount = buildings.size,
            avgHappiness = npcs.map { it.happiness }.average().toFloat(),
            avgEnergy = npcs.map { it.energy }.average().toFloat(),
            employmentRate = npcs.count { it.workLocation != null }.toFloat() / npcs.size * 100
        )
        
        val issues = analyzeCityIssues(buildings, npcs, resources)
        
        val aiAdvice = deepSeekClient.generateCityAdvice(cityData, issues)
        
        return IntelligentCityAnalysis(
            cityData = cityData,
            identifiedIssues = issues,
            aiRecommendations = aiAdvice,
            priorityActions = extractPriorityActions(aiAdvice),
            riskAssessment = assessRisks(cityData, issues),
            opportunities = identifyOpportunities(cityData, buildings)
        )
    }
    
    /**
     * 智能城市预测系统
     */
    suspend fun intelligentCityForecast(
        currentData: CityData,
        historicalSnapshots: List<CitySnapshot>
    ): IntelligentForecast {
        
        val aiForecast = deepSeekClient.generateCityForecast(currentData, historicalSnapshots)
        
        return IntelligentForecast(
            forecastText = aiForecast,
            predictedPopulation = predictPopulation(currentData, historicalSnapshots),
            predictedHappiness = predictHappiness(currentData, historicalSnapshots),
            predictedEconomicGrowth = predictEconomicGrowth(currentData),
            riskFactors = identifyRiskFactors(currentData),
            opportunities = identifyGrowthOpportunities(currentData),
            recommendedActions = extractRecommendedActions(aiForecast)
        )
    }
    
    /**
     * 智能建筑推荐系统
     */
    suspend fun intelligentBuildingRecommendation(
        cityData: CityData,
        availableBudget: Int,
        buildings: List<Building>,
        npcs: List<AINPC>
    ): IntelligentBuildingRecommendation {
        
        val currentBuildings = buildings.map { it.type.getDisplayName() }
        
        val aiRecommendation = deepSeekClient.generateBuildingRecommendation(
            cityData = cityData,
            availableBudget = availableBudget,
            currentBuildings = currentBuildings
        )
        
        return IntelligentBuildingRecommendation(
            recommendationText = aiRecommendation,
            topRecommendations = extractBuildingRecommendations(aiRecommendation),
            budgetAnalysis = analyzeBudget(availableBudget, cityData),
            impactPrediction = predictBuildingImpact(cityData, buildings),
            timingAdvice = getTimingAdvice(cityData)
        )
    }
    
    // ========== 辅助方法 ==========
    
    private fun determineEmotionFromResponse(response: String, currentHappiness: Float): NPCEmotion {
        return when {
            response.contains("开心") || response.contains("高兴") || response.contains("满意") -> NPCEmotion.HAPPY
            response.contains("生气") || response.contains("愤怒") || response.contains("不满") -> NPCEmotion.ANGRY
            response.contains("兴奋") || response.contains("激动") -> NPCEmotion.EXCITED
            response.contains("难过") || response.contains("伤心") || response.contains("失望") -> NPCEmotion.SAD
            else -> if (currentHappiness > 50) NPCEmotion.HAPPY else NPCEmotion.NEUTRAL
        }
    }
    
    private fun calculateRelationshipChange(response: String, personality: Personality): Int {
        val baseChange = when {
            response.contains("谢谢") || response.contains("感谢") -> 5
            response.contains("帮助") || response.contains("建议") -> 3
            response.contains("理解") || response.contains("同意") -> 2
            response.contains("不同意") || response.contains("反对") -> -2
            else -> 1
        }
        
        return when (personality) {
            Personality.EXTROVERT -> baseChange + 1
            Personality.INTROVERT -> baseChange - 1
            Personality.OPTIMISTIC -> baseChange + 1
            Personality.PESSIMISTIC -> baseChange - 1
            else -> baseChange
        }
    }
    
    private fun analyzeCityIssues(
        buildings: List<Building>,
        npcs: List<AINPC>,
        resources: List<Resource>
    ): List<String> {
        val issues = mutableListOf<String>()
        
        val avgHappiness = npcs.map { it.happiness }.average()
        if (avgHappiness < 50) issues.add("居民满意度偏低")
        
        val unemployedCount = npcs.count { it.workLocation == null }
        if (unemployedCount > npcs.size * 0.3) issues.add("失业率过高")
        
        val hungryCount = npcs.count { it.hunger > 70 }
        if (hungryCount > npcs.size * 0.2) issues.add("食物供应不足")
        
        val lonelyCount = npcs.count { it.socialNeed > 70 }
        if (lonelyCount > npcs.size * 0.3) issues.add("缺乏社交设施")
        
        val residentialCount = buildings.count { it.type in listOf(BuildingType.HOUSE, BuildingType.APARTMENT) }
        if (residentialCount < npcs.size / 3) issues.add("住宅数量不足")
        
        return issues
    }
    
    private fun extractPriorityActions(advice: String): List<String> {
        return advice.split("\n").filter { it.contains("建议") || it.contains("应该") }.take(3)
    }
    
    private fun assessRisks(cityData: CityData, issues: List<String>): List<String> {
        val risks = mutableListOf<String>()
        
        if (cityData.avgHappiness < 40) risks.add("居民流失风险")
        if (cityData.employmentRate < 60) risks.add("经济衰退风险")
        if (cityData.population > cityData.buildingCount * 4) risks.add("住房短缺风险")
        
        return risks
    }
    
    private fun identifyOpportunities(cityData: CityData, buildings: List<Building>): List<String> {
        val opportunities = mutableListOf<String>()
        
        if (cityData.avgHappiness > 70) opportunities.add("发展旅游业")
        if (buildings.count { it.type == BuildingType.SCHOOL } > 0) opportunities.add("发展教育产业")
        if (buildings.count { it.type == BuildingType.HOSPITAL } > 0) opportunities.add("发展医疗产业")
        
        return opportunities
    }
    
    private fun predictPopulation(current: CityData, historical: List<CitySnapshot>): Int {
        if (historical.size < 2) return current.population + 5
        
        val growthRate = (current.population - historical.last().population).toFloat() / historical.last().population
        return (current.population * (1 + growthRate)).toInt()
    }
    
    private fun predictHappiness(current: CityData, historical: List<CitySnapshot>): Float {
        if (historical.size < 2) return current.avgHappiness
        
        val trend = (current.avgHappiness - historical.last().happiness) / historical.size
        return (current.avgHappiness + trend).coerceIn(0f, 100f)
    }
    
    private fun predictEconomicGrowth(current: CityData): Float {
        return (current.buildingCount * current.avgHappiness / 100f).coerceAtMost(50f)
    }
    
    private fun identifyRiskFactors(data: CityData): List<String> {
        val risks = mutableListOf<String>()
        if (data.avgHappiness < 50) risks.add("满意度下降")
        if (data.employmentRate < 70) risks.add("就业率偏低")
        return risks
    }
    
    private fun identifyGrowthOpportunities(data: CityData): List<String> {
        val opportunities = mutableListOf<String>()
        if (data.avgHappiness > 70) opportunities.add("人口增长机会")
        if (data.buildingCount > 10) opportunities.add("商业发展机会")
        return opportunities
    }
    
    private fun extractRecommendedActions(forecast: String): List<String> {
        return forecast.split("\n").filter { it.contains("建议") || it.contains("应该") }.take(3)
    }
    
    private fun extractBuildingRecommendations(recommendation: String): List<String> {
        return recommendation.split("\n").filter { it.contains("建筑") || it.contains("建造") }.take(3)
    }
    
    private fun analyzeBudget(budget: Int, data: CityData): String {
        return when {
            budget > 10000 -> "预算充足，可以建造高级建筑"
            budget > 5000 -> "预算适中，建议优先建造必要设施"
            else -> "预算紧张，建议先发展经济"
        }
    }
    
    private fun predictBuildingImpact(data: CityData, buildings: List<Building>): String {
        val impact = buildings.size * data.avgHappiness / 100
        return "预计提升满意度${impact.toInt()}%"
    }
    
    private fun getTimingAdvice(data: CityData): String {
        return when {
            data.avgHappiness > 70 -> "当前是建造的好时机"
            data.avgHappiness < 40 -> "建议先解决居民需求"
            else -> "可以适度发展"
        }
    }
}

// ========== 数据类定义 ==========

enum class NPCEmotion {
    HAPPY, NEUTRAL, SAD, ANGRY, EXCITED
}

data class NPCDialogueResponse(
    val npcName: String,
    val message: String,
    val emotion: NPCEmotion,
    val relationshipChange: Int
)

data class CityContext(
    val population: Int,
    val buildingCount: Int,
    val weather: String,
    val avgHappiness: Float,
    val recentEvents: List<String>
)

data class CityEnvironment(
    val weather: String,
    val timeOfDay: Int,
    val cityHappiness: Float,
    val trafficLevel: Float
)

data class IntelligentCityAnalysis(
    val cityData: CityData,
    val identifiedIssues: List<String>,
    val aiRecommendations: String,
    val priorityActions: List<String>,
    val riskAssessment: List<String>,
    val opportunities: List<String>
)

data class IntelligentForecast(
    val forecastText: String,
    val predictedPopulation: Int,
    val predictedHappiness: Float,
    val predictedEconomicGrowth: Float,
    val riskFactors: List<String>,
    val opportunities: List<String>,
    val recommendedActions: List<String>
)

data class IntelligentBuildingRecommendation(
    val recommendationText: String,
    val topRecommendations: List<String>,
    val budgetAnalysis: String,
    val impactPrediction: String,
    val timingAdvice: String
)