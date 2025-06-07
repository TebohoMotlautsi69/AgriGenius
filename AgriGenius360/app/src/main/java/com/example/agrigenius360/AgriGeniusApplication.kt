package com.example.agrigenius360

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AgriGeniusApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val usersDAO: UsersDAO by lazy { database.usersDAO() }
    val plantGrowthDAO: PlantGrowthDAO by lazy { database.plantGrowthDAO() }
    val plantDAO: PlantDAO by lazy { database.plantDAO() }

    override fun onCreate() {
        super.onCreate()
        createNotification()
        newMeasurementReminder()
    }

    private fun createNotification() {
        val growthChannel = NotificationChannel("growth_alerts_channel","Plant Growth Alerts",
            NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for plant growth performance compared to optimal."
        }

        val reminderChannel = NotificationChannel("measurement_reminders_channel","Measurement Reminders",
            NotificationManager.IMPORTANCE_DEFAULT).apply{
                description = "Reminders to take new plant measurements."
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(growthChannel)
        notificationManager.createNotificationChannel(reminderChannel)
    }

    private fun newMeasurementReminder(){
        val reminderMeasureWorkRequest = PeriodicWorkRequestBuilder<ReminderMeaurementWork>(5000,
            TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ReminderMeaurementWork",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderMeasureWorkRequest
        )
    }
}