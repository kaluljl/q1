package com.citysimulator.game.ai

import com.citysimulator.game.data.model.CityPolicy
import com.citysimulator.game.data.model.PolicyEffect

/**
 * 政策效果计算器
 * 
 * 根据已实施的政策计算对城市各项指标的影响
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
object PolicyEffectCalculator {
    
    /**
     * 计算所有政策的总效果
     */
    fun calculateTotalEffects(implementedPolicies: List<CityPolicy>): PolicyEffect {
        return implementedPolicies.fold(
            PolicyEffect(
                goldMultiplier = 1.0f,
                populationMultiplier = 1.0f
            )
        ) { acc, policy ->
            PolicyEffect(
                goldMultiplier = acc.goldMultiplier * policy.effects.goldMultiplier,
                populationMultiplier = acc.populationMultiplier * policy.effects.populationMultiplier,
                prosperityBonus = acc.prosperityBonus + policy.effects.prosperityBonus,
                buildingEfficiencyBonus = acc.buildingEfficiencyBonus + policy.effects.buildingEfficiencyBonus,
                happinessBonus = acc.happinessBonus + policy.effects.happinessBonus,
                pollutionReduction = acc.pollutionReduction + policy.effects.pollutionReduction,
                crimeReduction = acc.crimeReduction + policy.effects.crimeReduction,
                educationBonus = acc.educationBonus + policy.effects.educationBonus,
                healthBonus = acc.healthBonus + policy.effects.healthBonus,
                tourismBonus = acc.tourismBonus + policy.effects.tourismBonus,
                specialEffects = acc.specialEffects + policy.effects.specialEffects
            )
        }
    }
    
    /**
     * 应用政策效果到金币收入
     */
    fun applyPolicyToGoldIncome(baseIncome: Int, policies: List<CityPolicy>): Int {
        val effects = calculateTotalEffects(policies)
        return (baseIncome * effects.goldMultiplier).toInt()
    }
    
    /**
     * 应用政策效果到人口增长
     */
    fun applyPolicyToPopulationGrowth(baseGrowth: Double, policies: List<CityPolicy>): Double {
        val effects = calculateTotalEffects(policies)
        return baseGrowth * effects.populationMultiplier
    }
    
    /**
     * 应用政策效果到繁荣度
     */
    fun applyPolicyToProsperity(baseProsperity: Int, policies: List<CityPolicy>): Int {
        val effects = calculateTotalEffects(policies)
        return (baseProsperity + effects.prosperityBonus).toInt()
    }
    
    /**
     * 应用政策效果到建筑效率
     */
    fun applyPolicyToBuildingEfficiency(baseEfficiency: Float, policies: List<CityPolicy>): Float {
        val effects = calculateTotalEffects(policies)
        return baseEfficiency * (1 + effects.buildingEfficiencyBonus)
    }
    
    /**
     * 获取每月政策维护总成本
     */
    fun getTotalMonthlyCost(implementedPolicies: List<CityPolicy>): Int {
        return implementedPolicies.sumOf { it.monthlyCost }
    }
    
    /**
     * 获取政策带来的幸福度加成
     */
    fun getHappinessBonus(implementedPolicies: List<CityPolicy>): Float {
        val effects = calculateTotalEffects(implementedPolicies)
        return effects.happinessBonus
    }
    
    /**
     * 获取政策概述文本
     */
    fun getPolicySummary(implementedPolicies: List<CityPolicy>): String {
        if (implementedPolicies.isEmpty()) {
            return "未实施任何政策"
        }
        
        val effects = calculateTotalEffects(implementedPolicies)
        val summary = buildString {
            append("已实施 ${implementedPolicies.size} 项政策\n")
            
            if (effects.goldMultiplier > 1.0f) {
                append("• 收入提升: ${((effects.goldMultiplier - 1) * 100).toInt()}%\n")
            }
            if (effects.prosperityBonus > 0) {
                append("• 繁荣度加成: +${effects.prosperityBonus.toInt()}\n")
            }
            if (effects.happinessBonus > 0) {
                append("• 幸福度加成: +${effects.happinessBonus.toInt()}\n")
            }
            if (effects.buildingEfficiencyBonus > 0) {
                append("• 建筑效率提升: ${(effects.buildingEfficiencyBonus * 100).toInt()}%\n")
            }
            if (effects.pollutionReduction > 0) {
                append("• 污染减少: ${effects.pollutionReduction.toInt()}%\n")
            }
            if (effects.crimeReduction > 0) {
                append("• 犯罪减少: ${effects.crimeReduction.toInt()}%\n")
            }
        }
        
        return summary.toString().trim()
    }
}

