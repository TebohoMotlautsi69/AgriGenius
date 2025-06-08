package com.example.agrigenius360.di

import android.content.Context
import com.example.agrigenius360.AppDatabase
import com.example.agrigenius360.PlantDAO
import com.example.agrigenius360.PlantGrowthDAO
import com.example.agrigenius360.PlantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun providePlantDAO(appDatabase: AppDatabase): PlantDAO {
        return appDatabase.plantDAO()
    }

    @Singleton
    @Provides
    fun providePlantGrowthDAO(appDatabase: AppDatabase): PlantGrowthDAO {
        return appDatabase.plantGrowthDAO()
    }

    @Singleton
    @Provides
    fun providePlantRepository(
        plantDAO: PlantDAO,
        plantGrowthDAO: PlantGrowthDAO
    ): PlantRepository {
        return PlantRepository(plantDAO, plantGrowthDAO)
    }
}