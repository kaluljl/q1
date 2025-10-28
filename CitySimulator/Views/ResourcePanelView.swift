//
//  ResourcePanelView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct ResourcePanelView: View {
    @EnvironmentObject var gameManager: GameManager
    @Environment(\.dismiss) private var dismiss
    @State private var selectedCategory: ResourceCategory = .basic
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 资源概览
                ResourceOverviewSection()
                
                // 分类选择器
                ResourceCategorySelector(selectedCategory: $selectedCategory)
                
                // 资源列表
                ResourceListView(category: selectedCategory)
            }
            .navigationTitle("资源管理")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("完成") {
                        dismiss()
                    }
                }
            }
        }
    }
}

/// 资源概览部分
struct ResourceOverviewSection: View {
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 16) {
            // 总价值
            HStack {
                VStack(alignment: .leading) {
                    Text("总资源价值")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text("\(formatNumber(gameManager.resources.calculateTotalValue()))")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
                
                Spacer()
                
                VStack(alignment: .trailing) {
                    Text("存储使用率")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Text("\(Int(getStorageUsagePercentage() * 100))%")
                        .font(.headline)
                        .foregroundColor(getStorageUsagePercentage() > 0.8 ? .red : .green)
                }
            }
            
            // 存储进度条
            ProgressView(value: getStorageUsagePercentage())
                .progressViewStyle(LinearProgressViewStyle(tint: getStorageUsagePercentage() > 0.8 ? .red : .blue))
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
        .padding()
    }
    
    private func getStorageUsagePercentage() -> Double {
        var totalUsed = 0.0
        var totalCapacity = 0.0
        
        for resourceType in ResourceType.allCases {
            let used = gameManager.resources.resources[resourceType] ?? 0
            let capacity = gameManager.resources.maxCapacities[resourceType] ?? 1000
            
            totalUsed += used
            totalCapacity += capacity
        }
        
        return totalCapacity > 0 ? totalUsed / totalCapacity : 0.0
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

/// 资源分类选择器
struct ResourceCategorySelector: View {
    @Binding var selectedCategory: ResourceCategory
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(ResourceCategory.allCases, id: \.self) { category in
                    CategoryButton(
                        category: category,
                        isSelected: selectedCategory == category
                    ) {
                        selectedCategory = category
                    }
                }
            }
            .padding(.horizontal, 16)
        }
        .padding(.vertical, 8)
    }
}

/// 分类按钮
struct CategoryButton: View {
    let category: ResourceCategory
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: getCategoryIcon())
                    .font(.title2)
                    .foregroundColor(isSelected ? .white : getCategoryColor())
                
                Text(category.displayName)
                    .font(.caption)
                    .foregroundColor(isSelected ? .white : .primary)
            }
            .frame(width: 80, height: 60)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? getCategoryColor() : Color(.systemGray6))
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    private func getCategoryIcon() -> String {
        switch category {
        case .basic:
            return "cube.box"
        case .special:
            return "sparkles"
        case .energy:
            return "bolt"
        case .currency:
            return "dollarsign.circle"
        }
    }
    
    private func getCategoryColor() -> Color {
        switch category {
        case .basic:
            return .blue
        case .special:
            return .purple
        case .energy:
            return .yellow
        case .currency:
            return .green
        }
    }
}

/// 资源列表视图
struct ResourceListView: View {
    let category: ResourceCategory
    @EnvironmentObject var gameManager: GameManager
    
    private var filteredResources: [ResourceType] {
        ResourceType.allCases.filter { $0.category == category }
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(filteredResources, id: \.self) { resourceType in
                    ResourceRowView(resourceType: resourceType)
                }
            }
            .padding()
        }
    }
}

/// 资源行视图
struct ResourceRowView: View {
    let resourceType: ResourceType
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        HStack(spacing: 16) {
            // 资源图标
            ZStack {
                Circle()
                    .fill(getResourceColor().opacity(0.2))
                    .frame(width: 50, height: 50)
                
                Image(systemName: resourceType.iconName)
                    .font(.title2)
                    .foregroundColor(getResourceColor())
            }
            
            // 资源信息
            VStack(alignment: .leading, spacing: 4) {
                Text(resourceType.displayName)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                Text("稀有度: \(resourceType.rarity.displayName)")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            // 数量和进度
            VStack(alignment: .trailing, spacing: 4) {
                Text(formatNumber(getCurrentAmount()))
                    .font(.headline)
                    .foregroundColor(.primary)
                
                Text("/ \(formatNumber(getMaxCapacity()))")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                // 进度条
                ProgressView(value: getStoragePercentage())
                    .progressViewStyle(LinearProgressViewStyle(tint: getResourceColor()))
                    .frame(width: 100)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
                .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
    }
    
    private func getCurrentAmount() -> Double {
        return gameManager.resources.resources[resourceType] ?? 0
    }
    
    private func getMaxCapacity() -> Double {
        return gameManager.resources.maxCapacities[resourceType] ?? 1000
    }
    
    private func getStoragePercentage() -> Double {
        let current = getCurrentAmount()
        let max = getMaxCapacity()
        return max > 0 ? current / max : 0.0
    }
    
    private func getResourceColor() -> Color {
        switch resourceType.rarity {
        case .common:
            return .gray
        case .uncommon:
            return .green
        case .rare:
            return .blue
        case .epic:
            return .purple
        case .legendary:
            return .orange
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

struct ResourcePanelView_Previews: PreviewProvider {
    static var previews: some View {
        ResourcePanelView()
            .environmentObject(GameManager())
    }
}
