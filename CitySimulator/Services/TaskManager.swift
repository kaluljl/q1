//
//  TaskManager.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation
import Combine

/// 任务类型枚举
enum TaskType: String, CaseIterable, Codable {
    case main = "main"
    case side = "side"
    case daily = "daily"
    case event = "event"
    
    var displayName: String {
        switch self {
        case .main: return "主线任务"
        case .side: return "支线任务"
        case .daily: return "日常任务"
        case .event: return "活动任务"
        }
    }
}

/// 任务状态枚举
enum TaskStatus: String, CaseIterable, Codable {
    case available = "available"
    case accepted = "accepted"
    case completed = "completed"
    case claimed = "claimed"
    case expired = "expired"
    
    var displayName: String {
        switch self {
        case .available: return "可接取"
        case .accepted: return "进行中"
        case .completed: return "已完成"
        case .claimed: return "已领取"
        case .expired: return "已过期"
        }
    }
}

/// 任务奖励结构
struct TaskReward: Codable {
    var resources: [ResourceType: Double] = [:]
    var gold: Double = 0
    var diamond: Double = 0
    var experience: Int = 0
    var items: [String] = []
    
    init(resources: [ResourceType: Double] = [:], gold: Double = 0, diamond: Double = 0, experience: Int = 0, items: [String] = []) {
        self.resources = resources
        self.gold = gold
        self.diamond = diamond
        self.experience = experience
        self.items = items
    }
}

/// 任务数据模型
struct Task: Codable, Identifiable {
    let id = UUID()
    var title: String
    var description: String
    var type: TaskType
    var status: TaskStatus
    var requirements: [String: Any]
    var progress: [String: Any]
    var rewards: TaskReward
    var unlockLevel: Int
    var expireTime: Date?
    var createdTime: Date
    
    init(title: String, description: String, type: TaskType, requirements: [String: Any], rewards: TaskReward, unlockLevel: Int = 1) {
        self.title = title
        self.description = description
        self.type = type
        self.status = .available
        self.requirements = requirements
        self.progress = [:]
        self.rewards = rewards
        self.unlockLevel = unlockLevel
        self.createdTime = Date()
    }
    
