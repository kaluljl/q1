package com.citysimulator.game.data.supabase.model

import java.util.Date

/**
 * Supabase建筑数据模型
 * 用于与Supabase数据库交互
 */
data class SupabaseBuilding(
    val id: String,
    val type: String,
    val level: Int,
    val position_x: Int,
    val position_y: Int,
    val build_time: String,
    val status: String,
    val is_under_construction: Boolean,
    val is_upgrading: Boolean,
    val is_maintenance_required: Boolean,
    val last_maintenance_date: String,
    val efficiency: Float,
    val capacity: Int,
    val income: Int,
    val maintenance_cost: Int,
    val user_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

/**
 * Supabase城市数据模型
 */
data class SupabaseCity(
    val id: String,
    val name: String,
    val population: Int,
    val happiness: Float,
    val energy: Float,
    val gold: Int,
    val user_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

/**
 * Supabase资源数据模型
 */
data class SupabaseResource(
    val id: String,
    val type: String,
    val amount: Double,
    val max_capacity: Double,
    val production_rate: Double,
    val consumption_rate: Double,
    val city_id: String,
    val user_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

/**
 * Supabase NPC数据模型
 */
data class SupabaseNPC(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val personality: String,
    val profession: String,
    val happiness: Float,
    val energy: Float,
    val health: Float,
    val wealth: Float,
    val home_location_x: Int?,
    val home_location_y: Int?,
    val work_location_x: Int?,
    val work_location_y: Int?,
    val current_activity: String,
    val last_activity_time: String,
    val thoughts: String,
    val city_id: String,
    val user_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
