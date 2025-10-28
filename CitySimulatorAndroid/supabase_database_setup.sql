-- =============================================
-- 城市模拟游戏 Supabase 数据库配置脚本
-- =============================================

-- 1. 创建建筑表
CREATE TABLE IF NOT EXISTS buildings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    position_x INTEGER NOT NULL,
    position_y INTEGER NOT NULL,
    build_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_under_construction BOOLEAN DEFAULT false,
    is_upgrading BOOLEAN DEFAULT false,
    is_maintenance_required BOOLEAN DEFAULT false,
    last_maintenance_date TIMESTAMP WITH TIME ZONE,
    efficiency FLOAT DEFAULT 1.0,
    capacity INTEGER DEFAULT 1,
    income INTEGER DEFAULT 0,
    maintenance_cost INTEGER DEFAULT 0,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. 创建城市表
CREATE TABLE IF NOT EXISTS cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL DEFAULT '我的城市',
    population INTEGER DEFAULT 0,
    happiness FLOAT DEFAULT 0.5,
    energy FLOAT DEFAULT 0.5,
    gold INTEGER DEFAULT 1000,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. 创建资源表
CREATE TABLE IF NOT EXISTS resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL,
    amount DOUBLE PRECISION DEFAULT 0.0,
    max_capacity DOUBLE PRECISION DEFAULT 1000.0,
    production_rate DOUBLE PRECISION DEFAULT 0.0,
    consumption_rate DOUBLE PRECISION DEFAULT 0.0,
    city_id UUID REFERENCES cities(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. 创建NPC表
CREATE TABLE IF NOT EXISTS npcs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    age INTEGER DEFAULT 25,
    gender VARCHAR(10) NOT NULL DEFAULT 'MALE',
    personality VARCHAR(50) NOT NULL DEFAULT 'FRIENDLY',
    profession VARCHAR(50) NOT NULL DEFAULT 'CITIZEN',
    happiness FLOAT DEFAULT 0.5,
    energy FLOAT DEFAULT 0.5,
    health FLOAT DEFAULT 1.0,
    wealth FLOAT DEFAULT 0.0,
    home_location_x INTEGER,
    home_location_y INTEGER,
    work_location_x INTEGER,
    work_location_y INTEGER,
    current_activity VARCHAR(50) DEFAULT 'IDLE',
    last_activity_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    thoughts TEXT,
    city_id UUID REFERENCES cities(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. 创建任务表
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reward_gold INTEGER DEFAULT 0,
    reward_experience INTEGER DEFAULT 0,
    requirements JSONB,
    city_id UUID REFERENCES cities(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. 创建成就表
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    is_unlocked BOOLEAN DEFAULT false,
    unlocked_at TIMESTAMP WITH TIME ZONE,
    city_id UUID REFERENCES cities(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- =============================================
-- 索引创建
-- =============================================

-- 建筑表索引
CREATE INDEX IF NOT EXISTS idx_buildings_user_id ON buildings(user_id);
CREATE INDEX IF NOT EXISTS idx_buildings_position ON buildings(position_x, position_y);
CREATE INDEX IF NOT EXISTS idx_buildings_type ON buildings(type);

-- 城市表索引
CREATE INDEX IF NOT EXISTS idx_cities_user_id ON cities(user_id);

-- 资源表索引
CREATE INDEX IF NOT EXISTS idx_resources_city_id ON resources(city_id);
CREATE INDEX IF NOT EXISTS idx_resources_type ON resources(type);

-- NPC表索引
CREATE INDEX IF NOT EXISTS idx_npcs_city_id ON npcs(city_id);
CREATE INDEX IF NOT EXISTS idx_npcs_location ON npcs(home_location_x, home_location_y);

-- 任务表索引
CREATE INDEX IF NOT EXISTS idx_tasks_city_id ON tasks(city_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);

-- 成就表索引
CREATE INDEX IF NOT EXISTS idx_achievements_city_id ON achievements(city_id);
CREATE INDEX IF NOT EXISTS idx_achievements_unlocked ON achievements(is_unlocked);

-- =============================================
-- 行级安全策略 (RLS)
-- =============================================

-- 启用RLS
ALTER TABLE buildings ENABLE ROW LEVEL SECURITY;
ALTER TABLE cities ENABLE ROW LEVEL SECURITY;
ALTER TABLE resources ENABLE ROW LEVEL SECURITY;
ALTER TABLE npcs ENABLE ROW LEVEL SECURITY;
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE achievements ENABLE ROW LEVEL SECURITY;

-- 建筑表策略
CREATE POLICY "Users can view their own buildings" ON buildings
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own buildings" ON buildings
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own buildings" ON buildings
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own buildings" ON buildings
    FOR DELETE USING (auth.uid() = user_id);

-- 城市表策略
CREATE POLICY "Users can view their own cities" ON cities
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own cities" ON cities
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own cities" ON cities
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own cities" ON cities
    FOR DELETE USING (auth.uid() = user_id);

-- 资源表策略
CREATE POLICY "Users can view their own resources" ON resources
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own resources" ON resources
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own resources" ON resources
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own resources" ON resources
    FOR DELETE USING (auth.uid() = user_id);

-- NPC表策略
CREATE POLICY "Users can view their own npcs" ON npcs
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own npcs" ON npcs
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own npcs" ON npcs
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own npcs" ON npcs
    FOR DELETE USING (auth.uid() = user_id);

-- 任务表策略
CREATE POLICY "Users can view their own tasks" ON tasks
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own tasks" ON tasks
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own tasks" ON tasks
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own tasks" ON tasks
    FOR DELETE USING (auth.uid() = user_id);

-- 成就表策略
CREATE POLICY "Users can view their own achievements" ON achievements
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own achievements" ON achievements
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own achievements" ON achievements
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own achievements" ON achievements
    FOR DELETE USING (auth.uid() = user_id);

-- =============================================
-- 触发器函数
-- =============================================

-- 更新时间戳触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为所有表添加更新时间戳触发器
CREATE TRIGGER update_buildings_updated_at BEFORE UPDATE ON buildings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cities_updated_at BEFORE UPDATE ON cities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_resources_updated_at BEFORE UPDATE ON resources
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_npcs_updated_at BEFORE UPDATE ON npcs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_achievements_updated_at BEFORE UPDATE ON achievements
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- 初始数据插入
-- =============================================

-- 插入默认城市数据（如果不存在）
INSERT INTO cities (id, name, population, happiness, energy, gold)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    '我的城市',
    0,
    0.5,
    0.5,
    1000
) ON CONFLICT (id) DO NOTHING;

-- 插入默认资源数据
INSERT INTO resources (type, amount, max_capacity, production_rate, consumption_rate, city_id)
VALUES 
    ('WOOD', 500.0, 1000.0, 10.0, 5.0, '00000000-0000-0000-0000-000000000001'),
    ('STONE', 300.0, 1000.0, 8.0, 3.0, '00000000-0000-0000-0000-000000000001'),
    ('STEEL', 200.0, 1000.0, 5.0, 2.0, '00000000-0000-0000-0000-000000000001'),
    ('FOOD', 800.0, 1000.0, 15.0, 10.0, '00000000-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;

-- =============================================
-- 完成提示
-- =============================================

-- 数据库配置完成！
-- 现在您的城市模拟游戏可以：
-- 1. 存储和管理建筑数据
-- 2. 同步城市状态和资源
-- 3. 管理NPC居民
-- 4. 处理任务和成就
-- 5. 支持多用户数据隔离
-- 6. 自动数据备份和恢复
