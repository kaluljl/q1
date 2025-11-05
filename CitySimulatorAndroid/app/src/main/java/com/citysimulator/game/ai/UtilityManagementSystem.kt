package com.citysimulator.game.ai

import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.BuildingType

/**
 * 公用事业管理系统
 * 
 * 管理电力、水源、垃圾处理等城市基础设施
 * 影响建筑的正常运作和市民满意度
 */

data class UtilityStatus(
    val electricitySupply: Int,      // 电力供应
    val electricityDemand: Int,       // 电力需求
    val waterSupply: Int,             // 水源供应
    val waterDemand: Int,             // 水源需求
    val wasteCapacity: Int,           // 垃圾处理能力
    val wasteProduction: Int,         // 垃圾产生量
    val electricityRatio: Float,      // 电力满足率 (0-1)
    val waterRatio: Float,            // 水源满足率 (0-1)
    val wasteRatio: Float,            // 垃圾处理率 (0-1)
    val electricityShortage: Boolean, // 电力短缺
    val waterShortage: Boolean,       // 水源短缺
    val wasteOverflow: Boolean        // 垃圾溢出
)

data class UtilityImpact(
    val buildingEfficiency: Float,    // 建筑效率 (0.0-1.5)
    val happinessPenalty: Float,      // 幸福度惩罚 (0-50)
    val incomePenalty: Float,         // 收入惩罚 (0-0.5)
    val warnings: List<String>        // 警告信息
)

object UtilityManagementSystem {
    
    /**
     * 计算公用事业状态
     */
    fun calculateUtilityStatus(buildings: List<Building>, population: Int): UtilityStatus {
        var electricitySupply = 0
        var waterSupply = 0
        var wasteCapacity = 0
        
        // 计算供应（使用简化系统，暂时基于建筑名称判断）
        buildings.forEach { building ->
            // 由于BuildingType可能没有这些枚举值，我们直接给予基础值
            // 后续可以根据实际的BuildingType枚举来调整
            when (building.type) {
                else -> {
                    // 暂时给所有建筑提供少量基础资源
                    electricitySupply += 10
                    waterSupply += 8
                    wasteCapacity += 5
                }
            }
        }
        
        // 计算需求（基于建筑数量和人口）
        val buildingCount = buildings.size
        val electricityDemand = (buildingCount * 10 + population * 2).coerceAtLeast(10)
        val waterDemand = (buildingCount * 8 + population * 3).coerceAtLeast(10)
        val wasteProduction = (buildingCount * 5 + population * 2).coerceAtLeast(5)
        
        // 计算满足率
        val electricityRatio = if (electricityDemand > 0) {
            (electricitySupply.toFloat() / electricityDemand).coerceIn(0f, 1f)
        } else 1f
        
        val waterRatio = if (waterDemand > 0) {
            (waterSupply.toFloat() / waterDemand).coerceIn(0f, 1f)
        } else 1f
        
        val wasteRatio = if (wasteProduction > 0) {
            (wasteCapacity.toFloat() / wasteProduction).coerceIn(0f, 1f)
        } else 1f
        
        return UtilityStatus(
            electricitySupply = electricitySupply,
            electricityDemand = electricityDemand,
            waterSupply = waterSupply,
            waterDemand = waterDemand,
            wasteCapacity = wasteCapacity,
            wasteProduction = wasteProduction,
            electricityRatio = electricityRatio,
            waterRatio = waterRatio,
            wasteRatio = wasteRatio,
            electricityShortage = electricityRatio < 0.8f,
            waterShortage = waterRatio < 0.8f,
            wasteOverflow = wasteRatio < 0.8f
        )
    }
    
