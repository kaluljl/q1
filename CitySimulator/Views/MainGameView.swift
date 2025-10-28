//
//  MainGameView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI
import Combine
import AVFoundation

struct MainGameView: View {
    @EnvironmentObject var gameManager: GameManager
    @StateObject private var resourceManager = GameResourcesManager.shared
    @State private var selectedTab = 0
    @State private var showingBuildingMenu = false
    @State private var showingResourcePanel = false
    @State private var showingTaskPanel = false
    @State private var showingAchievementPanel = false
    @State private var currentTime = Date()
    @State private var weatherType: WeatherType = .sunny
    @State private var animationOffset: CGFloat = 0
    
    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        ZStack {
            // 动态背景
            DynamicBackgroundView(weatherType: weatherType, time: currentTime)
                .ignoresSafeArea()
            
            // 天气效果
            WeatherEffectView(weatherType: weatherType)
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                // 顶部状态栏
                TopStatusBar()
                
                // 主游戏区域
                GameArea()
                
                // 底部控制栏
                BottomControlBar(
                    selectedTab: $selectedTab,
                    showingBuildingMenu: $showingBuildingMenu,
                    showingResourcePanel: $showingResourcePanel,
                    showingTaskPanel: $showingTaskPanel,
                    showingAchievementPanel: $showingAchievementPanel
                )
            }
        }
        .onReceive(timer) { _ in
            currentTime = Date()
            updateWeather()
        }
        .sheet(isPresented: $showingBuildingMenu) {
            BuildingMenuView()
        }
        .sheet(isPresented: $showingResourcePanel) {
            ResourcePanelView()
        }
        .sheet(isPresented: $showingTaskPanel) {
            TaskPanelView()
        }
        .sheet(isPresented: $showingAchievementPanel) {
            AchievementPanelView()
        }
    }
    
    private func updateWeather() {
        // 随机天气变化
        if Int.random(in: 1...100) == 1 {
            weatherType = WeatherType.allCases.randomElement() ?? .sunny
        }
    }
}

/// 顶部状态栏
struct TopStatusBar: View {
    @EnvironmentObject var gameManager: GameManager
    @State private var currentTime = Date()
    @State private var weatherType: WeatherType = .sunny
    @State private var pulseAnimation = false
    
    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        HStack {
            // 城市信息
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(gameManager.currentCity.name)
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    // 城市等级徽章
                    CityLevelBadge(level: gameManager.currentCity.level)
                }
                
                HStack {
                    Text("等级 \(gameManager.currentCity.level)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    Text("经验 \(gameManager.currentCity.experience)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                // 经验进度条
                ExperienceProgressBar(
                    current: gameManager.currentCity.experience,
                    required: gameManager.currentCity.experienceRequiredForNextLevel
                )
            }
            
            Spacer()
            
            // 时间和天气
            VStack(alignment: .trailing, spacing: 4) {
                HStack(spacing: 8) {
                    // 时间显示
                    TimeDisplayView(time: currentTime)
                    
                    // 天气显示
                    WeatherDisplayView(weatherType: weatherType)
                }
                
                // 资源与繁荣度显示
                HStack(spacing: 12) {
                    ResourceIconView(type: .gold, amount: gameManager.resources.resources[.gold] ?? 0)
                    ResourceIconView(type: .diamond, amount: gameManager.resources.resources[.diamond] ?? 0)
                    
                    // 繁荣度紧凑显示（使用引擎实时计算结果）
                    HStack(spacing: 4) {
                        Image(systemName: "chart.line.uptrend.xyaxis")
                            .foregroundColor(.green)
                            .font(.caption)
                        Text("繁荣度 \(gameManager.prosperity.score) · \(gameManager.prosperity.label)")
                            .font(.caption)
                            .fontWeight(.medium)
                            .foregroundColor(.primary)
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .fill(Color(.systemGray6))
                    )
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(.ultraThinMaterial)
                .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)
        )
        .padding(.horizontal, 16)
        .padding(.top, 8)
        .onReceive(timer) { _ in
            currentTime = Date()
        }
    }
}

