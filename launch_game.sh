#!/bin/bash

# 城市模拟经营游戏启动脚本
# City Simulator Game Launch Script

echo "🎮 启动城市模拟经营游戏..."
echo "City Simulator Game Starting..."

# 检查Xcode是否安装
if ! command -v xcodebuild &> /dev/null; then
    echo "❌ 错误: 未找到Xcode，请先安装Xcode"
    echo "❌ Error: Xcode not found, please install Xcode first"
    exit 1
fi

# 检查项目文件是否存在
if [ ! -f "CitySimulator.xcodeproj/project.pbxproj" ]; then
    echo "❌ 错误: 未找到项目文件，请确保在正确的目录中运行此脚本"
    echo "❌ Error: Project file not found, please run this script in the correct directory"
    exit 1
fi

echo "✅ 项目文件检查通过"
echo "✅ Project file check passed"

# 清理项目
echo "🧹 清理项目..."
echo "🧹 Cleaning project..."
xcodebuild clean -project CitySimulator.xcodeproj -scheme CitySimulator

# 构建项目
echo "🔨 构建项目..."
echo "🔨 Building project..."
xcodebuild build -project CitySimulator.xcodeproj -scheme CitySimulator -destination 'platform=iOS Simulator,name=iPhone 15 Pro'

if [ $? -eq 0 ]; then
    echo "✅ 构建成功！"
    echo "✅ Build successful!"
    
    # 启动模拟器
    echo "📱 启动iOS模拟器..."
    echo "📱 Starting iOS Simulator..."
    open -a Simulator
    
    # 等待模拟器启动
    sleep 3
    
    # 安装并运行应用
    echo "🚀 安装并运行游戏..."
    echo "🚀 Installing and running game..."
    xcodebuild test -project CitySimulator.xcodeproj -scheme CitySimulator -destination 'platform=iOS Simulator,name=iPhone 15 Pro'
    
    echo "🎉 游戏已启动！"
    echo "🎉 Game launched!"
    echo ""
    echo "📋 游戏功能:"
    echo "📋 Game Features:"
    echo "  • 🏗️ 城市建设 - City Building"
    echo "  • 📊 资源管理 - Resource Management"
    echo "  • 👥 人口发展 - Population Development"
    echo "  • 📋 任务系统 - Task System"
    echo "  • 🏆 成就系统 - Achievement System"
    echo "  • 🌤️ 天气系统 - Weather System"
    echo "  • 🎵 音效系统 - Sound Effects"
    echo ""
    echo "🎮 享受游戏吧！"
    echo "🎮 Enjoy the game!"
    
else
    echo "❌ 构建失败，请检查错误信息"
    echo "❌ Build failed, please check error messages"
    exit 1
fi