    /// 检查任务是否完成
    func isCompleted() -> Bool {
        for (key, requiredValue) in requirements {
            if let currentValue = progress[key] {
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
    
    /// 获取任务进度百分比
    func getProgressPercentage() -> Double {
        var totalProgress = 0.0
        var totalRequired = 0.0
        
        for (key, requiredValue) in requirements {
            if let currentValue = progress[key] {
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

/// 任务管理器
class TaskManager: ObservableObject {
    @Published var tasks: [Task] = []
    @Published var availableTasks: [Task] = []
    @Published var activeTasks: [Task] = []
    @Published var completedTasks: [Task] = []
    
    init() {
        initializeTasks()
    }
    
    /// 初始化任务
    private func initializeTasks() {
        // 创建初始主线任务
        let mainTask1 = Task(
            title: "建设第一座住宅",
            description: "建造一座普通住宅来吸引居民",
            type: .main,
            requirements: ["buildHouse": 1],
            rewards: TaskReward(resources: [.wood: 50, .stone: 30], gold: 100, experience: 50),
            unlockLevel: 1
        )
        
        let mainTask2 = Task(
            title: "发展商业",
            description: "建造一座商店来增加收入",
            type: .main,
            requirements: ["buildShop": 1],
            rewards: TaskReward(resources: [.wood: 40, .stone: 20], gold: 150, experience: 75),
            unlockLevel: 2
        )
        
        // 创建日常任务
        let dailyTask1 = Task(
            title: "收集资源",
            description: "收集100单位木材",
            type: .daily,
            requirements: ["collectWood": 100],
            rewards: TaskReward(gold: 50, experience: 25),
            unlockLevel: 1
        )
        
        tasks = [mainTask1, mainTask2, dailyTask1]
        updateTaskLists()
    }
    
    /// 解锁新任务
    func unlockNewTasks(for cityLevel: Int) {
        // 根据城市等级解锁新任务
        let newTasks = generateTasksForLevel(cityLevel)
        tasks.append(contentsOf: newTasks)
        updateTaskLists()
    }
    
    /// 为指定等级生成任务
    private func generateTasksForLevel(_ level: Int) -> [Task] {
        var newTasks: [Task] = []
        
        switch level {
        case 3:
            let task = Task(
                title: "建设工业区",
                description: "建造一座木材厂来生产资源",
                type: .main,
                requirements: ["buildLumberMill": 1],
                rewards: TaskReward(resources: [.wood: 30, .stone: 50], gold: 200, experience: 100),
                unlockLevel: 3
            )
            newTasks.append(task)
            
        case 5:
            let task = Task(
                title: "发展教育",
                description: "建造一座学校来提升居民教育水平",
                type: .side,
                requirements: ["buildSchool": 1],
                rewards: TaskReward(resources: [.wood: 80, .stone: 100], gold: 300, experience: 150),
                unlockLevel: 5
            )
            newTasks.append(task)
            
        default:
            break
        }
        
        return newTasks
    }
    
    /// 更新任务列表
    private func updateTaskLists() {
        availableTasks = tasks.filter { $0.status == .available }
        activeTasks = tasks.filter { $0.status == .accepted }
        completedTasks = tasks.filter { $0.status == .completed }
    }
    
    /// 接取任务
    func acceptTask(_ task: Task) -> Bool {
        guard let index = tasks.firstIndex(where: { $0.id == task.id }),
              tasks[index].status == .available else {
            return false
        }
        
        tasks[index].status = .accepted
        updateTaskLists()
        return true
    }
    
    /// 完成任务
    func completeTask(_ task: Task) -> Bool {
        guard let index = tasks.firstIndex(where: { $0.id == task.id }),
              tasks[index].status == .accepted else {
            return false
        }
        
        if tasks[index].isCompleted() {
            tasks[index].status = .completed
            updateTaskLists()
            return true
        }
        
        return false
    }
    
    /// 领取任务奖励
    func claimTaskReward(_ task: Task) -> TaskReward? {
        guard let index = tasks.firstIndex(where: { $0.id == task.id }),
              tasks[index].status == .completed else {
            return nil
        }
        
        tasks[index].status = .claimed
        updateTaskLists()
        return tasks[index].rewards
    }
    
    /// 检查任务完成情况
    func checkTaskCompletion() {
        for i in 0..<tasks.count {
            if tasks[i].status == .accepted && tasks[i].isCompleted() {
                tasks[i].status = .completed
            }
        }
        updateTaskLists()
    }
    
    /// 更新任务进度
    func updateTaskProgress(taskId: UUID, progress: [String: Any]) {
        guard let index = tasks.firstIndex(where: { $0.id == taskId }) else {
            return
        }
        
        for (key, value) in progress {
            tasks[index].progress[key] = value
        }
        
        // 检查任务是否完成
        if tasks[index].isCompleted() && tasks[index].status == .accepted {
            tasks[index].status = .completed
        }
        
        updateTaskLists()
    }
    
    /// 获取任务统计信息
    func getTaskStatistics() -> [String: Any] {
        let typeCount = Dictionary(grouping: tasks, by: { $0.type })
            .mapValues { $0.count }
        
        let statusCount = Dictionary(grouping: tasks, by: { $0.status })
            .mapValues { $0.count }
        
        let completedCount = tasks.filter { $0.status == .completed || $0.status == .claimed }.count
        let totalRewards = tasks.filter { $0.status == .claimed }.map { $0.rewards }
        
        var totalGold = 0.0
        var totalDiamond = 0.0
        var totalExperience = 0
        
        for reward in totalRewards {
            totalGold += reward.gold
            totalDiamond += reward.diamond
            totalExperience += reward.experience
        }
        
        return [
            "totalTasks": tasks.count,
            "typeCount": typeCount,
            "statusCount": statusCount,
            "completedCount": completedCount,
            "totalGoldRewards": totalGold,
            "totalDiamondRewards": totalDiamond,
            "totalExperienceRewards": totalExperience
        ]
    }
    
    /// 获取指定类型的任务
    func getTasks(ofType type: TaskType) -> [Task] {
        return tasks.filter { $0.type == type }
    }
    
    /// 获取指定状态的任务
    func getTasks(withStatus status: TaskStatus) -> [Task] {
        return tasks.filter { $0.status == status }
    }
    
    /// 获取可接取的任务
    func getAvailableTasks() -> [Task] {
        return tasks.filter { $0.status == .available }
    }
    
    /// 获取进行中的任务
    func getActiveTasks() -> [Task] {
        return tasks.filter { $0.status == .accepted }
    }
    
    /// 获取已完成的任务
    func getCompletedTasks() -> [Task] {
        return tasks.filter { $0.status == .completed }
    }
}