/// 资源图标视图
struct ResourceIconView: View {
    let type: ResourceType
    let amount: Double
    
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: getResourceIcon())
                .foregroundColor(getResourceColor())
                .font(.caption)
            
            Text(formatNumber(amount))
                .font(.caption)
                .fontWeight(.medium)
                .foregroundColor(.primary)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(.systemGray6))
        )
    }
    
    private func getResourceIcon() -> String {
        switch type {
        case .gold: return GameIcons.gold
        case .diamond: return GameIcons.diamond
        case .wood: return GameIcons.wood
        case .stone: return GameIcons.stone
        case .steel: return GameIcons.steel
        case .food: return GameIcons.food
        default: return "cube.fill"
        }
    }
    
    private func getResourceColor() -> Color {
        switch type {
        case .gold: return GameColors.gold
        case .diamond: return GameColors.diamond
        case .wood: return GameColors.wood
        case .stone: return GameColors.stone
        case .steel: return GameColors.steel
        case .food: return GameColors.food
        default: return .blue
        }
    }
    
    private func formatNumber(_ number: Double) -> String {
        if number >= 1000000 {
            return String(format: "%.1fM", number / 1000000)
        } else if number >= 1000 {
            return String(format: "%.1fK", number / 1000)
        } else {
            return String(format: "%.0f", number)
        }
    }
}

/// 游戏区域
struct GameArea: View {
    @EnvironmentObject var gameManager: GameManager
    @State private var selectedBuilding: Building?
    @State private var showingBuildingDetails = false
    
    var body: some View {
        ZStack {
            // 城市网格
            CityGridView(
                buildings: gameManager.buildings.buildings,
                selectedBuilding: $selectedBuilding,
                showingBuildingDetails: $showingBuildingDetails
            )
            
            // 建筑详情弹窗
            if showingBuildingDetails, let building = selectedBuilding {
                BuildingDetailView(
                    building: building,
                    isPresented: $showingBuildingDetails
                )
                .transition(.scale.combined(with: .opacity))
            }
        }
        .onTapGesture {
            if gameManager.buildings.isBuildingMode {
                // 处理建筑放置
                gameManager.buildings.endBuildingMode()
            }
        }
    }
}

/// 城市网格视图
struct CityGridView: View {
    let buildings: [Building]
    @Binding var selectedBuilding: Building?
    @Binding var showingBuildingDetails: Bool
    @State private var gridSize: CGFloat = 20
    
    var body: some View {
        ScrollView([.horizontal, .vertical]) {
            LazyVGrid(columns: Array(repeating: GridItem(.fixed(60), spacing: 2), count: 20), spacing: 2) {
                ForEach(0..<400, id: \.self) { index in
                    let x = index % 20
                    let y = index / 20
                    let position = GridPosition(x: x, y: y)
                    
                    GridCellView(
                        position: position,
                        building: buildings.first { $0.position == position },
                        onTap: {
                            if let building = buildings.first(where: { $0.position == position }) {
                                selectedBuilding = building
                                showingBuildingDetails = true
                            }
                        }
                    )
                }
            }
            .padding()
        }
        .background(Color.green.opacity(0.1))
    }
}

/// 网格单元格视图
struct GridCellView: View {
    let position: GridPosition
    let building: Building?
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            ZStack {
                // 基础网格
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.green.opacity(0.3))
                    .frame(width: 60, height: 60)
                
                // 建筑
                if let building = building {
                    BuildingView(building: building)
                }
                
                // 网格线
                RoundedRectangle(cornerRadius: 4)
                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    .frame(width: 60, height: 60)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}

/// 建筑视图
struct BuildingView: View {
    let building: Building
    @State private var glowAnimation = false
    @State private var scaleAnimation = false
    
