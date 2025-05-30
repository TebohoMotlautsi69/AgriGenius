package com.example.agrigenius360

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class ReminderMeaurementWork(
    appContext: Context,
    workerParams: WorkerParameters
) :CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result {
        val application = applicationContext as AgriGeniusApplication
        val plantDAO = application.plantDAO
        val plantGrowthDAO = application.plantGrowthDAO
        val plants = plantDAO.getAllPlants().firstOrNull() ?: emptyList()
        val currentTime = System.currentTimeMillis()

        plants.forEach { plant ->
            plant.measurementFrequencyDays?.let { frequent ->
                if(frequent > 0){
                    val plantId: Int = plant.id
                    val lastHeight = plantGrowthDAO.getMeasurementsForPlant(plantId).firstOrNull()?.maxByOrNull { it.measurementDate }
                    val lastPlantDate: Long = lastHeight?.measurementDate ?: plant.plantDate
                    val nextMeasureDue = lastPlantDate + TimeUnit.DAYS.toMillis(frequent.toLong())

                    if(nextMeasureDue <= currentTime){
                       sendMeasurementReminderNotfication(plant.id, plant.name, frequent)
                    }
                }
            }
        }
    return Result.success()
}

private fun sendMeasurementReminderNotfication(plantId: Int, plantName: String, frequencyDays: Int) {
    val CHANNEL_ID = "measurement_reminders_channel"
    val notificationId = plantId + 2000
    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("route", "growthHistory/$plantId")
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val title = "Time to Measure Your $plantName"
    val contentText = " Your ${plantName} is due for the a new measurement!"

    val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.growth)
        .setContentTitle(title)
        .setContentText(contentText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId, builder.build())
        }
    }else{
        println("Notification permission denied for Growth Performance Alert.")
    }
}
}
