package com.example.prodject_sqldb.db

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.prodject_sqldb.R
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.DbNameClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"
    var idAid = 0

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: AlarmReceiver started")
        val dbManager = DbManager(context)
        dbManager.openDb()

        idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, 0)

        val list = dbManager.readDbData("", idAid)
        for (item in list) {
            val expirationDate = item.expDate
            val currentDate = getCurrentDate()
            if (isExpired(expirationDate, currentDate)) {
                sendNotification(context, item)
            }
        }

        dbManager.closeDb()
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