package com.citysimulator.game.data.model

/**
 * 简化的建筑系统
 * 
 * 设计理念：
 * - 易于理解、功能清晰、有成长感
 * - 4大核心类别：住宅、经济、公共服务、道路装饰
 * - 每个建筑2-3个升级阶段
 * - 明确的成本和收益
 * 
 * @author AI进化论-花生
 * @since 2.0
 */

/**
 * 简化的建筑类型枚举
 * 按照"四大支柱"设计
 */
enum class SimplifiedBuildingType {
    // ==================== 1. 住宅区 ====================
    SMALL_HOUSE,        // 小木屋 (初级) - 容纳5人
    APARTMENT,          // 公寓楼 (中级) - 容纳15人
    MODERN_RESIDENCE,   // 现代化住宅 (高级) - 容纳30人
    
    // ==================== 2. 经济建筑 ====================
    // 初级经济
    LEMONADE_STAND,     // 柠檬水摊 - 初始赚钱建筑
    SMALL_FARM,         // 小农场 - 生产食物
    
    // 中级经济
    CONVENIENCE_STORE,  // 便利店 - 中级商店
    FOOD_FACTORY,       // 食品加工厂 - 加工食物
    
    // 高级经济
    SHOPPING_MALL,      // 购物中心 - 高级商业
    TECH_PARK,          // 科技园区 - 最高收入
    
    // ==================== 3. 公共服务 (四大件) ====================
    // 电力
    WINDMILL,           // 风车 (初级电力)
    COAL_PLANT,         // 火电厂 (中级电力)
    SOLAR_PLANT,        // 太阳能电站 (高级电力)
    
    // 水源
    WATER_WELL,         // 水井 (初级供水)
    WATER_PUMP,         // 水泵站 (中级供水)
    WATER_PURIFIER,     // 水净化厂 (高级供水)
    
    // 垃圾处理
    GARBAGE_DUMP,       // 垃圾堆 (初级)
    RECYCLING_CENTER,   // 回收中心 (中级)
    ECO_FACILITY,       // 环保处理厂 (高级)
    
    // 安全与健康
    SMALL_CLINIC,       // 小诊所 (初级医疗)
    HOSPITAL,           // 医院 (高级医疗)
    POLICE_STATION,     // 派出所/警察局
    
    // ==================== 4. 道路与装饰 ====================
    DIRT_ROAD,          // 土路 (初级)
    PAVED_ROAD,         // 柏油路 (中级)
    TREE_LINED_ROAD,    // 林荫大道 (高级)
    
    SMALL_PARK,         // 小公园 - 提升周边满意度
    PLAZA,              // 广场 - 更大范围提升
    FOUNTAIN;           // 喷泉 - 装饰
    
    /**
     * 获取建筑的显示名称
     */
    fun getDisplayName(): String {
        return when (this) {
            // 住宅
            SMALL_HOUSE -> "小木屋"
            APARTMENT -> "公寓楼"
            MODERN_RESIDENCE -> "现代化住宅"
            
            // 经济
            LEMONADE_STAND -> "柠檬水摊"
            SMALL_FARM -> "小农场"
            CONVENIENCE_STORE -> "便利店"
            FOOD_FACTORY -> "食品加工厂"
            SHOPPING_MALL -> "购物中心"
            TECH_PARK -> "科技园区"
            
            // 电力
            WINDMILL -> "风车"
            COAL_PLANT -> "火电厂"
            SOLAR_PLANT -> "太阳能电站"
            
            // 水源
            WATER_WELL -> "水井"
            WATER_PUMP -> "水泵站"
            WATER_PURIFIER -> "水净化厂"
            
            // 垃圾
            GARBAGE_DUMP -> "垃圾堆"
            RECYCLING_CENTER -> "回收中心"
            ECO_FACILITY -> "环保处理厂"
            
            // 医疗安全
            SMALL_CLINIC -> "小诊所"
            HOSPITAL -> "医院"
            POLICE_STATION -> "警察局"
            
            // 道路
            DIRT_ROAD -> "土路"
            PAVED_ROAD -> "柏油路"
            TREE_LINED_ROAD -> "林荫大道"
            
            // 装饰
            SMALL_PARK -> "小公园"
            PLAZA -> "广场"
            FOUNTAIN -> "喷泉"
        }
    }
    
