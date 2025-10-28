#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""修复Building.kt中缺失的when分支"""

file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/data/model/Building.kt"

with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# 需要添加的分支
additions = {
    'getDescription': [
        '            BuildingType.WASTE_MANAGEMENT -> "垃圾管理中心，处理废弃物"\n',
        '            BuildingType.WATER_TOWER -> "水塔，提供城市供水"\n',
        '            BuildingType.WATER_TREATMENT_PLANT -> "水处理厂，净化供水"\n',
        '            BuildingType.BANK -> "银行，提供金融服务"\n',
        '            BuildingType.OFFICE -> "办公楼，提供商业办公空间"\n',
        '            BuildingType.STADIUM -> "体育场，举办体育赛事"\n',
    ],
    'getIconResourceId': [
        '            BuildingType.WASTE_MANAGEMENT -> "ic_waste_management"\n',
        '            BuildingType.WATER_TOWER -> "ic_water_tower"\n',
        '            BuildingType.WATER_TREATMENT_PLANT -> "ic_water_treatment"\n',
        '            BuildingType.BANK -> "ic_bank"\n',
        '            BuildingType.OFFICE -> "ic_office"\n',
        '            BuildingType.STADIUM -> "ic_stadium"\n',
    ]
}

# 找到需要插入的位置
new_lines = []
i = 0
while i < len(lines):
    new_lines.append(lines[i])
    
    # 在getDescription的AI_CENTER后插入
    if 'BuildingType.AI_CENTER -> "人工智能，智能管理"' in lines[i]:
        for addition in additions['getDescription']:
            new_lines.append(addition)
    
    # 在getIconResourceId的AI_CENTER后插入
    if 'BuildingType.AI_CENTER -> "ic_ai_center"' in lines[i]:
        for addition in additions['getIconResourceId']:
            new_lines.append(addition)
    
    i += 1

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print(f"Fixed: {file_path}")

