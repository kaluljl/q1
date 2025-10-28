//
//  Resource.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation

/// 资源类型枚举
enum ResourceType: String, CaseIterable, Codable {
    // 基础资源
    case wood = "wood"
    case stone = "stone"
    case steel = "steel"
    case food = "food"
    
    // 特殊资源
    case oil = "oil"
    case coal = "coal"
    case ore = "ore"
    case rareEarth = "rareEarth"
    
    // 能源
    case energy = "energy"
    
    // 货币
    case gold = "gold"
    case diamond = "diamond"
    
    /// 获取资源类型的中文名称
    var displayName: String {
        switch self {
        case .wood: return "木材"
        case .stone: return "石材"
        case .steel: return "钢材"
        case .food: return "食物"
        case .oil: return "石油"
        case .coal: return "煤炭"
        case .ore: return "矿石"
        case .rareEarth: return "稀土"
        case .energy: return "能源"
        case .gold: return "金币"
        case .diamond: return "钻石"
        }
    }
    
    /// 获取资源图标名称
    var iconName: String {
        switch self {
        case .wood: return "tree"
        case .stone: return "mountain"
        case .steel: return "gear"
        case .food: return "apple"
        case .oil: return "drop"
        case .coal: return "coal"
        case .ore: return "diamond"
        case .rareEarth: return "sparkles"
        case .energy: return "bolt"
        case .gold: return "dollarsign.circle"
        case .diamond: return "diamond.fill"
        }
    }
    
    /// 获取资源分类
    var category: ResourceCategory {
        switch self {
        case .wood, .stone, .steel, .food:
            return .basic
        case .oil, .coal, .ore, .rareEarth:
            return .special
        case .energy:
            return .energy
        case .gold, .diamond:
            return .currency
        }
    }
    
    /// 获取资源稀有度
    var rarity: ResourceRarity {
        switch self {
        case .wood, .stone, .food:
            return .common
        case .steel, .energy:
            return .uncommon
        case .oil, .coal, .ore:
            return .rare
        case .rareEarth, .diamond:
            return .epic
        case .gold:
            return .common
        }
    }
}

/// 资源分类枚举
enum ResourceCategory: String, CaseIterable {
    case basic = "basic"
    case special = "special"
    case energy = "energy"
    case currency = "currency"
    
    var displayName: String {
        switch self {
        case .basic: return "基础资源"
        case .special: return "特殊资源"
        case .energy: return "能源"
        case .currency: return "货币"
        }
    }
}

/// 资源稀有度枚举
enum ResourceRarity: String, CaseIterable {
    case common = "common"
    case uncommon = "uncommon"
    case rare = "rare"
    case epic = "epic"
    case legendary = "legendary"
    
    var displayName: String {
        switch self {
        case .common: return "普通"
        case .uncommon: return "不常见"
        case .rare: return "稀有"
        case .epic: return "史诗"
        case .legendary: return "传说"
        }
    }
    
    var color: String {
        switch self {
        case .common: return "gray"
        case .uncommon: return "green"
        case .rare: return "blue"
        case .epic: return "purple"
        case .legendary: return "orange"
        }
    }
}

/// 资源数据模型
struct Resource: Codable, Identifiable {
    let id = UUID()
    var type: ResourceType
    var amount: Double
    var maxCapacity: Double
    var productionRate: Double
    var consumptionRate: Double
    
    init(type: ResourceType, amount: Double = 0, maxCapacity: Double = 1000) {
        self.type = type
        self.amount = amount
        self.maxCapacity = maxCapacity
        self.productionRate = 0
        self.consumptionRate = 0
    }
    
    /// 检查资源是否充足
    func isSufficient(for amount: Double) -> Bool {
        return self.amount >= amount
    }
    
    /// 检查存储空间是否足够
    func canStore(amount: Double) -> Bool {
        return self.amount + amount <= maxCapacity
    }
    
    /// 获取存储百分比
    var storagePercentage: Double {
        return amount / maxCapacity
    }
    
