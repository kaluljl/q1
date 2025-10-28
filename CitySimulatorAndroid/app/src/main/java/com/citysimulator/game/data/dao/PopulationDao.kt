package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PopulationDao {
    @Query("SELECT * FROM population")
    fun getAllPopulation(): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE id = :id")
    fun getPopulationById(id: String): Flow<Population?>

    @Query("SELECT * FROM population WHERE profession = :profession")
    fun getPopulationByProfession(profession: Profession): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE educationLevel = :educationLevel")
    fun getPopulationByEducation(educationLevel: EducationLevel): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE healthStatus = :healthStatus")
    fun getPopulationByHealth(healthStatus: HealthStatus): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE workplaceId IS NOT NULL")
    fun getEmployedPopulation(): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE workplaceId IS NULL")
    fun getUnemployedPopulation(): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE residenceId IS NOT NULL")
    fun getPopulationWithResidence(): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE residenceId IS NULL")
    fun getPopulationWithoutResidence(): Flow<List<Population>>

    @Query("SELECT * FROM population WHERE healthStatus != 'HEALTHY'")
    fun getPopulationNeedingMedicalCare(): Flow<List<Population>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopulation(population: Population)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopulationList(population: List<Population>)

    @Update
    suspend fun updatePopulation(population: Population)

    @Delete
    suspend fun deletePopulation(population: Population)

    @Query("DELETE FROM population WHERE id = :id")
    suspend fun deletePopulationById(id: String)

    @Query("UPDATE population SET happiness = :happiness WHERE id = :id")
    suspend fun updateHappiness(id: String, happiness: Float)

    @Query("UPDATE population SET income = :income WHERE id = :id")
    suspend fun updateIncome(id: String, income: Int)

    @Query("UPDATE population SET profession = :profession WHERE id = :id")
    suspend fun updateProfession(id: String, profession: Profession)

    @Query("UPDATE population SET educationLevel = :educationLevel WHERE id = :id")
    suspend fun updateEducationLevel(id: String, educationLevel: EducationLevel)

    @Query("UPDATE population SET healthStatus = :healthStatus WHERE id = :id")
    suspend fun updateHealthStatus(id: String, healthStatus: HealthStatus)

    @Query("UPDATE population SET residenceId = :residenceId WHERE id = :id")
    suspend fun updateResidence(id: String, residenceId: String?)

    @Query("UPDATE population SET workplaceId = :workplaceId WHERE id = :id")
    suspend fun updateWorkplace(id: String, workplaceId: String?)

    data class PopulationStatistics(
        val total: Int,
        val employed: Int,
        val unemployed: Int
    )

    @Query(
        "SELECT " +
        "(SELECT COUNT(*) FROM population) AS total, " +
        "(SELECT COUNT(*) FROM population WHERE workplaceId IS NOT NULL) AS employed, " +
        "(SELECT COUNT(*) FROM population WHERE workplaceId IS NULL) AS unemployed"
    )
    suspend fun getPopulationStatistics(): PopulationStatistics
}


