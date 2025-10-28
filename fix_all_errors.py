#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""批量修复所有编译错误"""

import re
import os

def fix_resource_management():
    """修复ResourceManagementEngine.kt中的sumOf歧义"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ResourceManagementEngine.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 明确指定sumOf类型
    content = content.replace('buildings.sumOf {', 'buildings.sumOf<Int> {')
    content = content.replace('.sumOf { building', '.sumOf<Int> { building')
    content = content.replace('.sumOf { when', '.sumOf<Int> { when')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_problem_detection():
    """修复ProblemDetectionEngine.kt"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ProblemDetectionEngine.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复sumOf歧义
    content = content.replace('.count {', '.count<Building> {')
    content = content.replace('buildings.sumOf {', 'buildings.sumOf<Int> {')
    content = re.sub(r'\.sumOf\s*\{\s*when', '.sumOf<Int> { when', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_ui_components():
    """修复UI组件中的API问题"""
    files = [
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/component/ResourcePanel.kt",
    ]
    
    for file_path in files:
        if not os.path.exists(file_path):
            continue
            
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 修复LinearProgressIndicator
        content = re.sub(r'progress\s*=\s*\{\s*([^}]+)\s*\}', r'progress = \1', content)
        
        # 修复CircularProgressIndicator
        content = re.sub(r'CircularProgressIndicator\(\s*progress\s*=\s*\{\s*([^}]+)\s*\}', 
                        r'CircularProgressIndicator(progress = \1', content)
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed: {file_path}")

def fix_color_orange():
    """修复Color.Orange错误"""
    files = [
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/component/EconomyCard.kt",
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/component/ProblemIndicatorPanel.kt",
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/EconomyDashboardScreen.kt",
    ]
    
    for file_path in files:
        if not os.path.exists(file_path):
            continue
            
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        content = content.replace('Color.Orange', 'Color(0xFFFF9800)')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed: {file_path}")

def fix_scaffold_api():
    """修复Scaffold API"""
    files = [
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/CitizenDetailScreen.kt",
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/CitizenListScreen.kt",
        "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/EconomyDashboardScreen.kt",
    ]
    
    for file_path in files:
        if not os.path.exists(file_path):
            continue
            
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        content = content.replace('topAppBar =', 'topBar =')
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed: {file_path}")

if __name__ == '__main__':
    print("Starting fixes...")
    fix_resource_management()
    fix_problem_detection()
    fix_ui_components()
    fix_color_orange()
    fix_scaffold_api()
    print("All fixes applied!")


