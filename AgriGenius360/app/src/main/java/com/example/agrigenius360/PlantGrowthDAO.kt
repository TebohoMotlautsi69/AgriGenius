package com.example.agrigenius360

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantGrowthDAO {

    @Insert
    suspend fun insertMeasurement(measurement: PlantGrowthEntity)

    @Query("SELECT * FROM plant_growth_records WHERE plantId = :plantId ORDER BY measurementDate ASC")
    fun getMeasurementsForPlant(plantId: Int): Flow<List<PlantGrowthEntity>>

    @Query("SELECT * FROM plant_growth_records ORDER BY measurementDate ASC")
    fun getAllMeasurements(): Flow<List<PlantGrowthEntity>>

    @Query("DELETE FROM plant_growth_records WHERE id = :measurementId")
    suspend fun deleteMeasurement(measurementId: Int)

    @Query("SELECT * FROM plant_growth_records WHERE plantId = :plantId ORDER BY measurementDate DESC LIMIT 1")
    fun getLatestMeasurementForPlant(plantId: Int): Flow<PlantGrowthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantGrowth(record: PlantGrowthEntity)

    @Query("SELECT * FROM plant_growth_records WHERE plantId = :plantId ORDER BY measurementDate ASC")
    fun getGrowthRecordsForPlant(plantId: Int): Flow<List<PlantGrowthEntity>>

    @Query("SELECT * FROM plant_growth_records WHERE plantName = :plantName ORDER BY measurementDate ASC")
    fun getGrowthRecordsForPlantByName(plantName: String): Flow<List<PlantGrowthEntity>>

}