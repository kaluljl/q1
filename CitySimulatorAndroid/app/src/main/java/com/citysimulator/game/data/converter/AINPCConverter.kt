package com.citysimulator.game.data.converter

import androidx.room.TypeConverter
import com.citysimulator.game.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * AI NPC数据类型转换器
 */
class AINPCConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromPairIntInt(value: Pair<Int, Int>?): String? {
        return value?.let { "${it.first},${it.second}" }
    }
    
    @TypeConverter
    fun toPairIntInt(value: String?): Pair<Int, Int>? {
        return value?.split(",")?.let {
            if (it.size == 2) Pair(it[0].toInt(), it[1].toInt()) else null
        }
    }
    
    @TypeConverter
    fun fromScheduleItemList(value: List<ScheduleItem>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toScheduleItemList(value: String?): List<ScheduleItem>? {
        val type = object : TypeToken<List<ScheduleItem>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromStringFloatMap(value: Map<String, Float>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringFloatMap(value: String?): Map<String, Float>? {
        val type = object : TypeToken<Map<String, Float>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromNPCPreferences(value: NPCPreferences?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toNPCPreferences(value: String?): NPCPreferences? {
        return gson.fromJson(value, NPCPreferences::class.java)
    }
    
    @TypeConverter
    fun fromPersonality(value: Personality?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toPersonality(value: String?): Personality? {
        return value?.let { Personality.valueOf(it) }
    }
    
    @TypeConverter
    fun fromNPCActivity(value: NPCActivity?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toNPCActivity(value: String?): NPCActivity? {
        return value?.let { NPCActivity.valueOf(it) }
    }
    
    @TypeConverter
    fun fromAIState(value: AIState?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toAIState(value: String?): AIState? {
        return value?.let { AIState.valueOf(it) }
    }
}

