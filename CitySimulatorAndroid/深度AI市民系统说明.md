# 深度AI市民系统 - 技术文档

## 🎯 核心理念

> "从'管理资源'转向'管理生命'，从'市长'到'神明'"

这是一个革命性的城市模拟游戏设计，核心不再是钢筋水泥，而是**拥有灵魂的AI市民**。

---

## ✅ 已实现的核心系统

### 1. 多维人格特质系统 ✅

**文件**: `PersonalityTraits.kt`

每个市民拥有8个独立的人格维度（0.0-1.0）：

- **外向性 (Extraversion)**: 影响社交能力和交友速度
- **勤奋度 (Diligence)**: 影响工作效率和技能学习
- **好奇心 (Curiosity)**: 影响探索新事物和接受新工作的意愿
- **友善度 (Kindness)**: 影响与他人的互动，引发合作或冲突
- **情绪稳定性 (Stability)**: 影响对负面事件的反应
- **创造力 (Creativity)**: 影响创新和艺术倾向
- **野心 (Ambition)**: 影响职业发展和目标追求
- **叛逆性 (Rebelliousness)**: 影响对权威的态度

**核心特性**：
- 随机生成：每个市民都是独特的
- 遗传系统：子女继承父母特质并产生变异
- 动态分类：自动识别主导人格类型（工作狂、社交达人、艺术家等）

```kotlin
val traits = PersonalityTraits.generateRandom()
val childTraits = PersonalityTraits.inherit(parent1.traits, parent2.traits)
```

---

### 2. 马斯洛需求层次系统 ✅

**文件**: `MaslowNeeds.kt`

实现完整的五层需求理论：

#### 第一层：生理需求
- 饥饿感、口渴、疲劳、健康

#### 第二层：安全需求
- 住所稳定性、工作保障、收入稳定性、城市安全度、医疗可及性

#### 第三层：社交需求
- 友谊、爱情、家庭、社区归属感、社交活动

#### 第四层：尊重需求
- 成就感、他人认可、社会地位、能力感、名声

#### 第五层：自我实现
- 梦想追求、创造力发挥、人生意义感、个人成长、社会贡献

**核心机制**：
- 加权满足度：底层需求权重更高
- 动态优先级：自动识别最紧迫的需求
- 级联影响：底层需求不满足时，高层需求无法实现

```kotlin
val satisfaction = needs.getOverallSatisfaction() // 0.0-1.0
val urgent = needs.getMostUrgentNeed() // 返回最需要满足的需求
```

---

### 3. 市民记忆系统 ✅

**文件**: `CitizenMemory.kt`

每个市民拥有独特的人生记忆库，记录重大事件：

**记忆类型**（30+种）：
- 人生里程碑：出生、毕业、第一份工作、升职、失业
- 社交关系：初恋、失恋、结婚、离婚、交友、绝交
- 重大决定：搬家、换工作、职业转变
- 城市事件：灾难、庆典、目击犯罪、助人/被助
- 成就与挫折：实现梦想、梦想破灭、获奖、受辱
- 健康相关：重病、康复、意外

**核心功能**：
- **情感影响值** (-1.0到1.0)：记录事件对情绪的影响
- **重要性分级**：从琐碎到人生转折
- **人生故事生成**：自动整合记忆形成叙事
- **智能管理**：最多保留100条，自动清理不重要的旧记忆

```kotlin
val memory = CitizenMemory(
    eventType = MemoryEventType.GOT_MARRIED,
    title = "与${partner.name}结婚",
    emotionalImpact = 0.9f,
    importance = MemoryImportance.LIFE_CHANGING
)
memoryCollection.addMemory(memory)
val story = memoryCollection.generateLifeStory()
```

---

### 4. 动态社交关系网络 ✅

**文件**: `SocialRelationship.kt`

复杂的人际关系网络，包含14种关系类型：

**家庭关系**：家人、配偶、子女、父母
**亲密关系**：恋人、前任、暗恋对象
**友谊**：挚友、密友、朋友、熟人
**工作关系**：同事、上司、下属、商业伙伴
**竞争与冲突**：竞争对手、仇人
**其他**：邻居、导师、学生

**关系属性**：
- **强度 (Strength)**: 关系牢固程度 (0-1)
- **情感度 (Affection)**: 好感/敌意 (-1到1)
- **信任度 (Trust)**: 相互信任程度 (0-1)
- **亲密度 (Intimacy)**: 亲密程度 (0-1)
- **共同记忆**：共享的人生事件
- **互动历史**：互动次数和频率

**动态演化**：
- 关系会随互动变化（正面/负面）
- 长时间不互动会变疏远
- 重大事件可能导致关系破裂
- 危机关系会触发特殊事件

```kotlin
val relationship = SocialRelationship(
    citizen1Id = "A",
    citizen2Id = "B",
    relationshipType = RelationshipType.ROMANTIC_PARTNER,
    affection = 0.8f
)
val updated = relationship.updateAfterInteraction(isPositive = true)
```

