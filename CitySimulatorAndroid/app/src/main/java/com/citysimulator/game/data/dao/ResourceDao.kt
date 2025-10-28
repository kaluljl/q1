package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.Resource
import com.citysimulator.game.data.model.ResourceType
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources")
    fun getAllResources(): Flow<List<Resource>>

    @Query("SELECT * FROM resources WHERE id = :id")
    fun getResourceById(id: String): Flow<Resource?>

    @Query("SELECT * FROM resources WHERE type = :type LIMIT 1")
    fun getResourceByType(type: ResourceType): Flow<Resource?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: Resource)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<Resource>)

    @Update
    suspend fun updateResource(resource: Resource)

    @Delete
    suspend fun deleteResource(resource: Resource)

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun deleteResourceById(id: String)

    @Query("UPDATE resources SET amount = :amount WHERE id = :id")
    suspend fun updateResourceAmount(id: String, amount: Double)

    @Query("UPDATE resources SET amount = amount + :amount WHERE id = :id")
    suspend fun addResourceAmount(id: String, amount: Double)

    @Query("UPDATE resources SET amount = amount - :amount WHERE id = :id")
    suspend fun subtractResourceAmount(id: String, amount: Double)

    @Query("UPDATE resources SET productionRate = :productionRate WHERE id = :id")
    suspend fun updateProductionRate(id: String, productionRate: Double)

    @Query("UPDATE resources SET consumptionRate = :consumptionRate WHERE id = :id")
    suspend fun updateConsumptionRate(id: String, consumptionRate: Double)

    @Query("UPDATE resources SET maxCapacity = :maxCapacity WHERE id = :id")
    suspend fun updateMaxCapacity(id: String, maxCapacity: Double)
}