    /// 获取资源价值（用于计算城市等级）
    func getValue() -> Double {
        let baseValue: [ResourceType: Double] = [
            .wood: 1.0,
            .stone: 1.5,
            .steel: 3.0,
            .food: 2.0,
            .oil: 5.0,
            .coal: 4.0,
            .ore: 6.0,
            .rareEarth: 10.0,
            .energy: 2.5,
            .gold: 1.0,
            .diamond: 100.0
        ]
        
        return amount * (baseValue[type] ?? 1.0)
    }
}

/// 资源管理器
class ResourceManager: ObservableObject {
    @Published var resources: [ResourceType: Double] = [:]
    @Published var maxCapacities: [ResourceType: Double] = [:]
    @Published var productionRates: [ResourceType: Double] = [:]
    @Published var consumptionRates: [ResourceType: Double] = [:]
    
    init() {
        initializeResources()
    }
    
    /// 初始化资源
    private func initializeResources() {
        // 初始化基础资源
        resources[.wood] = 100
        resources[.stone] = 100
        resources[.steel] = 50
        resources[.food] = 200
        resources[.gold] = 1000
        resources[.diamond] = 10
        
        // 初始化存储容量
        maxCapacities[.wood] = 1000
        maxCapacities[.stone] = 1000
        maxCapacities[.steel] = 500
        maxCapacities[.food] = 2000
        maxCapacities[.oil] = 500
        maxCapacities[.coal] = 500
        maxCapacities[.ore] = 300
        maxCapacities[.rareEarth] = 100
        maxCapacities[.energy] = 1000
        maxCapacities[.gold] = 100000
        maxCapacities[.diamond] = 1000
        
        // 初始化生产速率
        for resourceType in ResourceType.allCases {
            productionRates[resourceType] = 0
            consumptionRates[resourceType] = 0
        }
    }
    
    /// 更新资源生产速率
    func updateProductionRates(_ rates: [ResourceType: Double]) {
        for (type, rate) in rates {
            productionRates[type] = rate
        }
    }
    
    /// 更新资源生产
    func updateProduction() {
        for (type, rate) in productionRates {
            if rate > 0 {
                addResource(type: type, amount: rate)
            }
        }
        
        // 更新资源消耗
        for (type, rate) in consumptionRates {
            if rate > 0 {
                consumeResource(type: type, amount: rate)
            }
        }
    }
    
    /// 添加资源
    func addResource(type: ResourceType, amount: Double) {
        let currentAmount = resources[type] ?? 0
        let maxCapacity = maxCapacities[type] ?? 1000
        let newAmount = min(currentAmount + amount, maxCapacity)
        resources[type] = newAmount
    }
    
    /// 消耗资源
    func consumeResource(type: ResourceType, amount: Double) -> Bool {
        let currentAmount = resources[type] ?? 0
        if currentAmount >= amount {
            resources[type] = currentAmount - amount
            return true
        }
        return false
    }
    
    /// 检查资源是否充足
    func hasEnoughResources(_ required: [ResourceType: Double]) -> Bool {
        for (type, amount) in required {
            if (resources[type] ?? 0) < amount {
                return false
            }
        }
        return true
    }
    
    /// 消耗资源（用于建造等）
    func spendResources(_ required: [ResourceType: Double]) -> Bool {
        if hasEnoughResources(required) {
            for (type, amount) in required {
                resources[type] = (resources[type] ?? 0) - amount
            }
            return true
        }
        return false
    }
    
    /// 计算总资源价值
    func calculateTotalValue() -> Double {
        var totalValue = 0.0
        for (type, amount) in resources {
            let resource = Resource(type: type, amount: amount)
            totalValue += resource.getValue()
        }
        return totalValue
    }
    
    /// 升级资源存储容量
    func upgradeStorageCapacity(type: ResourceType, increase: Double) {
        maxCapacities[type] = (maxCapacities[type] ?? 1000) + increase
    }
    
    /// 获取资源存储百分比
    func getStoragePercentage(type: ResourceType) -> Double {
        let current = resources[type] ?? 0
        let max = maxCapacities[type] ?? 1000
        return current / max
    }
}
