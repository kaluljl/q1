//
//  GameResources.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import SwiftUI
import AVFoundation

/// 游戏资源管理器
class GameResourcesManager: ObservableObject {
    static let shared = GameResourcesManager()
    
    // 音效播放器
    private var audioPlayers: [String: AVAudioPlayer] = [:]
    
    // 资源路径
    private let resourcePaths = ResourcePaths()
    
    private init() {
        setupAudioSession()
        preloadResources()
    }
    
    /// 设置音频会话
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.ambient, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("音频会话设置失败: \(error)")
        }
    }
    
    /// 预加载资源
    private func preloadResources() {
        // 预加载常用音效
        preloadSoundEffects()
    }
    
    /// 预加载音效
    private func preloadSoundEffects() {
        let soundEffects = [
            "build": "build_sound.wav",
            "upgrade": "upgrade_sound.wav",
            "complete": "complete_sound.wav",
            "click": "click_sound.wav",
            "error": "error_sound.wav"
        ]
        
        for (key, filename) in soundEffects {
            if let url = Bundle.main.url(forResource: filename, withExtension: nil) {
                do {
                    let player = try AVAudioPlayer(contentsOf: url)
                    player.prepareToPlay()
                    audioPlayers[key] = player
                } catch {
                    print("加载音效失败 \(filename): \(error)")
                }
            }
        }
    }
    
    /// 播放音效
    func playSound(_ soundName: String) {
        guard let player = audioPlayers[soundName] else { return }
        player.stop()
        player.currentTime = 0
        player.play()
    }
    
    /// 播放建筑音效
    func playBuildSound() {
        playSound("build")
    }
    
    /// 播放升级音效
    func playUpgradeSound() {
        playSound("upgrade")
    }
    
    /// 播放完成音效
    func playCompleteSound() {
        playSound("complete")
    }
    
    /// 播放点击音效
    func playClickSound() {
        playSound("click")
    }
    
    /// 播放错误音效
    func playErrorSound() {
        playSound("error")
    }
}

/// 资源路径配置
struct ResourcePaths {
    // 音效路径
    let soundEffects = "Sounds/Effects/"
    let backgroundMusic = "Sounds/Music/"
    
    // 图片路径
    let uiIcons = "Images/UI/"
    let buildingIcons = "Images/Buildings/"
    let backgroundImages = "Images/Backgrounds/"
    
    // 动画路径
    let animations = "Animations/"
}

/// 游戏图标资源
struct GameIcons {
    // 建筑图标 (使用SF Symbols)
    static let house = "house.fill"
    static let apartment = "building.2.fill"
    static let villa = "house.lodge.fill"
    static let skyscraper = "building.columns.fill"
    
    static let shop = "storefront.fill"
    static let supermarket = "cart.fill"
    static let mall = "building.2.crop.circle.fill"
    static let restaurant = "fork.knife"
    static let hotel = "bed.double.fill"
    
    static let lumberMill = "tree.fill"
    static let quarry = "mountain.2.fill"
    static let steelMill = "gear.circle.fill"
    static let foodFactory = "leaf.fill"
    static let powerPlant = "bolt.circle.fill"
    
    static let school = "graduationcap.fill"
    static let hospital = "cross.circle.fill"
    static let policeStation = "shield.fill"
    static let fireStation = "flame.fill"
    static let park = "tree.circle.fill"
    static let library = "book.fill"
    
    static let road = "road.lanes"
    static let bridge = "bridge.fill"
    static let busStop = "bus.fill"
    static let subwayStation = "tram.fill"
    
    // UI图标
    static let gold = "dollarsign.circle.fill"
    static let diamond = "diamond.fill"
    static let wood = "tree"
    static let stone = "mountain.2"
    static let steel = "gear"
    static let food = "apple.logo"
    
    // 状态图标
    static let construction = "hammer.fill"
    static let upgrade = "arrow.up.circle.fill"
    static let maintenance = "wrench.fill"
    static let warning = "exclamationmark.triangle.fill"
    static let success = "checkmark.circle.fill"
    static let error = "xmark.circle.fill"
    
    // 天气图标
    static let sunny = "sun.max.fill"
    static let cloudy = "cloud.fill"
    static let rainy = "cloud.rain.fill"
    static let stormy = "cloud.bolt.fill"
    static let snowy = "cloud.snow.fill"
    static let foggy = "cloud.fog.fill"
}

/// 游戏颜色主题
struct GameColors {
    // 建筑颜色
    static let residential = Color.blue
    static let commercial = Color.green
    static let industrial = Color.orange
    static let publicFacility = Color.purple
    static let transportation = Color.gray
    
    // 资源颜色
    static let gold = Color.yellow
    static let diamond = Color.cyan
    static let wood = Color.brown
    static let stone = Color.gray
    static let steel = Color.blue
    static let food = Color.green
    
    // 状态颜色
    static let construction = Color.orange
    static let upgrade = Color.blue
    static let maintenance = Color.red
    static let warning = Color.yellow
    static let success = Color.green
    static let error = Color.red
    