    /**
     * 获取建筑的简短描述
     */
    fun getDescription(): String {
        return when (this) {
            // 住宅
            SMALL_HOUSE -> "基础住房，容纳5人"
            APARTMENT -> "中级住房，容纳15人"
            MODERN_RESIDENCE -> "高级住房，容纳30人"
            
            // 经济
            LEMONADE_STAND -> "最简单的赚钱方式 💰+5/分钟"
            SMALL_FARM -> "生产食物 🌾"
            CONVENIENCE_STORE -> "中级商店 💰+15/分钟"
            FOOD_FACTORY -> "加工食物提升收入 💰+25/分钟"
            SHOPPING_MALL -> "大型商业中心 💰+50/分钟"
            TECH_PARK -> "高科技产业 💰+100/分钟"
            
            // 电力
            WINDMILL -> "提供50⚡电力"
            COAL_PLANT -> "提供150⚡电力"
            SOLAR_PLANT -> "提供300⚡清洁电力"
            
            // 水源
            WATER_WELL -> "提供50💧水源"
            WATER_PUMP -> "提供150💧水源"
            WATER_PURIFIER -> "提供300💧净化水"
            
            // 垃圾
            GARBAGE_DUMP -> "处理50🗑垃圾（会降低周边满意度）"
            RECYCLING_CENTER -> "处理150🗑垃圾并回收"
            ECO_FACILITY -> "处理300🗑垃圾，环保无污染"
            
            // 医疗安全
            SMALL_CLINIC -> "提供基础医疗服务"
            HOSPITAL -> "提供完善医疗服务"
            POLICE_STATION -> "维护治安，提升安全感"
            
            // 道路
            DIRT_ROAD -> "基础道路"
            PAVED_ROAD -> "中级道路，提升通行速度"
            TREE_LINED_ROAD -> "高级道路，美观且高效"
            
            // 装饰
            SMALL_PARK -> "提升周边3格满意度+10"
            PLAZA -> "提升周边5格满意度+20"
            FOUNTAIN -> "美观装饰，满意度+5"
        }
    }
    
    /**
     * 获取建筑成本
     */
    fun getBuildCost(): Int {
        return when (this) {
            // 住宅 (越高级越贵)
            SMALL_HOUSE -> 50
            APARTMENT -> 200
            MODERN_RESIDENCE -> 800
            
            // 经济 (投资越大收益越高)
            LEMONADE_STAND -> 30
            SMALL_FARM -> 100
            CONVENIENCE_STORE -> 300
            FOOD_FACTORY -> 600
            SHOPPING_MALL -> 1500
            TECH_PARK -> 3000
            
            // 公共服务 (必需品，价格适中)
            WINDMILL -> 150
            COAL_PLANT -> 500
            SOLAR_PLANT -> 1200
            
            WATER_WELL -> 100
            WATER_PUMP -> 400
            WATER_PURIFIER -> 1000
            
            GARBAGE_DUMP -> 80
            RECYCLING_CENTER -> 350
            ECO_FACILITY -> 900
            
            SMALL_CLINIC -> 200
            HOSPITAL -> 800
            POLICE_STATION -> 300
            
            // 道路和装饰 (便宜但重要)
            DIRT_ROAD -> 10
            PAVED_ROAD -> 30
            TREE_LINED_ROAD -> 80
            
            SMALL_PARK -> 150
            PLAZA -> 400
            FOUNTAIN -> 100
        }
    }
    
    /**
     * 获取每30秒收入（负数表示维护成本）
     * 游戏内1个月 = 现实30秒
     */
    fun getMonthlyIncome(): Int {
        return when (this) {
            // 住宅 (维护成本)
            SMALL_HOUSE -> -1
            APARTMENT -> -3
            MODERN_RESIDENCE -> -8
            
            // 经济 (主要收入来源)
            LEMONADE_STAND -> 5
            SMALL_FARM -> 8
            CONVENIENCE_STORE -> 15
            FOOD_FACTORY -> 25
            SHOPPING_MALL -> 50
            TECH_PARK -> 100
            
            // 公共服务 (维护成本)
            WINDMILL, COAL_PLANT, SOLAR_PLANT -> -5
            WATER_WELL, WATER_PUMP, WATER_PURIFIER -> -3
            GARBAGE_DUMP, RECYCLING_CENTER, ECO_FACILITY -> -4
            SMALL_CLINIC -> -5
            HOSPITAL -> -10
            POLICE_STATION -> -8
            
            // 道路和装饰 (低维护成本)
            DIRT_ROAD, PAVED_ROAD, TREE_LINED_ROAD -> 0
            SMALL_PARK, PLAZA, FOUNTAIN -> -2
        }
    }
    
