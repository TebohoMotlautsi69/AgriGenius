package com.example.agrigenius360

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantGrowthDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRecord(record: PlantGrowthEntity)

    @Query("SELECT * FROM plant_growth_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<PlantGrowthEntity>>

    @Query("SELECT * FROM plant_growth_records WHERE cropType = :cropType ORDER BY timestamp DESC")
    fun getRecordsByCropType(cropType: String): Flow<List<PlantGrowthEntity>>

    @Query("SELECT * FROM plant_growth_records WHERE plantName = :plantName AND cropType = :cropType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRecordForPlant(plantName: String, cropType: String): PlantGrowthEntity?

    @Query("SELECT AVG(growthRate) FROM plant_growth_records WHERE cropType = :cropType")
    suspend fun getAverageGrowthRateForCrop(cropType: String): Float?
}