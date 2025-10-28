//
//  Population.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import Foundation

/// 人口职业枚举
enum PopulationJob: String, CaseIterable, Codable {
    case worker = "worker"
    case farmer = "farmer"
    case merchant = "merchant"
    case teacher = "teacher"
    case doctor = "doctor"
    case police = "police"
    case scientist = "scientist"
    case artist = "artist"
    case engineer = "engineer"
    case manager = "manager"
    
    var displayName: String {
        switch self {
        case .worker: return "工人"
        case .farmer: return "农民"
        case .merchant: return "商人"
        case .teacher: return "教师"
        case .doctor: return "医生"
        case .police: return "警察"
        case .scientist: return "科学家"
        case .artist: return "艺术家"
        case .engineer: return "工程师"
        case .manager: return "管理者"
        }
    }
    
    var requiredEducation: EducationLevel {
        switch self {
        case .worker, .farmer:
            return .elementary
        case .merchant, .police:
            return .middle
        case .teacher, .doctor, .engineer:
            return .high
        case .scientist, .artist, .manager:
            return .university
        }
    }
    
    var income: Double {
        switch self {
        case .worker, .farmer:
            return 100.0
        case .merchant, .police:
            return 150.0
        case .teacher, .doctor, .engineer:
            return 200.0
        case .scientist, .artist, .manager:
            return 300.0
        }
    }
}

/// 教育水平枚举
enum EducationLevel: String, CaseIterable, Codable {
    case elementary = "elementary"
    case middle = "middle"
    case high = "high"
    case university = "university"
    case graduate = "graduate"
    
    var displayName: String {
        switch self {
        case .elementary: return "小学"
        case .middle: return "中学"
        case .high: return "高中"
        case .university: return "大学"
        case .graduate: return "研究生"
        }
    }
    
    var level: Int {
        switch self {
        case .elementary: return 1
        case .middle: return 2
        case .high: return 3
        case .university: return 4
        case .graduate: return 5
        }
    }
}

/// 健康状况枚举
enum HealthStatus: String, CaseIterable, Codable {
    case healthy = "healthy"
    case subhealthy = "subhealthy"
    case sick = "sick"
    case critical = "critical"
    
    var displayName: String {
        switch self {
        case .healthy: return "健康"
        case .subhealthy: return "亚健康"
        case .sick: return "生病"
        case .critical: return "重病"
        }
    }
    
    var workEfficiency: Double {
        switch self {
        case .healthy: return 1.0
        case .subhealthy: return 0.8
        case .sick: return 0.5
        case .critical: return 0.0
        }
    }
}

/// 人口数据模型
struct Population: Codable, Identifiable {
    let id = UUID()
    var name: String
    var age: Int
    var job: PopulationJob?
    var education: EducationLevel
    var health: HealthStatus
    var happiness: Double
    var income: Double
    var residence: UUID? // 居住的建筑ID
    var workplace: UUID? // 工作的建筑ID
    
    init(name: String, age: Int = 25, education: EducationLevel = .elementary) {
        self.name = name
        self.age = age
        self.education = education
        self.health = .healthy
        self.happiness = 50.0
        self.income = 0.0
    }
    
    /// 更新人口属性
    mutating func updateAttributes() {
        // 更新年龄
        age += 1
        
        // 更新健康状况
        updateHealth()
        
        // 更新幸福感
        updateHappiness()
        
        // 更新收入
        updateIncome()
    }
    
    /// 更新健康状况
    private mutating func updateHealth() {
        let random = Double.random(in: 0...1)
        
        switch health {
        case .healthy:
            if random < 0.1 {
                health = .subhealthy
            }
        case .subhealthy:
            if random < 0.3 {
                health = .sick
            } else if random < 0.7 {
                health = .healthy
            }
        case .sick:
            if random < 0.2 {
                health = .critical
            } else if random < 0.6 {
                health = .subhealthy
            }
        case .critical:
            if random < 0.4 {
                health = .sick
            }
        }
    }
    
    /// 更新幸福感
    private mutating func updateHappiness() {
        var happinessChange = 0.0
        
        // 基于健康状况
        switch health {
        case .healthy:
            happinessChange += 2.0
        case .subhealthy:
            happinessChange -= 1.0
        case .sick:
            happinessChange -= 3.0
        case .critical:
            happinessChange -= 5.0
        }
        
        // 基于收入
        if income > 200 {
            happinessChange += 1.0
        } else if income < 100 {
            happinessChange -= 2.0
        }
        
        // 基于教育水平
        happinessChange += Double(education.level) * 0.5
        
        happiness = max(0, min(100, happiness + happinessChange))
    }
    
