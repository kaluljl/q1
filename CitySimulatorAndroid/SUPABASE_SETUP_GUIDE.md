# 🗄️ Supabase数据库配置指南

## 📋 配置步骤

### 1. 访问Supabase控制台
1. 打开 [supabase.com](https://supabase.com)
2. 登录您的账户
3. 选择项目：`bydjjbxogotpuftxzkkf`

### 2. 创建数据库表
1. 在左侧菜单中点击 **"SQL Editor"**
2. 点击 **"New query"**
3. 复制并粘贴 `supabase_database_setup.sql` 文件中的内容
4. 点击 **"Run"** 执行SQL脚本

### 3. 验证表创建
在SQL编辑器中运行以下查询来验证表是否创建成功：

```sql
-- 检查所有表
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('buildings', 'cities', 'resources', 'npcs', 'tasks', 'achievements');

-- 检查建筑表结构
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'buildings';
```

## 🏗️ 数据库表结构

### 建筑表 (buildings)
- **id**: 唯一标识符
- **type**: 建筑类型 (HOUSE, SHOP, etc.)
- **level**: 建筑等级
- **position_x/y**: 建筑位置
- **build_time**: 建造时间
- **status**: 建筑状态
- **efficiency**: 效率
- **capacity**: 容量
- **income**: 收入
- **maintenance_cost**: 维护成本

### 城市表 (cities)
- **id**: 城市ID
- **name**: 城市名称
- **population**: 人口数量
- **happiness**: 幸福度
- **energy**: 能量
- **gold**: 金币

### 资源表 (resources)
- **id**: 资源ID
- **type**: 资源类型 (WOOD, STONE, STEEL, FOOD)
- **amount**: 当前数量
- **max_capacity**: 最大容量
- **production_rate**: 生产速率
- **consumption_rate**: 消耗速率

### NPC表 (npcs)
- **id**: NPC ID
- **name**: 姓名
- **age**: 年龄
- **gender**: 性别
- **personality**: 性格
- **profession**: 职业
- **happiness**: 幸福度
- **energy**: 能量
- **health**: 健康度
- **wealth**: 财富

### 任务表 (tasks)
- **id**: 任务ID
- **title**: 任务标题
- **description**: 任务描述
- **type**: 任务类型
- **status**: 任务状态
- **reward_gold**: 金币奖励
- **reward_experience**: 经验奖励

### 成就表 (achievements)
- **id**: 成就ID
- **name**: 成就名称
- **description**: 成就描述
- **category**: 成就分类
- **is_unlocked**: 是否解锁

## 🔒 安全配置

### 行级安全策略 (RLS)
- 所有表都启用了RLS
- 用户只能访问自己的数据
- 自动数据隔离

### 索引优化
- 用户ID索引
- 位置索引
- 类型索引
- 状态索引

## 🚀 测试连接

### 1. 在应用中测试
1. 启动城市模拟游戏
2. 点击 **"云端"** 按钮
3. 点击 **"测试连接"**
4. 查看连接状态

### 2. 手动测试
在Supabase SQL编辑器中运行：

```sql
-- 测试插入建筑
INSERT INTO buildings (type, level, position_x, position_y, build_time, status)
VALUES ('HOUSE', 1, 5, 5, NOW(), 'NORMAL');

-- 测试查询建筑
SELECT * FROM buildings;

-- 测试删除建筑
DELETE FROM buildings WHERE position_x = 5 AND position_y = 5;
```

## 📊 数据管理

### 查看数据
```sql
-- 查看所有建筑
SELECT * FROM buildings ORDER BY created_at DESC;

-- 查看城市统计
SELECT 
    COUNT(*) as total_buildings,
    SUM(income) as total_income,
    AVG(efficiency) as avg_efficiency
FROM buildings;

-- 查看资源状态
SELECT type, amount, max_capacity, production_rate
FROM resources;
```

### 数据备份
- Supabase自动备份
- 支持时间点恢复
- 数据导出功能

## 🎯 完成检查清单

- [ ] 执行数据库创建脚本
- [ ] 验证所有表创建成功
- [ ] 测试应用连接
- [ ] 验证数据插入和查询
- [ ] 检查安全策略
- [ ] 测试多用户数据隔离

## 🆘 故障排除

### 常见问题
1. **连接失败**: 检查API密钥和URL
2. **权限错误**: 检查RLS策略
3. **表不存在**: 重新执行SQL脚本
4. **数据不同步**: 检查网络连接

### 联系支持
- Supabase文档: [docs.supabase.com](https://docs.supabase.com)
- 社区支持: [github.com/supabase/supabase](https://github.com/supabase/supabase)

---

🎉 **配置完成后，您的城市模拟游戏将拥有完整的云端数据同步能力！**

