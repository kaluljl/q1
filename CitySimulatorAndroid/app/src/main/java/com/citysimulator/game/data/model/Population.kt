package com.citysimulator.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.citysimulator.game.data.converter.DateConverter
import java.util.Date

/**
 * 职业类型枚举
 * 
 * 定义城市居民可以从事的各种职业。
 */
enum class Profession {
    WORKER,         // 工人
    FARMER,         // 农民
    MERCHANT,       // 商人
    TEACHER,        // 教师
    DOCTOR,         // 医生
    POLICE,         // 警察
    SCIENTIST,      // 科学家
    ENGINEER,       // 工程师
    ARTIST,         // 艺术家
    MANAGER,        // 管理者
    STUDENT,        // 学生
    RETIREE         // 退休人员
}

/**
 * 教育水平枚举
 * 
 * 定义居民的教育水平等级。
 */
enum class EducationLevel {
    PRIMARY,        // 小学
    SECONDARY,      // 中学
    HIGH_SCHOOL,    // 高中
    COLLEGE,        // 大学
    GRADUATE,       // 研究生
    PHD;            // 博士
    
    fun getDisplayName(): String = when (this) {
        PRIMARY -> "小学"
        SECONDARY -> "中学"
        HIGH_SCHOOL -> "高中"
        COLLEGE -> "大学"
        GRADUATE -> "研究生"
        PHD -> "博士"
    }
}

/**
 * 健康状况枚举
 * 
 * 定义居民的健康状况等级。
 */
enum class HealthStatus {
    EXCELLENT,      // 优秀
    GOOD,           // 良好
    FAIR,           // 一般
    POOR,           // 较差
    CRITICAL        // 危险
}

