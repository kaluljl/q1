package com.citysimulator.game.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.citysimulator.game.data.converter.BuildingPositionConverter
import com.citysimulator.game.data.converter.DateConverter
import com.citysimulator.game.data.converter.AINPCConverter
import com.citysimulator.game.data.converter.TechnologyConverter
import com.citysimulator.game.data.converter.CityPolicyConverter
import com.citysimulator.game.data.model.Building
import com.citysimulator.game.data.model.City
import com.citysimulator.game.data.model.Population
import com.citysimulator.game.data.model.Resource
import com.citysimulator.game.data.model.AINPC
import com.citysimulator.game.data.model.Technology
import com.citysimulator.game.data.model.CityPolicy
import com.citysimulator.game.data.dao.BuildingDao
import com.citysimulator.game.data.dao.CityDao
import com.citysimulator.game.data.dao.PopulationDao
import com.citysimulator.game.data.dao.ResourceDao
import com.citysimulator.game.data.dao.AINPCDao
import com.citysimulator.game.data.dao.TechnologyDao
import com.citysimulator.game.data.dao.CityPolicyDao

/**
 * 城市模拟经营游戏数据库
 * 
 * Room数据库配置，包含所有数据表和DAO接口。
 * 使用版本控制管理数据库结构变更。
 * 
 * @property VERSION 数据库版本号
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@Database(
    entities = [
        City::class,
        Building::class,
        Resource::class,
        Population::class,
        AINPC::class,
        Technology::class,
        CityPolicy::class
    ],
    version = 7,  // 添加索引优化
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    AINPCConverter::class,
    BuildingPositionConverter::class,
    TechnologyConverter::class,
    CityPolicyConverter::class
)
abstract class CitySimulatorDatabase : RoomDatabase() {
    
    companion object {
        const val VERSION = 7  // 添加索引优化
        private const val DATABASE_NAME = "city_simulator_database"
        
        @Volatile
        private var INSTANCE: CitySimulatorDatabase? = null
        
        /**
         * 获取数据库实例（单例模式）
         * 
         * @param context 应用上下文
         * @return 数据库实例
         */
        fun getDatabase(context: Context): CitySimulatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitySimulatorDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration() // 开发阶段使用，生产环境需要Migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * 城市数据访问对象
     */
    abstract fun cityDao(): CityDao
    
    /**
     * 建筑数据访问对象
     */
    abstract fun buildingDao(): BuildingDao
    
    /**
     * 资源数据访问对象
     */
    abstract fun resourceDao(): ResourceDao
    
    /**
     * 人口数据访问对象
     */
    abstract fun populationDao(): PopulationDao
    
    /**
     * AI NPC数据访问对象
     */
    abstract fun ainpcDao(): AINPCDao
    
    /**
     * 科技树数据访问对象
     */
    abstract fun technologyDao(): TechnologyDao
    
    /**
     * 城市政策数据访问对象
     */
    abstract fun cityPolicyDao(): CityPolicyDao
}
