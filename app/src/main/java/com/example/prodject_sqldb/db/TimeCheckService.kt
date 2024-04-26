package com.example.prodject_sqldb.db

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.prodject_sqldb.R

class TimeCheckService(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private val NOTIFICATION_CHANNEL_ID = "my_channel_id"

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val message = inputData.getString("message")

        if (title != null && message != null) {
            showNotification(title, message)
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_add)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager?.notify(1, notification)
    }
}