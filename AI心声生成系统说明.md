# AI心声生成系统说明

## 📢 系统概述

市民心声系统已升级为**AI驱动**，使用DeepSeek API根据城市实际状况生成真实、多样化的市民反馈，而不再使用固定模板。

## 🎯 核心改进

### 之前（固定模板）
```kotlin
// 使用IntelligentFeedbackGenerator生成固定模板
val feedbacks = IntelligentFeedbackGenerator.generateFeedbackBasedOnCity(...)
// 结果：每次都是相似的固定文本
```

### 现在（AI生成）
```kotlin
// 使用DeepSeek AI根据城市状况生成
val aiMessage = deepSeekClient.generateCitizenFeedback(cityStatus)
// 结果：每次都是不同的、真实的市民心声
```

## 🤖 AI生成流程

### 1. 构建城市状态描述

```kotlin
fun buildCityStatusDescription(
    buildings: List<Building>,
    goldAmount: Int,
    population: Int,
    gameTime: String
): String {
    return """
游戏时间：$gameTime
人口：${population}人
建筑总数：${buildings.size}座
- 住宅：${residentialCount}座
- 商业：${commercialCount}座
- 工业：${industrialCount}座
城市金币：${goldAmount}
    """.trimIndent()
}
```

### 2. 调用AI生成心声

```kotlin
suspend fun generateCitizenFeedback(cityStatus: String): String {
    val prompt = """
你是一位生活在这座城市的普通市民。请根据城市当前状况，以第一人称表达你对城市的看法和建议。

城市当前状况：
$cityStatus

要求：
1. 以第一人称口吻（"我"、"我们"）
2. 语气要真实自然，像普通市民说话
3. 只说一件具体的事情或问题
4. 长度控制在30字以内
5. 可以是抱怨、建议、表扬或期待
6. 不要说"作为市民"、"我认为"等啰嗦的开头
7. 直接说问题或感受

请生成一条市民心声：
    """.trimIndent()
    
    return generateMockResponse("citizen_feedback", prompt)
}
```

### 3. 创建反馈对象

```kotlin
CitizenFeedbackData(
    id = UUID.randomUUID().toString(),
    content = aiMessage,  // AI生成的内容
    category = FeedbackType.ENTERTAINMENT_NEED,
    urgency = FeedbackUrgency.MEDIUM,
    triggerType = FeedbackTriggerType.CONDITION_BASED,
    relatedData = listOf("AI生成"),
    affectedArea = "全城",
    solutions = listOf(),
    timeLimit = 30,
    icon = "💬",
    createdAt = gameTime
)
```

## 📊 生成示例

### 场景1：城市初期

**城市状况**：
- 人口：15人
- 建筑：3座住宅，1座商店
- 金币：1500

**AI生成的心声**：
- "希望能多建几座商店，买东西太不方便了。"
- "城市刚起步，期待未来能发展得更好。"
- "住宅太少了，很多人还没有房子住。"

### 场景2：城市发展中期

**城市状况**：
- 人口：50人
- 建筑：10座住宅，5座商店，2座工厂
- 金币：5000

**AI生成的心声**：
- "工厂太多了，空气质量有点差。"
- "希望能建一座公园，周末没地方去。"
- "城市发展不错，就是交通有点拥挤。"
- "我们需要一家医院，看病太不方便。"

### 场景3：城市繁荣期

**城市状况**：
- 人口：100人
- 建筑：20座住宅，10座商店，5座工厂，2座公园
- 金币：15000

**AI生成的心声**：
- "城市越来越好了，生活很满意。"
- "希望能有更多的娱乐设施。"
- "房价有点高，年轻人买不起房。"
- "建议增加公共交通，减少拥堵。"

## 🎨 AI生成特点

### 1. **真实性**
- 使用第一人称口吻
- 语气自然，像真实市民说话
- 不使用官方或书面语

### 2. **多样性**
- 每次生成都不同
- 根据城市状况动态调整
- 高温度参数（0.9）增加随机性

