//
//  BuildingMenuView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct BuildingMenuView: View {
    @EnvironmentObject var gameManager: GameManager
    @Environment(\.dismiss) private var dismiss
    @State private var selectedCategory: BuildingCategory = .residential
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 分类选择器
                CategorySelector(selectedCategory: $selectedCategory)
                
                // 建筑列表
                BuildingListView(
                    category: selectedCategory,
                    availableBuildings: gameManager.buildings.availableBuildingTypes
                )
            }
            .navigationTitle("建造建筑")
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

/// 分类选择器
struct CategorySelector: View {
    @Binding var selectedCategory: BuildingCategory
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(BuildingCategory.allCases, id: \.self) { category in
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
    let category: BuildingCategory
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
    
    private func getCategoryColor() -> Color {
        switch category {
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

/// 建筑列表视图
struct BuildingListView: View {
    let category: BuildingCategory
    let availableBuildings: [BuildingType]
    @EnvironmentObject var gameManager: GameManager
    
    private var filteredBuildings: [BuildingType] {
        availableBuildings.filter { $0.category == category }
    }
    
    var body: some View {
        ScrollView {
            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 2), spacing: 16) {
                ForEach(filteredBuildings, id: \.self) { buildingType in
                    BuildingCardView(buildingType: buildingType)
                }
            }
            .padding()
        }
    }
}

/// 建筑卡片视图
struct BuildingCardView: View {
    let buildingType: BuildingType
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 12) {
            // 建筑图标
            ZStack {
                Circle()
                    .fill(getBuildingColor().opacity(0.2))
                    .frame(width: 60, height: 60)
                
                Image(systemName: getBuildingIcon())
                    .font(.system(size: 30))
                    .foregroundColor(getBuildingColor())
            }
            
            // 建筑信息
            VStack(spacing: 4) {
                Text(buildingType.displayName)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                Text("等级 \(buildingType.unlockLevel)")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            // 建造成本
            VStack(spacing: 2) {
                Text("建造成本")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                let buildCost = getBuildCost()
                ForEach(Array(buildCost.keys), id: \.self) { resourceType in
                    HStack {
                        Image(systemName: resourceType.iconName)
                            .foregroundColor(resourceType.category == .currency ? .yellow : .blue)
                            .font(.caption)
                        
                        Text("\(Int(buildCost[resourceType] ?? 0))")
                            .font(.caption)
                            .foregroundColor(.primary)
                    }
                }
            }
            
            // 建造按钮
            Button(action: {
                startBuilding()
            }) {
                Text("建造")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .fill(canBuild() ? getBuildingColor() : Color.gray)
                    )
            }
            .disabled(!canBuild())
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(.systemBackground))
                .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
        )
    }
    
    private func getBuildingIcon() -> String {
        switch buildingType.category {
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
        switch buildingType.category {
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
    
    private func getBuildCost() -> [ResourceType: Double] {
        let building = Building(type: buildingType, position: GridPosition(x: 0, y: 0))
        return building.getBuildCost()
    }
    
    private func canBuild() -> Bool {
        let buildCost = getBuildCost()
        return gameManager.resources.hasEnoughResources(buildCost)
    }
    
    private func startBuilding() {
        gameManager.buildings.startBuildingMode(buildingType: buildingType)
    }
}

struct BuildingMenuView_Previews: PreviewProvider {
    static var previews: some View {
        BuildingMenuView()
            .environmentObject(GameManager())
    }
}
