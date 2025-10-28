//
//  BuildingManager.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation
import Combine

/// 建筑管理器
class BuildingManager: ObservableObject {
    @Published var buildings: [Building] = []
    @Published var availableBuildingTypes: [BuildingType] = []
    @Published var selectedBuildingType: BuildingType?
    @Published var isBuildingMode: Bool = false
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        initializeAvailableBuildings()
    }
    
    /// 初始化可用建筑类型
    private func initializeAvailableBuildings() {
        availableBuildingTypes = BuildingType.allCases.filter { $0.unlockLevel <= 1 }
    }
    
    /// 解锁新建筑类型
    func unlockNewBuildingTypes(for cityLevel: Int) {
        let newTypes = BuildingType.allCases.filter { 
            $0.unlockLevel <= cityLevel && !availableBuildingTypes.contains($0)
        }
        availableBuildingTypes.append(contentsOf: newTypes)
    }
    
    /// 开始建造模式
    func startBuildingMode(buildingType: BuildingType) {
        selectedBuildingType = buildingType
        isBuildingMode = true
    }
    
    /// 结束建造模式
    func endBuildingMode() {
        selectedBuildingType = nil
        isBuildingMode = false
    }
    
    /// 在指定位置建造建筑
    func buildBuilding(at position: GridPosition, type: BuildingType) -> Bool {
        // 检查位置是否被占用
        if isPositionOccupied(position) {
            return false
        }
        
        // 检查是否有足够的资源
        let buildCost = getBuildCost(for: type)
        // 这里需要从GameManager获取资源管理器来检查资源
        
        // 创建新建筑
        var newBuilding = Building(type: type, position: position)
        newBuilding.isUnderConstruction = true
        
        buildings.append(newBuilding)
        return true
    }
    
    /// 检查位置是否被占用
    func isPositionOccupied(_ position: GridPosition) -> Bool {
        return buildings.contains { $0.position == position }
    }
    
    /// 获取建筑建造成本
    func getBuildCost(for type: BuildingType) -> [ResourceType: Double] {
        let building = Building(type: type, position: GridPosition(x: 0, y: 0))
        return building.getBuildCost()
    }
    
    /// 升级建筑
    func upgradeBuilding(_ building: Building) -> Bool {
        guard let index = buildings.firstIndex(where: { $0.id == building.id }) else {
            return false
        }
        
        let upgradeCost = building.getUpgradeCost()
        // 这里需要检查资源是否充足
        
        buildings[index].level += 1
        buildings[index].isUpgrading = true
        buildings[index].lastUpgradeTime = Date()
        buildings[index].updateBuildingProperties()
        
        return true
    }
    
    /// 维护建筑
    func maintainBuilding(_ building: Building) -> Bool {
        guard let index = buildings.firstIndex(where: { $0.id == building.id }) else {
            return false
        }
        
        buildings[index].isMaintenanceRequired = false
        buildings[index].lastMaintenanceTime = Date()
        
        return true
    }
    
    /// 拆除建筑
    func demolishBuilding(_ building: Building) -> Bool {
        guard let index = buildings.firstIndex(where: { $0.id == building.id }) else {
            return false
        }
        
        buildings.remove(at: index)
        return true
    }
    
    /// 更新建筑状态
    func updateBuildingStates() {
        for i in 0..<buildings.count {
            // 检查是否需要维护
            if buildings[i].needsMaintenance() {
                buildings[i].isMaintenanceRequired = true
            }
            
            // 更新建造状态
            if buildings[i].isUnderConstruction {
                // 检查建造是否完成
                let buildTime: TimeInterval = 60 // 1分钟建造时间
                if Date().timeIntervalSince(buildings[i].buildTime) >= buildTime {
                    buildings[i].isUnderConstruction = false
                }
            }
            
            // 更新升级状态
            if buildings[i].isUpgrading {
                let upgradeTime = buildings[i].getUpgradeTime()
                if let lastUpgradeTime = buildings[i].lastUpgradeTime,
                   Date().timeIntervalSince(lastUpgradeTime) >= upgradeTime {
                    buildings[i].isUpgrading = false
                }
            }
        }
    }
    
    /// 获取建筑统计信息
    func getBuildingStatistics() -> [String: Any] {
        let categoryCount = Dictionary(grouping: buildings, by: { $0.type.category })
            .mapValues { $0.count }
        
        let levelDistribution = Dictionary(grouping: buildings, by: { $0.level })
            .mapValues { $0.count }
        
        let totalCapacity = buildings.map { $0.capacity }.reduce(0, +)
        let totalIncome = buildings.map { $0.income }.reduce(0, +)
        let totalMaintenanceCost = buildings.map { $0.maintenanceCost }.reduce(0, +)
        
        return [
            "totalBuildings": buildings.count,
            "categoryCount": categoryCount,
            "levelDistribution": levelDistribution,
            "totalCapacity": totalCapacity,
            "totalIncome": totalIncome,
            "totalMaintenanceCost": totalMaintenanceCost
        ]
    }
    
    /// 获取指定类型的建筑
    func getBuildings(ofType type: BuildingType) -> [Building] {
        return buildings.filter { $0.type == type }
    }
    
    /// 获取指定分类的建筑
    func getBuildings(ofCategory category: BuildingCategory) -> [Building] {
        return buildings.filter { $0.type.category == category }
    }
    
    /// 获取需要维护的建筑
    func getBuildingsNeedingMaintenance() -> [Building] {
        return buildings.filter { $0.isMaintenanceRequired }
    }
    
    /// 获取正在建造的建筑
    func getBuildingsUnderConstruction() -> [Building] {
        return buildings.filter { $0.isUnderConstruction }
    }
    
    /// 获取正在升级的建筑
    func getBuildingsUpgrading() -> [Building] {
        return buildings.filter { $0.isUpgrading }
    }
}
