package com.citysimulator.game.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.citysimulator.game.data.converter.DateConverter
import java.util.Date

/**
 * 建筑类型枚举
 * 
 * 定义游戏中所有可建造的建筑类型，按功能分类。
 */
enum class BuildingType {
    // 住宅类建筑
    HOUSE,           // 普通住宅
    APARTMENT,       // 公寓
    VILLA,           // 别墅
    SKYSCRAPER,      // 摩天大楼
    
    // 商业类建筑
    SHOP,            // 商店
    SUPERMARKET,     // 超市
    MALL,            // 商场
    RESTAURANT,      // 餐厅
    HOTEL,           // 酒店
    
    // 工业生产建筑
    FARM,            // 农场 - 生产粮食
    LUMBER_MILL,     // 木材厂 - 生产木材
    QUARRY,          // 采石场 - 生产石料
    STEEL_MILL,      // 钢铁厂 - 生产钢材
    FOOD_FACTORY,    // 食品厂 - 加工食品
    BAKERY,          // 面包房 - 用粮食生产高级食物
    SAWMILL,         // 锯木厂 - 用木材生产板材
    POWER_PLANT,     // 发电厂
    
    // 公共设施
    SCHOOL,          // 学校
    HOSPITAL,        // 医院
    POLICE_STATION,  // 警察局
    FIRE_STATION,    // 消防局
    PARK,            // 公园 - 提升繁荣度
    LIBRARY,         // 图书馆 - 提升教育水平
    
    // 交通设施
    ROAD,            // 道路
    BRIDGE,          // 桥梁
    BUS_STOP,        // 公交站
    SUBWAY_STATION,  // 地铁站
    
    // 工业建筑
    FACTORY,         // 工厂
    
    // 能源建筑
    SOLAR_PLANT,     // 太阳能发电厂
    WIND_FARM,       // 风力发电厂
    
    // 环保建筑
    RECYCLING_CENTER,      // 回收中心
    WASTE_MANAGEMENT,      // 垃圾管理中心
    
    // 水利设施
    WATER_TOWER,           // 水塔
    WATER_TREATMENT_PLANT, // 水处理厂
    
    // 金融建筑
    BANK,                  // 银行
    
    // 办公建筑
    OFFICE,                // 办公楼
    
    // 体育建筑
    STADIUM,               // 体育场
    
    // 智能建筑
    SMART_CENTER,    // 智慧中心
    
    // 教育建筑
    UNIVERSITY,      // 大学
    
    // 文化建筑
    MUSEUM,          // 博物馆
    THEATER,         // 剧院
    
    // 高级建筑
    SPACE_CENTER,    // 太空中心
    AI_CENTER;       // AI中心
    
    /**
     * 获取建筑类型的显示名称
     */
    fun getDisplayName(): String {
        return when (this) {
            HOUSE -> "住宅"
            APARTMENT -> "公寓"
            VILLA -> "别墅"
            SKYSCRAPER -> "摩天大楼"
            SHOP -> "商店"
            SUPERMARKET -> "超市"
            MALL -> "商场"
            RESTAURANT -> "餐厅"
            HOTEL -> "酒店"
            FARM -> "农场"
            LUMBER_MILL -> "木材厂"
            QUARRY -> "采石场"
            STEEL_MILL -> "钢铁厂"
            FOOD_FACTORY -> "食品厂"
            BAKERY -> "面包房"
            SAWMILL -> "锯木厂"
            POWER_PLANT -> "发电厂"
            SCHOOL -> "学校"
            HOSPITAL -> "医院"
            POLICE_STATION -> "警察局"
            FIRE_STATION -> "消防局"
            PARK -> "公园"
            LIBRARY -> "图书馆"
            ROAD -> "道路"
            BRIDGE -> "桥梁"
            BUS_STOP -> "公交站"
            SUBWAY_STATION -> "地铁站"
            FACTORY -> "工厂"
            SOLAR_PLANT -> "太阳能发电厂"
            WIND_FARM -> "风力发电厂"
            RECYCLING_CENTER -> "回收中心"
            WASTE_MANAGEMENT -> "垃圾管理中心"
            WATER_TOWER -> "水塔"
            WATER_TREATMENT_PLANT -> "水处理厂"
            BANK -> "银行"
            OFFICE -> "办公楼"
            STADIUM -> "体育场"
            SMART_CENTER -> "智慧中心"
            UNIVERSITY -> "大学"
            MUSEUM -> "博物馆"
            THEATER -> "剧院"
            SPACE_CENTER -> "太空中心"
            AI_CENTER -> "AI中心"
        }
    }
}

/**
 * 建筑类别枚举
 * 
 * 将建筑按功能进行分类，便于管理和显示。
 */
