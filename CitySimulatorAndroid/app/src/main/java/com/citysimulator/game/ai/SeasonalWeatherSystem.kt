package com.citysimulator.game.ai

import com.citysimulator.game.data.model.WeatherType
import java.util.*
import kotlin.random.Random

/**
 * 季节性天气系统
 * 
 * 根据游戏日期和时间智能生成符合季节特征的天气
 * 
 * 季节划分（北半球）：
 * - 春季：3-5月
 * - 夏季：6-8月
 * - 秋季：9-11月
 * - 冬季：12-2月
 * 
 * @author AI进化论-花生
 */
object SeasonalWeatherSystem {
    
    /**
     * 季节枚举
     */
    enum class Season {
        SPRING,  // 春季
        SUMMER,  // 夏季
        AUTUMN,  // 秋季
        WINTER   // 冬季
    }
    
    /**
     * 根据游戏日期获取季节
     */
    fun getSeason(date: Date): Season {
        val calendar = Calendar.getInstance().apply { time = date }
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH 从0开始
        
        return when (month) {
            3, 4, 5 -> Season.SPRING
            6, 7, 8 -> Season.SUMMER
            9, 10, 11 -> Season.AUTUMN
            else -> Season.WINTER // 12, 1, 2
        }
    }
    
    /**
     * 根据季节和时间生成合理的天气
     * 
     * @param date 游戏日期
     * @return 天气类型
     */
    fun generateSeasonalWeather(date: Date): WeatherType {
        val season = getSeason(date)
        val calendar = Calendar.getInstance().apply { time = date }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        // 获取季节天气权重
        val weatherWeights = getSeasonalWeatherWeights(season, hour)
        
        // 根据权重随机选择天气
        return selectWeatherByWeight(weatherWeights)
    }
    
    /**
     * 获取季节天气权重
     * 
     * 权重越高，出现概率越大
     */
    private fun getSeasonalWeatherWeights(season: Season, hour: Int): Map<WeatherType, Int> {
        return when (season) {
            Season.SPRING -> mapOf(
                WeatherType.SUNNY to 40,      // 晴天 40%
                WeatherType.CLOUDY to 30,     // 多云 30%
                WeatherType.RAINY to 20,      // 雨天 20%（春雨）
                WeatherType.FOGGY to 10,      // 雾天 10%（春雾）
                WeatherType.STORMY to 0,      // 暴风雨 0%
                WeatherType.SNOWY to 0        // 雪天 0%（春季不下雪）
            )
            
            Season.SUMMER -> mapOf(
                WeatherType.SUNNY to 60,      // 晴天 60%（夏季多晴）
                WeatherType.CLOUDY to 20,     // 多云 20%
                WeatherType.RAINY to 10,      // 雨天 10%
                WeatherType.STORMY to 10,     // 暴风雨 10%（夏季雷阵雨）
                WeatherType.FOGGY to 0,       // 雾天 0%
                WeatherType.SNOWY to 0        // 雪天 0%
            )
            
            Season.AUTUMN -> mapOf(
                WeatherType.SUNNY to 35,      // 晴天 35%
                WeatherType.CLOUDY to 35,     // 多云 35%（秋高气爽）
                WeatherType.RAINY to 20,      // 雨天 20%（秋雨绵绵）
                WeatherType.FOGGY to 10,      // 雾天 10%（秋雾）
                WeatherType.STORMY to 0,      // 暴风雨 0%
                WeatherType.SNOWY to 0        // 雪天 0%
            )
            
            Season.WINTER -> mapOf(
                WeatherType.SUNNY to 30,      // 晴天 30%
                WeatherType.CLOUDY to 30,     // 多云 30%
                WeatherType.SNOWY to 25,      // 雪天 25%（冬季下雪）
                WeatherType.FOGGY to 10,      // 雾天 10%
                WeatherType.RAINY to 5,       // 雨天 5%（冬雨）
                WeatherType.STORMY to 0       // 暴风雨 0%
            )
        }
    }
    
    /**
     * 根据权重选择天气
     */
    private fun selectWeatherByWeight(weights: Map<WeatherType, Int>): WeatherType {
        val totalWeight = weights.values.sum()
        if (totalWeight == 0) return WeatherType.SUNNY
        
        var random = Random.nextInt(totalWeight)
        
        for ((weather, weight) in weights) {
            random -= weight
            if (random < 0) {
                return weather
            }
        }
        
        return WeatherType.SUNNY // 默认返回晴天
    }
    
    /**
     * 获取季节描述（中文）
     */
    fun getSeasonDescription(season: Season): String {
        return when (season) {
            Season.SPRING -> "春季 🌸"
            Season.SUMMER -> "夏季 ☀️"
            Season.AUTUMN -> "秋季 🍂"
            Season.WINTER -> "冬季 ❄️"
        }
    }
    
    /**
     * 获取天气描述（中文）
     */
    fun getWeatherDescription(weather: WeatherType): String {
        return when (weather) {
            WeatherType.SUNNY -> "晴天 ☀️"
            WeatherType.CLOUDY -> "多云 ☁️"
            WeatherType.RAINY -> "雨天 🌧️"
            WeatherType.SNOWY -> "雪天 ❄️"
            WeatherType.STORMY -> "暴风雨 ⛈️"
            WeatherType.FOGGY -> "雾天 🌫️"
        }
    }
    
    /**
     * 获取季节温度范围（摄氏度）
     */
    fun getSeasonTemperatureRange(season: Season): Pair<Int, Int> {
        return when (season) {
            Season.SPRING -> 10 to 20  // 春季：10-20°C
            Season.SUMMER -> 25 to 35  // 夏季：25-35°C
            Season.AUTUMN -> 10 to 20  // 秋季：10-20°C
            Season.WINTER -> -5 to 8   // 冬季：-5-8°C
        }
    }
    
    /**
     * 生成季节温度
     */
    fun generateSeasonalTemperature(season: Season, weather: WeatherType): Int {
        val (minTemp, maxTemp) = getSeasonTemperatureRange(season)
        var temperature = Random.nextInt(minTemp, maxTemp + 1)
        
        // 根据天气调整温度
        temperature += when (weather) {
            WeatherType.SUNNY -> 2      // 晴天更热
            WeatherType.RAINY -> -3     // 雨天更冷
            WeatherType.SNOWY -> -5     // 雪天最冷
            WeatherType.STORMY -> -2    // 暴风雨稍冷
            WeatherType.FOGGY -> -1     // 雾天稍冷
            WeatherType.CLOUDY -> 0     // 多云不变
        }
        
        return temperature
    }
}

