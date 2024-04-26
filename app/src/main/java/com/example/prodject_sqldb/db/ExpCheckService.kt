package com.example.prodject_sqldb.db

import android.annotation.SuppressLint

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.prodject_sqldb.R
import java.text.SimpleDateFormat

class ExpCheckService(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            setForegroundAsync(createForegroundInfo())
            checkExpiryDatesAndScheduleNotifications()
            Result.success()
        } catch (e: Exception) {
            Log.e("ExpCheckService", "Error in ExpCheckService: $e")
            Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle("Checking Medication Expiry")
            .setTicker("Checking Medication Expiry")
            .setSmallIcon(R.drawable.ic_add)
            .setOngoing(true)
            .build()

        return ForegroundInfo(1, notification)
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkExpiryDatesAndScheduleNotifications() {
        val medicineList: List<ListItem> = getAllMedicines(applicationContext)
        val currentTimeInMillis = System.currentTimeMillis()

        val expiredMedicines = mutableListOf<ListItem>()

        for (medicine in medicineList) {
            val expiryDateInMillis = SimpleDateFormat("dd-MM-yy").parse(medicine.expDate)?.time

            if (expiryDateInMillis != null && expiryDateInMillis <= currentTimeInMillis) {
                expiredMedicines.add(medicine)
            }
        }

        if (expiredMedicines.isNotEmpty()) {
            for (medicine in expiredMedicines) {
                sendNotification(applicationContext, "Лекарство ${medicine.title} просрочено!")
            }
        }
    }

    fun getAllMedicines(context: Context): List<ListItem> {
        val medicineList = mutableListOf<ListItem>()
        val query = "SELECT * FROM medicines"
        val dbHelper = DbHelper(context)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor?.moveToFirst() == true) {
            do {
                val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_TITLE))
                val dataExpData = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_EXPIRATION_DATE))

                val medicine = ListItem()
                medicine.title = dataTitle
                medicine.id = dataId
                medicine.expDate= dataExpData
                medicineList.add(medicine)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return medicineList
    }

    fun sendNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "channel_id")
            .setContentTitle("Medicine Expiry Notification")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_add)
            .build()

        notificationManager.notify(1, notification)
    }
}