---

### 5. 信息传播与闲言碎语系统 ✅

**文件**: `GossipAndInformationSystem.kt`

模拟真实的信息传播和谣言扩散：

**八卦话题**：
- 城市发展、城市问题、新建筑、污染、交通
- 犯罪、庆典、灾难、丑闻、恋爱八卦
- 工作、邻里琐事、政策

**传播机制**：
- **传播速率**：基于话题和情感色彩计算
- **可信度衰减**：传播过程中可信度逐渐下降
- **内容变异**：消息可能在传播中被扭曲
- **性格影响**：外向型更愿意传播消息

**舆论形成**：
- 多条相关八卦形成公众舆论
- 舆论强度影响市民集体情绪
- 负面舆论降低城市幸福度

```kotlin
val gossip = GossipAndInformationSystem.createGossip(
    topic = GossipTopic.SCANDAL,
    content = "听说新工厂污染严重！",
    sentiment = GossipSentiment.WORRYING,
    originCitizenId = "市民A"
)

if (GossipAndInformationSystem.willCitizenSpread(citizen, gossip)) {
    val spread = GossipAndInformationSystem.spreadGossip(gossip, from, to)
}
```

---

### 6. 自发性群体活动系统 ✅

**文件**: `SpontaneousEventSystem.kt`

市民会根据集体情绪**自发组织活动**，不是预设脚本！

**活动类型**：
- **抗议 (PROTEST)**: 愤怒>0.7 + 幸福度<0.4 时触发
- **庆典 (CELEBRATION)**: 集体喜悦>0.7 时触发
- **社区聚会 (COMMUNITY_GATHERING)**: 团结度>0.6 时触发
- **快闪 (FLASH_MOB)**: 创造力>0.6 时触发
- 罢工、慈善活动、游行

**集体情绪计算**：
- 愤怒、喜悦、团结、创造力、挫折感、兴奋度
- 基于市民个体情绪和性格聚合

**活动特征**：
- **参与规模**：10%-30%市民自发参与
- **诉求生成**：抗议会自动生成合理的诉求
- **地点选择**：选择人口密集区域
- **持续时间**：1-6小时动态持续

**对城市影响**：
- 抗议会降低工作效率
- 庆典会提升幸福度
- 玩家需要回应市民诉求

```kotlin
val event = SpontaneousEventSystem.checkForSpontaneousEvent(
    citizens = allCitizens,
    cityHappiness = 0.3f,
    cityProsperity = 0.7f
)

if (event != null && event.type == EventType.PROTEST) {
    println("市民自发抗议：${event.description}")
    println("诉求：${event.demands.joinToString()}")
}
```

---

### 7. 玩家干预与建议系统 ✅

**文件**: `PlayerInterventionSystem.kt`

玩家是"神明"，可以给予建议，但市民有自由意志！

**建议类型**：
- 职业建议、住房建议、社交建议
- 健康建议、生活改善、财务建议

**接受概率计算**：
- **信任度 (30%权重)**: 市民对市长的信任
- **性格匹配**: 建议需符合市民性格
- **当前需求**: 痛点越大越愿意听
- **建议质量**: 好建议更容易被接受

**信任度系统**：
- 基于城市整体状况（幸福度、繁荣度）
- 基于市民个人状况（是否受益）
- 基于历史互动（过去建议是否有效）
- 范围：0-1

**公共请求**：
- 玩家可向全体市民发布请求
- 计算支持率（支持者/反对者/中立）
- 请求类型：努力工作、共同庆祝、保持耐心、节约资源、志愿服务

```kotlin
val response = PlayerInterventionSystem.suggestToCitizen(
    citizen = citizen,
    suggestion = Suggestion(
        type = MayorSuggestionType.CAREER_ADVICE,
        content = "你为什么不试试去那家新公司面试？"
    ),
    trustInMayor = 0.7f
)

if (response.isAccepted) {
    println("${citizen.name}: ${response.reason}")
    trustInMayor += response.trustChange
}
```

---

### 8. 情绪表达视觉组件 ✅

**文件**: `CitizenEmotionBubble.kt`

让市民的情绪可视化：

**情绪气泡**：
- 7种情绪状态：非常开心、开心、满足、平静、不开心、难过、愤怒
- 动态emoji显示：😄😊🙂😐😕😢😠
- 颜色编码：绿色（开心）→ 黄色（中性）→ 红色（愤怒）
- 呼吸动画：气泡有轻微缩放效果

**思维气泡**：
- 显示市民当前想法
- 3秒自动消失
- 淡入淡出动画

**活动指示器**：
- 显示市民当前活动的emoji
- 睡觉💤、工作💼、购物🛒、娱乐🎮、锻炼🏃等
- 颜色编码不同活动类型

```kotlin
@Composable
fun CitizenMarker(citizen: Citizen) {
    Box {
        CitizenEmotionBubble(citizen = citizen)
        CitizenActivityIndicator(activity = citizen.currentActivity)
    }
}
```

