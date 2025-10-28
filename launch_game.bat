@echo off
REM 城市模拟经营游戏启动脚本
REM City Simulator Game Launch Script

echo 🎮 启动城市模拟经营游戏...
echo City Simulator Game Starting...

REM 检查Xcode是否安装
where xcodebuild >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Xcode，请先安装Xcode
    echo ❌ Error: Xcode not found, please install Xcode first
    pause
    exit /b 1
)

REM 检查项目文件是否存在
if not exist "CitySimulator.xcodeproj\project.pbxproj" (
    echo ❌ 错误: 未找到项目文件，请确保在正确的目录中运行此脚本
    echo ❌ Error: Project file not found, please run this script in the correct directory
    pause
    exit /b 1
)

echo ✅ 项目文件检查通过
echo ✅ Project file check passed

REM 清理项目
echo 🧹 清理项目...
echo 🧹 Cleaning project...
xcodebuild clean -project CitySimulator.xcodeproj -scheme CitySimulator

REM 构建项目
echo 🔨 构建项目...
echo 🔨 Building project...
xcodebuild build -project CitySimulator.xcodeproj -scheme CitySimulator -destination "platform=iOS Simulator,name=iPhone 15 Pro"

if %errorlevel% equ 0 (
    echo ✅ 构建成功！
    echo ✅ Build successful!
    
    REM 启动模拟器
    echo 📱 启动iOS模拟器...
    echo 📱 Starting iOS Simulator...
    start "" "C:\Program Files\Xcode\Contents\Developer\Applications\Simulator.app"
    
    REM 等待模拟器启动
    timeout /t 3 /nobreak >nul
    
    REM 安装并运行应用
    echo 🚀 安装并运行游戏...
    echo 🚀 Installing and running game...
    xcodebuild test -project CitySimulator.xcodeproj -scheme CitySimulator -destination "platform=iOS Simulator,name=iPhone 15 Pro"
    
    echo 🎉 游戏已启动！
    echo 🎉 Game launched!
    echo.
    echo 📋 游戏功能:
    echo 📋 Game Features:
    echo   • 🏗️ 城市建设 - City Building
    echo   • 📊 资源管理 - Resource Management
    echo   • 👥 人口发展 - Population Development
    echo   • 📋 任务系统 - Task System
    echo   • 🏆 成就系统 - Achievement System
    echo   • 🌤️ 天气系统 - Weather System
    echo   • 🎵 音效系统 - Sound Effects
    echo.
    echo 🎮 享受游戏吧！
    echo 🎮 Enjoy the game!
    
) else (
    echo ❌ 构建失败，请检查错误信息
    echo ❌ Build failed, please check error messages
)

pause