    /**
     * 获取建筑类别
     */
    fun getCategory(): SimplifiedBuildingCategory {
        return when (this) {
            SMALL_HOUSE, APARTMENT, MODERN_RESIDENCE -> 
                SimplifiedBuildingCategory.RESIDENTIAL
            
            LEMONADE_STAND, SMALL_FARM, CONVENIENCE_STORE, 
            FOOD_FACTORY, SHOPPING_MALL, TECH_PARK -> 
                SimplifiedBuildingCategory.ECONOMIC
            
            WINDMILL, COAL_PLANT, SOLAR_PLANT,
            WATER_WELL, WATER_PUMP, WATER_PURIFIER,
            GARBAGE_DUMP, RECYCLING_CENTER, ECO_FACILITY,
            SMALL_CLINIC, HOSPITAL, POLICE_STATION -> 
                SimplifiedBuildingCategory.PUBLIC_SERVICE
            
            DIRT_ROAD, PAVED_ROAD, TREE_LINED_ROAD,
            SMALL_PARK, PLAZA, FOUNTAIN -> 
                SimplifiedBuildingCategory.INFRASTRUCTURE
        }
    }
    
    /**
     * 获取建筑可以升级到的下一级（如果有）
     */
    fun getUpgradeTo(): SimplifiedBuildingType? {
        return when (this) {
            // 住宅升级链
            SMALL_HOUSE -> APARTMENT
            APARTMENT -> MODERN_RESIDENCE
            
            // 经济升级链
            LEMONADE_STAND -> CONVENIENCE_STORE
            CONVENIENCE_STORE -> SHOPPING_MALL
            SMALL_FARM -> FOOD_FACTORY
            FOOD_FACTORY -> TECH_PARK
            
            // 电力升级链
            WINDMILL -> COAL_PLANT
            COAL_PLANT -> SOLAR_PLANT
            
            // 水源升级链
            WATER_WELL -> WATER_PUMP
            WATER_PUMP -> WATER_PURIFIER
            
            // 垃圾升级链
            GARBAGE_DUMP -> RECYCLING_CENTER
            RECYCLING_CENTER -> ECO_FACILITY
            
            // 医疗升级链
            SMALL_CLINIC -> HOSPITAL
            
            // 道路升级链
            DIRT_ROAD -> PAVED_ROAD
            PAVED_ROAD -> TREE_LINED_ROAD
            
            // 装饰升级链
            SMALL_PARK -> PLAZA
            
            // 已经是最高级或不可升级
            else -> null
        }
    }
    
    /**
     * 获取升级成本
     */
    fun getUpgradeCost(): Int? {
        return getUpgradeTo()?.getBuildCost()
    }
    
    /**
     * 获取建筑的Emoji图标
     */
    fun getEmoji(): String {
        return when (this) {
            // 住宅
            SMALL_HOUSE -> "🏠"
            APARTMENT -> "🏢"
            MODERN_RESIDENCE -> "🏙️"
            
            // 经济
            LEMONADE_STAND -> "🍋"
            SMALL_FARM -> "🌾"
            CONVENIENCE_STORE -> "🏪"
            FOOD_FACTORY -> "🏭"
            SHOPPING_MALL -> "🛒"
            TECH_PARK -> "💻"
            
            // 能源
            WINDMILL -> "🌀"
            COAL_PLANT -> "⚡"
            SOLAR_PLANT -> "☀️"
            
            // 水源
            WATER_WELL -> "🚰"
            WATER_PUMP -> "💧"
            WATER_PURIFIER -> "💦"
            
            // 垃圾
            GARBAGE_DUMP -> "🗑️"
            RECYCLING_CENTER -> "♻️"
            ECO_FACILITY -> "🌱"
            
            // 医疗安全
            SMALL_CLINIC -> "🏥"
            HOSPITAL -> "🏨"
            POLICE_STATION -> "👮"
            
            // 道路
            DIRT_ROAD -> "🛤️"
            PAVED_ROAD -> "🛣️"
            TREE_LINED_ROAD -> "🌳"
            
            // 装饰
            SMALL_PARK -> "🌲"
            PLAZA -> "🎪"
            FOUNTAIN -> "⛲"
        }
    }
}

/**
 * 简化的建筑类别
 */
enum class SimplifiedBuildingCategory {
    RESIDENTIAL,      // 住宅区 - 提供人口
    ECONOMIC,         // 经济建筑 - 赚钱
    PUBLIC_SERVICE,   // 公共服务 - 满足需求
    INFRASTRUCTURE;   // 道路装饰 - 连接和美化
    
    fun getDisplayName(): String {
        return when (this) {
            RESIDENTIAL -> "住宅区"
            ECONOMIC -> "经济建筑"
            PUBLIC_SERVICE -> "公共服务"
            INFRASTRUCTURE -> "基础设施"
        }
    }
    
    fun getEmoji(): String {
        return when (this) {
            RESIDENTIAL -> "🏠"
            ECONOMIC -> "💰"
            PUBLIC_SERVICE -> "🏥"
            INFRASTRUCTURE -> "🛣️"
        }
    }
}