---

## 🎮 游戏体验变革

### 从"资源管理"到"生命观察"

**传统模拟游戏**：
- 玩家：市长
- 目标：建设繁华城市
- 关注点：数值、资源、效率

**深度AI市民系统**：
- 玩家：神明/观察者
- 目标：见证文明演进
- 关注点：故事、情感、人生

### 核心玩法

1. **观察个体人生**
   - 点击市民查看完整人生故事
   - 阅读他们的记忆和重要时刻
   - 了解他们的梦想和恐惧

2. **塑造环境**
   - 建设建筑、制定政策
   - 创造机会和挑战
   - 间接影响市民命运

3. **引导而非控制**
   - 给予建议但不能强制
   - 需要赢得市民信任
   - 见证他们的自由选择

4. **应对涌现事件**
   - 市民自发组织抗议
   - 处理集体情绪危机
   - 回应公众诉求

5. **见证社会演化**
   - 舆论传播和公众意见形成
   - 社区文化自然发展
   - 世代更替和文明传承

---

## 📊 技术架构

### 数据模型层
```
PersonalityTraits.kt      - 人格特质
MaslowNeeds.kt           - 需求层次
CitizenMemory.kt         - 记忆系统
SocialRelationship.kt    - 社交关系
```

### AI系统层
```
GossipAndInformationSystem.kt    - 信息传播
SpontaneousEventSystem.kt        - 群体活动
PlayerInterventionSystem.kt      - 玩家干预
```

### 表现层
```
CitizenEmotionBubble.kt   - 情绪可视化
SimpleCitizenMarker.kt    - 市民渲染
```

### 整合层（待实现）
```
EnhancedCitizenViewModel.kt  - 整合所有系统
DeepCitizenAI.kt             - 高级AI决策
```

---

## 🚀 下一步计划

### 待整合到现有Citizen模型：

1. **数据模型扩展**
   - 将 `PersonalityTraits` 添加到 `Citizen`
   - 将 `MaslowNeeds` 添加到 `Citizen`
   - 每个市民关联 `CitizenMemoryCollection`
   - 每个市民关联 `SocialNetwork`

2. **AI决策引擎**
   - 基于人格、需求、记忆做决策
   - 动态生成行为和想法
   - DeepSeek AI集成用于对话生成

3. **事件触发系统**
   - 监测群体情绪自动触发事件
   - 事件结果影响市民记忆和关系
   - 玩家干预影响事件走向

4. **UI增强**
   - 市民详情页展示完整人生故事
   - 事件通知和处理界面
   - 舆论面板和信任度仪表板

5. **性能优化**
   - 分批处理市民更新
   - 关系和记忆按需加载
   - 事件系统异步处理

---

## 💡 设计哲学

> **"玩家不再是全能的统治者，而是城市的守护神。你创造环境，但每个市民都是拥有自由意志的独立个体。他们会相爱、会争吵、会有梦想、会感到绝望。你的工作不是控制他们，而是见证并引导一个鲜活文明的诞生与演进。"**

这是模拟游戏的革命性进化！🎯

---

## 📝 使用示例

```kotlin
// 1. 创建一个拥有独特人格的市民
val personality = PersonalityTraits.generateRandom()
val needs = MaslowNeeds()
val memories = CitizenMemoryCollection(citizenId = "citizen_1")
val socialNetwork = SocialNetwork(citizenId = "citizen_1")

// 2. 市民经历重大事件
val marriageMemory = CitizenMemory(
    eventType = MemoryEventType.GOT_MARRIED,
    title = "结婚纪念日",
    emotionalImpact = 0.9f,
    importance = MemoryImportance.LIFE_CHANGING
)
memories.addMemory(marriageMemory)

// 3. 形成社交关系
val relationship = SocialRelationship(
    citizen1Id = "citizen_1",
    citizen2Id = "citizen_2",
    relationshipType = RelationshipType.SPOUSE,
    affection = 0.9f
)
socialNetwork.addRelationship(relationship)

// 4. 信息在市民间传播
val gossip = GossipAndInformationSystem.createGossip(
    topic = GossipTopic.CELEBRATION,
    content = "听说市中心要建新公园了！",
    sentiment = GossipSentiment.EXCITING
)

// 5. 检测是否触发自发事件
val event = SpontaneousEventSystem.checkForSpontaneousEvent(
    citizens = allCitizens,
    cityHappiness = 0.8f,
    cityProsperity = 0.9f
)

// 6. 玩家给市民建议
val suggestionResponse = PlayerInterventionSystem.suggestToCitizen(
    citizen = citizen,
    suggestion = Suggestion(
        type = MayorSuggestionType.CAREER_ADVICE,
        content = "考虑学习新技能提升自己"
    ),
    trustInMayor = trustLevel
)
```

---

**构建时间**: 2024年11月
**状态**: 核心系统已实现 ✅
**下一步**: 整合到游戏主循环 🚀

