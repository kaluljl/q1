#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""批量修复ChainReactionEngine.kt中的Float类型错误"""

import re

file_path = "CitySimulatorAndroid/app/src/main/java/com/citysimulator/game/ai/ChainReactionEngine.kt"

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 修复Impact构造函数中的整数为Float
# 匹配 Impact("xxx", 整数, ...
pattern = r'Impact\("([^"]+)",\s*(-?\d+),'
replacement = r'Impact("\1", \2f,'
content = re.sub(pattern, replacement, content)

# 修复其他可能的Float赋值
patterns_replacements = [
    (r'value\s*=\s*(-?\d+)([,\)])', r'value = \1f\2'),
    (r'targetValue:\s*(-?\d+)f', r'targetValue: \1f'),
    (r'delay\s*=\s*(\d+),', r'delay = \1,'),  # delay应该是Int，不改
    (r'duration\s*=\s*(-?\d+),', r'duration = \1,'),  # duration应该是Int，不改
]

for pattern, replacement in patterns_replacements:
    content = re.sub(pattern, replacement, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"Fixed: {file_path}")