enum class BuildingCategory {
    RESIDENTIAL,     // 住宅
    COMMERCIAL,      // 商业
    INDUSTRIAL,      // 工业
    PUBLIC,          // 公共设施
    TRANSPORTATION   // 交通设施
}

/**
 * 建筑状态枚举
 * 
 * 定义建筑可能处于的各种状态。
 */
enum class BuildingStatus {
    NORMAL,          // 正常
    CONSTRUCTION,    // 建设中
    UPGRADING,       // 升级中
    MAINTENANCE,     // 需要维护
    DAMAGED          // 损坏
}

/**
 * 建筑位置数据类
 * 
 * 表示建筑在城市网格中的位置。
 */
data class BuildingPosition(
    val x: Int,
    val y: Int
)

/**
 * 建筑数据模型
 * 
 * 代表城市中的建筑物，包含建筑的基本信息和状态。
 * 使用Room数据库进行持久化存储。
 * 
 * @property id 建筑唯一标识符
 * @property type 建筑类型
 * @property level 建筑等级
 * @property position 建筑位置
 * @property buildTime 建造时间
 * @property status 建筑状态
 * @property isUnderConstruction 是否正在建造
 * @property isUpgrading 是否正在升级
 * @property isMaintenanceRequired 是否需要维护
 * @property lastMaintenanceDate 最后维护日期
 * @property efficiency 建筑效率
 * @property capacity 建筑容量
 * @property income 建筑收入
 * @property maintenanceCost 维护成本
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "buildings")
@TypeConverters(DateConverter::class)
data class Building(
    @PrimaryKey
    val id: String,
    val type: BuildingType,
    val level: Int = 1,
    @Embedded(prefix = "position_")
    val position: BuildingPosition,
    val buildTime: Date = Date(),
    val status: BuildingStatus = BuildingStatus.NORMAL,
    val isUnderConstruction: Boolean = false,
    val isUpgrading: Boolean = false,
    val isMaintenanceRequired: Boolean = false,
    val lastMaintenanceDate: Date = Date(),
    val efficiency: Float = 1.0f,
    val capacity: Int = 0,
    val income: Int = 0,
    val maintenanceCost: Int = 0,
    val customName: String? = null  // 自定义名称，用于简化建筑系统
) {
    /**
     * 获取建筑类别
     * 
     * @return 建筑所属类别
     */
    fun getCategory(): BuildingCategory {
        return when (type) {
            BuildingType.HOUSE, BuildingType.APARTMENT, BuildingType.VILLA, BuildingType.SKYSCRAPER -> BuildingCategory.RESIDENTIAL
            BuildingType.SHOP, BuildingType.SUPERMARKET, BuildingType.MALL, BuildingType.RESTAURANT, BuildingType.HOTEL, BuildingType.BANK, BuildingType.OFFICE -> BuildingCategory.COMMERCIAL
            BuildingType.FARM, BuildingType.LUMBER_MILL, BuildingType.QUARRY, BuildingType.STEEL_MILL, BuildingType.FOOD_FACTORY, BuildingType.BAKERY, BuildingType.SAWMILL, BuildingType.POWER_PLANT, BuildingType.FACTORY, BuildingType.SOLAR_PLANT, BuildingType.WIND_FARM, BuildingType.RECYCLING_CENTER, BuildingType.WASTE_MANAGEMENT, BuildingType.WATER_TOWER, BuildingType.WATER_TREATMENT_PLANT -> BuildingCategory.INDUSTRIAL
            BuildingType.SCHOOL, BuildingType.HOSPITAL, BuildingType.POLICE_STATION, BuildingType.FIRE_STATION, BuildingType.PARK, BuildingType.LIBRARY, BuildingType.UNIVERSITY, BuildingType.MUSEUM, BuildingType.THEATER, BuildingType.SMART_CENTER, BuildingType.SPACE_CENTER, BuildingType.AI_CENTER, BuildingType.STADIUM -> BuildingCategory.PUBLIC
            BuildingType.ROAD, BuildingType.BRIDGE, BuildingType.BUS_STOP, BuildingType.SUBWAY_STATION -> BuildingCategory.TRANSPORTATION
        }
    }
    
    /**
     * 获取建筑显示名称
     * 
     * @return 建筑的中文显示名称（优先使用自定义名称）
     */
    fun getDisplayName(): String {
        // 如果有自定义名称，优先使用
        if (!customName.isNullOrEmpty()) {
            return customName
        }
        
        // 否则使用BuildingType的默认名称
        val defaultName = when (type) {
            BuildingType.HOUSE -> "住宅"
            BuildingType.APARTMENT -> "公寓"
            BuildingType.VILLA -> "别墅"
            BuildingType.SKYSCRAPER -> "摩天大楼"
            BuildingType.SHOP -> "商店"
            BuildingType.SUPERMARKET -> "超市"
            BuildingType.MALL -> "商场"
            BuildingType.RESTAURANT -> "餐厅"
            BuildingType.HOTEL -> "酒店"
            BuildingType.FARM -> "农场"
            BuildingType.LUMBER_MILL -> "木材厂"
            BuildingType.QUARRY -> "采石场"
            BuildingType.STEEL_MILL -> "钢铁厂"
            BuildingType.FOOD_FACTORY -> "食品厂"
            BuildingType.BAKERY -> "面包房"
            BuildingType.SAWMILL -> "锯木厂"
            BuildingType.POWER_PLANT -> "发电厂"
            BuildingType.SCHOOL -> "学校"
            BuildingType.HOSPITAL -> "医院"
            BuildingType.POLICE_STATION -> "警察局"
            BuildingType.FIRE_STATION -> "消防局"
            BuildingType.PARK -> "公园"
            BuildingType.LIBRARY -> "图书馆"
            BuildingType.ROAD -> "道路"
            BuildingType.BRIDGE -> "桥梁"
            BuildingType.BUS_STOP -> "公交站"
            BuildingType.SUBWAY_STATION -> "地铁站"
            BuildingType.FACTORY -> "工厂"
            BuildingType.SOLAR_PLANT -> "太阳能发电厂"
            BuildingType.WIND_FARM -> "风力发电厂"
            BuildingType.RECYCLING_CENTER -> "回收中心"
            BuildingType.SMART_CENTER -> "智慧中心"
            BuildingType.UNIVERSITY -> "大学"
            BuildingType.MUSEUM -> "博物馆"
            BuildingType.THEATER -> "剧院"
            BuildingType.SPACE_CENTER -> "太空中心"
            BuildingType.AI_CENTER -> "AI中心"
            BuildingType.WASTE_MANAGEMENT -> "垃圾管理中心"
            BuildingType.WATER_TOWER -> "水塔"
            BuildingType.WATER_TREATMENT_PLANT -> "水处理厂"
            BuildingType.BANK -> "银行"
            BuildingType.OFFICE -> "办公楼"
            BuildingType.STADIUM -> "体育场"
        }
        return defaultName
    }
    
    /**
     * 获取建筑描述
     * 
     * @return 建筑的功能描述
     */
    fun getDescription(): String {
        return when (type) {
            BuildingType.HOUSE -> "为居民提供住所，增加城市人口"
            BuildingType.APARTMENT -> "高效的住宅建筑，容纳更多居民"
            BuildingType.VILLA -> "豪华住宅，提升居民幸福感"
            BuildingType.SKYSCRAPER -> "现代化高层建筑，容纳大量居民"
            BuildingType.SHOP -> "提供日常商品，增加城市收入"
            BuildingType.SUPERMARKET -> "大型购物场所，提升商业收入"
            BuildingType.MALL -> "综合性商业中心，大幅增加收入"
            BuildingType.RESTAURANT -> "提供餐饮服务，增加收入"
            BuildingType.HOTEL -> "接待游客，增加旅游收入"
            BuildingType.FARM -> "生产粮食资源，供应城市食物"
            BuildingType.LUMBER_MILL -> "生产木材资源"
            BuildingType.QUARRY -> "开采石材资源"
            BuildingType.STEEL_MILL -> "生产钢材资源"
            BuildingType.FOOD_FACTORY -> "生产食品资源"
            BuildingType.BAKERY -> "将粮食加工成高级食物，提升繁荣度"
            BuildingType.SAWMILL -> "将木材加工成板材，用于建造高级建筑"
            BuildingType.POWER_PLANT -> "为城市提供电力"
            BuildingType.SCHOOL -> "提升居民教育水平"
            BuildingType.HOSPITAL -> "提供医疗服务，提升健康指数"
            BuildingType.POLICE_STATION -> "维护城市安全"
            BuildingType.FIRE_STATION -> "提供消防服务"
            BuildingType.PARK -> "提升环境质量和居民幸福感"
            BuildingType.LIBRARY -> "提升居民教育水平"
            BuildingType.ROAD -> "连接城市各个区域"
            BuildingType.BRIDGE -> "跨越河流和障碍"
            BuildingType.BUS_STOP -> "提供公共交通服务"
            BuildingType.SUBWAY_STATION -> "高效的地铁交通"
            BuildingType.FACTORY -> "生产工业产品，增加收入"
            BuildingType.SOLAR_PLANT -> "清洁能源发电，减少污染"
            BuildingType.WIND_FARM -> "风力发电，环保节能"
            BuildingType.RECYCLING_CENTER -> "处理废物，保护环境"
            BuildingType.SMART_CENTER -> "智慧城市管理，提升效率"
            BuildingType.UNIVERSITY -> "高等教育机构，培养人才"
            BuildingType.MUSEUM -> "文化展示，提升城市品位"
            BuildingType.THEATER -> "艺术表演，丰富文化生活"
            BuildingType.SPACE_CENTER -> "太空科技，引领未来"
            BuildingType.AI_CENTER -> "人工智能，智能管理"
            BuildingType.WASTE_MANAGEMENT -> "垃圾管理中心，处理废弃物"
            BuildingType.WATER_TOWER -> "水塔，提供城市供水"
            BuildingType.WATER_TREATMENT_PLANT -> "水处理厂，净化供水"
            BuildingType.BANK -> "银行，提供金融服务"
            BuildingType.OFFICE -> "办公楼，提供商业办公空间"
            BuildingType.STADIUM -> "体育场，举办体育赛事"
        }
    }
    
    /**
     * 获取建筑图标资源ID
     * 
     * @return 图标资源ID
     */
    fun getIconResourceId(): String {
        return when (type) {
            BuildingType.HOUSE -> "ic_house"
            BuildingType.APARTMENT -> "ic_apartment"
            BuildingType.VILLA -> "ic_villa"
            BuildingType.SKYSCRAPER -> "ic_skyscraper"
            BuildingType.SHOP -> "ic_shop"
            BuildingType.SUPERMARKET -> "ic_supermarket"
            BuildingType.MALL -> "ic_mall"
            BuildingType.RESTAURANT -> "ic_restaurant"
            BuildingType.HOTEL -> "ic_hotel"
            BuildingType.FARM -> "ic_farm"
            BuildingType.LUMBER_MILL -> "ic_lumber_mill"
            BuildingType.QUARRY -> "ic_quarry"
            BuildingType.STEEL_MILL -> "ic_steel_mill"
            BuildingType.FOOD_FACTORY -> "ic_food_factory"
            BuildingType.BAKERY -> "ic_bakery"
            BuildingType.SAWMILL -> "ic_sawmill"
            BuildingType.POWER_PLANT -> "ic_power_plant"
            BuildingType.SCHOOL -> "ic_school"
            BuildingType.HOSPITAL -> "ic_hospital"
            BuildingType.POLICE_STATION -> "ic_police_station"
            BuildingType.FIRE_STATION -> "ic_fire_station"
            BuildingType.PARK -> "ic_park"
            BuildingType.LIBRARY -> "ic_library"
            BuildingType.ROAD -> "ic_road"
            BuildingType.BRIDGE -> "ic_bridge"
            BuildingType.BUS_STOP -> "ic_bus_stop"
            BuildingType.SUBWAY_STATION -> "ic_subway_station"
            BuildingType.FACTORY -> "ic_factory"
            BuildingType.SOLAR_PLANT -> "ic_solar_plant"
            BuildingType.WIND_FARM -> "ic_wind_farm"
            BuildingType.RECYCLING_CENTER -> "ic_recycling_center"
            BuildingType.SMART_CENTER -> "ic_smart_center"
            BuildingType.UNIVERSITY -> "ic_university"
            BuildingType.MUSEUM -> "ic_museum"
            BuildingType.THEATER -> "ic_theater"
            BuildingType.SPACE_CENTER -> "ic_space_center"
            BuildingType.AI_CENTER -> "ic_ai_center"
            BuildingType.WASTE_MANAGEMENT -> "ic_waste_management"
            BuildingType.WATER_TOWER -> "ic_water_tower"
            BuildingType.WATER_TREATMENT_PLANT -> "ic_water_treatment"
            BuildingType.BANK -> "ic_bank"
            BuildingType.OFFICE -> "ic_office"
            BuildingType.STADIUM -> "ic_stadium"
        }
    }
    
    /**
     * 获取升级所需时间（秒）
     * 
     * @return 升级所需时间
     */
    fun getUpgradeTime(): Long {
        return level * 3600L // 每级增加1小时
    }
    
    /**
     * 获取维护间隔时间（秒）
     * 
     * @return 维护间隔时间
     */
    fun getMaintenanceInterval(): Long {
        return 7 * 24 * 3600L // 7天
    }
    
    /**
     * 检查是否需要维护
     * 
     * @return 是否需要维护
     */
    fun needsMaintenance(): Boolean {
        val now = Date()
        val timeSinceLastMaintenance = now.time - lastMaintenanceDate.time
        return timeSinceLastMaintenance > getMaintenanceInterval()
    }
}
