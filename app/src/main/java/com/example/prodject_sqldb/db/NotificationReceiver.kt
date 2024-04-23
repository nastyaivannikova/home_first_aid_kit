package com.example.prodject_sqldb.db

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.prodject_sqldb.R

class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message")

        if (message != null) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, "channel_id")
                .setContentTitle("Уведомление о просроченном лекарстве")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_add)
                .build()

            notificationManager.notify(1, notification)
        }
    }
}