package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 资源类型枚举
 * 
 * 定义游戏中所有资源类型，按类别分组。
 */
enum class ResourceType {
    // 基础资源
    WOOD,           // 木材
    STONE,          // 石材
    STEEL,          // 钢材
    FOOD,           // 食品
    
    // 货币资源
    GOLD,           // 金币
    DIAMOND,        // 钻石
    
    // 特殊资源
    OIL,            // 石油
    COAL,           // 煤炭
    ORE,            // 矿石
    RARE_EARTH,     // 稀土
    COMPONENT,      // 零部件
    PROCESSED_FOOD  // 加工食品
}

/**
 * 资源类别枚举
 * 
 * 将资源按功能进行分类。
 */
enum class ResourceCategory {
    BASIC,          // 基础资源
    CURRENCY,       // 货币
    SPECIAL         // 特殊资源
}

/**
 * 资源数据模型
 * 
 * 代表游戏中的各种资源，包含资源的基本信息和属性。
 * 使用Room数据库进行持久化存储。
 * 
 * @property id 资源唯一标识符
 * @property type 资源类型
 * @property amount 资源数量
 * @property maxCapacity 最大存储容量
 * @property productionRate 生产速率
 * @property consumptionRate 消耗速率
 * @property lastUpdateTime 最后更新时间
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey
    val id: String,
    val type: ResourceType,
    val amount: Double = 0.0,
    val maxCapacity: Double = 1000.0,
    val productionRate: Double = 0.0,
    val consumptionRate: Double = 0.0,
    val lastUpdateTime: Long = System.currentTimeMillis()
) {
    /**
     * 获取资源类别
     * 
     * @return 资源所属类别
     */
    fun getCategory(): ResourceCategory {
        return when (type) {
            ResourceType.WOOD, ResourceType.STONE, ResourceType.STEEL, ResourceType.FOOD -> ResourceCategory.BASIC
            ResourceType.GOLD, ResourceType.DIAMOND -> ResourceCategory.CURRENCY
            ResourceType.OIL, ResourceType.COAL, ResourceType.ORE, ResourceType.RARE_EARTH, ResourceType.COMPONENT, ResourceType.PROCESSED_FOOD -> ResourceCategory.SPECIAL
        }
    }
    
    /**
     * 获取资源显示名称
     * 
     * @return 资源的中文显示名称
     */
    fun getDisplayName(): String {
        return when (type) {
            ResourceType.WOOD -> "木材"
            ResourceType.STONE -> "石材"
            ResourceType.STEEL -> "钢材"
            ResourceType.FOOD -> "食品"
            ResourceType.GOLD -> "金币"
            ResourceType.DIAMOND -> "钻石"
            ResourceType.OIL -> "石油"
            ResourceType.COAL -> "煤炭"
            ResourceType.ORE -> "矿石"
            ResourceType.RARE_EARTH -> "稀土"
            ResourceType.COMPONENT -> "零部件"
            ResourceType.PROCESSED_FOOD -> "加工食品"
        }
    }
    
    /**
     * 获取资源描述
     * 
     * @return 资源的用途描述
     */
    fun getDescription(): String {
        return when (type) {
            ResourceType.WOOD -> "用于建造住宅和商业建筑"
            ResourceType.STONE -> "用于建造工业和公共建筑"
            ResourceType.STEEL -> "用于建造高级建筑"
            ResourceType.FOOD -> "维持城市人口的基本需求"
            ResourceType.GOLD -> "城市的主要货币，用于各种交易"
            ResourceType.DIAMOND -> "稀有货币，用于特殊购买"
            ResourceType.OIL -> "高级工业原料"
            ResourceType.COAL -> "传统能源资源"
            ResourceType.ORE -> "金属冶炼原料"
            ResourceType.RARE_EARTH -> "高科技产业原料"
            ResourceType.COMPONENT -> "机械制造原料"
            ResourceType.PROCESSED_FOOD -> "高级食品加工品"
        }
    }
    
    /**
     * 获取资源图标资源ID
     * 
     * @return 图标资源ID
     */
    fun getIconResourceId(): String {
        return when (type) {
            ResourceType.WOOD -> "ic_wood"
            ResourceType.STONE -> "ic_stone"
            ResourceType.STEEL -> "ic_steel"
            ResourceType.FOOD -> "ic_food"
            ResourceType.GOLD -> "ic_gold"
            ResourceType.DIAMOND -> "ic_diamond"
            ResourceType.OIL -> "ic_oil"
            ResourceType.COAL -> "ic_coal"
            ResourceType.ORE -> "ic_ore"
            ResourceType.RARE_EARTH -> "ic_rare_earth"
            ResourceType.COMPONENT -> "ic_component"
            ResourceType.PROCESSED_FOOD -> "ic_processed_food"
        }
    }
    
    /**
     * 获取资源单位
     * 
     * @return 资源数量单位
     */
    fun getUnit(): String {
        return when (type) {
            ResourceType.WOOD, ResourceType.STONE, ResourceType.STEEL -> "吨"
            ResourceType.FOOD, ResourceType.PROCESSED_FOOD -> "份"
            ResourceType.GOLD, ResourceType.DIAMOND -> "个"
            ResourceType.OIL, ResourceType.COAL -> "桶"
            ResourceType.ORE, ResourceType.RARE_EARTH -> "千克"
            ResourceType.COMPONENT -> "件"
        }
    }
    
    /**
     * 格式化资源数量显示
     * 
     * @return 格式化后的数量字符串
     */
    fun getFormattedAmount(): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> String.format("%.0f", amount)
        }
    }
    
    /**
     * 检查资源是否充足
     * 
     * @param requiredAmount 所需数量
     * @return 是否充足
     */
    fun hasEnough(requiredAmount: Double): Boolean {
        return amount >= requiredAmount
    }
    
    /**
     * 检查存储是否已满
     * 
     * @return 是否已满
     */
    fun isStorageFull(): Boolean {
        return amount >= maxCapacity
    }
    
    /**
     * 获取存储使用率
     * 
     * @return 存储使用率 (0.0 - 1.0)
     */
    fun getStorageUsage(): Double {
        return amount / maxCapacity
    }
    
    /**
     * 获取净生产速率
     * 
     * @return 净生产速率（生产速率 - 消耗速率）
     */
    fun getNetProductionRate(): Double {
        return productionRate - consumptionRate
    }
    
    /**
     * 计算资源增长
     * 
     * @param timeElapsed 经过的时间（秒）
     * @return 资源增长量
     */
    fun calculateGrowth(timeElapsed: Long): Double {
        val netRate = getNetProductionRate()
        return netRate * timeElapsed / 3600.0 // 转换为小时
    }
}
