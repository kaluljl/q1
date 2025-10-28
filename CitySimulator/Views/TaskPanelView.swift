//
//  TaskPanelView.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

struct TaskPanelView: View {
    @EnvironmentObject var gameManager: GameManager
    @Environment(\.dismiss) private var dismiss
    @State private var selectedTaskType: TaskType = .main
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 任务统计
                TaskStatisticsSection()
                
                // 任务类型选择器
                TaskTypeSelector(selectedTaskType: $selectedTaskType)
                
                // 任务列表
                TaskListView(taskType: selectedTaskType)
            }
            .navigationTitle("任务中心")
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

/// 任务统计部分
struct TaskStatisticsSection: View {
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 16) {
            // 任务完成统计
            HStack {
                VStack(alignment: .leading) {
                    Text("任务完成")
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text("\(getCompletedTaskCount())/\(getTotalTaskCount())")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
                
                Spacer()
                
                VStack(alignment: .trailing) {
                    Text("完成率")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Text("\(Int(getCompletionPercentage() * 100))%")
                        .font(.headline)
                        .foregroundColor(.green)
                }
            }
            
            // 进度条
            ProgressView(value: getCompletionPercentage())
                .progressViewStyle(LinearProgressViewStyle(tint: .blue))
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
        .padding()
    }
    
    private func getTotalTaskCount() -> Int {
        return gameManager.tasks.tasks.count
    }
    
    private func getCompletedTaskCount() -> Int {
        return gameManager.tasks.tasks.filter { $0.status == .completed || $0.status == .claimed }.count
    }
    
    private func getCompletionPercentage() -> Double {
        let total = getTotalTaskCount()
        let completed = getCompletedTaskCount()
        return total > 0 ? Double(completed) / Double(total) : 0.0
    }
}

/// 任务类型选择器
struct TaskTypeSelector: View {
    @Binding var selectedTaskType: TaskType
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(TaskType.allCases, id: \.self) { taskType in
                    TaskTypeButton(
                        taskType: taskType,
                        isSelected: selectedTaskType == taskType
                    ) {
                        selectedTaskType = taskType
                    }
                }
            }
            .padding(.horizontal, 16)
        }
        .padding(.vertical, 8)
    }
}

/// 任务类型按钮
struct TaskTypeButton: View {
    let taskType: TaskType
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: getTaskTypeIcon())
                    .font(.title2)
                    .foregroundColor(isSelected ? .white : getTaskTypeColor())
                
                Text(taskType.displayName)
                    .font(.caption)
                    .foregroundColor(isSelected ? .white : .primary)
            }
            .frame(width: 80, height: 60)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? getTaskTypeColor() : Color(.systemGray6))
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    private func getTaskTypeIcon() -> String {
        switch taskType {
        case .main:
            return "star"
        case .side:
            return "circle"
        case .daily:
            return "calendar"
        case .event:
            return "gift"
        }
    }
    
    private func getTaskTypeColor() -> Color {
        switch taskType {
        case .main:
            return .blue
        case .side:
            return .green
        case .daily:
            return .orange
        case .event:
            return .purple
        }
    }
}

/// 任务列表视图
struct TaskListView: View {
    let taskType: TaskType
    @EnvironmentObject var gameManager: GameManager
    
    private var filteredTasks: [Task] {
        gameManager.tasks.tasks.filter { $0.type == taskType }
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(filteredTasks) { task in
                    TaskRowView(task: task)
                }
            }
            .padding()
        }
    }
}

/// 任务行视图
struct TaskRowView: View {
    let task: Task
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        VStack(spacing: 12) {
            // 任务标题和状态
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(task.title)
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Text(task.description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                TaskStatusBadge(status: task.status)
            }
            
            // 任务进度
            if task.status == .accepted {
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text("进度")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Spacer()
                        
                        Text("\(Int(task.getProgressPercentage() * 100))%")
                            .font(.caption)
                            .foregroundColor(.primary)
                    }
                    
                    ProgressView(value: task.getProgressPercentage())
                        .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                }
            }
            
            // 任务奖励
            TaskRewardView(rewards: task.rewards)
            
            // 操作按钮
            TaskActionButtons(task: task)
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
                .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
        )
    }
}

/// 任务状态徽章
struct TaskStatusBadge: View {
    let status: TaskStatus
    
    var body: some View {
        Text(status.displayName)
            .font(.caption)
            .fontWeight(.medium)
            .foregroundColor(.white)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(getStatusColor())
            )
    }
    
    private func getStatusColor() -> Color {
        switch status {
        case .available:
            return .green
        case .accepted:
            return .blue
        case .completed:
            return .orange
        case .claimed:
            return .gray
        case .expired:
            return .red
        }
    }
}

/// 任务奖励视图
struct TaskRewardView: View {
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

/// 任务操作按钮
struct TaskActionButtons: View {
    let task: Task
    @EnvironmentObject var gameManager: GameManager
    
    var body: some View {
        HStack(spacing: 12) {
            switch task.status {
            case .available:
                Button("接取") {
                    gameManager.tasks.acceptTask(task)
                }
                .buttonStyle(TaskButtonStyle(color: .blue))
                
            case .accepted:
                Button("查看进度") {
                    // 显示任务详情
                }
                .buttonStyle(TaskButtonStyle(color: .green))
                
            case .completed:
                Button("领取奖励") {
                    if let rewards = gameManager.tasks.claimTaskReward(task) {
                        // 应用奖励
                        applyRewards(rewards)
                    }
                }
                .buttonStyle(TaskButtonStyle(color: .orange))
                
            case .claimed:
                Text("已完成")
                    .font(.caption)
                    .foregroundColor(.gray)
                
            case .expired:
                Text("已过期")
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
    }
    
    private func applyRewards(_ rewards: TaskReward) {
        // 应用资源奖励
        for (resourceType, amount) in rewards.resources {
            gameManager.resources.addResource(type: resourceType, amount: amount)
        }
        
        // 应用金币奖励
        if rewards.gold > 0 {
            gameManager.resources.addResource(type: .gold, amount: rewards.gold)
        }
        
        // 应用钻石奖励
        if rewards.diamond > 0 {
            gameManager.resources.addResource(type: .diamond, amount: rewards.diamond)
        }
        
        // 应用经验奖励
        if rewards.experience > 0 {
            gameManager.currentCity.experience += rewards.experience
        }
    }
}

/// 任务按钮样式
struct TaskButtonStyle: ButtonStyle {
    let color: Color
    
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline)
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(color)
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
    }
}

struct TaskPanelView_Previews: PreviewProvider {
    static var previews: some View {
        TaskPanelView()
            .environmentObject(GameManager())
    }
}
