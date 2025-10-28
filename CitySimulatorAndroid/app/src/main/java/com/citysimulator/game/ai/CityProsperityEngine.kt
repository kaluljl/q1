package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import kotlin.math.*

/**
 * 城市繁荣度计算引擎
 * 
 * 根据四个维度计算城市繁荣度：
 * 1. 人口增长 - 城市吸引力
 * 2. 财政收入 - 经济健康  
 * 3. 市民幸福 - 生活质量
 * 4. 城市宜居 - 环境质量
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
class CityProsperityEngine {
    
    /**
     * 计算城市繁荣度
     */
    fun calculateProsperity(
        buildings: List<Building>,
        resources: List<Resource>,
        population: Int,
        goldAmount: Int
    ): CityProsperity {
        
        // 分析建筑分布
        val factors = analyzeBuildings(buildings)
        
        // 计算四个维度
        val populationGrowth = calculatePopulationGrowth(factors, population)
        val financialHealth = calculateFinancialHealth(factors, goldAmount, resources)
        val citizenHappiness = calculateCitizenHappiness(factors, buildings)
        val cityLivability = calculateCityLivability(factors, buildings)
        
        // 计算总体繁荣度 (加权平均)
        val overallProsperity = (populationGrowth * 0.3f + 
                                financialHealth * 0.3f + 
                                citizenHappiness * 0.2f + 
                                cityLivability * 0.2f)
        
        // 确定繁荣度等级
        val prosperityLevel = determineProsperityLevel(overallProsperity)
        
        // 计算无限增长的繁荣度数值 (基于建筑数量和四个维度)
        val prosperityScore = buildings.size * 10 + overallProsperity
        
        // 计算其他指标
        val monthlyIncome = calculateMonthlyIncome(factors)
        val monthlyExpense = calculateMonthlyExpense(factors)
        val citizenSatisfaction = (citizenHappiness + cityLivability) / 2f
        val environmentQuality = cityLivability
        
        return CityProsperity(
            populationGrowth = populationGrowth,
            financialHealth = financialHealth,
            citizenHappiness = citizenHappiness,
            cityLivability = cityLivability,
            overallProsperity = prosperityScore,  // 使用无限增长的数值
            prosperityLevel = prosperityLevel,
            totalPopulation = population,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            citizenSatisfaction = citizenSatisfaction,
            environmentQuality = environmentQuality
        )
    }
    
    /**
     * 分析建筑分布，提取影响因素
     */
    private fun analyzeBuildings(buildings: List<Building>): ProsperityFactors {
        var housingCapacity = 0
        var jobOpportunities = 0
        var commercialBuildings = 0
        var industrialBuildings = 0
        var tourismAttractions = 0
        var educationFacilities = 0
        var healthcareFacilities = 0
        var entertainmentFacilities = 0
        var greenSpaces = 0
        
        buildings.forEach { building ->
            when (building.type) {
                // 住房建筑
                BuildingType.HOUSE -> housingCapacity += 4
                BuildingType.APARTMENT -> housingCapacity += 8
                BuildingType.VILLA -> housingCapacity += 2
                BuildingType.SKYSCRAPER -> housingCapacity += 20
                
                // 商业建筑
                BuildingType.SHOP -> {
                    commercialBuildings++
                    jobOpportunities += 2
                }
                BuildingType.SUPERMARKET -> {
                    commercialBuildings++
                    jobOpportunities += 5
                }
                BuildingType.MALL -> {
                    commercialBuildings++
                    jobOpportunities += 10
                    entertainmentFacilities++
                }
                BuildingType.RESTAURANT -> {
                    commercialBuildings++
                    jobOpportunities += 3
                    entertainmentFacilities++
                }
                BuildingType.HOTEL -> {
                    commercialBuildings++
                    jobOpportunities += 8
                    tourismAttractions++
                }
                // 其他建筑类型
                else -> {
                    // 默认处理
                }
            }
        }
        
        // 计算人口密度 (假设20x20网格)
        val totalArea = 400
        val populationDensity = if (totalArea > 0) housingCapacity.toFloat() / totalArea else 0f
        
        // 计算污染水平 (工业建筑越多污染越严重)
        val pollutionLevel = (industrialBuildings * 10f).coerceAtMost(100f)
        
        // 计算交通拥堵 (建筑密度越高越拥堵)
        val trafficCongestion = (buildings.size * 2f).coerceAtMost(100f)
        
        return ProsperityFactors(
            housingCapacity = housingCapacity,
            jobOpportunities = jobOpportunities,
            populationDensity = populationDensity,
            commercialBuildings = commercialBuildings,
            industrialBuildings = industrialBuildings,
            tourismAttractions = tourismAttractions,
            educationFacilities = educationFacilities,
            healthcareFacilities = healthcareFacilities,
            entertainmentFacilities = entertainmentFacilities,
            greenSpaces = greenSpaces,
            pollutionLevel = pollutionLevel,
            trafficCongestion = trafficCongestion
        )
    }
    
    /**
     * 计算人口增长维度 (0-100)
     * 基于住房容量、就业机会、城市吸引力
     */
    private fun calculatePopulationGrowth(factors: ProsperityFactors, currentPopulation: Int): Float {
        val housingScore = (factors.housingCapacity * 5f).coerceAtMost(50f)
        val jobScore = (factors.jobOpportunities * 2f).coerceAtMost(30f)
        val attractionScore = (factors.tourismAttractions * 5f).coerceAtMost(20f)
        
        val baseScore = housingScore + jobScore + attractionScore
        
        // 人口密度影响 (适中最好)
        val densityBonus = when {
            factors.populationDensity < 0.1f -> 10f  // 人口稀少，有增长空间
            factors.populationDensity < 0.3f -> 15f  // 理想密度
            factors.populationDensity < 0.5f -> 10f  // 稍显拥挤
            else -> 5f  // 过于拥挤
        }
        
        return (baseScore + densityBonus).coerceIn(0f, 100f)
    }
    
    /**
     * 计算财政收入维度 (0-100)
     * 基于商业建筑、收入支出比、经济活力
     */
    private fun calculateFinancialHealth(factors: ProsperityFactors, goldAmount: Int, resources: List<Resource>): Float {
        val commercialScore = (factors.commercialBuildings * 8f).coerceAtMost(40f)
        val tourismScore = (factors.tourismAttractions * 10f).coerceAtMost(30f)
        
        // 金币数量影响
        val goldScore = when {
            goldAmount < 1000 -> 10f
            goldAmount < 5000 -> 20f
            goldAmount < 10000 -> 30f
            else -> 40f
        }
        
        val baseScore = commercialScore + tourismScore + goldScore
        
        // 资源丰富度奖励
        val resourceBonus = (resources.size * 2f).coerceAtMost(10f)
        
        return (baseScore + resourceBonus).coerceIn(0f, 100f)
    }
    
    /**
     * 计算市民幸福维度 (0-100)
     * 基于教育、医疗、娱乐设施，以及就业机会
     */
    private fun calculateCitizenHappiness(factors: ProsperityFactors, buildings: List<Building>): Float {
        val educationScore = (factors.educationFacilities * 15f).coerceAtMost(30f)
        val healthcareScore = (factors.healthcareFacilities * 15f).coerceAtMost(30f)
        val entertainmentScore = (factors.entertainmentFacilities * 10f).coerceAtMost(20f)
        val jobScore = (factors.jobOpportunities * 1f).coerceAtMost(20f)
        
        val baseScore = educationScore + healthcareScore + entertainmentScore + jobScore
        
        // 建筑多样性奖励
        val buildingTypes = buildings.map { it.type }.distinct().size
        val diversityBonus = (buildingTypes * 2f).coerceAtMost(10f)
        
        return (baseScore + diversityBonus).coerceIn(0f, 100f)
    }
    
    /**
     * 计算城市宜居维度 (0-100)
     * 基于环境质量、交通状况、绿地面积
     */
    private fun calculateCityLivability(factors: ProsperityFactors, buildings: List<Building>): Float {
        val greenScore = (factors.greenSpaces * 20f).coerceAtMost(40f)
        
        // 污染影响 (污染越少分数越高)
        val pollutionScore = (100f - factors.pollutionLevel).coerceAtLeast(0f) * 0.3f
        
        // 交通拥堵影响 (拥堵越少分数越高)
        val trafficScore = (100f - factors.trafficCongestion).coerceAtLeast(0f) * 0.3f
        
        val baseScore = greenScore + pollutionScore + trafficScore
        
        // 建筑密度影响 (适中最好)
        val density = buildings.size.toFloat() / 400f  // 20x20网格
        val densityBonus = when {
            density < 0.1f -> 10f  // 建筑太少
            density < 0.3f -> 15f  // 理想密度
            density < 0.5f -> 10f  // 稍显拥挤
            else -> 5f  // 过于拥挤
        }
        
        return (baseScore + densityBonus).coerceIn(0f, 100f)
    }
    
    /**
     * 确定繁荣度等级
     */
    private fun determineProsperityLevel(overallProsperity: Float): ProsperityLevel {
        return when {
            overallProsperity >= 90f -> ProsperityLevel.METROPOLIS
            overallProsperity >= 80f -> ProsperityLevel.THRIVING
            overallProsperity >= 60f -> ProsperityLevel.PROSPEROUS
            overallProsperity >= 40f -> ProsperityLevel.MODERATE
            overallProsperity >= 20f -> ProsperityLevel.DEVELOPING
            else -> ProsperityLevel.POOR
        }
    }
    
    /**
     * 计算月收入
     */
    private fun calculateMonthlyIncome(factors: ProsperityFactors): Int {
        return factors.commercialBuildings * 100 + factors.tourismAttractions * 200
    }
    
    /**
     * 计算月支出
     */
    private fun calculateMonthlyExpense(factors: ProsperityFactors): Int {
        return factors.housingCapacity * 10 + factors.jobOpportunities * 5
    }
}
