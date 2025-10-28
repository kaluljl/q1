//
//  SoundEffects.swift
//  CitySimulator
//
//  Created by AI进化论-花生 on 2024/01/01.
//

import AVFoundation
import SwiftUI

/// 音效管理器
class SoundEffectsManager: ObservableObject {
    static let shared = SoundEffectsManager()
    
    private var audioPlayers: [String: AVAudioPlayer] = [:]
    private var isSoundEnabled = true
    private var isMusicEnabled = true
    
    private init() {
        setupAudioSession()
        preloadSoundEffects()
    }
    
    /// 设置音频会话
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.ambient, mode: .default, options: [.mixWithOthers])
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("音频会话设置失败: \(error)")
        }
    }
    
    /// 预加载音效
    private func preloadSoundEffects() {
        let soundEffects = [
            "build": "build_sound",
            "upgrade": "upgrade_sound", 
            "complete": "complete_sound",
            "click": "click_sound",
            "error": "error_sound",
            "collect": "collect_sound",
            "levelUp": "level_up_sound",
            "achievement": "achievement_sound",
            "coin": "coin_sound",
            "notification": "notification_sound"
        ]
        
        for (key, filename) in soundEffects {
            loadSoundEffect(key: key, filename: filename)
        }
    }
    
    /// 加载音效文件
    private func loadSoundEffect(key: String, filename: String) {
        // 尝试加载不同格式的音效文件
        let extensions = ["wav", "mp3", "m4a", "aiff"]
        
        for ext in extensions {
            if let url = Bundle.main.url(forResource: filename, withExtension: ext) {
                do {
                    let player = try AVAudioPlayer(contentsOf: url)
                    player.prepareToPlay()
                    player.volume = 0.7
                    audioPlayers[key] = player
                    break
                } catch {
                    print("加载音效失败 \(filename).\(ext): \(error)")
                }
            }
        }
        
        // 如果没有找到音效文件，创建系统音效
        if audioPlayers[key] == nil {
            createSystemSound(key: key)
        }
    }
    
    /// 创建系统音效
    private func createSystemSound(key: String) {
        // 使用系统音效作为备选
        let systemSounds: [String: SystemSoundID] = [
            "click": 1104,      // 点击音效
            "error": 1005,      // 错误音效
            "complete": 1003,   // 完成音效
            "notification": 1007 // 通知音效
        ]
        
        if let soundID = systemSounds[key] {
            // 这里可以存储系统音效ID，稍后播放
        }
    }
    
    /// 播放音效
    func playSound(_ soundName: String) {
        guard isSoundEnabled else { return }
        
        if let player = audioPlayers[soundName] {
            player.stop()
            player.currentTime = 0
            player.play()
        } else {
            // 播放系统音效
            playSystemSound(soundName)
        }
    }
    
    /// 播放系统音效
    private func playSystemSound(_ soundName: String) {
        let systemSounds: [String: SystemSoundID] = [
            "click": 1104,
            "error": 1005,
            "complete": 1003,
            "notification": 1007
        ]
        
        if let soundID = systemSounds[soundName] {
            AudioServicesPlaySystemSound(soundID)
        }
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
    
    /// 播放收集音效
    func playCollectSound() {
        playSound("collect")
    }
    
    /// 播放升级音效
    func playLevelUpSound() {
        playSound("levelUp")
    }
    
    /// 播放成就音效
    func playAchievementSound() {
        playSound("achievement")
    }
    
    /// 播放金币音效
    func playCoinSound() {
        playSound("coin")
    }
    
    /// 播放通知音效
    func playNotificationSound() {
        playSound("notification")
    }
    
    /// 设置音效开关
    func setSoundEnabled(_ enabled: Bool) {
        isSoundEnabled = enabled
    }
    
    /// 设置音乐开关
    func setMusicEnabled(_ enabled: Bool) {
        isMusicEnabled = enabled
    }
    
    /// 获取音效状态
    var soundEnabled: Bool {
        return isSoundEnabled
    }
    
    /// 获取音乐状态
    var musicEnabled: Bool {
        return isMusicEnabled
    }
}

/// 背景音乐管理器
class BackgroundMusicManager: ObservableObject {
    static let shared = BackgroundMusicManager()
    
    private var musicPlayer: AVAudioPlayer?
    private var isPlaying = false
    
    private init() {
        setupAudioSession()
    }
    
