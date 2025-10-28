package com.citysimulator.game.data.dao

import androidx.room.*
import com.citysimulator.game.data.model.AINPC
import kotlinx.coroutines.flow.Flow

/**
 * AI NPC数据访问对象
 */
@Dao
interface AINPCDao {
    
    @Query("SELECT * FROM ai_npcs")
    fun getAllNPCs(): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE id = :id")
    suspend fun getNPCById(id: String): AINPC?
    
    @Query("SELECT * FROM ai_npcs WHERE profession = :profession")
    fun getNPCsByProfession(profession: String): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE currentActivity = :activity")
    fun getNPCsByActivity(activity: String): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE personality = :personality")
    fun getNPCsByPersonality(personality: String): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE currentLocation = :location")
    fun getNPCsAtLocation(location: String): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE happiness > :threshold")
    fun getHappyNPCs(threshold: Float): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE energy < :threshold")
    fun getTiredNPCs(threshold: Float): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE hunger > :threshold")
    fun getHungryNPCs(threshold: Float): Flow<List<AINPC>>
    
    @Query("SELECT * FROM ai_npcs WHERE socialNeed > :threshold")
    fun getLonelyNPCs(threshold: Float): Flow<List<AINPC>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNPC(npc: AINPC)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNPCs(npcs: List<AINPC>)
    
    @Update
    suspend fun updateNPC(npc: AINPC)
    
    @Delete
    suspend fun deleteNPC(npc: AINPC)
    
    @Query("DELETE FROM ai_npcs WHERE id = :id")
    suspend fun deleteNPCById(id: String)
    
    @Query("UPDATE ai_npcs SET currentLocation = :location WHERE id = :id")
    suspend fun updateNPCLocation(id: String, location: String)
    
    @Query("UPDATE ai_npcs SET currentActivity = :activity WHERE id = :id")
    suspend fun updateNPCActivity(id: String, activity: String)
    
    @Query("UPDATE ai_npcs SET happiness = :happiness WHERE id = :id")
    suspend fun updateNPCHappiness(id: String, happiness: Float)
    
    @Query("UPDATE ai_npcs SET energy = :energy WHERE id = :id")
    suspend fun updateNPCEnergy(id: String, energy: Float)
    
    @Query("UPDATE ai_npcs SET hunger = :hunger WHERE id = :id")
    suspend fun updateNPCHunger(id: String, hunger: Float)
    
    @Query("UPDATE ai_npcs SET socialNeed = :socialNeed WHERE id = :id")
    suspend fun updateNPCSocialNeed(id: String, socialNeed: Float)
    
    @Query("UPDATE ai_npcs SET aiState = :aiState WHERE id = :id")
    suspend fun updateNPCAIState(id: String, aiState: String)
    
    @Query("UPDATE ai_npcs SET lastUpdateTime = :time WHERE id = :id")
    suspend fun updateNPCLastUpdateTime(id: String, time: Long)
    
    @Query("SELECT COUNT(*) FROM ai_npcs")
    suspend fun getNPCCount(): Int
    
    @Query("SELECT AVG(happiness) FROM ai_npcs")
    suspend fun getAverageHappiness(): Float?
    
    @Query("SELECT AVG(energy) FROM ai_npcs")
    suspend fun getAverageEnergy(): Float?
    
    @Query("SELECT AVG(hunger) FROM ai_npcs")
    suspend fun getAverageHunger(): Float?
    
    @Query("SELECT AVG(socialNeed) FROM ai_npcs")
    suspend fun getAverageSocialNeed(): Float?
}
