//
//  Building.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation

/// 建筑类型枚举
enum BuildingType: String, CaseIterable, Codable {
    // 住宅建筑
    case house = "house"
    case apartment = "apartment"
    case villa = "villa"
    case skyscraper = "skyscraper"
    
    // 商业建筑
    case shop = "shop"
    case supermarket = "supermarket"
    case mall = "mall"
    case restaurant = "restaurant"
    case hotel = "hotel"
    
    // 工业建筑
    case lumberMill = "lumberMill"
    case quarry = "quarry"
    case steelMill = "steelMill"
    case foodFactory = "foodFactory"
    case powerPlant = "powerPlant"
    
    // 公共设施
    case school = "school"
    case hospital = "hospital"
    case policeStation = "policeStation"
    case fireStation = "fireStation"
    case park = "park"
    case library = "library"
    
    // 交通设施
    case road = "road"
    case bridge = "bridge"
    case busStop = "busStop"
    case subwayStation = "subwayStation"
    
    /// 获取建筑类型的中文名称
    var displayName: String {
        switch self {
        case .house: return "普通住宅"
        case .apartment: return "公寓楼"
        case .villa: return "别墅"
        case .skyscraper: return "摩天大楼"
        case .shop: return "商店"
        case .supermarket: return "超市"
        case .mall: return "购物中心"
        case .restaurant: return "餐厅"
        case .hotel: return "酒店"
        case .lumberMill: return "木材厂"
        case .quarry: return "采石场"
        case .steelMill: return "钢铁厂"
        case .foodFactory: return "食品加工厂"
        case .powerPlant: return "发电厂"
        case .school: return "学校"
        case .hospital: return "医院"
        case .policeStation: return "警察局"
        case .fireStation: return "消防局"
        case .park: return "公园"
        case .library: return "图书馆"
        case .road: return "道路"
        case .bridge: return "桥梁"
        case .busStop: return "公交站"
        case .subwayStation: return "地铁站"
        }
    }
    
    /// 获取建筑类型分类
    var category: BuildingCategory {
        switch self {
        case .house, .apartment, .villa, .skyscraper:
            return .residential
        case .shop, .supermarket, .mall, .restaurant, .hotel:
            return .commercial
        case .lumberMill, .quarry, .steelMill, .foodFactory, .powerPlant:
            return .industrial
        case .school, .hospital, .policeStation, .fireStation, .park, .library:
            return .public
        case .road, .bridge, .busStop, .subwayStation:
            return .transportation
        }
    }
    
    /// 获取建筑解锁等级
    var unlockLevel: Int {
        switch self {
        case .house: return 1
        case .apartment: return 3
        case .villa: return 8
        case .skyscraper: return 15
        case .shop: return 2
        case .supermarket: return 5
        case .mall: return 12
        case .restaurant: return 4
        case .hotel: return 10
        case .lumberMill: return 3
        case .quarry: return 3
        case .steelMill: return 6
        case .foodFactory: return 5
        case .powerPlant: return 8
        case .school: return 4
        case .hospital: return 6
        case .policeStation: return 5
        case .fireStation: return 5
        case .park: return 2
        case .library: return 7
        case .road: return 1
        case .bridge: return 9
        case .busStop: return 6
        case .subwayStation: return 12
        }
    }
}

/// 建筑分类枚举
enum BuildingCategory: String, CaseIterable {
    case residential = "residential"
    case commercial = "commercial"
    case industrial = "industrial"
    case public = "public"
    case transportation = "transportation"
    
    var displayName: String {
        switch self {
        case .residential: return "住宅区"
        case .commercial: return "商业区"
        case .industrial: return "工业区"
        case .public: return "公共设施"
        case .transportation: return "交通设施"
        }
    }
}

/// 建筑数据模型
struct Building: Codable, Identifiable {
    let id = UUID()
    var type: BuildingType
    var level: Int
    var position: GridPosition
    var buildTime: Date
    var lastUpgradeTime: Date?
    var lastMaintenanceTime: Date
    
    // 建筑属性
    var capacity: Int = 0
    var income: Double = 0.0
    var maintenanceCost: Double = 0.0
    var happinessBonus: Double = 0.0
    var pollutionLevel: Double = 0.0
    
