package com.example.agrigenius360

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val optimalGrowthRateCmPerDay: Float = 0.0f,
    val plantDate: Long = System.currentTimeMillis(),
    val measurementFrequencyDays: Int? = null
)
