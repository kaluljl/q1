package com.citysimulator.game.data.converter

import androidx.room.TypeConverter
import com.citysimulator.game.data.model.BuildingPosition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 建筑位置类型转换器
 * 
 * 用于Room数据库中的BuildingPosition类型转换。
 * 将BuildingPosition对象转换为JSON字符串进行存储，读取时再转换回BuildingPosition对象。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
class BuildingPositionConverter {
    
    private val gson = Gson()
    
    /**
     * 将BuildingPosition对象转换为JSON字符串
     * 
     * @param position 要转换的BuildingPosition对象
     * @return JSON字符串
     */
    @TypeConverter
    fun fromBuildingPosition(position: BuildingPosition?): String? {
        return position?.let { gson.toJson(it) }
    }
    
    /**
     * 将JSON字符串转换为BuildingPosition对象
     * 
     * @param json JSON字符串
     * @return BuildingPosition对象
     */
    @TypeConverter
    fun toBuildingPosition(json: String?): BuildingPosition? {
        return json?.let {
            gson.fromJson(it, BuildingPosition::class.java)
        }
    }
}



