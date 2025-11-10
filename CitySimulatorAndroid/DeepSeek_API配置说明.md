# DeepSeek API 配置说明

## 🤖 什么是DeepSeek API？

DeepSeek是一个强大的AI模型，可以让游戏中的市民心声、对话、日记等内容由真实AI动态生成，而不是使用固定模板。

## 📋 配置步骤

### 1. 获取API密钥

1. 访问 [DeepSeek开放平台](https://platform.deepseek.com/)
2. 注册账号并登录
3. 进入"API Keys"页面
4. 创建新的API密钥（会以`sk-`开头）
5. 复制API密钥（只显示一次，请妥善保存）

### 2. 配置API密钥到游戏中

打开文件：`app/src/main/java/com/citysimulator/game/di/AppModule.kt`

找到以下代码：

```kotlin
@Provides
@Singleton
fun provideDeepSeekClient(): DeepSeekClient {
    return DeepSeekClient(
        apiKey = "your-api-key-here",  // 👈 在这里替换成你的API密钥
        baseUrl = "https://api.deepseek.com/v1"
    )
}
```

将 `"your-api-key-here"` 替换成你的真实API密钥：

```kotlin
@Provides
@Singleton
fun provideDeepSeekClient(): DeepSeekClient {
    return DeepSeekClient(
        apiKey = "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxx",  // 你的真实密钥
        baseUrl = "https://api.deepseek.com/v1"
    )
}
```

### 3. 重新编译APK

```powershell
cd E:\a5\CitySimulatorAndroid
.\gradlew assembleDebug
```

### 4. 安装新的APK

安装位置：`app/build/outputs/apk/debug/app-debug.apk`

## 💰 费用说明

- DeepSeek API按使用量计费
- 新用户通常有免费额度
- 市民心声：每条约消耗50-100 tokens，成本极低
- 建议设置每月消费限额

## 🔒 安全提示

⚠️ **重要**：
1. **不要**将包含真实API密钥的代码提交到公开的Git仓库
2. **不要**将API密钥分享给其他人
3. 建议在测试完成后，将API密钥改回 `"your-api-key-here"`

## 🎮 使用效果

### 未配置API密钥（使用模拟数据）

游戏会从20条预设模板中随机选择：
- ✅ 运行稳定，不需要网络
- ❌ 内容固定，重复度高

### 配置API密钥后（使用真实AI）

每次生成都是AI根据城市状况创作的新内容：
- ✅ 内容多样，每次都不同
- ✅ 更真实，更贴合城市实际情况
- ✅ AI会根据人口、建筑、金币等数据生成相关反馈
- ❌ 需要网络连接
- ❌ 有少量API调用成本

## 🔧 工作原理

系统会自动检测API密钥：

```kotlin
if (apiKey.isNotEmpty() && apiKey != "your-api-key-here" && apiKey.startsWith("sk-")) {
    // 使用真实DeepSeek API
    println("🤖 尝试调用真实DeepSeek API...")
    val response = callRealDeepSeekAPI(...)
} else {
    // 使用模拟响应（20条模板）
    println("⚠️ 未配置API密钥，使用模拟响应")
    val response = generateMockResponse("citizen_feedback", prompt)
}
```

如果API调用失败（网络问题、余额不足等），会自动降级到模拟响应：

```kotlin
catch (e: Exception) {
    println("❌ DeepSeek API失败，降级到模拟响应: ${e.message}")
    val response = generateMockResponse("citizen_feedback", prompt)
}
```

## 📊 调试信息

游戏运行时会在Logcat中输出调试信息：

- `🤖 尝试调用真实DeepSeek API...` - 开始调用真实API
- `✅ DeepSeek API成功调用` - API调用成功
- `⚠️ 未配置API密钥，使用模拟响应` - 使用模拟数据
- `❌ DeepSeek API失败，降级到模拟响应` - API失败，降级

## 🎯 推荐配置

- **测试阶段**：不配置API密钥，使用免费的模拟数据
- **正式使用**：配置API密钥，体验真实AI生成的沉浸感

## 📝 示例对比

### 模拟响应（固定模板）

```
"希望能多建几座公园，周末都没地方遛弯。"
"城市发展得不错，就是房价有点高。"
"我们需要一家医院，看病太不方便了。"
```

### 真实AI生成

```
城市状况：人口50人，建筑12座，金币3000
AI生成："建了这么多工厂，但配套的住宅不够，工人们上下班都很辛苦。"

城市状况：人口10人，建筑3座，金币500
AI生成："城市刚起步，希望能先解决基本的住房问题，让更多人安居乐业。"

城市状况：人口100人，建筑30座，金币15000
AI生成："城市发展很快，但公共设施还跟不上，希望能建个图书馆丰富大家的文化生活。"
```

可以看到，真实AI会根据城市的实际数据生成更贴切、更个性化的反馈！

## ❓ 常见问题

**Q: 不配置API密钥可以玩吗？**  
A: 完全可以！游戏会使用20条精心设计的模板，同样有趣。

**Q: API调用会很慢吗？**  
A: 通常1-3秒内完成，异步调用不会卡顿游戏。

**Q: 如何查看是否在使用真实API？**  
A: 查看Logcat日志，会显示"🤖 尝试调用真实DeepSeek API..."

**Q: 可以切换回模拟模式吗？**  
A: 可以！将API密钥改回 `"your-api-key-here"` 即可。

---

现在，您的游戏已经支持真实的DeepSeek AI生成了！🎉

是否配置API密钥完全由您决定，两种模式都能提供良好的游戏体验。✨