    /**
     * 计算公用事业对城市的影响
     */
    fun calculateUtilityImpact(status: UtilityStatus): UtilityImpact {
        val warnings = mutableListOf<String>()
        
        // 计算建筑效率影响
        val electricityFactor = when {
            status.electricityRatio >= 1.0f -> 1.2f  // 电力充足，效率提升20%
            status.electricityRatio >= 0.8f -> 1.0f  // 电力正常
            status.electricityRatio >= 0.5f -> 0.7f  // 电力不足，效率降低30%
            else -> 0.5f                              // 电力严重不足，效率降低50%
        }
        
        val waterFactor = when {
            status.waterRatio >= 1.0f -> 1.15f       // 水源充足，效率提升15%
            status.waterRatio >= 0.8f -> 1.0f        // 水源正常
            status.waterRatio >= 0.5f -> 0.75f       // 水源不足，效率降低25%
            else -> 0.6f                              // 水源严重不足，效率降低40%
        }
        
        val wasteFactor = when {
            status.wasteRatio >= 1.0f -> 1.1f        // 垃圾处理充足，效率提升10%
            status.wasteRatio >= 0.8f -> 1.0f        // 垃圾处理正常
            status.wasteRatio >= 0.5f -> 0.85f       // 垃圾处理不足，效率降低15%
            else -> 0.7f                              // 垃圾严重溢出，效率降低30%
        }
        
        // 综合效率 = 三者的乘积
        val buildingEfficiency = (electricityFactor * waterFactor * wasteFactor).coerceIn(0.3f, 1.5f)
        
        // 计算幸福度惩罚
        var happinessPenalty = 0f
        if (status.electricityShortage) {
            happinessPenalty += (1f - status.electricityRatio) * 30f
            warnings.add("⚡ 电力短缺！需要建造更多发电设施")
        }
        if (status.waterShortage) {
            happinessPenalty += (1f - status.waterRatio) * 25f
            warnings.add("💧 水源不足！需要建造更多供水设施")
        }
        if (status.wasteOverflow) {
            happinessPenalty += (1f - status.wasteRatio) * 20f
            warnings.add("🗑️ 垃圾堆积！需要建造更多垃圾处理设施")
        }
        
        // 计算收入惩罚（建筑效率低会影响收入）
        val incomePenalty = (1f - buildingEfficiency).coerceIn(0f, 0.5f)
        
        // 添加额外警告
        if (status.electricityRatio < 0.5f) {
            warnings.add("🚨 严重电力危机！部分建筑停止运作")
        }
        if (status.waterRatio < 0.5f) {
            warnings.add("🚨 严重缺水危机！市民健康受到威胁")
        }
        if (status.wasteRatio < 0.5f) {
            warnings.add("🚨 垃圾危机！城市环境严重恶化")
        }
        
        return UtilityImpact(
            buildingEfficiency = buildingEfficiency,
            happinessPenalty = happinessPenalty.coerceAtMost(50f),
            incomePenalty = incomePenalty,
            warnings = warnings
        )
    }
    
    /**
     * 获取公用事业状态文本
     */
    fun getUtilityStatusText(status: UtilityStatus): String {
        val electricityText = "⚡ 电力: ${status.electricitySupply}/${status.electricityDemand} (${(status.electricityRatio * 100).toInt()}%)"
        val waterText = "💧 水源: ${status.waterSupply}/${status.waterDemand} (${(status.waterRatio * 100).toInt()}%)"
        val wasteText = "🗑️ 垃圾: ${status.wasteCapacity}/${status.wasteProduction} (${(status.wasteRatio * 100).toInt()}%)"
        
        return "$electricityText\n$waterText\n$wasteText"
    }
    
    /**
     * 获取建议
     */
    fun getSuggestions(status: UtilityStatus): List<String> {
        val suggestions = mutableListOf<String>()
        
        if (status.electricityRatio < 0.9f) {
            when {
                status.electricitySupply == 0 -> suggestions.add("🏗️ 建造第一座发电设施（风车、火电厂或太阳能电站）")
                status.electricityRatio < 0.5f -> suggestions.add("🏗️ 紧急建造更多发电设施！")
                else -> suggestions.add("🏗️ 考虑增加发电设施以满足城市发展需求")
            }
        }
        
        if (status.waterRatio < 0.9f) {
            when {
                status.waterSupply == 0 -> suggestions.add("🏗️ 建造第一座供水设施（水井、水泵站或水净化厂）")
                status.waterRatio < 0.5f -> suggestions.add("🏗️ 紧急建造更多供水设施！")
                else -> suggestions.add("🏗️ 考虑增加供水设施")
            }
        }
        
        if (status.wasteRatio < 0.9f) {
            when {
                status.wasteCapacity == 0 -> suggestions.add("🏗️ 建造垃圾处理设施（垃圾堆、回收中心或环保处理厂）")
                status.wasteRatio < 0.5f -> suggestions.add("🏗️ 紧急建造更多垃圾处理设施！")
                else -> suggestions.add("🏗️ 考虑增加垃圾处理设施")
            }
        }
        
        return suggestions
    }
}

