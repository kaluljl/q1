//
//  CityProsperityEngine.swift
//  CitySimulator
//
//  Created by GPT on 2025/10/24.
//

import Foundation

/// 城市繁荣度数据结构（0-100）
struct CityProsperity: Codable {
    let score: Int                // 综合繁荣度分数 0-100
    let economy: Double           // 经济指数 0-1（收入/支出/资源价值）
    let happiness: Double         // 幸福指数 0-1（基于人口幸福）
    let environment: Double       // 环境指数 0-1（污染/绿化）
    let populationGrowth: Double  // 人口成长指数 0-1（容量/增长）
    let label: String             // 标签（普通/繁荣/兴盛/鼎盛）
    
    static func empty() -> CityProsperity {
        return CityProsperity(score: 0, economy: 0, happiness: 0, environment: 0, populationGrowth: 0, label: "普通")
    }
}

/// 计算城市繁荣度的引擎（轻量级，避免与 UI 强耦合）
final class CityProsperityEngine {
    func compute(city: City,
                 resources: ResourceManager,
                 buildings: BuildingManager,
                 population: PopulationManager) -> CityProsperity {
        // 经济：资源总价值与建筑收入、维护的平衡
        let totalResourceValue = resources.calculateTotalValue() // 假设已有实现
        let totalIncome = buildings.buildings.map { $0.income }.reduce(0, +)
        let totalMaintenance = buildings.buildings.map { $0.maintenanceCost }.reduce(0, +)
        let economyBase = totalIncome - totalMaintenance + totalResourceValue / 50.0
        let economy = clamp01(economyBase / 1000.0)
        
        // 幸福：使用人口管理器的平均幸福（0-100）归一化到 0-1
        let happiness = clamp01(population.averageHappiness / 100.0)
        
        // 环境：根据污染与公共设施（简单估算：公共类建筑加分，工业类建筑的污染减分）
        let pollution = buildings.buildings.map { $0.pollutionLevel }.reduce(0, +)
        let publicCount = buildings.getBuildings(ofCategory: .public).count
        let environmentBase = Double(publicCount) * 0.02 - pollution * 0.001
        let environment = clamp01(0.5 + environmentBase)
        
        // 人口成长：容量与当前人口的比值，以及最近增长趋势（这里用容量占比做近似）
        let capacity = buildings.buildings.map { $0.capacity }.reduce(0, +)
        let totalPop = population.totalPopulation
        let capacityRatio = capacity > 0 ? Double(totalPop) / Double(capacity) : 0.0
        let populationGrowth = clamp01(0.6 - (capacityRatio - 0.7) * 0.5) // 容量充足更高分
        
        // 综合分：加权求和后映射到 0-100
        let weighted = 0.35 * economy + 0.30 * happiness + 0.20 * environment + 0.15 * populationGrowth
        let score = Int(round(clamp01(weighted) * 100))
        
        let label: String
        switch score {
        case ..<30: label = "普通"
        case 30..<60: label = "繁荣"
        case 60..<85: label = "兴盛"
        default: label = "鼎盛"
        }
        
        return CityProsperity(score: score,
                              economy: economy,
                              happiness: happiness,
                              environment: environment,
                              populationGrowth: populationGrowth,
                              label: label)
    }
    
    private func clamp01(_ x: Double) -> Double { max(0, min(1, x)) }
}
