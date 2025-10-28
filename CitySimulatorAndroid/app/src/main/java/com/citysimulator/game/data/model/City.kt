package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.citysimulator.game.data.converter.DateConverter
import java.util.Date

/**
 * 城市数据模型
 * 
 * 代表玩家经营的城市，包含城市的基本信息和统计数据。
 * 使用Room数据库进行持久化存储。
 * 
 * @property id 城市唯一标识符
 * @property name 城市名称
 * @property level 城市等级
 * @property experience 城市经验值
 * @property gridSize 城市网格大小
 * @property foundedDate 城市建立日期
 * @property lastPlayedDate 最后游戏日期
 * @property population 城市人口数量
 * @property happiness 城市幸福指数
 * @property environment 城市环境指数
 * @property economy 城市经济指数
 * @property education 城市教育指数
 * @property health 城市健康指数
 * @property safety 城市安全指数
 * @property transportation 城市交通指数
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "cities")
@TypeConverters(DateConverter::class)
data class City(
    @PrimaryKey
    val id: String,
    val name: String,
    val level: Int = 1,
    val experience: Int = 0,
    val gridSize: Int = 20,
    val foundedDate: Date = Date(),
    val lastPlayedDate: Date = Date(),
    val population: Int = 0,
    val happiness: Float = 50f,
    val environment: Float = 50f,
    val economy: Float = 50f,
    val education: Float = 50f,
    val health: Float = 50f,
    val safety: Float = 50f,
    val transportation: Float = 50f
) {
    /**
     * 获取城市等级显示名称
     * 
     * @return 城市等级的中文显示名称
     */
    fun getLevelDisplayName(): String {
        return when (level) {
            1 -> "新手城市"
            2 -> "小型城市"
            3 -> "中型城市"
            4 -> "大型城市"
            5 -> "特大城市"
            6 -> "国际都市"
            7 -> "世界名城"
            8 -> "传奇城市"
            9 -> "神话城市"
            10 -> "完美城市"
            else -> "未知等级"
        }
    }
    
    /**
     * 获取下一等级所需经验值
     * 
     * @return 升级所需经验值
     */
    fun getRequiredExperienceForNextLevel(): Int {
        return level * 1000
    }
    
    /**
     * 获取当前等级进度百分比
     * 
     * @return 进度百分比 (0.0 - 1.0)
     */
    fun getLevelProgress(): Float {
        val currentLevelExp = (level - 1) * 1000
        val nextLevelExp = level * 1000
        val progress = (experience - currentLevelExp).toFloat() / (nextLevelExp - currentLevelExp)
        return progress.coerceIn(0f, 1f)
    }
    
    /**
     * 检查是否可以升级
     * 
     * @return 是否可以升级
     */
    fun canLevelUp(): Boolean {
        return experience >= getRequiredExperienceForNextLevel()
    }
    
    /**
     * 获取城市综合评分
     * 
     * @return 综合评分 (0.0 - 100.0)
     */
    fun getOverallScore(): Float {
        return (happiness + environment + economy + education + health + safety + transportation) / 7f
    }
    
    /**
     * 获取城市状态描述
     * 
     * @return 城市状态描述
     */
    fun getStatusDescription(): String {
        val score = getOverallScore()
        return when {
            score >= 90f -> "繁荣昌盛"
            score >= 80f -> "欣欣向荣"
            score >= 70f -> "稳步发展"
            score >= 60f -> "平稳运行"
            score >= 50f -> "需要改善"
            score >= 40f -> "面临挑战"
            score >= 30f -> "困难重重"
            else -> "危机四伏"
        }
    }
}