    // 天气颜色
    static let sunny = Color.yellow
    static let cloudy = Color.gray
    static let rainy = Color.blue
    static let stormy = Color.purple
    static let snowy = Color.white
    static let foggy = Color.gray.opacity(0.7)
}

/// 游戏字体
struct GameFonts {
    static let title = Font.title
    static let headline = Font.headline
    static let subheadline = Font.subheadline
    static let body = Font.body
    static let caption = Font.caption
    static let caption2 = Font.caption2
    
    // 自定义字体
    static let gameTitle = Font.custom("Avenir-Heavy", size: 24)
    static let gameSubtitle = Font.custom("Avenir-Medium", size: 18)
    static let gameBody = Font.custom("Avenir-Regular", size: 16)
}

/// 游戏动画
struct GameAnimations {
    // 基础动画
    static let fadeIn = Animation.easeIn(duration: 0.3)
    static let fadeOut = Animation.easeOut(duration: 0.3)
    static let slideIn = Animation.easeInOut(duration: 0.4)
    static let slideOut = Animation.easeInOut(duration: 0.4)
    
    // 建筑动画
    static let buildAnimation = Animation.spring(response: 0.6, dampingFraction: 0.8)
    static let upgradeAnimation = Animation.easeInOut(duration: 0.5)
    static let destroyAnimation = Animation.easeIn(duration: 0.3)
    
    // UI动画
    static let buttonPress = Animation.easeInOut(duration: 0.1)
    static let menuSlide = Animation.spring(response: 0.5, dampingFraction: 0.7)
    static let popupShow = Animation.spring(response: 0.4, dampingFraction: 0.8)
    
    // 特效动画
    static let glow = Animation.easeInOut(duration: 2).repeatForever(autoreverses: true)
    static let pulse = Animation.easeInOut(duration: 1).repeatForever(autoreverses: true)
    static let rotate = Animation.linear(duration: 2).repeatForever(autoreverses: false)
}

/// 游戏背景资源
struct GameBackgrounds {
    // 天空渐变
    static let morningSky = LinearGradient(
        gradient: Gradient(colors: [Color.orange.opacity(0.3), Color.yellow.opacity(0.2)]),
        startPoint: .top,
        endPoint: .bottom
    )
    
    static let daySky = LinearGradient(
        gradient: Gradient(colors: [Color.blue.opacity(0.2), Color.cyan.opacity(0.1)]),
        startPoint: .top,
        endPoint: .bottom
    )
    
    static let eveningSky = LinearGradient(
        gradient: Gradient(colors: [Color.orange.opacity(0.4), Color.red.opacity(0.3)]),
        startPoint: .top,
        endPoint: .bottom
    )
    
    static let nightSky = LinearGradient(
        gradient: Gradient(colors: [Color.purple.opacity(0.4), Color.black.opacity(0.3)]),
        startPoint: .top,
        endPoint: .bottom
    )
    
    // 地面纹理
    static let grassTexture = Color.green.opacity(0.3)
    static let dirtTexture = Color.brown.opacity(0.3)
    static let stoneTexture = Color.gray.opacity(0.3)
}

/// 游戏音效配置
struct GameSounds {
    // 音效文件名
    static let buildSound = "build_sound.wav"
    static let upgradeSound = "upgrade_sound.wav"
    static let completeSound = "complete_sound.wav"
    static let clickSound = "click_sound.wav"
    static let errorSound = "error_sound.wav"
    static let collectSound = "collect_sound.wav"
    static let levelUpSound = "level_up_sound.wav"
    static let achievementSound = "achievement_sound.wav"
    
    // 背景音乐
    static let backgroundMusic = "background_music.mp3"
    static let menuMusic = "menu_music.mp3"
    static let gameMusic = "game_music.mp3"
}

/// 游戏特效
struct GameEffects {
    // 粒子效果
    static let buildParticles = "build_particles"
    static let upgradeParticles = "upgrade_particles"
    static let collectParticles = "collect_particles"
    static let levelUpParticles = "level_up_particles"
    
    // 动画效果
    static let coinAnimation = "coin_animation"
    static let starAnimation = "star_animation"
    static let sparkleAnimation = "sparkle_animation"
}

/// 资源加载器
class ResourceLoader {
    static let shared = ResourceLoader()
    
    private init() {}
    
    /// 加载图片资源
    func loadImage(named name: String) -> Image? {
        if let uiImage = UIImage(named: name) {
            return Image(uiImage: uiImage)
        }
        return nil
    }
    
    /// 加载音效资源
    func loadSound(named name: String) -> AVAudioPlayer? {
        guard let url = Bundle.main.url(forResource: name, withExtension: nil) else {
            return nil
        }
        
        do {
            let player = try AVAudioPlayer(contentsOf: url)
            player.prepareToPlay()
            return player
        } catch {
            print("加载音效失败 \(name): \(error)")
            return nil
        }
    }
    
    /// 加载动画资源
    func loadAnimation(named name: String) -> Any? {
        // 这里可以加载Lottie动画或其他动画资源
        return nil
    }
}
