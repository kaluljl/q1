package com.citysimulator.game.data.supabase

import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.BuildingPosition
import com.citysimulator.game.data.model.BuildingStatus
import com.citysimulator.game.data.model.BuildingType
import com.citysimulator.game.data.supabase.model.SupabaseBuilding
import com.citysimulator.game.data.supabase.model.SupabaseCity
import com.citysimulator.game.data.supabase.model.SupabaseResource
import com.citysimulator.game.data.supabase.model.SupabaseNPC
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Supabase数据仓库
 * 负责与Supabase数据库的所有交互
 */
@Singleton
class SupabaseRepository @Inject constructor(
    private val supabaseConfig: SupabaseConfig,
    private val realSupabaseClient: RealSupabaseClient
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    
    // ==================== 建筑相关操作 ====================
    
    /**
     * 获取所有建筑
     */
    suspend fun getAllBuildings(): List<Building> {
        return try {
            val buildingsData = realSupabaseClient.getAllBuildings()
            buildingsData.map { data ->
                Building(
                    id = data["id"] as? String ?: "",
                    type = BuildingType.valueOf(data["type"] as? String ?: "HOUSE"),
                    level = (data["level"] as? Number)?.toInt() ?: 1,
                    position = BuildingPosition(
                        x = (data["position_x"] as? Number)?.toInt() ?: 0,
                        y = (data["position_y"] as? Number)?.toInt() ?: 0
                    ),
                    buildTime = Date(), // 简化处理
                    status = BuildingStatus.valueOf(data["status"] as? String ?: "NORMAL"),
                    isUnderConstruction = data["is_under_construction"] as? Boolean ?: false,
                    isUpgrading = data["is_upgrading"] as? Boolean ?: false,
                    isMaintenanceRequired = data["is_maintenance_required"] as? Boolean ?: false,
                    lastMaintenanceDate = Date(), // 简化处理
                    efficiency = (data["efficiency"] as? Number)?.toFloat() ?: 1.0f,
                    capacity = (data["capacity"] as? Number)?.toInt() ?: 1,
                    income = (data["income"] as? Number)?.toInt() ?: 0,
                    maintenanceCost = (data["maintenance_cost"] as? Number)?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            println("获取建筑列表失败: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * 插入新建筑
     */
    suspend fun insertBuilding(building: Building): Boolean {
        return try {
            val buildingData = mapOf(
                "id" to building.id,
                "type" to building.type.name,
                "level" to building.level,
                "position_x" to building.position.x,
                "position_y" to building.position.y,
                "build_time" to dateFormat.format(building.buildTime),
                "status" to building.status.name,
                "is_under_construction" to building.isUnderConstruction,
                "is_upgrading" to building.isUpgrading,
                "is_maintenance_required" to building.isMaintenanceRequired,
                "last_maintenance_date" to dateFormat.format(building.lastMaintenanceDate),
                "efficiency" to building.efficiency,
                "capacity" to building.capacity,
                "income" to building.income,
                "maintenance_cost" to building.maintenanceCost
            )
            
            val success = realSupabaseClient.insertBuilding(buildingData)
            if (success) {
                println("建筑保存到Supabase成功: ${building.id}")
            }
            success
        } catch (e: Exception) {
            println("保存建筑失败: ${e.message}")
            false
        }
    }
    
    /**
     * 更新建筑
     */
    suspend fun updateBuilding(building: Building): Boolean {
        return try {
            val buildingData = mapOf(
                "type" to building.type.name,
                "level" to building.level,
                "position_x" to building.position.x,
                "position_y" to building.position.y,
                "status" to building.status.name,
                "is_under_construction" to building.isUnderConstruction,
                "is_upgrading" to building.isUpgrading,
                "is_maintenance_required" to building.isMaintenanceRequired,
                "efficiency" to building.efficiency,
                "capacity" to building.capacity,
                "income" to building.income,
                "maintenance_cost" to building.maintenanceCost
            )
            
            val success = realSupabaseClient.updateBuilding(building.id, buildingData)
            if (success) {
                println("建筑更新到Supabase成功: ${building.id}")
            }
            success
        } catch (e: Exception) {
            println("更新建筑失败: ${e.message}")
            false
        }
    }
    
    /**
     * 删除建筑
     */
    suspend fun deleteBuilding(buildingId: String): Boolean {
        return try {
            val success = realSupabaseClient.deleteBuilding(buildingId)
            if (success) {
                println("建筑从Supabase删除成功: $buildingId")
            }
            success
        } catch (e: Exception) {
            println("删除建筑失败: ${e.message}")
            false
        }
    }
    
    /**
     * 测试Supabase连接
     */
    suspend fun testConnection(): Boolean {
        return try {
            val success = realSupabaseClient.testConnection()
            if (success) {
                println("✅ Supabase连接测试成功")
            } else {
                println("❌ Supabase连接测试失败")
            }
            success
        } catch (e: Exception) {
            println("❌ 连接测试异常: ${e.message}")
            false
        }
    }
    
    // ==================== 城市相关操作 ====================
    
    /**
     * 获取城市数据
     */
    suspend fun getCity(cityId: String): SupabaseCity? {
        return try {
            supabaseConfig.supabaseClient
                .from("cities")
                .select()
                .eq("id", cityId)
                .decodeSingle<SupabaseCity>()
        } catch (e: Exception) {
            println("获取城市数据失败: ${e.message}")
            null
        }
    }
    
    /**
     * 更新城市数据
     */
    suspend fun updateCity(city: SupabaseCity): Boolean {
        return try {
            supabaseConfig.supabaseClient
                .from("cities")
                .update(city)
            
            println("城市数据更新成功: ${city.id}")
            true
        } catch (e: Exception) {
            println("更新城市数据失败: ${e.message}")
            false
        }
    }
    
    // ==================== 实时订阅 ====================
    
    /**
     * 订阅建筑变化
     */
    fun subscribeToBuildings(): Flow<List<Building>> = flow {
        try {
            supabaseConfig.supabaseClient
                .from("buildings")
                .subscribe { }
            val buildings = getAllBuildings()
            emit(buildings)
        } catch (e: Exception) {
            println("订阅建筑变化失败: ${e.message}")
            emit(emptyList())
        }
    }
    
    // ==================== 数据转换方法 ====================
    
    /**
     * 将Supabase建筑转换为本地建筑模型
     */
    private fun SupabaseBuilding.toBuilding(): Building {
        return Building(
            id = this.id,
            type = BuildingType.valueOf(this.type),
            level = this.level,
            position = BuildingPosition(this.position_x, this.position_y),
            buildTime = Date(), // 简化处理
            status = BuildingStatus.valueOf(this.status),
            isUnderConstruction = this.is_under_construction,
            isUpgrading = this.is_upgrading,
            isMaintenanceRequired = this.is_maintenance_required,
            lastMaintenanceDate = Date(), // 简化处理
            efficiency = this.efficiency,
            capacity = this.capacity,
            income = this.income,
            maintenanceCost = this.maintenance_cost
        )
    }
    
    /**
     * 将本地建筑模型转换为Supabase建筑
     */
    private fun Building.toSupabaseBuilding(): SupabaseBuilding {
        return SupabaseBuilding(
            id = this.id,
            type = this.type.name,
            level = this.level,
            position_x = this.position.x,
            position_y = this.position.y,
            build_time = dateFormat.format(this.buildTime),
            status = this.status.name,
            is_under_construction = this.isUnderConstruction,
            is_upgrading = this.isUpgrading,
            is_maintenance_required = this.isMaintenanceRequired,
            last_maintenance_date = dateFormat.format(this.lastMaintenanceDate),
            efficiency = this.efficiency,
            capacity = this.capacity,
            income = this.income,
            maintenance_cost = this.maintenanceCost
        )
    }
}