    var body: some View {
        ZStack {
            // 建筑背景
            RoundedRectangle(cornerRadius: 8)
                .fill(buildingBackgroundColor)
                .frame(width: 50, height: 50)
                .shadow(color: buildingShadowColor, radius: 4, x: 0, y: 2)
            
            // 建筑图标
            Image(systemName: getBuildingIcon())
                .font(.title2)
                .foregroundColor(getBuildingColor())
                .scaleEffect(scaleAnimation ? 1.2 : 1.0)
            
            // 建筑等级指示器
            if building.level > 1 {
                VStack {
                    HStack {
                        Spacer()
                        Text("\(building.level)")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .padding(2)
                            .background(Circle().fill(.red))
                    }
                    Spacer()
                }
                .frame(width: 50, height: 50)
            }
            
            // 状态指示器
            VStack {
                HStack {
                    Spacer()
                    StatusIndicatorView(building: building)
                }
                Spacer()
            }
            .frame(width: 50, height: 50)
        }
        .scaleEffect(glowAnimation ? 1.05 : 1.0)
        .onAppear {
            startAnimations()
        }
    }
    
    private func startAnimations() {
        // 发光动画
        withAnimation(.easeInOut(duration: 2).repeatForever(autoreverses: true)) {
            glowAnimation = true
        }
        
        // 缩放动画
        withAnimation(.easeInOut(duration: 1.5).repeatForever(autoreverses: true)) {
            scaleAnimation = true
        }
    }
    
    private func getBuildingIcon() -> String {
        switch building.type {
        case .house: return GameIcons.house
        case .apartment: return GameIcons.apartment
        case .villa: return GameIcons.villa
        case .skyscraper: return GameIcons.skyscraper
        case .shop: return GameIcons.shop
        case .supermarket: return GameIcons.supermarket
        case .mall: return GameIcons.mall
        case .restaurant: return GameIcons.restaurant
        case .hotel: return GameIcons.hotel
        case .lumberMill: return GameIcons.lumberMill
        case .quarry: return GameIcons.quarry
        case .steelMill: return GameIcons.steelMill
        case .foodFactory: return GameIcons.foodFactory
        case .powerPlant: return GameIcons.powerPlant
        case .school: return GameIcons.school
        case .hospital: return GameIcons.hospital
        case .policeStation: return GameIcons.policeStation
        case .fireStation: return GameIcons.fireStation
        case .park: return GameIcons.park
        case .library: return GameIcons.library
        case .road: return GameIcons.road
        case .bridge: return GameIcons.bridge
        case .busStop: return GameIcons.busStop
        case .subwayStation: return GameIcons.subwayStation
        }
    }
    
    private func getBuildingColor() -> Color {
        switch building.type.category {
        case .residential:
            return GameColors.residential
        case .commercial:
            return GameColors.commercial
        case .industrial:
            return GameColors.industrial
        case .public:
            return GameColors.publicFacility
        case .transportation:
            return GameColors.transportation
        }
    }
    
    private var buildingBackgroundColor: Color {
        getBuildingColor().opacity(0.2)
    }
    
    private var buildingShadowColor: Color {
        getBuildingColor().opacity(0.3)
    }
}

/// 建筑状态指示器
struct StatusIndicatorView: View {
    let building: Building
    @State private var pulseAnimation = false
    
    var body: some View {
        VStack(spacing: 2) {
            if building.isUnderConstruction {
                StatusDot(color: GameColors.construction, icon: GameIcons.construction)
            }
            
            if building.isUpgrading {
                StatusDot(color: GameColors.upgrade, icon: GameIcons.upgrade)
            }
            
            if building.isMaintenanceRequired {
                StatusDot(color: GameColors.maintenance, icon: GameIcons.maintenance)
            }
        }
        .scaleEffect(pulseAnimation ? 1.2 : 1.0)
        .onAppear {
            withAnimation(GameAnimations.pulse) {
                pulseAnimation = true
            }
        }
    }
}

