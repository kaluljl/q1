package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import java.util.*
import kotlin.math.*

/**
 * 智能人口增长系统
 * 
 * 根据城市发展状况、建筑类型、资源状况等因素动态计算人口增长
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
object IntelligentPopulationSystem {
    
    /**
     * 计算人口增长
     * 
     * @param currentPopulation 当前人口
     * @param buildings 建筑列表
     * @param resources 资源列表
     * @param goldAmount 金币数量
     * @param timeElapsed 经过的时间（小时）
     * @return 人口增长结果
     */
    fun calculatePopulationGrowth(
        currentPopulation: Int,
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int,
        timeElapsed: Double = 1.0 // 默认1小时
    ): PopulationGrowthResult {
        
        // 分析城市状况
        val cityAnalysis = analyzeCityForPopulation(buildings, resources, goldAmount)
        
        // 计算基础增长率
        val baseGrowthRate = calculateBaseGrowthRate(cityAnalysis)
        
        // 计算各种影响因素
        val housingFactor = calculateHousingFactor(cityAnalysis)
        val employmentFactor = calculateEmploymentFactor(cityAnalysis)
        val serviceFactor = calculateServiceFactor(cityAnalysis)
        val economicFactor = calculateEconomicFactor(cityAnalysis)
        val environmentalFactor = calculateEnvironmentalFactor(cityAnalysis)
        val resourceFactor = calculateResourceFactor(cityAnalysis)
        
        // 计算综合增长率
        val totalGrowthRate = baseGrowthRate * housingFactor * employmentFactor * 
                            serviceFactor * economicFactor * environmentalFactor * resourceFactor
        
        // 计算人口变化
        val populationChange = (totalGrowthRate * timeElapsed * currentPopulation / 100).toInt()
        val newPopulation = maxOf(0, currentPopulation + populationChange)
        
        // 计算人口容量
        val populationCapacity = calculatePopulationCapacity(cityAnalysis)
        
        // 判断是否达到容量限制
        val isAtCapacity = newPopulation >= populationCapacity
        
        return PopulationGrowthResult(
            currentPopulation = currentPopulation,
            newPopulation = newPopulation,
            populationChange = populationChange,
            growthRate = totalGrowthRate,
            populationCapacity = populationCapacity,
            isAtCapacity = isAtCapacity,
            factors = PopulationGrowthFactors(
                housingFactor = housingFactor,
                employmentFactor = employmentFactor,
                serviceFactor = serviceFactor,
                economicFactor = economicFactor,
                environmentalFactor = environmentalFactor,
                resourceFactor = resourceFactor
            ),
            analysis = cityAnalysis
        )
    }
    
    /**
     * 分析城市状况
     */
    private fun analyzeCityForPopulation(
        buildings: List<Building>,
        resources: List<Resource>,
        goldAmount: Int
    ): CityPopulationAnalysis {
        val buildingTypes = buildings.groupBy { it.type }
        
        // 计算各类建筑数量
        val residentialBuildings = getResidentialBuildings(buildingTypes)
        val commercialBuildings = getCommercialBuildings(buildingTypes)
        val industrialBuildings = getIndustrialBuildings(buildingTypes)
        val publicBuildings = getPublicBuildings(buildingTypes)
        
        // 计算建筑总容量
        val totalCapacity = buildings.sumOf { it.capacity }
        val residentialCapacity = residentialBuildings.sumOf { it.capacity }
        val commercialCapacity = commercialBuildings.sumOf { it.capacity }
        val industrialCapacity = industrialBuildings.sumOf { it.capacity }
        
        // 计算建筑效率
        val averageEfficiency = if (buildings.isNotEmpty()) {
            buildings.map { it.efficiency }.average()
        } else 0.0
        
        // 计算资源状况 - 简化版
        val woodAmount = 100.0 // 临时默认值
        val stoneAmount = 100.0
        val foodAmount = 100.0
        
        // 计算城市吸引力
        val attractiveness = calculateCityAttractiveness(
            residentialCapacity, commercialCapacity, publicBuildings.size, averageEfficiency
        )
        
        return CityPopulationAnalysis(
            totalBuildings = buildings.size,
            residentialBuildings = residentialBuildings,
            commercialBuildings = commercialBuildings,
            industrialBuildings = industrialBuildings,
            publicBuildings = publicBuildings,
            totalCapacity = totalCapacity,
            residentialCapacity = residentialCapacity,
            commercialCapacity = commercialCapacity,
            industrialCapacity = industrialCapacity,
            averageEfficiency = averageEfficiency,
            woodAmount = woodAmount,
            stoneAmount = stoneAmount,
            foodAmount = foodAmount,
            goldAmount = goldAmount,
            attractiveness = attractiveness
        )
    }
    
    /**
     * 计算基础增长率
     */
    private fun calculateBaseGrowthRate(analysis: CityPopulationAnalysis): Double {
        return when {
            analysis.totalBuildings < 5 -> 0.5 // 城市太小，增长缓慢
            analysis.totalBuildings < 20 -> 1.0 // 正常增长
            analysis.totalBuildings < 50 -> 1.5 // 快速发展
            else -> 2.0 // 大城市效应
        }
    }
    
    /**
     * 计算住房因素
     */
    private fun calculateHousingFactor(analysis: CityPopulationAnalysis): Double {
        val housingRatio = if (analysis.residentialCapacity > 0) {
            analysis.residentialCapacity.toDouble() / maxOf(1, analysis.totalCapacity)
        } else 0.0
        
        return when {
            housingRatio < 0.3 -> 0.3 // 住房严重不足
            housingRatio < 0.5 -> 0.6 // 住房不足
            housingRatio < 0.7 -> 1.0 // 住房充足
            housingRatio < 0.9 -> 1.2 // 住房充裕
            else -> 1.0 // 住房过多，增长放缓
        }
    }
    
    /**
     * 计算就业因素
     */
    private fun calculateEmploymentFactor(analysis: CityPopulationAnalysis): Double {
        val employmentRatio = if (analysis.commercialCapacity > 0) {
            analysis.commercialCapacity.toDouble() / maxOf(1, analysis.residentialCapacity)
        } else 0.0
        
        return when {
            employmentRatio < 0.2 -> 0.4 // 就业机会严重不足
            employmentRatio < 0.4 -> 0.7 // 就业机会不足
            employmentRatio < 0.6 -> 1.0 // 就业机会充足
            employmentRatio < 0.8 -> 1.3 // 就业机会丰富
            else -> 1.1 // 就业机会过多
        }
    }
    
    /**
     * 计算公共服务因素
     */
    private fun calculateServiceFactor(analysis: CityPopulationAnalysis): Double {
        val serviceRatio = if (analysis.residentialCapacity > 0) {
            analysis.publicBuildings.size.toDouble() / (analysis.residentialCapacity / 10)
        } else 0.0
        
        return when {
            serviceRatio < 0.1 -> 0.5 // 公共服务严重不足
            serviceRatio < 0.3 -> 0.8 // 公共服务不足
            serviceRatio < 0.5 -> 1.0 // 公共服务充足
            serviceRatio < 0.7 -> 1.2 // 公共服务丰富
            else -> 1.0 // 公共服务过多
        }
    }
    
    /**
     * 计算经济因素
     */
    private fun calculateEconomicFactor(analysis: CityPopulationAnalysis): Double {
        val economicHealth = when {
            analysis.goldAmount < 100 -> 0.5 // 经济困难
            analysis.goldAmount < 500 -> 0.8 // 经济一般
            analysis.goldAmount < 1000 -> 1.0 // 经济良好
            analysis.goldAmount < 2000 -> 1.2 // 经济繁荣
            else -> 1.1 // 经济过热
        }
        
        return economicHealth
    }
    
    /**
     * 计算环境因素
     */
    private fun calculateEnvironmentalFactor(analysis: CityPopulationAnalysis): Double {
        val industrialRatio = if (analysis.totalCapacity > 0) {
            analysis.industrialCapacity.toDouble() / analysis.totalCapacity
        } else 0.0
        
        return when {
            industrialRatio < 0.1 -> 1.2 // 环境良好
            industrialRatio < 0.2 -> 1.0 // 环境一般
            industrialRatio < 0.3 -> 0.8 // 环境较差
            industrialRatio < 0.4 -> 0.6 // 环境差
            else -> 0.4 // 环境恶劣
        }
    }
    
    /**
     * 计算资源因素
     */
    private fun calculateResourceFactor(analysis: CityPopulationAnalysis): Double {
        val resourceScore = when {
            analysis.woodAmount < 20 -> 0.6
            analysis.woodAmount < 50 -> 0.8
            analysis.woodAmount < 100 -> 1.0
            else -> 1.1
        } + when {
            analysis.stoneAmount < 10 -> 0.6
            analysis.stoneAmount < 30 -> 0.8
            analysis.stoneAmount < 60 -> 1.0
            else -> 1.1
        } + when {
            analysis.foodAmount < 20 -> 0.6
            analysis.foodAmount < 50 -> 0.8
            analysis.foodAmount < 100 -> 1.0
            else -> 1.1
        }
        
        return resourceScore / 3.0
    }
    
    /**
     * 计算人口容量
     */
    private fun calculatePopulationCapacity(analysis: CityPopulationAnalysis): Int {
        val baseCapacity = analysis.residentialCapacity * 2 // 住宅容量的2倍
        val serviceBonus = analysis.publicBuildings.size * 5 // 每个公共建筑增加5人口容量
        val commercialBonus = (analysis.commercialCapacity * 0.5).toInt() // 商业建筑提供就业
        val attractivenessBonus = (analysis.attractiveness * 10).toInt() // 城市吸引力加成
        
        return baseCapacity + serviceBonus + commercialBonus + attractivenessBonus
    }
    
    /**
     * 计算城市吸引力
     */
    private fun calculateCityAttractiveness(
        residentialCapacity: Int,
        commercialCapacity: Int,
        publicBuildingCount: Int,
        averageEfficiency: Double
    ): Double {
        val diversityScore = when {
            publicBuildingCount >= 3 -> 1.2
            publicBuildingCount >= 2 -> 1.0
            publicBuildingCount >= 1 -> 0.8
            else -> 0.6
        }
        
        val efficiencyScore = when {
            averageEfficiency >= 1.2 -> 1.2
            averageEfficiency >= 1.0 -> 1.0
            averageEfficiency >= 0.8 -> 0.8
            else -> 0.6
        }
        
        val balanceScore = if (residentialCapacity > 0 && commercialCapacity > 0) {
            val ratio = commercialCapacity.toDouble() / residentialCapacity
            when {
                ratio in 0.3..0.7 -> 1.2 // 住宅商业比例合理
                ratio in 0.2..0.8 -> 1.0 // 比例一般
                else -> 0.8 // 比例失衡
            }
        } else 0.5
        
        return (diversityScore + efficiencyScore + balanceScore) / 3.0
    }
    
    /**
     * 获取住宅建筑
     */
    private fun getResidentialBuildings(buildingTypes: Map<BuildingType, List<Building>>): List<Building> {
        return listOfNotNull(
            buildingTypes[BuildingType.HOUSE],
            buildingTypes[BuildingType.APARTMENT],
            buildingTypes[BuildingType.VILLA],
            buildingTypes[BuildingType.SKYSCRAPER]
        ).flatten()
    }
    
    /**
     * 获取商业建筑
     */
    private fun getCommercialBuildings(buildingTypes: Map<BuildingType, List<Building>>): List<Building> {
        return listOfNotNull(
            buildingTypes[BuildingType.SHOP],
            buildingTypes[BuildingType.SUPERMARKET],
            buildingTypes[BuildingType.MALL],
            buildingTypes[BuildingType.RESTAURANT],
            buildingTypes[BuildingType.HOTEL]
        ).flatten()
    }
    
    /**
     * 获取工业建筑
     */
    private fun getIndustrialBuildings(buildingTypes: Map<BuildingType, List<Building>>): List<Building> {
        return listOfNotNull(
            buildingTypes[BuildingType.FACTORY],
            buildingTypes[BuildingType.POWER_PLANT]
        ).flatten()
    }
    
    /**
     * 获取公共建筑
     */
    private fun getPublicBuildings(buildingTypes: Map<BuildingType, List<Building>>): List<Building> {
        return listOfNotNull(
            buildingTypes[BuildingType.SCHOOL],
            buildingTypes[BuildingType.HOSPITAL],
            buildingTypes[BuildingType.POLICE_STATION],
            buildingTypes[BuildingType.FIRE_STATION],
            buildingTypes[BuildingType.PARK]
        ).flatten()
    }
}

/**
 * 人口增长结果数据类
 */
data class PopulationGrowthResult(
    val currentPopulation: Int,
    val newPopulation: Int,
    val populationChange: Int,
    val growthRate: Double,
    val populationCapacity: Int,
    val isAtCapacity: Boolean,
    val factors: PopulationGrowthFactors,
    val analysis: CityPopulationAnalysis
)

/**
 * 人口增长因素数据类
 */
data class PopulationGrowthFactors(
    val housingFactor: Double,
    val employmentFactor: Double,
    val serviceFactor: Double,
    val economicFactor: Double,
    val environmentalFactor: Double,
    val resourceFactor: Double
)

/**
 * 城市人口分析数据类
 */
data class CityPopulationAnalysis(
    val totalBuildings: Int,
    val residentialBuildings: List<Building>,
    val commercialBuildings: List<Building>,
    val industrialBuildings: List<Building>,
    val publicBuildings: List<Building>,
    val totalCapacity: Int,
    val residentialCapacity: Int,
    val commercialCapacity: Int,
    val industrialCapacity: Int,
    val averageEfficiency: Double,
    val woodAmount: Double,
    val stoneAmount: Double,
    val foodAmount: Double,
    val goldAmount: Int,
    val attractiveness: Double
)
