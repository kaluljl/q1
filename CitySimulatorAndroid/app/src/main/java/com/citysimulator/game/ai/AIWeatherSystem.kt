package com.citysimulator.game.ai

import com.citysimulator.game.data.model.WeatherType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * AI天气和环境系统
 */
class AIWeatherSystem {
    
    private var currentWeather: WeatherType = WeatherType.SUNNY
    private var currentTemperature: Float = 20f
    private var currentHumidity: Float = 50f
    private var currentWindSpeed: Float = 5f
    
    /**
     * 生成智能天气变化流
     */
    fun generateWeatherFlow(): Flow<WeatherState> = flow {
        while (true) {
            val newWeather = predictNextWeather(currentWeather)
            currentWeather = newWeather
            
            // 更新环境参数
            updateEnvironmentParameters(newWeather)
            
            emit(WeatherState(
                weather = currentWeather,
                temperature = currentTemperature,
                humidity = currentHumidity,
                windSpeed = currentWindSpeed,
                description = getWeatherDescription(currentWeather),
                effects = getWeatherEffects(currentWeather)
            ))
            
            // 每10分钟更新一次天气
            delay(600000L)
        }
    }
    
    /**
     * 基于当前天气预测下一个天气状态
     */
    private fun predictNextWeather(current: WeatherType): WeatherType {
        return when (current) {
            WeatherType.SUNNY -> {
                when (Random.nextInt(100)) {
                    in 0..70 -> WeatherType.SUNNY // 70%保持晴天
                    in 71..85 -> WeatherType.CLOUDY // 15%转多云
                    in 86..95 -> WeatherType.RAINY // 10%转雨天
                    else -> WeatherType.FOGGY // 5%转雾
                }
            }
            WeatherType.CLOUDY -> {
                when (Random.nextInt(100)) {
                    in 0..40 -> WeatherType.CLOUDY // 40%保持多云
                    in 41..65 -> WeatherType.SUNNY // 25%转晴
                    in 66..85 -> WeatherType.RAINY // 20%转雨
                    in 86..95 -> WeatherType.STORMY // 10%转暴风雨
                    else -> WeatherType.SNOWY // 5%转雪
                }
            }
            WeatherType.RAINY -> {
                when (Random.nextInt(100)) {
                    in 0..50 -> WeatherType.RAINY // 50%继续下雨
                    in 51..75 -> WeatherType.CLOUDY // 25%转多云
                    in 76..90 -> WeatherType.SUNNY // 15%转晴
                    else -> WeatherType.SNOWY // 10%转雪（如果温度低）
                }
            }
            WeatherType.STORMY -> {
                when (Random.nextInt(100)) {
                    in 0..60 -> WeatherType.STORMY // 60%继续暴风雨
                    in 61..80 -> WeatherType.RAINY // 20%转雨
                    in 81..95 -> WeatherType.CLOUDY // 15%转多云
                    else -> WeatherType.SUNNY // 5%转晴
                }
            }
            WeatherType.SNOWY -> {
                when (Random.nextInt(100)) {
                    in 0..60 -> WeatherType.SNOWY // 60%继续下雪
                    in 61..80 -> WeatherType.CLOUDY // 20%转多云
                    in 81..95 -> WeatherType.RAINY // 15%转雨
                    else -> WeatherType.SUNNY // 5%转晴
                }
            }
            WeatherType.FOGGY -> {
                when (Random.nextInt(100)) {
                    in 0..50 -> WeatherType.FOGGY // 50%继续雾
                    in 51..80 -> WeatherType.CLOUDY // 30%转多云
                    else -> WeatherType.SUNNY // 20%转晴
                }
            }
        }
    }
    
    /**
     * 根据天气更新环境参数
     */
    private fun updateEnvironmentParameters(weather: WeatherType) {
        when (weather) {
            WeatherType.SUNNY -> {
                currentTemperature = 20f + Random.nextFloat() * 10f
                currentHumidity = 40f + Random.nextFloat() * 20f
                currentWindSpeed = 2f + Random.nextFloat() * 5f
            }
            WeatherType.CLOUDY -> {
                currentTemperature = 15f + Random.nextFloat() * 10f
                currentHumidity = 50f + Random.nextFloat() * 20f
                currentWindSpeed = 3f + Random.nextFloat() * 7f
            }
            WeatherType.RAINY -> {
                currentTemperature = 10f + Random.nextFloat() * 10f
                currentHumidity = 70f + Random.nextFloat() * 20f
                currentWindSpeed = 5f + Random.nextFloat() * 10f
            }
            WeatherType.STORMY -> {
                currentTemperature = 10f + Random.nextFloat() * 10f
                currentHumidity = 80f + Random.nextFloat() * 15f
                currentWindSpeed = 20f + Random.nextFloat() * 20f
            }
            WeatherType.SNOWY -> {
                currentTemperature = -5f + Random.nextFloat() * 10f
                currentHumidity = 60f + Random.nextFloat() * 20f
                currentWindSpeed = 3f + Random.nextFloat() * 8f
            }
            WeatherType.FOGGY -> {
                currentTemperature = 10f + Random.nextFloat() * 10f
                currentHumidity = 80f + Random.nextFloat() * 15f
                currentWindSpeed = 1f + Random.nextFloat() * 3f
            }
        }
    }
    
