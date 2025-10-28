//
//  WeatherSystem.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI

/// 天气类型枚举
enum WeatherType: String, CaseIterable {
    case sunny = "sunny"
    case cloudy = "cloudy"
    case rainy = "rainy"
    case stormy = "stormy"
    case snowy = "snowy"
    case foggy = "foggy"
    
    var displayName: String {
        switch self {
        case .sunny: return "晴朗"
        case .cloudy: return "多云"
        case .rainy: return "雨天"
        case .stormy: return "暴风雨"
        case .snowy: return "雪天"
        case .foggy: return "雾天"
        }
    }
    
    var iconName: String {
        switch self {
        case .sunny: return "sun.max.fill"
        case .cloudy: return "cloud.fill"
        case .rainy: return "cloud.rain.fill"
        case .stormy: return "cloud.bolt.fill"
        case .snowy: return "cloud.snow.fill"
        case .foggy: return "cloud.fog.fill"
        }
    }
    
    var color: Color {
        switch self {
        case .sunny: return .yellow
        case .cloudy: return .gray
        case .rainy: return .blue
        case .stormy: return .purple
        case .snowy: return .white
        case .foggy: return .gray.opacity(0.7)
        }
    }
    
    var backgroundColor: [Color] {
        switch self {
        case .sunny: return [Color.yellow.opacity(0.3), Color.orange.opacity(0.2)]
        case .cloudy: return [Color.gray.opacity(0.3), Color.blue.opacity(0.2)]
        case .rainy: return [Color.blue.opacity(0.4), Color.gray.opacity(0.3)]
        case .stormy: return [Color.purple.opacity(0.4), Color.black.opacity(0.3)]
        case .snowy: return [Color.white.opacity(0.4), Color.blue.opacity(0.2)]
        case .foggy: return [Color.gray.opacity(0.5), Color.white.opacity(0.3)]
        }
    }
}

/// 动态背景视图
struct DynamicBackgroundView: View {
    let weatherType: WeatherType
    let time: Date
    @State private var animationOffset: CGFloat = 0
    
    var body: some View {
        ZStack {
            // 基础渐变背景
            LinearGradient(
                gradient: Gradient(colors: weatherType.backgroundColor),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            
            // 时间相关的天空颜色
            TimeBasedSkyView(time: time)
                .opacity(0.6)
            
            // 云朵动画
            if weatherType == .cloudy || weatherType == .sunny {
                CloudAnimationView()
                    .opacity(0.4)
            }
        }
        .onAppear {
            withAnimation(.linear(duration: 20).repeatForever(autoreverses: false)) {
                animationOffset = 360
            }
        }
    }
}

/// 基于时间的天空视图
struct TimeBasedSkyView: View {
    let time: Date
    
    var body: some View {
        let hour = Calendar.current.component(.hour, from: time)
        
        return ZStack {
            if hour >= 6 && hour < 12 {
                // 早晨
                LinearGradient(
                    gradient: Gradient(colors: [Color.orange.opacity(0.3), Color.yellow.opacity(0.2)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            } else if hour >= 12 && hour < 18 {
                // 下午
                LinearGradient(
                    gradient: Gradient(colors: [Color.blue.opacity(0.2), Color.cyan.opacity(0.1)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            } else if hour >= 18 && hour < 22 {
                // 傍晚
                LinearGradient(
                    gradient: Gradient(colors: [Color.orange.opacity(0.4), Color.red.opacity(0.3)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            } else {
                // 夜晚
                LinearGradient(
                    gradient: Gradient(colors: [Color.purple.opacity(0.4), Color.black.opacity(0.3)]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            }
        }
    }
}

/// 云朵动画视图
struct CloudAnimationView: View {
    @State private var cloudOffset: CGFloat = -100
    
    var body: some View {
        HStack(spacing: 200) {
            ForEach(0..<3, id: \.self) { index in
                CloudView()
                    .offset(x: cloudOffset + CGFloat(index * 200))
            }
        }
        .onAppear {
            withAnimation(.linear(duration: 30).repeatForever(autoreverses: false)) {
                cloudOffset = UIScreen.main.bounds.width + 100
            }
        }
    }
}

/// 单个云朵视图
struct CloudView: View {
    var body: some View {
        ZStack {
            Circle()
                .fill(Color.white.opacity(0.8))
                .frame(width: 60, height: 60)
                .offset(x: -20, y: 0)
            
            Circle()
                .fill(Color.white.opacity(0.8))
                .frame(width: 80, height: 80)
                .offset(x: 0, y: 0)
            
            Circle()
                .fill(Color.white.opacity(0.8))
                .frame(width: 70, height: 70)
                .offset(x: 20, y: 0)
            
            Circle()
                .fill(Color.white.opacity(0.8))
                .frame(width: 50, height: 50)
                .offset(x: 0, y: -20)
        }
    }
}

/// 天气效果视图
struct WeatherEffectView: View {
    let weatherType: WeatherType
    @State private var rainOffset: CGFloat = 0
    @State private var snowOffset: CGFloat = 0
    
    var body: some View {
        ZStack {
            switch weatherType {
            case .rainy, .stormy:
                RainEffectView(offset: rainOffset)
            case .snowy:
                SnowEffectView(offset: snowOffset)
            case .foggy:
                FogEffectView()
            default:
                EmptyView()
            }
        }
        .onAppear {
            startWeatherAnimation()
        }
    }
    
    private func startWeatherAnimation() {
        switch weatherType {
        case .rainy, .stormy:
            withAnimation(.linear(duration: 1).repeatForever(autoreverses: false)) {
                rainOffset = 100
            }
        case .snowy:
            withAnimation(.linear(duration: 3).repeatForever(autoreverses: false)) {
                snowOffset = 100
            }
        default:
            break
        }
    }
}

/// 雨滴效果视图
struct RainEffectView: View {
    let offset: CGFloat
    
    var body: some View {
        VStack(spacing: 2) {
            ForEach(0..<50, id: \.self) { index in
                Rectangle()
                    .fill(Color.blue.opacity(0.6))
                    .frame(width: 1, height: CGFloat.random(in: 10...30))
                    .offset(x: CGFloat.random(in: -200...200), y: offset)
                    .animation(.linear(duration: 0.5).repeatForever(autoreverses: false), value: offset)
            }
        }
    }
}

/// 雪花效果视图
struct SnowEffectView: View {
    let offset: CGFloat
    
    var body: some View {
        VStack(spacing: 5) {
            ForEach(0..<30, id: \.self) { index in
                Image(systemName: "snowflake")
                    .foregroundColor(.white.opacity(0.8))
                    .font(.caption)
                    .offset(x: CGFloat.random(in: -200...200), y: offset)
                    .animation(.linear(duration: 2).repeatForever(autoreverses: false), value: offset)
            }
        }
    }
}

/// 雾效果视图
struct FogEffectView: View {
    @State private var fogOpacity: Double = 0.3
    
    var body: some View {
        Rectangle()
            .fill(Color.white.opacity(fogOpacity))
            .ignoresSafeArea()
            .onAppear {
                withAnimation(.easeInOut(duration: 2).repeatForever(autoreverses: true)) {
                    fogOpacity = 0.6
                }
            }
    }
}