    // 资源生产/消耗
    var productionRate: [ResourceType: Double]?
    var consumptionRate: [ResourceType: Double]?
    
    // 建筑状态
    var isUnderConstruction: Bool = false
    var isMaintenanceRequired: Bool = false
    var isUpgrading: Bool = false
    
    init(type: BuildingType, level: Int = 1, position: GridPosition) {
        self.type = type
        self.level = level
        self.position = position
        self.buildTime = Date()
        self.lastMaintenanceTime = Date()
        
        // 根据建筑类型设置初始属性
        updateBuildingProperties()
    }
    
    /// 更新建筑属性
    mutating func updateBuildingProperties() {
        switch type {
        case .house:
            capacity = 4 * level
            income = 0.0
            maintenanceCost = 10.0 * Double(level)
            happinessBonus = 5.0 * Double(level)
            
        case .apartment:
            capacity = 8 * level
            income = 0.0
            maintenanceCost = 15.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
            
        case .villa:
            capacity = 2 * level
            income = 0.0
            maintenanceCost = 25.0 * Double(level)
            happinessBonus = 10.0 * Double(level)
            
        case .skyscraper:
            capacity = 20 * level
            income = 0.0
            maintenanceCost = 50.0 * Double(level)
            happinessBonus = 2.0 * Double(level)
            
        case .shop:
            capacity = 0
            income = 50.0 * Double(level)
            maintenanceCost = 20.0 * Double(level)
            happinessBonus = 2.0 * Double(level)
            
        case .supermarket:
            capacity = 0
            income = 100.0 * Double(level)
            maintenanceCost = 30.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
            
        case .mall:
            capacity = 0
            income = 300.0 * Double(level)
            maintenanceCost = 80.0 * Double(level)
            happinessBonus = 5.0 * Double(level)
            
        case .restaurant:
            capacity = 0
            income = 80.0 * Double(level)
            maintenanceCost = 25.0 * Double(level)
            happinessBonus = 4.0 * Double(level)
            
        case .hotel:
            capacity = 0
            income = 200.0 * Double(level)
            maintenanceCost = 60.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
            
        case .lumberMill:
            capacity = 0
            income = 0.0
            maintenanceCost = 40.0 * Double(level)
            happinessBonus = -1.0 * Double(level)
            pollutionLevel = 2.0 * Double(level)
            productionRate = [.wood: 10.0 * Double(level)]
            
        case .quarry:
            capacity = 0
            income = 0.0
            maintenanceCost = 35.0 * Double(level)
            happinessBonus = -1.0 * Double(level)
            pollutionLevel = 2.0 * Double(level)
            productionRate = [.stone: 8.0 * Double(level)]
            
        case .steelMill:
            capacity = 0
            income = 0.0
            maintenanceCost = 60.0 * Double(level)
            happinessBonus = -2.0 * Double(level)
            pollutionLevel = 3.0 * Double(level)
            productionRate = [.steel: 5.0 * Double(level)]
            
        case .foodFactory:
            capacity = 0
            income = 0.0
            maintenanceCost = 30.0 * Double(level)
            happinessBonus = 1.0 * Double(level)
            pollutionLevel = 1.0 * Double(level)
            productionRate = [.food: 15.0 * Double(level)]
            
        case .powerPlant:
            capacity = 0
            income = 0.0
            maintenanceCost = 100.0 * Double(level)
            happinessBonus = -3.0 * Double(level)
            pollutionLevel = 5.0 * Double(level)
            productionRate = [.energy: 20.0 * Double(level)]
            
        case .school:
            capacity = 0
            income = 0.0
            maintenanceCost = 50.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
            
        case .hospital:
            capacity = 0
            income = 0.0
            maintenanceCost = 80.0 * Double(level)
            happinessBonus = 4.0 * Double(level)
            
        case .policeStation:
            capacity = 0
            income = 0.0
            maintenanceCost = 60.0 * Double(level)
            happinessBonus = 2.0 * Double(level)
            
        case .fireStation:
            capacity = 0
            income = 0.0
            maintenanceCost = 70.0 * Double(level)
            happinessBonus = 2.0 * Double(level)
            
        case .park:
            capacity = 0
            income = 0.0
            maintenanceCost = 20.0 * Double(level)
            happinessBonus = 6.0 * Double(level)
            
        case .library:
            capacity = 0
            income = 0.0
            maintenanceCost = 40.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
            
        case .road:
            capacity = 0
            income = 0.0
            maintenanceCost = 5.0 * Double(level)
            happinessBonus = 1.0 * Double(level)
            
        case .bridge:
            capacity = 0
            income = 0.0
            maintenanceCost = 15.0 * Double(level)
            happinessBonus = 1.0 * Double(level)
            
        case .busStop:
            capacity = 0
            income = 0.0
            maintenanceCost = 10.0 * Double(level)
            happinessBonus = 2.0 * Double(level)
            
        case .subwayStation:
            capacity = 0
            income = 0.0
            maintenanceCost = 30.0 * Double(level)
            happinessBonus = 3.0 * Double(level)
        }
    }
    