    /// 更新收入
    private mutating func updateIncome() {
        if let job = job {
            let baseIncome = job.income
            let educationMultiplier = 1.0 + Double(education.level) * 0.1
            let healthMultiplier = health.workEfficiency
            income = baseIncome * educationMultiplier * healthMultiplier
        } else {
            income = 0.0
        }
    }
    
    /// 检查是否可以工作
    func canWork() -> Bool {
        return health != .critical && job != nil
    }
    
    /// 获取工作效率
    func getWorkEfficiency() -> Double {
        return health.workEfficiency
    }
}

/// 人口管理器
class PopulationManager: ObservableObject {
    @Published var populations: [Population] = []
    @Published var totalPopulation: Int = 0
    @Published var employedPopulation: Int = 0
    @Published var unemployedPopulation: Int = 0
    @Published var averageHappiness: Double = 0.0
    @Published var averageEducation: Double = 0.0
    @Published var averageHealth: Double = 0.0
    
    private var happinessModifier: Double = 1.0
    
    init() {
        // 初始化一些人口
        addInitialPopulation()
    }
    
    /// 添加初始人口
    private func addInitialPopulation() {
        let initialNames = ["张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十"]
        
        for name in initialNames {
            let population = Population(name: name)
            populations.append(population)
        }
        
        updateStatistics()
    }
    
    /// 添加新人口
    func addPopulation(_ population: Population) {
        populations.append(population)
        updateStatistics()
    }
    
    /// 移除人口
    func removePopulation(_ population: Population) {
        populations.removeAll { $0.id == population.id }
        updateStatistics()
    }
    
    /// 更新人口状态
    func updatePopulationStatus() {
        for i in 0..<populations.count {
            populations[i].updateAttributes()
        }
        updateStatistics()
    }
    
    /// 更新幸福感修正器
    func updateHappinessModifier(_ modifier: Double) {
        happinessModifier = modifier
    }
    
    /// 更新统计数据
    private func updateStatistics() {
        totalPopulation = populations.count
        employedPopulation = populations.filter { $0.job != nil }.count
        unemployedPopulation = totalPopulation - employedPopulation
        
        if totalPopulation > 0 {
            averageHappiness = populations.map { $0.happiness }.reduce(0, +) / Double(totalPopulation)
            averageEducation = populations.map { Double($0.education.level) }.reduce(0, +) / Double(totalPopulation)
            averageHealth = populations.map { $0.health.workEfficiency }.reduce(0, +) / Double(totalPopulation)
        }
    }
    
    /// 分配工作
    func assignJob(to population: Population, job: PopulationJob, workplace: UUID) -> Bool {
        guard let index = populations.firstIndex(where: { $0.id == population.id }) else {
            return false
        }
        
        // 检查教育水平是否满足要求
        if population.education.level < job.requiredEducation.level {
            return false
        }
        
        populations[index].job = job
        populations[index].workplace = workplace
        updateStatistics()
        return true
    }
    
    /// 解除工作
    func removeJob(from population: Population) {
        guard let index = populations.firstIndex(where: { $0.id == population.id }) else {
            return
        }
        
        populations[index].job = nil
        populations[index].workplace = nil
        updateStatistics()
    }
    
    /// 提升教育水平
    func upgradeEducation(for population: Population, newLevel: EducationLevel) -> Bool {
        guard let index = populations.firstIndex(where: { $0.id == population.id }) else {
            return false
        }
        
        if newLevel.level > populations[index].education.level {
            populations[index].education = newLevel
            updateStatistics()
            return true
        }
        
        return false
    }
    
    /// 治疗人口
    func healPopulation(_ population: Population) -> Bool {
        guard let index = populations.firstIndex(where: { $0.id == population.id }) else {
            return false
        }
        
        if populations[index].health != .healthy {
            populations[index].health = .healthy
            updateStatistics()
            return true
        }
        
        return false
    }
    
    /// 获取人口统计信息
    func getPopulationStatistics() -> [String: Any] {
        let jobDistribution = Dictionary(grouping: populations, by: { $0.job?.displayName ?? "无业" })
            .mapValues { $0.count }
        
        let educationDistribution = Dictionary(grouping: populations, by: { $0.education.displayName })
            .mapValues { $0.count }
        
        let healthDistribution = Dictionary(grouping: populations, by: { $0.health.displayName })
            .mapValues { $0.count }
        
        return [
            "totalPopulation": totalPopulation,
            "employedPopulation": employedPopulation,
            "unemployedPopulation": unemployedPopulation,
            "averageHappiness": averageHappiness,
            "averageEducation": averageEducation,
            "averageHealth": averageHealth,
            "jobDistribution": jobDistribution,
            "educationDistribution": educationDistribution,
            "healthDistribution": healthDistribution
        ]
    }
}



