//
//  City.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation

/// 城市数据模型
struct City: Codable, Identifiable {
    let id = UUID()
    var name: String
    var level: Int
    var experience: Int
    var gridSize: Int
    var foundedDate: Date
    var lastPlayedDate: Date
    
    // 城市统计数据
    var totalBuildings: Int = 0
    var totalPopulation: Int = 0
    var totalIncome: Double = 0
    var totalExpenses: Double = 0
    var happiness: Double = 0.0
    var environment: Double = 0.0
    
    init(name: String, level: Int = 1, experience: Int = 0, gridSize: Int = 20) {
        self.name = name
        self.level = level
        self.experience = experience
        self.gridSize = gridSize
        self.foundedDate = Date()
        self.lastPlayedDate = Date()
    }
    
    /// 计算城市等级所需经验值
    var experienceRequiredForNextLevel: Int {
        return level * 1000
    }
    
    /// 计算城市等级进度
    var levelProgress: Double {
        let currentLevelExp = (level - 1) * 1000
        let nextLevelExp = level * 1000
        let progress = Double(experience - currentLevelExp) / Double(nextLevelExp - currentLevelExp)
        return max(0, min(1, progress))
    }
    
    /// 获取城市等级对应的解锁内容
    var unlockedFeatures: [String] {
        var features: [String] = []
        
        if level >= 2 { features.append("高级住宅") }
        if level >= 3 { features.append("商业中心") }
        if level >= 5 { features.append("工业区") }
        if level >= 7 { features.append("公共设施") }
        if level >= 10 { features.append("交通系统") }
        if level >= 15 { features.append("科技研发") }
        if level >= 20 { features.append("联盟系统") }
        
        return features
    }
}