    /// 设置音频会话
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default, options: [.mixWithOthers])
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("音频会话设置失败: \(error)")
        }
    }
    
    /// 播放背景音乐
    func playBackgroundMusic() {
        guard !isPlaying else { return }
        
        // 尝试加载背景音乐文件
        let musicFiles = ["background_music", "game_music", "ambient_music"]
        
        for filename in musicFiles {
            let extensions = ["mp3", "m4a", "wav"]
            
            for ext in extensions {
                if let url = Bundle.main.url(forResource: filename, withExtension: ext) {
                    do {
                        musicPlayer = try AVAudioPlayer(contentsOf: url)
                        musicPlayer?.numberOfLoops = -1 // 循环播放
                        musicPlayer?.volume = 0.3
                        musicPlayer?.play()
                        isPlaying = true
                        return
                    } catch {
                        print("加载背景音乐失败 \(filename).\(ext): \(error)")
                    }
                }
            }
        }
        
        // 如果没有找到音乐文件，使用系统音效
        print("未找到背景音乐文件，使用静音模式")
    }
    
    /// 停止背景音乐
    func stopBackgroundMusic() {
        musicPlayer?.stop()
        isPlaying = false
    }
    
    /// 暂停背景音乐
    func pauseBackgroundMusic() {
        musicPlayer?.pause()
        isPlaying = false
    }
    
    /// 恢复背景音乐
    func resumeBackgroundMusic() {
        musicPlayer?.play()
        isPlaying = true
    }
    
    /// 设置音乐音量
    func setVolume(_ volume: Float) {
        musicPlayer?.volume = volume
    }
    
    /// 获取播放状态
    var playing: Bool {
        return isPlaying
    }
}

/// 触觉反馈管理器
class HapticFeedbackManager: ObservableObject {
    static let shared = HapticFeedbackManager()
    
    private init() {}
    
    /// 播放轻触反馈
    func light() {
        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
        impactFeedback.impactOccurred()
    }
    
    /// 播放中等触觉反馈
    func medium() {
        let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
        impactFeedback.impactOccurred()
    }
    
    /// 播放重触觉反馈
    func heavy() {
        let impactFeedback = UIImpactFeedbackGenerator(style: .heavy)
        impactFeedback.impactOccurred()
    }
    
    /// 播放成功反馈
    func success() {
        let notificationFeedback = UINotificationFeedbackGenerator()
        notificationFeedback.notificationOccurred(.success)
    }
    
    /// 播放警告反馈
    func warning() {
        let notificationFeedback = UINotificationFeedbackGenerator()
        notificationFeedback.notificationOccurred(.warning)
    }
    
    /// 播放错误反馈
    func error() {
        let notificationFeedback = UINotificationFeedbackGenerator()
        notificationFeedback.notificationOccurred(.error)
    }
    
    /// 播放选择反馈
    func selection() {
        let selectionFeedback = UISelectionFeedbackGenerator()
        selectionFeedback.selectionChanged()
    }
}

/// 音效设置视图
struct SoundSettingsView: View {
    @StateObject private var soundManager = SoundEffectsManager.shared
    @StateObject private var musicManager = BackgroundMusicManager.shared
    @StateObject private var hapticManager = HapticFeedbackManager.shared
    
    var body: some View {
        VStack(spacing: 20) {
            Text("音效设置")
                .font(.title2)
                .fontWeight(.bold)
            
            // 音效开关
            HStack {
                Text("音效")
                    .font(.headline)
                
                Spacer()
                
                Toggle("", isOn: Binding(
                    get: { soundManager.soundEnabled },
                    set: { soundManager.setSoundEnabled($0) }
                ))
                .onChange(of: soundManager.soundEnabled) { enabled in
                    if enabled {
                        soundManager.playClickSound()
                    }
                }
            }
            
            // 音乐开关
            HStack {
                Text("背景音乐")
                    .font(.headline)
                
                Spacer()
                
                Toggle("", isOn: Binding(
                    get: { musicManager.playing },
                    set: { enabled in
                        if enabled {
                            musicManager.playBackgroundMusic()
                        } else {
                            musicManager.stopBackgroundMusic()
                        }
                    }
                ))
            }
            
            // 音量控制
            VStack(alignment: .leading) {
                Text("音量")
                    .font(.headline)
                
                Slider(value: Binding(
                    get: { 0.3 },
                    set: { musicManager.setVolume($0) }
                ), in: 0...1)
            }
            
            // 测试按钮
            VStack(spacing: 10) {
                Button("测试音效") {
                    soundManager.playClickSound()
                }
                .buttonStyle(.bordered)
                
                Button("测试触觉反馈") {
                    hapticManager.medium()
                }
                .buttonStyle(.bordered)
            }
        }
        .padding()
    }
}
