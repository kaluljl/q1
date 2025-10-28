//
//  BuildingDetailView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct BuildingDetailView: View {
    let building: Building
    @Binding var isPresented: Bool
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 0) {
            // 标题栏
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(building.type.displayName)
                        .font(.title2)
                        .fontWeight(.bold)
                    
                    Text("等级 \(building.level)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                Button(action: { isPresented = false }) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.title2)
                        .foregroundColor(.gray)
                }
            }
            .padding()
            
            // 建筑信息
            ScrollView {
                VStack(spacing: 16) {
                    // 建筑图标
                    BuildingIconView(building: building)
                    
                    // 基本信息
                    BuildingInfoSection(building: building)
                    
                    // 功能信息
                    BuildingFunctionSection(building: building)
                    
                    // 操作按钮
                    BuildingActionSection(building: building)
                }
                .padding()
            }
        }
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(.systemBackground))
        )
        .frame(maxWidth: 400, maxHeight: 600)
        .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 5)
    }
}

/// 建筑图标视图
struct BuildingIconView: View {
    let building: Building
    
    var body: some View {
        ZStack {
            Circle()
                .fill(getBuildingColor().opacity(0.2))
                .frame(width: 80, height: 80)
            
            Image(systemName: getBuildingIcon())
                .font(.system(size: 40))
                .foregroundColor(getBuildingColor())
        }
    }
    
    private func getBuildingIcon() -> String {
        switch building.type.category {
        case .residential:
            return "house"
        case .commercial:
            return "storefront"
        case .industrial:
            return "building.2"
        case .public:
            return "building"
        case .transportation:
            return "road.lanes"
        }
    }
    
    private func getBuildingColor() -> Color {
        switch building.type.category {
        case .residential:
            return .blue
        case .commercial:
            return .green
        case .industrial:
            return .orange
        case .public:
            return .purple
        case .transportation:
            return .gray
        }
    }
}

/// 建筑信息部分
struct BuildingInfoSection: View {
    let building: Building
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("建筑信息")
                .font(.headline)
                .foregroundColor(.primary)
            
            VStack(spacing: 8) {
                InfoRow(title: "类型", value: building.type.category.displayName)
                InfoRow(title: "等级", value: "\(building.level)")
                InfoRow(title: "建造时间", value: formatDate(building.buildTime))
                
                if let lastUpgradeTime = building.lastUpgradeTime {
                    InfoRow(title: "最后升级", value: formatDate(lastUpgradeTime))
                }
                
                InfoRow(title: "维护状态", value: building.isMaintenanceRequired ? "需要维护" : "正常")
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

/// 建筑功能部分
struct BuildingFunctionSection: View {
    let building: Building
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("功能信息")
                .font(.headline)
                .foregroundColor(.primary)
            
            VStack(spacing: 8) {
                if building.capacity > 0 {
                    InfoRow(title: "容量", value: "\(building.capacity)")
                }
                
                if building.income > 0 {
                    InfoRow(title: "收入", value: "\(Int(building.income))/小时")
                }
                
                if building.maintenanceCost > 0 {
                    InfoRow(title: "维护费用", value: "\(Int(building.maintenanceCost))/天")
                }
                
                if building.happinessBonus != 0 {
                    InfoRow(title: "幸福感", value: building.happinessBonus > 0 ? "+\(Int(building.happinessBonus))" : "\(Int(building.happinessBonus))")
                }
                
                if building.pollutionLevel > 0 {
                    InfoRow(title: "污染等级", value: "\(Int(building.pollutionLevel))")
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
    }
}

/// 建筑操作部分
struct BuildingActionSection: View {
    let building: Building
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 12) {
            Text("操作")
                .font(.headline)
                .foregroundColor(.primary)
            
            VStack(spacing: 8) {
                // 升级按钮
                if !building.isUpgrading {
                    ActionButton(
                        title: "升级",
                        icon: "arrow.up.circle",
                        color: .blue,
                        isEnabled: canUpgrade()
                    ) {
                        upgradeBuilding()
                    }
                } else {
                    ActionButton(
                        title: "升级中...",
                        icon: "clock",
                        color: .gray,
                        isEnabled: false
                    ) {}
                }
                
                // 维护按钮
                if building.isMaintenanceRequired {
                    ActionButton(
                        title: "维护",
                        icon: "wrench",
                        color: .orange,
                        isEnabled: true
                    ) {
                        maintainBuilding()
                    }
                }
                
                // 拆除按钮
                ActionButton(
                    title: "拆除",
                    icon: "trash",
                    color: .red,
                    isEnabled: true
                ) {
                    demolishBuilding()
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
    }
    
    private func canUpgrade() -> Bool {
        let upgradeCost = building.getUpgradeCost()
        return gameManager.resources.hasEnoughResources(upgradeCost)
    }
    
    private func upgradeBuilding() {
        if gameManager.buildings.upgradeBuilding(building) {
            // 消耗资源
            let upgradeCost = building.getUpgradeCost()
            gameManager.resources.spendResources(upgradeCost)
        }
    }
    
    private func maintainBuilding() {
        gameManager.buildings.maintainBuilding(building)
    }
    
    private func demolishBuilding() {
        gameManager.buildings.demolishBuilding(building)
    }
}

/// 信息行
struct InfoRow: View {
    let title: String
    let value: String
    
    var body: some View {
        HStack {
            Text(title)
                .foregroundColor(.secondary)
            
            Spacer()
            
            Text(value)
                .fontWeight(.medium)
                .foregroundColor(.primary)
        }
    }
}

/// 操作按钮
struct ActionButton: View {
    let title: String
    let icon: String
    let color: Color
    let isEnabled: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: icon)
                Text(title)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(isEnabled ? color : Color.gray)
            )
            .foregroundColor(.white)
        }
        .disabled(!isEnabled)
    }
}

struct BuildingDetailView_Previews: PreviewProvider {
    static var previews: some View {
        let building = Building(type: .house, level: 1, position: GridPosition(x: 0, y: 0))
        
        BuildingDetailView(building: building, isPresented: .constant(true))
            .environmentObject(GameManager())
    }
}
