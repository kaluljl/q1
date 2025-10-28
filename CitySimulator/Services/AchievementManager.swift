//
//  AchievementManager.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation
import Combine

/// 成就类型枚举
enum AchievementType: String, CaseIterable, Codable {
    case building = "building"
    case resource = "resource"
    case population = "population"
    case economy = "economy"
    case task = "task"
    case social = "social"
    case special = "special"
    
    var displayName: String {
        switch self {
        case .building: return "城市建设"
        case .resource: return "资源管理"
        case .population: return "人口发展"
        case .economy: return "经济发展"
        case .task: return "任务完成"
        case .social: return "社交互动"
        case .special: return "特殊成就"
        }
    }
}

/// 成就稀有度枚举
enum AchievementRarity: String, CaseIterable, Codable {
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

/// 成就数据模型
struct Achievement: Codable, Identifiable {
    let id = UUID()
    var title: String
    var description: String
    var type: AchievementType
    var rarity: AchievementRarity
    var isUnlocked: Bool
    var unlockCondition: [String: Any]
    var currentProgress: [String: Any]
    var rewards: TaskReward
    var unlockLevel: Int
    var unlockedTime: Date?
    
    init(title: String, description: String, type: AchievementType, rarity: AchievementRarity, unlockCondition: [String: Any], rewards: TaskReward, unlockLevel: Int = 1) {
        self.title = title
        self.description = description
        self.type = type
        self.rarity = rarity
        self.isUnlocked = false
        self.unlockCondition = unlockCondition
        self.currentProgress = [:]
        self.rewards = rewards
        self.unlockLevel = unlockLevel
    }
    
    /// 检查成就是否解锁
    func isUnlockConditionMet() -> Bool {
        for (key, requiredValue) in unlockCondition {
            if let currentValue = currentProgress[key] {
                if let required = requiredValue as? Int, let current = currentValue as? Int {
                    if current < required {
                        return false
                    }
                } else if let required = requiredValue as? Double, let current = currentValue as? Double {
                    if current < required {
                        return false
                    }
                } else if let required = requiredValue as? String, let current = currentValue as? String {
                    if current != required {
                        return false
                    }
                }
            } else {
                return false
            }
        }
        return true
    }
    
    /// 获取成就进度百分比
    func getProgressPercentage() -> Double {
        var totalProgress = 0.0
        var totalRequired = 0.0
        
        for (key, requiredValue) in unlockCondition {
            if let currentValue = currentProgress[key] {
                if let required = requiredValue as? Int, let current = currentValue as? Int {
                    totalProgress += Double(current)
                    totalRequired += Double(required)
                } else if let required = requiredValue as? Double, let current = currentValue as? Double {
                    totalProgress += current
                    totalRequired += required
                }
            }
        }
        
        return totalRequired > 0 ? totalProgress / totalRequired : 0.0
    }
}

/// 成就管理器
class AchievementManager: ObservableObject {
    @Published var achievements: [Achievement] = []
    @Published var unlockedAchievements: [Achievement] = []
    @Published var lockedAchievements: [Achievement] = []
    
    init() {
        initializeAchievements()
    }
    
    /// 初始化成就
    private func initializeAchievements() {
        // 城市建设成就
        let buildingAchievement1 = Achievement(
            title: "第一座建筑",
            description: "建造第一座建筑",
            type: .building,
            rarity: .common,
            unlockCondition: ["buildCount": 1],
            rewards: TaskReward(gold: 100, experience: 50),
            unlockLevel: 1
        )
        
        let buildingAchievement2 = Achievement(
            title: "建筑大师",
            description: "建造10座不同类型的建筑",
            type: .building,
            rarity: .uncommon,
            unlockCondition: ["buildCount": 10],
            rewards: TaskReward(gold: 500, diamond: 10, experience: 200),
            unlockLevel: 5
        )
        
        // 资源管理成就
        let resourceAchievement1 = Achievement(
            title: "资源收集者",
            description: "累计收集1000单位木材",
            type: .resource,
            rarity: .common,
            unlockCondition: ["totalWoodCollected": 1000],
            rewards: TaskReward(resources: [.wood: 200], gold: 200, experience: 100),
            unlockLevel: 2
        )
        
        // 人口发展成就
        let populationAchievement1 = Achievement(
            title: "人口增长",
            description: "城市人口达到100人",
            type: .population,
            rarity: .uncommon,
            unlockCondition: ["totalPopulation": 100],
            rewards: TaskReward(gold: 300, experience: 150),
            unlockLevel: 3
        )
        
        // 经济发展成就
        let economyAchievement1 = Achievement(
            title: "经济繁荣",
            description: "单日收入达到1000金币",
            type: .economy,
            rarity: .rare,
            unlockCondition: ["dailyIncome": 1000],
            rewards: TaskReward(gold: 500, diamond: 20, experience: 300),
            unlockLevel: 5
        )
        
        // 任务完成成就
        let taskAchievement1 = Achievement(
            title: "任务达人",
            description: "完成10个主线任务",
            type: .task,
            rarity: .uncommon,
            unlockCondition: ["mainTasksCompleted": 10],
            rewards: TaskReward(gold: 400, experience: 200),
            unlockLevel: 4
        )
        
        achievements = [
            buildingAchievement1,
            buildingAchievement2,
            resourceAchievement1,
            populationAchievement1,
            economyAchievement1,
            taskAchievement1
        ]
        
        updateAchievementLists()
    }
    
