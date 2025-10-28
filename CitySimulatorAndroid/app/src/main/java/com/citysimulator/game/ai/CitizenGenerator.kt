package com.citysimulator.game.ai

import com.citysimulator.game.data.model.*
import java.util.*
import kotlin.random.Random

/**
 * 市民生成器
 * 
 * 根据住宅建筑自动生成市民
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
object CitizenGenerator {
    
    private val firstNames = listOf(
        "李", "王", "张", "刘", "陈", "杨", "黄", "赵", "周", "吴",
        "徐", "孙", "马", "朱", "胡", "郭", "何", "林", "罗", "高"
    )
    
    private val maleMiddleNames = listOf(
        "明", "强", "伟", "军", "磊", "勇", "杰", "涛", "超", "鹏",
        "浩", "宇", "俊", "峰", "辉", "华", "龙", "鑫", "博", "凯"
    )
    
    private val femaleMiddleNames = listOf(
        "芳", "娜", "敏", "静", "丽", "秀", "婷", "玲", "红", "霞",
        "梅", "莉", "兰", "萍", "雪", "云", "燕", "英", "月", "琳"
    )
    
    private val occupations = listOf(
        "工人", "教师", "医生", "司机", "服务员", "销售员", "程序员",
        "设计师", "厨师", "保安", "清洁工", "会计", "律师", "工程师",
        "记者", "艺术家", "运动员", "学生", "退休人员", "自由职业者"
    )
    
    /**
     * 根据建筑生成市民
     * 
     * @param building 住宅建筑
     * @return 生成的市民列表
     */
    fun generateCitizensForBuilding(building: Building): List<Citizen> {
        val count = getCitizenCountForBuilding(building)
        val citizens = mutableListOf<Citizen>()
        
        for (i in 0 until count) {
            val citizen = generateSingleCitizen(
                homeX = building.position.x,
                homeY = building.position.y
            )
            citizens.add(citizen)
        }
        
        return citizens
    }
    
    /**
     * 获取建筑应该容纳的市民数量
     */
    private fun getCitizenCountForBuilding(building: Building): Int {
        return when (building.type) {
            BuildingType.HOUSE -> Random.nextInt(2, 4) // 小木屋: 2-3人
            BuildingType.APARTMENT -> Random.nextInt(4, 7) // 公寓楼: 4-6人
            BuildingType.VILLA -> Random.nextInt(5, 9) // 现代化住宅: 5-8人
            BuildingType.SKYSCRAPER -> Random.nextInt(20, 51) // 摩天大楼: 20-50人
            else -> 0 // 其他建筑不生成市民
        }
    }
    
    /**
     * 生成单个市民
     */
    private fun generateSingleCitizen(
        homeX: Int,
        homeY: Int
    ): Citizen {
        val gender = if (Random.nextBoolean()) Gender.MALE else Gender.FEMALE
        val age = Random.nextInt(18, 66) // 18-65岁
        val name = generateName(gender)
        
        // 根据年龄决定是否有工作
        val hasJob = age in 22..60
        val occupation = if (hasJob) occupations.random() else null
        val salary = if (hasJob) Random.nextInt(3000, 15000) else 0
        
        // 根据年龄决定教育水平
        val education = when {
            age < 25 -> if (Random.nextBoolean()) EducationLevel.HIGH_SCHOOL else EducationLevel.COLLEGE
            age < 35 -> if (Random.nextDouble() < 0.3) EducationLevel.GRADUATE else EducationLevel.COLLEGE
            else -> if (Random.nextDouble() < 0.7) EducationLevel.HIGH_SCHOOL else EducationLevel.COLLEGE
        }
        
        // 根据年龄决定婚姻状态
        val maritalStatus = when {
            age < 25 -> MaritalStatus.SINGLE
            age < 35 -> if (Random.nextDouble() < 0.4) MaritalStatus.MARRIED else MaritalStatus.SINGLE
            age < 50 -> if (Random.nextDouble() < 0.7) MaritalStatus.MARRIED else MaritalStatus.SINGLE
            else -> when (Random.nextInt(4)) {
                0 -> MaritalStatus.SINGLE
                1 -> MaritalStatus.DIVORCED
                2 -> MaritalStatus.WIDOWED
                else -> MaritalStatus.MARRIED
            }
        }
        
        // 随机工作地点（如果有工作）
        val workX = if (hasJob) Random.nextInt(1, 20) else null
        val workY = if (hasJob) Random.nextInt(1, 20) else null
        
        return Citizen(
            id = UUID.randomUUID().toString(),
            name = name,
            age = age,
            gender = gender,
            birthDate = Date(System.currentTimeMillis() - age * 365L * 24 * 60 * 60 * 1000),
            homeX = homeX,
            homeY = homeY,
            maritalStatus = maritalStatus,
            occupation = occupation,
            workplaceX = workX,
            workplaceY = workY,
            salary = salary,
            workingHours = WorkingHours.values().random(),
            happiness = Random.nextFloat() * 0.3f + 0.5f, // 0.5-0.8
            health = Random.nextFloat() * 0.2f + 0.8f, // 0.8-1.0
            education = education,
            wealth = Random.nextInt(5000, 50000),
            needsFood = Random.nextFloat() * 0.3f + 0.3f, // 0.3-0.6
            needsWater = Random.nextFloat() * 0.3f + 0.3f,
            needsRest = Random.nextFloat() * 0.4f + 0.2f,
            needsEntertainment = Random.nextFloat() * 0.4f + 0.3f,
            needsMedical = Random.nextFloat() * 0.2f, // 0-0.2
            currentActivity = CitizenActivity.AT_HOME,
            currentX = homeX,
            currentY = homeY,
            personality = CitizenPersonality.values().random(),
            lastActivityChangeTime = Date(),
            totalCommuteTime = 0,
            averageCommuteTime = if (hasJob) Random.nextInt(15, 45) else 0,
            complaints = 0,
            movedTimes = 0,
            jobChanges = 0
        )
    }
    
    /**
     * 生成姓名
     */
    private fun generateName(gender: Gender): String {
        val firstName = firstNames.random()
        val middleName = if (gender == Gender.MALE) {
            maleMiddleNames.random()
        } else {
            femaleMiddleNames.random()
        }
        return "$firstName$middleName"
    }
    
    /**
     * 批量生成初始市民（用于已有建筑）
     */
    fun generateInitialCitizens(buildings: List<Building>): List<Citizen> {
        val allCitizens = mutableListOf<Citizen>()
        
        buildings.filter { it.type in listOf(
            BuildingType.HOUSE,
            BuildingType.APARTMENT,
            BuildingType.VILLA,
            BuildingType.SKYSCRAPER
        )}.forEach { building ->
            val citizens = generateCitizensForBuilding(building)
            allCitizens.addAll(citizens)
        }
        
        return allCitizens
    }
}

