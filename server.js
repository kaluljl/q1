const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3000;

// 中间件
app.use(cors());
app.use(express.json());

// 模拟城市数据API
app.get('/api/city/status', (req, res) => {
    res.json({
        status: 'running',
        timestamp: new Date().toISOString(),
        services: {
            database: 'connected',
            ai: 'active',
            feedback: 'generating'
        }
    });
});

// 模拟建筑数据API
app.get('/api/buildings', (req, res) => {
    res.json([
        {
            id: 'building_1',
            type: 'HOUSE',
            level: 1,
            position: { x: 5, y: 5 },
            status: 'NORMAL'
        },
        {
            id: 'building_2', 
            type: 'SHOP',
            level: 1,
            position: { x: 8, y: 8 },
            status: 'NORMAL'
        }
    ]);
});

// 模拟资源数据API
app.get('/api/resources', (req, res) => {
    res.json([
        {
            id: 'wood_1',
            type: 'WOOD',
            amount: 100,
            maxCapacity: 200
        },
        {
            id: 'stone_1',
            type: 'STONE', 
            amount: 50,
            maxCapacity: 150
        }
    ]);
});

// 模拟市民反馈API
app.get('/api/feedback', (req, res) => {
    res.json([
        {
            id: 'feedback_1',
            type: 'TRAFFIC_CONGESTION',
            message: '交通太堵了，上班总是迟到',
            priority: 4,
            urgency: 'HIGH',
            createdAt: new Date().toISOString()
        },
        {
            id: 'feedback_2',
            type: 'NEED_PARK',
            message: '希望多建几个公园，带孩子散步都没地方去',
            priority: 2,
            urgency: 'LOW',
            createdAt: new Date().toISOString()
        }
    ]);
});

// 健康检查端点
app.get('/health', (req, res) => {
    res.json({ 
        status: 'OK', 
        timestamp: new Date().toISOString(),
        uptime: process.uptime()
    });
});

// 启动服务器
app.listen(PORT, () => {
    console.log(`🚀 城市模拟器服务器已启动！`);
    console.log(`📡 API服务运行在: http://localhost:${PORT}`);
    console.log(`🏥 健康检查: http://localhost:${PORT}/health`);
    console.log(`🏙️ 城市状态: http://localhost:${PORT}/api/city/status`);
    console.log(`🏢 建筑数据: http://localhost:${PORT}/api/buildings`);
    console.log(`📦 资源数据: http://localhost:${PORT}/api/resources`);
    console.log(`💬 市民反馈: http://localhost:${PORT}/api/feedback`);
    console.log(`\n按 Ctrl+C 停止服务器`);
});
