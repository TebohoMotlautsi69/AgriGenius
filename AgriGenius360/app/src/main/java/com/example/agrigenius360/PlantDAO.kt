package com.example.agrigenius360

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDAO {
    @Insert
    suspend fun insertPlant(plant: PlantEntity): Long

    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Int): PlantEntity?

    @Query("SELECT *FROM plants WHERE name =:plantName AND type = :cropType LIMIT 1")
    suspend fun getPlantByNameAndType(plantName: String, cropType: String): PlantEntity?


    @Query("SELECT * FROM plants WHERE name IN (:plantNames)")
    fun getPlantsByNames(plantNames: List<String>): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE name = :plantName LIMIT 1")
    suspend fun getPlantIdByName(plantName: String): PlantEntity?

    @Query("SELECT name FROM plants")
    fun getAllPlantNames(): Flow<List<String>>

}