/**
 * 人口数据模型
 * 
 * 代表城市中的居民，包含居民的详细信息和属性。
 * 使用Room数据库进行持久化存储。
 * 
 * @property id 居民唯一标识符
 * @property name 居民姓名
 * @property age 年龄
 * @property profession 职业
 * @property educationLevel 教育水平
 * @property healthStatus 健康状况
 * @property happiness 幸福指数
 * @property income 收入
 * @property residenceId 居住建筑ID
 * @property workplaceId 工作建筑ID
 * @property joinDate 加入城市日期
 * @property lastUpdateDate 最后更新日期
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Entity(tableName = "population")
@TypeConverters(DateConverter::class)
data class Population(
    @PrimaryKey
    val id: String,
    val name: String,
    val age: Int,
    val profession: Profession,
    val educationLevel: EducationLevel,
    val healthStatus: HealthStatus,
    val happiness: Float = 50f,
    val income: Int = 0,
    val residenceId: String? = null,
    val workplaceId: String? = null,
    val joinDate: Date = Date(),
    val lastUpdateDate: Date = Date()
) {
    /**
     * 获取职业显示名称
     * 
     * @return 职业的中文显示名称
     */
    fun getProfessionDisplayName(): String {
        return when (profession) {
            Profession.WORKER -> "工人"
            Profession.FARMER -> "农民"
            Profession.MERCHANT -> "商人"
            Profession.TEACHER -> "教师"
            Profession.DOCTOR -> "医生"
            Profession.POLICE -> "警察"
            Profession.SCIENTIST -> "科学家"
            Profession.ENGINEER -> "工程师"
            Profession.ARTIST -> "艺术家"
            Profession.MANAGER -> "管理者"
            Profession.STUDENT -> "学生"
            Profession.RETIREE -> "退休人员"
        }
    }
    
    /**
     * 获取教育水平显示名称
     * 
     * @return 教育水平的中文显示名称
     */
    fun getEducationDisplayName(): String {
        return when (educationLevel) {
            EducationLevel.PRIMARY -> "小学"
            EducationLevel.SECONDARY -> "中学"
            EducationLevel.HIGH_SCHOOL -> "高中"
            EducationLevel.COLLEGE -> "大学"
            EducationLevel.GRADUATE -> "研究生"
            EducationLevel.PHD -> "博士"
        }
    }
    
    /**
     * 获取健康状况显示名称
     * 
     * @return 健康状况的中文显示名称
     */
    fun getHealthDisplayName(): String {
        return when (healthStatus) {
            HealthStatus.EXCELLENT -> "优秀"
            HealthStatus.GOOD -> "良好"
            HealthStatus.FAIR -> "一般"
            HealthStatus.POOR -> "较差"
            HealthStatus.CRITICAL -> "危险"
        }
    }
    
    /**
     * 获取年龄组别
     * 
     * @return 年龄组别
     */
    fun getAgeGroup(): String {
        return when (age) {
            in 0..17 -> "青少年"
            in 18..35 -> "青年"
            in 36..50 -> "中年"
            in 51..65 -> "中老年"
            else -> "老年"
        }
    }
    
    /**
     * 检查是否已就业
     * 
     * @return 是否已就业
     */
    fun isEmployed(): Boolean {
        return workplaceId != null && profession != Profession.STUDENT && profession != Profession.RETIREE
    }
    
    /**
     * 检查是否有住所
     * 
     * @return 是否有住所
     */
    fun hasResidence(): Boolean {
        return residenceId != null
    }
    
    /**
     * 获取工作效率
     * 
     * @return 工作效率 (0.0 - 1.0)
     */
    fun getWorkEfficiency(): Float {
        var efficiency = 1.0f
        
        // 年龄影响
        when (age) {
            in 18..35 -> efficiency *= 1.2f
            in 36..50 -> efficiency *= 1.0f
            in 51..65 -> efficiency *= 0.8f
            else -> efficiency *= 0.5f
        }
        
        // 教育水平影响
        efficiency *= when (educationLevel) {
            EducationLevel.PRIMARY -> 0.6f
            EducationLevel.SECONDARY -> 0.7f
            EducationLevel.HIGH_SCHOOL -> 0.8f
            EducationLevel.COLLEGE -> 1.0f
            EducationLevel.GRADUATE -> 1.2f
            EducationLevel.PHD -> 1.5f
        }
        
        // 健康状况影响
        efficiency *= when (healthStatus) {
            HealthStatus.EXCELLENT -> 1.2f
            HealthStatus.GOOD -> 1.0f
            HealthStatus.FAIR -> 0.8f
            HealthStatus.POOR -> 0.6f
            HealthStatus.CRITICAL -> 0.3f
        }
        
        // 幸福指数影响
        efficiency *= (happiness / 100f)
        
        return efficiency.coerceIn(0f, 1.5f)
    }
    
    /**
     * 获取消费能力
     * 
     * @return 消费能力指数
     */
    fun getConsumptionPower(): Float {
        var power = income.toFloat()
        
        // 年龄影响消费
        when (age) {
            in 18..35 -> power *= 1.3f  // 年轻人消费高
            in 36..50 -> power *= 1.0f  // 中年人消费稳定
            in 51..65 -> power *= 0.8f  // 中老年人消费减少
            else -> power *= 0.5f        // 老年人消费最低
        }
        
        // 幸福指数影响消费
        power *= (happiness / 100f)
        
        return power
    }
    
    /**
     * 获取税收贡献
     * 
     * @return 税收贡献金额
     */
    fun getTaxContribution(): Int {
        if (!isEmployed()) return 0
        
        val baseTax = (income * 0.1f).toInt() // 基础税率10%
        val efficiencyMultiplier = getWorkEfficiency()
        
        return (baseTax * efficiencyMultiplier).toInt()
    }
    
    /**
     * 检查是否需要医疗服务
     * 
     * @return 是否需要医疗服务
     */
    fun needsMedicalCare(): Boolean {
        return healthStatus == HealthStatus.POOR || healthStatus == HealthStatus.CRITICAL
    }
    
    /**
     * 检查是否可以工作
     * 
     * @return 是否可以工作
     */
    fun canWork(): Boolean {
        return age >= 18 && age <= 65 && healthStatus != HealthStatus.CRITICAL
    }
    
    /**
     * 获取综合评分
     * 
     * @return 综合评分 (0.0 - 100.0)
     */
    fun getOverallScore(): Float {
        val educationScore = when (educationLevel) {
            EducationLevel.PRIMARY -> 20f
            EducationLevel.SECONDARY -> 40f
            EducationLevel.HIGH_SCHOOL -> 60f
            EducationLevel.COLLEGE -> 80f
            EducationLevel.GRADUATE -> 90f
            EducationLevel.PHD -> 100f
        }
        
        val healthScore = when (healthStatus) {
            HealthStatus.EXCELLENT -> 100f
            HealthStatus.GOOD -> 80f
            HealthStatus.FAIR -> 60f
            HealthStatus.POOR -> 40f
            HealthStatus.CRITICAL -> 20f
        }
        
        val workScore = if (isEmployed()) getWorkEfficiency() * 100f else 0f
        
        return (educationScore + healthScore + happiness + workScore) / 4f
    }
}