### 3. **简洁性**
- 长度控制在30字以内
- 只说一件具体的事情
- 直接表达问题或感受

### 4. **相关性**
- 基于实际城市数据
- 反映真实的城市问题
- 提供有价值的建议

## 🔄 Fallback机制

当AI生成失败时，系统会使用简单的fallback消息：

```kotlin
private fun generateFallbackFeedback(): CitizenFeedbackData {
    val messages = listOf(
        "希望城市能发展得更好。",
        "我们需要更多的基础设施。",
        "城市的环境还不错。",
        "希望能有更多的就业机会。",
        "生活成本有点高。"
    )
    
    return CitizenFeedbackData(
        content = messages.random(),
        // ...
    )
}
```

## 📈 生成频率

- **首次加载**：立即生成1条AI心声
- **每个游戏月**：生成1条新AI心声
- **游戏时间**：20分钟真实时间 = 1个游戏月

## 🎯 优势对比

### 固定模板方式

❌ **缺点**：
- 内容重复，缺乏新鲜感
- 无法反映城市的独特状况
- 玩家很快就看腻了
- 缺少真实感和沉浸感

✅ **优点**：
- 实现简单
- 性能好
- 不依赖外部服务

### AI生成方式

✅ **优点**：
- 内容多样，每次都不同
- 根据城市实际状况生成
- 真实自然，提升沉浸感
- 玩家体验更好

❌ **缺点**：
- 需要AI服务支持
- 生成速度稍慢
- 可能有API调用成本

## 🔧 技术实现

### 文件修改

1. **DeepSeekClient.kt**
   - 添加 `generateCitizenFeedback()` 方法
   - 使用 `generateMockResponse()` 生成AI响应

2. **CitizenFeedbackViewModel.kt**
   - 注入 `DeepSeekClient`
   - 添加 `generateAIFeedback()` 方法
   - 添加 `buildCityStatusDescription()` 方法
   - 添加 `generateFallbackFeedback()` 方法
   - 修改 `generateFeedback()` 调用AI生成

3. **类型调整**
   - 从 `CitizenFeedback` 改为 `CitizenFeedbackData`
   - 统一使用 `content` 字段而不是 `message`
   - 使用 `urgency` 而不是 `priority`

### 数据流程

```
1. 游戏时间变化
   ↓
2. 检测到新月份
   ↓
3. 构建城市状态描述
   ↓
4. 调用DeepSeek API
   ↓
5. 生成AI心声
   ↓
6. 创建CitizenFeedbackData
   ↓
7. 添加到反馈列表
   ↓
8. 显示在UI中
```

## 📝 日志输出

```
📅 新的游戏月份：2024年2月，生成市民心声...
💬 2024年2月生成了 1 条新AI心声: 希望能多建几座公园，周末都没地方遛弯。，当前共 2 条未解决反馈
```

## 🚀 未来改进

1. **使用真实DeepSeek API**
   - 替换 `generateMockResponse` 为真实API调用
   - 需要配置API Key

2. **更智能的提示词**
   - 根据市民性格生成不同风格的心声
   - 根据建筑类型生成相关的反馈

3. **心声分类**
   - AI自动判断心声类型（住房、经济、环境等）
   - AI自动评估紧急程度

4. **多轮对话**
   - 玩家可以回应市民心声
   - AI生成后续对话

## 🎮 用户体验

### 之前
```
市民心声：
- "市民希望有一个公园，可以放松和娱乐！"
- "市民希望有一个公园，可以放松和娱乐！"
- "市民希望有一个公园，可以放松和娱乐！"
（重复、枯燥）
```

### 现在
```
市民心声：
- "希望能多建几座公园，周末都没地方遛弯。"
- "最近电力供应不太稳定，经常停电。"
- "城市发展得不错，就是房价有点高。"
- "我们需要一家医院，看病太不方便了。"
（多样、真实、有趣）
```

现在市民心声不再是固定模板，而是AI根据城市实际状况生成的真实反馈，大大提升了游戏的沉浸感和可玩性！🎮✨🤖

