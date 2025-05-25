package com.example.agrigenius360

import android.app.Application

class AgriGeniusApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val usersDAO: UsersDAO by lazy { database.usersDAO() }
    val plantGrowthDAO: PlantGrowthDAO by lazy { database.plantGrowthDAO() }
    val plantDAO: PlantDAO by lazy { database.plantDAO() }
}