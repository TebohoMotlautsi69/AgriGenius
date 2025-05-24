package com.example.agrigenius360

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "plant_growth_records")
data class PlantGrowthEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val initialHeight: Float,
    val finalHeight: Float,
    val days: Float,
    val growthRate: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val cropType: String = "Unknown",
    val plantName: String = "My Plant"
)
