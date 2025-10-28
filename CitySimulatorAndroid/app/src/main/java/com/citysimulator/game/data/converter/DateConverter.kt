package com.citysimulator.game.data.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * 日期类型转换器
 * 
 * 用于Room数据库中的Date类型转换。
 * 将Date对象转换为Long时间戳进行存储，读取时再转换回Date对象。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
class DateConverter {
    
    /**
     * 将Date对象转换为Long时间戳
     * 
     * @param date 要转换的Date对象
     * @return 时间戳（毫秒）
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * 将Long时间戳转换为Date对象
     * 
     * @param timestamp 时间戳（毫秒）
     * @return Date对象
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}



