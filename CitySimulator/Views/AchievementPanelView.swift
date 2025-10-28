//
//  AchievementPanelView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct AchievementPanelView: View {
    @EnvironmentObject var gameManager: GameManager
    @Environment(\.dismiss) private var dismiss
    @State private var selectedAchievementType: AchievementType = .building
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 成就统计
                AchievementStatisticsSection()
                
                // 成就类型选择器
                AchievementTypeSelector(selectedAchievementType: $selectedAchievementType)
                
                // 成就列表
                AchievementListView(achievementType: selectedAchievementType)
            }
            .navigationTitle("成就中心")
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

/// 成就统计部分
struct AchievementStatisticsSection: View {
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 16) {
            // 成就完成统计
            HStack {
                VStack(alignment: .leading) {
                    Text("成就解锁")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text("\(getUnlockedAchievementCount())/\(getTotalAchievementCount())")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
                
                Spacer()
                
                VStack(alignment: .trailing) {
                    Text("解锁率")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Text("\(Int(getUnlockPercentage() * 100))%")
                        .font(.headline)
                        .foregroundColor(.green)
                }
            }
            
            // 进度条
            ProgressView(value: getUnlockPercentage())
                .progressViewStyle(LinearProgressViewStyle(tint: .blue))
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
        .padding()
    }
    
    private func getTotalAchievementCount() -> Int {
        return gameManager.achievements.achievements.count
    }
    
    private func getUnlockedAchievementCount() -> Int {
        return gameManager.achievements.achievements.filter { $0.isUnlocked }.count
    }
    
    private func getUnlockPercentage() -> Double {
        let total = getTotalAchievementCount()
        let unlocked = getUnlockedAchievementCount()
        return total > 0 ? Double(unlocked) / Double(total) : 0.0
    }
}

/// 成就类型选择器
struct AchievementTypeSelector: View {
    @Binding var selectedAchievementType: AchievementType
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(AchievementType.allCases, id: \.self) { achievementType in
                    AchievementTypeButton(
                        achievementType: achievementType,
                        isSelected: selectedAchievementType == achievementType
                    ) {
                        selectedAchievementType = achievementType
                    }
                }
            }
            .padding(.horizontal, 16)
        }
        .padding(.vertical, 8)
    }
}

/// 成就类型按钮
struct AchievementTypeButton: View {
    let achievementType: AchievementType
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: getAchievementTypeIcon())
                    .font(.title2)
                    .foregroundColor(isSelected ? .white : getAchievementTypeColor())
                
                Text(achievementType.displayName)
                    .font(.caption)
                    .foregroundColor(isSelected ? .white : .primary)
            }
            .frame(width: 80, height: 60)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? getAchievementTypeColor() : Color(.systemGray6))
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    private func getAchievementTypeIcon() -> String {
        switch achievementType {
        case .building:
            return "building"
        case .resource:
            return "cube.box"
        case .population:
            return "person.3"
        case .economy:
            return "dollarsign.circle"
        case .task:
            return "list.bullet"
        case .social:
            return "person.2"
        case .special:
            return "star"
        }
    }
    
    private func getAchievementTypeColor() -> Color {
        switch achievementType {
        case .building:
            return .blue
        case .resource:
            return .green
        case .population:
            return .orange
        case .economy:
            return .yellow
        case .task:
            return .purple
        case .social:
            return .pink
        case .special:
            return .red
        }
    }
}

/// 成就列表视图
struct AchievementListView: View {
    let achievementType: AchievementType
    @EnvironmentObject var gameManager: GameManager
    
    private var filteredAchievements: [Achievement] {
        gameManager.achievements.achievements.filter { $0.type == achievementType }
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(filteredAchievements) { achievement in
                    AchievementRowView(achievement: achievement)
                }
            }
            .padding()
        }
    }
}

/// 成就行视图
struct AchievementRowView: View {
    let achievement: Achievement
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 12) {
            // 成就标题和状态
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(achievement.title)
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text(achievement.description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                AchievementStatusBadge(isUnlocked: achievement.isUnlocked, rarity: achievement.rarity)
            }
            
            // 成就进度
            if !achievement.isUnlocked {
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text("进度")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Spacer()
                        
                        Text("\(Int(achievement.getProgressPercentage() * 100))%")
                            .font(.caption)
                            .foregroundColor(.primary)
                    }
                    
                    ProgressView(value: achievement.getProgressPercentage())
                        .progressViewStyle(LinearProgressViewStyle(tint: getRarityColor()))
                }
            }
            
            // 成就奖励
            AchievementRewardView(rewards: achievement.rewards)
            
            // 解锁时间
            if achievement.isUnlocked, let unlockedTime = achievement.unlockedTime {
                HStack {
                    Text("解锁时间:")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Text(formatDate(unlockedTime))
                        .font(.caption)
                        .foregroundColor(.primary)
                    
                    Spacer()
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
                .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
    }
    
    private func getRarityColor() -> Color {
        switch achievement.rarity {
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
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

/// 成就状态徽章
struct AchievementStatusBadge: View {
    let isUnlocked: Bool
    let rarity: AchievementRarity
    
    var body: some View {
        VStack(spacing: 4) {
            Image(systemName: isUnlocked ? "checkmark.circle.fill" : "lock.circle.fill")
                .font(.title2)
                .foregroundColor(isUnlocked ? .green : .gray)
            
            Text(isUnlocked ? "已解锁" : "未解锁")
                .font(.caption)
                .foregroundColor(.white)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(isUnlocked ? .green : .gray)
                )
        }
    }
}

/// 成就奖励视图
struct AchievementRewardView: View {
    let rewards: TaskReward
    
    var body: some View {
        HStack {
            Text("奖励:")
                .font(.caption)
                .foregroundColor(.secondary)
            
            if rewards.gold > 0 {
                HStack(spacing: 2) {
                    Image(systemName: "dollarsign.circle")
                        .foregroundColor(.yellow)
                        .font(.caption)
                    Text("\(Int(rewards.gold))")
                        .font(.caption)
                        .foregroundColor(.primary)
                }
            }
            
            if rewards.diamond > 0 {
                HStack(spacing: 2) {
                    Image(systemName: "diamond.fill")
                        .foregroundColor(.blue)
                        .font(.caption)
                    Text("\(Int(rewards.diamond))")
                        .font(.caption)
                        .foregroundColor(.primary)
                }
            }
            
            if rewards.experience > 0 {
                HStack(spacing: 2) {
                    Image(systemName: "star.fill")
                        .foregroundColor(.orange)
                        .font(.caption)
                    Text("\(rewards.experience)")
                        .font(.caption)
                        .foregroundColor(.primary)
                }
            }
            
            Spacer()
        }
    }
}

struct AchievementPanelView_Previews: PreviewProvider {
    static var previews: some View {
        AchievementPanelView()
            .environmentObject(GameManager())
    }
}
