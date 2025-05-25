package com.example.agrigenius360

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "plant_growth_records")
data class PlantGrowthEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: Int,
    val heightCm: Double,
    val measurementDate: Long = System.currentTimeMillis(),
    val initialHeight: Float = 0.0f,
    val finalHeight: Float = 0.0f,
    val days: Float = 0.0f,
    val growthRate: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val cropType: String = "Unknown",
    val plantName: String = "My Plant"
)