/// 状态点
struct StatusDot: View {
    let color: Color
    let icon: String
    
    var body: some View {
        Image(systemName: icon)
            .font(.caption2)
            .foregroundColor(.white)
            .frame(width: 12, height: 12)
            .background(Circle().fill(color))
            .shadow(color: color.opacity(0.5), radius: 2, x: 0, y: 1)
    }
}

/// 底部控制栏
struct BottomControlBar: View {
    @Binding var selectedTab: Int
    @Binding var showingBuildingMenu: Bool
    @Binding var showingResourcePanel: Bool
    @Binding var showingTaskPanel: Bool
    @Binding var showingAchievementPanel: Bool
    
    var body: some View {
        HStack(spacing: 20) {
            // 建筑菜单
            EnhancedControlButton(
                icon: "plus.circle.fill",
                title: "建造",
                color: .blue,
                badgeCount: getBuildingBadgeCount()
            ) {
                showingBuildingMenu = true
            }
            
            // 资源面板
            EnhancedControlButton(
                icon: "cube.box.fill",
                title: "资源",
                color: .green,
                badgeCount: getResourceBadgeCount()
            ) {
                showingResourcePanel = true
            }
            
            // 任务面板
            EnhancedControlButton(
                icon: "list.bullet.rectangle.fill",
                title: "任务",
                color: .orange,
                badgeCount: getTaskBadgeCount()
            ) {
                showingTaskPanel = true
            }
            
            // 成就面板
            EnhancedControlButton(
                icon: "trophy.fill",
                title: "成就",
                color: .yellow,
                badgeCount: getAchievementBadgeCount()
            ) {
                showingAchievementPanel = true
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 16)
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(.ultraThinMaterial)
                .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: -5)
        )
        .padding(.horizontal, 16)
        .padding(.bottom, 8)
    }
    
    private func getBuildingBadgeCount() -> Int {
        // 返回需要建造的建筑数量
        return 0
    }
    
    private func getResourceBadgeCount() -> Int {
        // 返回资源警告数量
        return 0
    }
    
    private func getTaskBadgeCount() -> Int {
        // 返回可接取的任务数量
        return 0
    }
    
    private func getAchievementBadgeCount() -> Int {
        // 返回可解锁的成就数量
        return 0
    }
}

/// 增强的控制按钮
struct EnhancedControlButton: View {
    let icon: String
    let title: String
    let color: Color
    let badgeCount: Int
    let action: () -> Void
    
    @State private var isPressed = false
    @State private var glowAnimation = false
    @StateObject private var resourceManager = GameResourcesManager.shared
    
    var body: some View {
        Button(action: {
            // 触觉反馈
            let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
            impactFeedback.impactOccurred()
            
            // 播放点击音效
            resourceManager.playClickSound()
            
            action()
        }) {
            VStack(spacing: 6) {
                ZStack {
                    // 按钮背景
                    Circle()
                        .fill(color.opacity(0.2))
                        .frame(width: 50, height: 50)
                        .shadow(color: color.opacity(0.3), radius: 4, x: 0, y: 2)
                    
                    // 图标
                    Image(systemName: icon)
                        .font(.title2)
                        .foregroundColor(color)
                        .scaleEffect(isPressed ? 0.9 : 1.0)
                    
                    // 徽章
                    if badgeCount > 0 {
                        VStack {
                            HStack {
                                Spacer()
                                BadgeView(count: badgeCount)
                            }
                            Spacer()
                        }
                        .frame(width: 50, height: 50)
                    }
                }
                
                Text(title)
                    .font(GameFonts.caption)
                    .fontWeight(.medium)
                    .foregroundColor(.primary)
            }
            .frame(maxWidth: .infinity)
            .scaleEffect(isPressed ? 0.95 : 1.0)
            .scaleEffect(glowAnimation ? 1.05 : 1.0)
        }
        .buttonStyle(PlainButtonStyle())
        .onLongPressGesture(minimumDuration: 0, maximumDistance: .infinity, pressing: { pressing in
            withAnimation(GameAnimations.buttonPress) {
                isPressed = pressing
            }
        }, perform: {})
        .onAppear {
            withAnimation(GameAnimations.glow) {
                glowAnimation = true
            }
        }
    }
}

