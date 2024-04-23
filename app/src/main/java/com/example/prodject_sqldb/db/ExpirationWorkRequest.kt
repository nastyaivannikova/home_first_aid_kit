package com.example.prodject_sqldb.db

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.prodject_sqldb.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpirationWorkRequest(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams)  {

    private val TAG = "ExpirationWorkRequest"
    var idAid = 0

    override fun doWork(): Result {
        Log.d(TAG, "doWork: ExpirationWorkRequest started")
        val dbManager = DbManager(applicationContext)
        dbManager.openDb()

        idAid = inputData.getInt(MyIntentAidConstants.I_ID_AID_KEY, 0)

        val list = dbManager.readDbData("", idAid)
        for (item in list) {
            val expirationDate = item.expDate
            val currentDate = getCurrentDate()
            if (isExpired(expirationDate, currentDate)) {
                sendNotification(applicationContext, item)
            }
        }

        dbManager.closeDb()
        return Result.success()
    }
    private fun isExpired(expirationDate: String, currentDate: String): Boolean {
        val expirationCalendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()
        val expirationSdf = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        expirationCalendar.time = expirationSdf.parse(expirationDate)!!
        currentCalendar.time = expirationSdf.parse(currentDate)!!

        return expirationCalendar.before(currentCalendar)
    }

    private fun sendNotification(context: Context, item: ListItem) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, "medicine_channel")
            .setSmallIcon(R.drawable.ic_add)
            .setContentTitle("Expired Medicine")
            .setContentText("${item.title} is expired")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel("medicine_channel") == null) {
            val channel = NotificationChannel(
                "medicine_channel",
                "Medicine Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun getCurrentDate(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(time)
    }
}