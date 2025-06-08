package com.example.agrigenius360

import com.example.agrigenius360.PlantDAO
import com.example.agrigenius360.PlantEntity
import com.example.agrigenius360.PlantGrowthDAO
import com.example.agrigenius360.PlantGrowthEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor(
    private val plantDAO: PlantDAO,
    private val plantGrowthDAO: PlantGrowthDAO
) {

    suspend fun insertPlant(plant: PlantEntity) {
        plantDAO.insertPlant(plant)
    }

    suspend fun insertPlantGrowth(record: PlantGrowthEntity) {
        plantGrowthDAO.insertPlantGrowth(record)
    }

    fun getPlantsByNames(plantNames: List<String>): Flow<List<PlantEntity>> {
        return plantDAO.getPlantsByNames(plantNames)
    }

    suspend fun getPlantIdByName(plantName: String): PlantEntity? {
        return plantDAO.getPlantIdByName(plantName)
    }

    fun getGrowthRecordsForPlant(plantId: Int): Flow<List<PlantGrowthEntity>> {
        return plantGrowthDAO.getGrowthRecordsForPlant(plantId)
    }

    fun getAllPlantNames(): Flow<List<String>> {
        return plantDAO.getAllPlantNames()
    }

}