    /// 解锁新成就
    func unlockNewAchievements(for cityLevel: Int) {
        // 根据城市等级解锁新成就
        let newAchievements = generateAchievementsForLevel(cityLevel)
        achievements.append(contentsOf: newAchievements)
        updateAchievementLists()
    }
    
    /// 为指定等级生成成就
    private func generateAchievementsForLevel(_ level: Int) -> [Achievement] {
        var newAchievements: [Achievement] = []
        
        switch level {
        case 10:
            let achievement = Achievement(
                title: "城市扩张",
                description: "城市等级达到10级",
                type: .special,
                rarity: .epic,
                unlockCondition: ["cityLevel": 10],
                rewards: TaskReward(gold: 1000, diamond: 50, experience: 500),
                unlockLevel: 10
            )
            newAchievements.append(achievement)
            
        case 20:
            let achievement = Achievement(
                title: "都市传奇",
                description: "城市等级达到20级",
                type: .special,
                rarity: .legendary,
                unlockCondition: ["cityLevel": 20],
                rewards: TaskReward(gold: 2000, diamond: 100, experience: 1000),
                unlockLevel: 20
            )
            newAchievements.append(achievement)
            
        default:
            break
        }
        
        return newAchievements
    }
    
    /// 更新成就列表
    private func updateAchievementLists() {
        unlockedAchievements = achievements.filter { $0.isUnlocked }
        lockedAchievements = achievements.filter { !$0.isUnlocked }
    }
    
    /// 检查成就解锁
    func checkAchievements() {
        for i in 0..<achievements.count {
            if !achievements[i].isUnlocked && achievements[i].isUnlockConditionMet() {
                achievements[i].isUnlocked = true
                achievements[i].unlockedTime = Date()
            }
        }
        updateAchievementLists()
    }
    
    /// 更新成就进度
    func updateAchievementProgress(achievementId: UUID, progress: [String: Any]) {
        guard let index = achievements.firstIndex(where: { $0.id == achievementId }) else {
            return
        }
        
        for (key, value) in progress {
            achievements[index].currentProgress[key] = value
        }
        
        // 检查成就是否解锁
        if achievements[index].isUnlockConditionMet() && !achievements[index].isUnlocked {
            achievements[index].isUnlocked = true
            achievements[index].unlockedTime = Date()
        }
        
        updateAchievementLists()
    }
    
    /// 获取成就统计信息
    func getAchievementStatistics() -> [String: Any] {
        let typeCount = Dictionary(grouping: achievements, by: { $0.type })
            .mapValues { $0.count }
        
        let rarityCount = Dictionary(grouping: achievements, by: { $0.rarity })
            .mapValues { $0.count }
        
        let unlockedCount = achievements.filter { $0.isUnlocked }.count
        let lockedCount = achievements.filter { !$0.isUnlocked }.count
        
        let totalRewards = achievements.filter { $0.isUnlocked }.map { $0.rewards }
        
        var totalGold = 0.0
        var totalDiamond = 0.0
        var totalExperience = 0
        
        for reward in totalRewards {
            totalGold += reward.gold
            totalDiamond += reward.diamond
            totalExperience += reward.experience
        }
        
        return [
            "totalAchievements": achievements.count,
            "typeCount": typeCount,
            "rarityCount": rarityCount,
            "unlockedCount": unlockedCount,
            "lockedCount": lockedCount,
            "totalGoldRewards": totalGold,
            "totalDiamondRewards": totalDiamond,
            "totalExperienceRewards": totalExperience
        ]
    }
    
    /// 获取指定类型的成就
    func getAchievements(ofType type: AchievementType) -> [Achievement] {
        return achievements.filter { $0.type == type }
    }
    
    /// 获取指定稀有度的成就
    func getAchievements(ofRarity rarity: AchievementRarity) -> [Achievement] {
        return achievements.filter { $0.rarity == rarity }
    }
    
    /// 获取已解锁的成就
    func getUnlockedAchievements() -> [Achievement] {
        return achievements.filter { $0.isUnlocked }
    }
    
    /// 获取未解锁的成就
    func getLockedAchievements() -> [Achievement] {
        return achievements.filter { !$0.isUnlocked }
    }
}