/// 徽章视图
struct BadgeView: View {
    let count: Int
    
    var body: some View {
        Text("\(count)")
            .font(.caption2)
            .fontWeight(.bold)
            .foregroundColor(.white)
            .frame(minWidth: 16, minHeight: 16)
            .padding(.horizontal, 4)
            .background(
                Capsule()
                    .fill(.red)
                    .shadow(color: .red.opacity(0.5), radius: 2, x: 0, y: 1)
            )
            .offset(x: 15, y: -15)
    }
}

struct MainGameView_Previews: PreviewProvider {
    static var previews: some View {
        MainGameView()
            .environmentObject(GameManager())
    }
}

// MARK: - 新增UI组件

/// 城市等级徽章
struct CityLevelBadge: View {
    let level: Int
    @State private var pulseAnimation = false
    
    var body: some View {
        Text("Lv.\(level)")
            .font(.caption)
            .fontWeight(.bold)
            .foregroundColor(.white)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(levelColor)
                    .shadow(color: levelColor.opacity(0.3), radius: 4, x: 0, y: 2)
            )
            .scaleEffect(pulseAnimation ? 1.1 : 1.0)
            .onAppear {
                withAnimation(.easeInOut(duration: 1.5).repeatForever(autoreverses: true)) {
                    pulseAnimation = true
                }
            }
    }
    
    private var levelColor: Color {
        switch level {
        case 1...5: return .green
        case 6...10: return .blue
        case 11...20: return .purple
        case 21...30: return .orange
        default: return .red
        }
    }
}

/// 经验进度条
struct ExperienceProgressBar: View {
    let current: Int
    let required: Int
    
    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            HStack {
                Text("经验")
                    .font(.caption2)
                    .foregroundColor(.secondary)
                
                Spacer()
                
                Text("\(current)/\(required)")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            ProgressView(value: Double(current), total: Double(required))
                .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                .scaleEffect(y: 0.8)
        }
    }
}

/// 时间显示视图
struct TimeDisplayView: View {
    let time: Date
    
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: "clock")
                .foregroundColor(.blue)
                .font(.caption)
            
            Text(timeFormatter.string(from: time))
                .font(.caption)
                .fontWeight(.medium)
                .foregroundColor(.primary)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(.systemGray6))
        )
    }
    
    private var timeFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter
    }
}

/// 天气显示视图
struct WeatherDisplayView: View {
    let weatherType: WeatherType
    
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: getWeatherIcon())
                .foregroundColor(getWeatherColor())
                .font(.caption)
            
            Text(weatherType.displayName)
                .font(.caption)
                .fontWeight(.medium)
                .foregroundColor(.primary)
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(.systemGray6))
        )
    }
    
    private func getWeatherIcon() -> String {
        switch weatherType {
        case .sunny: return GameIcons.sunny
        case .cloudy: return GameIcons.cloudy
        case .rainy: return GameIcons.rainy
        case .stormy: return GameIcons.stormy
        case .snowy: return GameIcons.snowy
        case .foggy: return GameIcons.foggy
        }
    }
    
    private func getWeatherColor() -> Color {
        switch weatherType {
        case .sunny: return GameColors.sunny
        case .cloudy: return GameColors.cloudy
        case .rainy: return GameColors.rainy
        case .stormy: return GameColors.stormy
        case .snowy: return GameColors.snowy
        case .foggy: return GameColors.foggy
        }
    }
}
