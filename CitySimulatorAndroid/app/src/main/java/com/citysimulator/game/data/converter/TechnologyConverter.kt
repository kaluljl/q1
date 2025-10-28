package com.citysimulator.game.data.converter

import androidx.room.TypeConverter
import com.citysimulator.game.data.model.BuildingType
import com.citysimulator.game.data.model.TechEffect
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 科技树类型转换器
 * 
 * 处理复杂类型的数据库存储和读取
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
class TechnologyConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromBuildingTypeList(value: List<BuildingType>): String {
        return gson.toJson(value.map { it.name })
    }
    
    @TypeConverter
    fun toBuildingTypeList(value: String): List<BuildingType> {
        val listType = object : TypeToken<List<String>>() {}.type
        val stringList: List<String> = gson.fromJson(value, listType)
        return stringList.map { BuildingType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromTechEffect(value: TechEffect): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toTechEffect(value: String): TechEffect {
        return gson.fromJson(value, TechEffect::class.java)
    }
}

