#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""最终修复所有剩余错误"""

import re

def fix_chain_reaction():
    """修复ChainReactionEngine剩余的Float问题"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ChainReactionEngine.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复特定行的Int为Float
    content = re.sub(r'value\s*=\s*(\d+)([,\)])', r'value = \1f\2', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_problem_detection():
    """修复ProblemDetectionEngine"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ProblemDetectionEngine.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除之前错误的修改
    content = content.replace('.count<Building> {', '.count {')
    content = content.replace('buildings.sumOf<Int> {', 'buildings.sumOf {')
    
    # 正确的修复：在lambda内明确返回类型
    content = re.sub(
        r'buildings\.count\s*\{\s*it\.type in listOf',
        'buildings.count { it: Building -> it.type in listOf',
        content
    )
    
    content = re.sub(
        r'buildings\s*\.filter\s*\{\s*it\.type in listOf',
        'buildings.filter { it: Building -> it.type in listOf',
        content
    )
    
    content = re.sub(
        r'buildings\.sumOf\s*\{\s*when',
        'buildings.sumOf { building -> when',
        content
    )
    
    # 替换when (it.type)为when (building.type)
    content = content.replace('when (it.type)', 'when (building.type)')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_resource_management():
    """修复ResourceManagementEngine"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ResourceManagementEngine.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除之前错误的修改
    content = content.replace('buildings.sumOf<Int> {', 'buildings.sumOf {')
    
    # 正确的修复：在lambda内明确参数类型
    content = re.sub(
        r'buildings\.sumOf\s*\{\s*building',
        'buildings.sumOf { building: Building',
        content
    )
    
    content = re.sub(
        r'\.sumOf\s*\{\s*when',
        '.sumOf { building: Building -> when',
        content
    )
    
    # 替换when表达式中的it为building
    # 找到when语句并替换
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'when (it.type)' in line or 'when {' in line and i > 50 and i < 200:
            lines[i] = line.replace('when (it.type)', 'when (building.type)')
            if 'it.isUnderConstruction' in line:
                lines[i] = lines[i].replace('it.isUnderConstruction', 'building.isUnderConstruction')
    content = '\n'.join(lines)
    
    # sumOf明确指定返回Int
    content = re.sub(
        r'buildings\.sumOf\s*\{',
        'buildings.sumOf<Int> {',
        content
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_building_when():
    """修复Building.kt中getDisplayName的when"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/data/model/Building.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 找到getDisplayName的when表达式结束位置并添加分支
    new_lines = []
    for i, line in enumerate(lines):
        new_lines.append(line)
        if 'BuildingType.AI_CENTER -> "AI中心"' in line:
            # 检查是否是getDisplayName函数内
            if i > 240 and i < 290:
                new_lines.append('            BuildingType.WASTE_MANAGEMENT -> "垃圾管理中心"\n')
                new_lines.append('            BuildingType.WATER_TOWER -> "水塔"\n')
                new_lines.append('            BuildingType.WATER_TREATMENT_PLANT -> "水处理厂"\n')
                new_lines.append('            BuildingType.BANK -> "银行"\n')
                new_lines.append('            BuildingType.OFFICE -> "办公楼"\n')
                new_lines.append('            BuildingType.STADIUM -> "体育场"\n')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    print(f"Fixed: {file_path}")

def fix_topstatus_bar():
    """修复TopStatusBar.kt"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/component/TopStatusBar.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 在所有BuildingType when表达式的末尾添加else分支
    content = re.sub(
        r'(BuildingType\.SPACE_CENTER[^}]+})\s*\n(\s+)\}(\s+\})',
        r'\1\n\2    else -> ""\n\2}\3',
        content
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_citizen_detail():
    """修复CitizenDetailScreen.kt"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/CitizenDetailScreen.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 替换错误的EducationLevel枚举值
    replacements = {
        'EducationLevel.ELEMENTARY_SCHOOL': 'EducationLevel.PRIMARY',
        'EducationLevel.MIDDLE_SCHOOL': 'EducationLevel.SECONDARY',
        'EducationLevel.MASTER': 'EducationLevel.GRADUATE',
        'EducationLevel.DOCTOR': 'EducationLevel.PHD',
        'EducationLevel.NONE': 'EducationLevel.PRIMARY'
    }
    
    for old, new in replacements.items():
        content = content.replace(old, new)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

def fix_economy_dashboard():
    """修复EconomyDashboardScreen.kt"""
    file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ui/screen/EconomyDashboardScreen.kt"
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复中文乱码
    content = re.sub(r'jobs��λ', 'jobs岗位', content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed: {file_path}")

if __name__ == '__main__':
    print("Applying final fixes...")
    fix_chain_reaction()
    fix_problem_detection()
    fix_resource_management()
    fix_building_when()
    fix_topstatus_bar()
    fix_citizen_detail()
    fix_economy_dashboard()
    print("All final fixes applied!")


