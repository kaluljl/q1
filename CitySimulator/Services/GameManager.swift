//
//  GameManager.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation
import Combine

/// 游戏管理器 - 负责管理游戏的整体状态和逻辑
class GameManager: ObservableObject {
    @Published var currentCity: City
    @Published var resources: ResourceManager
    @Published var population: PopulationManager
    @Published var buildings: BuildingManager
    @Published var tasks: TaskManager
    @Published var achievements: AchievementManager
    @Published var prosperity: CityProsperity = CityProsperity.empty()
    private let prosperityEngine = CityProsperityEngine()
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        // 初始化各个管理器
        self.resources = ResourceManager()
        self.population = PopulationManager()
        self.buildings = BuildingManager()
        self.tasks = TaskManager()
        self.achievements = AchievementManager()
        
        // 创建初始城市
        self.currentCity = City(
            name: "我的城市",
            level: 1,
            experience: 0,
            gridSize: 20
        )
        
        setupBindings()
        startGameLoop()
    }
    
    /// 设置各个管理器之间的绑定关系
    private func setupBindings() {
        // 建筑变化影响资源生产
        buildings.$buildings
            .sink { [weak self] buildings in
                self?.updateResourceProduction(from: buildings)
            }
            .store(in: &cancellables)
        
        // 资源变化影响人口
        resources.$resources
            .sink { [weak self] resources in
                self?.updatePopulationHappiness(from: resources)
            }
            .store(in: &cancellables)
        
        // 人口变化影响城市等级
        population.$totalPopulation
            .sink { [weak self] totalPopulation in
                self?.updateCityLevel()
            }
            .store(in: &cancellables)
    }
    
    /// 启动游戏主循环
    private func startGameLoop() {
        Timer.publish(every: 1.0, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.gameTick()
            }
            .store(in: &cancellables)
    }
    
    /// 游戏主循环 - 每秒执行一次
    private func gameTick() {
        // 更新资源生产
        resources.updateProduction()
        
        // 更新建筑状态
        buildings.updateBuildingStates()
        
        // 更新人口状态
        population.updatePopulationStatus()
        
        // 计算并发布繁荣度（用于 UI 展示）
        prosperity = prosperityEngine.compute(city: currentCity,
                                              resources: resources,
                                              buildings: buildings,
                                              population: population)
        
        // 检查任务完成情况
        tasks.checkTaskCompletion()
        
        // 检查成就解锁
        achievements.checkAchievements()
    }
    
    /// 根据建筑更新资源生产
    private func updateResourceProduction(from buildings: [Building]) {
        var productionRates: [ResourceType: Double] = [:]
        
        for building in buildings {
            if let production = building.productionRate {
                for (resourceType, rate) in production {
                    productionRates[resourceType, default: 0] += rate
                }
            }
        }
        
        resources.updateProductionRates(productionRates)
    }
    
    /// 根据资源更新人口幸福感
    private func updatePopulationHappiness(from resources: [ResourceType: Double]) {
        // 根据资源充足程度调整人口幸福感
        let resourceSatisfaction = calculateResourceSatisfaction(resources)
        population.updateHappinessModifier(resourceSatisfaction)
    }
    
    /// 计算资源满意度
    private func calculateResourceSatisfaction(_ resources: [ResourceType: Double]) -> Double {
        let requiredResources: [ResourceType: Double] = [
            .wood: 100,
            .stone: 100,
            .steel: 50,
            .food: 200
        ]
        
        var totalSatisfaction = 0.0
        var resourceCount = 0
        
        for (resourceType, required) in requiredResources {
            if let current = resources[resourceType] {
                let satisfaction = min(current / required, 1.0)
                totalSatisfaction += satisfaction
                resourceCount += 1
            }
        }
        
        return resourceCount > 0 ? totalSatisfaction / Double(resourceCount) : 0.0
    }
    
    /// 更新城市等级
    private func updateCityLevel() {
        let newLevel = calculateCityLevel()
        if newLevel > currentCity.level {
            currentCity.level = newLevel
            // 解锁新建筑和功能
            unlockNewContent()
        }
    }
    
    /// 计算城市等级
    private func calculateCityLevel() -> Int {
        let population = population.totalPopulation
        let buildingCount = buildings.buildings.count
        let resourceValue = resources.calculateTotalValue()
        
        // 基于人口、建筑数量和资源价值计算等级
        let level = Int((population / 100) + (buildingCount / 10) + (resourceValue / 1000)) + 1
        return min(level, 50) // 最高50级
    }
    
    /// 解锁新内容
    private func unlockNewContent() {
        // 解锁新建筑类型
        buildings.unlockNewBuildingTypes(for: currentCity.level)
        
        // 解锁新任务
        tasks.unlockNewTasks(for: currentCity.level)
        
        // 解锁新成就
        achievements.unlockNewAchievements(for: currentCity.level)
    }
    
    /// 保存游戏数据
    func saveGame() {
        // 实现游戏数据保存逻辑
        print("游戏数据已保存")
    }
    
    /// 加载游戏数据
    func loadGame() {
        // 实现游戏数据加载逻辑
        print("游戏数据已加载")
    }
}