    /// 获取建筑建造成本
    func getBuildCost() -> [ResourceType: Double] {
        switch type {
        case .house:
            return [.wood: 50, .stone: 30, .steel: 10]
        case .apartment:
            return [.wood: 80, .stone: 60, .steel: 20]
        case .villa:
            return [.wood: 120, .stone: 100, .steel: 40]
        case .skyscraper:
            return [.wood: 200, .stone: 300, .steel: 150]
        case .shop:
            return [.wood: 40, .stone: 20, .steel: 5]
        case .supermarket:
            return [.wood: 80, .stone: 60, .steel: 20]
        case .mall:
            return [.wood: 150, .stone: 200, .steel: 100]
        case .restaurant:
            return [.wood: 60, .stone: 40, .steel: 15]
        case .hotel:
            return [.wood: 100, .stone: 150, .steel: 80]
        case .lumberMill:
            return [.wood: 30, .stone: 50, .steel: 20]
        case .quarry:
            return [.wood: 20, .stone: 10, .steel: 30]
        case .steelMill:
            return [.wood: 40, .stone: 80, .steel: 50]
        case .foodFactory:
            return [.wood: 50, .stone: 30, .steel: 25]
        case .powerPlant:
            return [.wood: 100, .stone: 200, .steel: 300]
        case .school:
            return [.wood: 80, .stone: 100, .steel: 40]
        case .hospital:
            return [.wood: 120, .stone: 150, .steel: 80]
        case .policeStation:
            return [.wood: 60, .stone: 80, .steel: 50]
        case .fireStation:
            return [.wood: 70, .stone: 90, .steel: 60]
        case .park:
            return [.wood: 20, .stone: 10, .steel: 5]
        case .library:
            return [.wood: 60, .stone: 80, .steel: 30]
        case .road:
            return [.wood: 5, .stone: 10, .steel: 2]
        case .bridge:
            return [.wood: 30, .stone: 50, .steel: 40]
        case .busStop:
            return [.wood: 15, .stone: 20, .steel: 10]
        case .subwayStation:
            return [.wood: 50, .stone: 100, .steel: 80]
        }
    }
    
    /// 获取建筑升级成本
    func getUpgradeCost() -> [ResourceType: Double] {
        let baseCost = getBuildCost()
        let multiplier = Double(level + 1)
        return baseCost.mapValues { $0 * multiplier }
    }
    
    /// 获取建筑升级时间（秒）
    func getUpgradeTime() -> TimeInterval {
        let baseTime: TimeInterval = 300 // 5分钟
        return baseTime * Double(level)
    }
    
    /// 检查是否需要维护
    func needsMaintenance() -> Bool {
        let maintenanceInterval: TimeInterval = 3600 * 24 * 7 // 7天
        return Date().timeIntervalSince(lastMaintenanceTime) > maintenanceInterval
    }
}

/// 网格位置结构
struct GridPosition: Codable, Equatable {
    var x: Int
    var y: Int
    
    init(x: Int, y: Int) {
        self.x = x
        self.y = y
    }
}
