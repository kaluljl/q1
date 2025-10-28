package com.citysimulator.game.data.converter

import androidx.room.TypeConverter
import com.citysimulator.game.data.model.PolicyEffect
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 城市政策类型转换器
 * 
 * 处理复杂类型的数据库存储和读取
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
class CityPolicyConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromPolicyEffect(value: PolicyEffect): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toPolicyEffect(value: String): PolicyEffect {
        return gson.fromJson(value, PolicyEffect::class.java)
    }
}