    /**
     * 获取天气描述
     */
    private fun getWeatherDescription(weather: WeatherType): String {
        return when (weather) {
            WeatherType.SUNNY -> "☀️ 晴朗的天气，适合户外活动"
            WeatherType.CLOUDY -> "☁️ 多云，温度适宜"
            WeatherType.RAINY -> "🌧️ 下雨了，居民会减少外出"
            WeatherType.STORMY -> "⛈️ 暴风雨，请待在室内"
            WeatherType.SNOWY -> "❄️ 下雪了，注意保暖"
            WeatherType.FOGGY -> "🌫️ 有雾，能见度较低"
        }
    }
    
    /**
     * 获取天气对城市的影响
     */
    private fun getWeatherEffects(weather: WeatherType): WeatherEffects {
        return when (weather) {
            WeatherType.SUNNY -> WeatherEffects(
                happinessModifier = 1.2f,
                energyModifier = 1.1f,
                productivityModifier = 1.1f,
                trafficModifier = 1.0f,
                description = "晴天提升居民心情和工作效率"
            )
            WeatherType.CLOUDY -> WeatherEffects(
                happinessModifier = 1.0f,
                energyModifier = 1.0f,
                productivityModifier = 1.0f,
                trafficModifier = 1.0f,
                description = "多云天气对城市影响较小"
            )
            WeatherType.RAINY -> WeatherEffects(
                happinessModifier = 0.8f,
                energyModifier = 0.9f,
                productivityModifier = 0.85f,
                trafficModifier = 0.7f,
                description = "雨天降低居民活动和交通效率"
            )
            WeatherType.STORMY -> WeatherEffects(
                happinessModifier = 0.6f,
                energyModifier = 0.7f,
                productivityModifier = 0.6f,
                trafficModifier = 0.4f,
                description = "暴风雨严重影响城市运作"
            )
            WeatherType.SNOWY -> WeatherEffects(
                happinessModifier = 0.9f,
                energyModifier = 0.85f,
                productivityModifier = 0.8f,
                trafficModifier = 0.6f,
                description = "雪天显著影响交通和工作效率"
            )
            WeatherType.FOGGY -> WeatherEffects(
                happinessModifier = 0.9f,
                energyModifier = 0.95f,
                productivityModifier = 0.9f,
                trafficModifier = 0.7f,
                description = "雾天降低能见度，影响交通"
            )
        }
    }
    
    /**
     * 根据季节生成合适的天气
     */
    fun generateSeasonalWeather(month: Int): WeatherType {
        return when (month) {
            in 3..5 -> { // 春季
                when (Random.nextInt(100)) {
                    in 0..40 -> WeatherType.SUNNY
                    in 41..70 -> WeatherType.CLOUDY
                    in 71..90 -> WeatherType.RAINY
                    else -> WeatherType.STORMY
                }
            }
            in 6..8 -> { // 夏季
                when (Random.nextInt(100)) {
                    in 0..60 -> WeatherType.SUNNY
                    in 61..80 -> WeatherType.CLOUDY
                    in 81..95 -> WeatherType.RAINY
                    else -> WeatherType.STORMY
                }
            }
            in 9..11 -> { // 秋季
                when (Random.nextInt(100)) {
                    in 0..35 -> WeatherType.SUNNY
                    in 36..65 -> WeatherType.CLOUDY
                    in 66..85 -> WeatherType.RAINY
                    in 86..95 -> WeatherType.STORMY
                    else -> WeatherType.FOGGY
                }
            }
            else -> { // 冬季
                when (Random.nextInt(100)) {
                    in 0..30 -> WeatherType.SUNNY
                    in 31..55 -> WeatherType.CLOUDY
                    in 56..75 -> WeatherType.SNOWY
                    in 76..90 -> WeatherType.RAINY
                    else -> WeatherType.FOGGY
                }
            }
        }
    }
    
    /**
     * 获取天气建议
     */
    fun getWeatherAdvice(weather: WeatherType): String {
        return when (weather) {
            WeatherType.SUNNY -> "好天气！适合建造和发展城市。"
            WeatherType.CLOUDY -> "天气平稳，正常发展即可。"
            WeatherType.RAINY -> "雨天会降低效率，注意调整工作安排。"
            WeatherType.STORMY -> "暴风雨天气，建议暂停户外活动。"
            WeatherType.SNOWY -> "雪天影响较大，建议增加室内设施。"
            WeatherType.FOGGY -> "雾天能见度低，注意交通安全。"
        }
    }
    
    /**
     * 计算天气对NPC的影响
     */
    fun calculateWeatherImpactOnNPC(
        weather: WeatherType,
        npcActivity: com.citysimulator.game.data.model.NPCActivity
    ): Float {
        val effects = getWeatherEffects(weather)
        
        return when (npcActivity) {
            com.citysimulator.game.data.model.NPCActivity.WORKING -> effects.productivityModifier
            com.citysimulator.game.data.model.NPCActivity.TRAVELING -> effects.trafficModifier
            com.citysimulator.game.data.model.NPCActivity.ENTERTAINMENT,
            com.citysimulator.game.data.model.NPCActivity.SOCIALIZING -> effects.happinessModifier
            else -> 1.0f
        }
    }
}

/**
 * 天气状态
 */
data class WeatherState(
    val weather: WeatherType,
    val temperature: Float,
    val humidity: Float,
    val windSpeed: Float,
    val description: String,
    val effects: WeatherEffects
)

/**
 * 天气影响效果
 */
data class WeatherEffects(
    val happinessModifier: Float, // 幸福度修正
    val energyModifier: Float, // 能量修正
    val productivityModifier: Float, // 生产力修正
    val trafficModifier: Float, // 交通效率修正
    val description: String
)

