//package com.example.prodject_sqldb.db
//
//import android.app.AlarmManager
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.database.sqlite.SQLiteDatabase
//import android.icu.util.Calendar
//import android.icu.util.TimeZone
//import android.os.Build
//import android.os.IBinder
//import android.provider.BaseColumns
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.example.prodject_sqldb.R
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//class ExpiryNotificationWorker(context: Context, workerParams: WorkerParameters)
//    : Worker(context, workerParams) {
//
//    val dbHelper = DbHelper(context)
//    var db: SQLiteDatabase? = null
//
//    fun openDb() {
//        db = dbHelper.writableDatabase
//    }
//
//    override fun doWork(): Result {
//        Log.d("MyLog", "Starting doWork()")
//        // Получите список просроченных лекарств
//        openDb()
//        val dbManager = DbManager(applicationContext)
//        val list = dbManager.getExpiredMedicines()
//
//        if (list.isNotEmpty()) {
//            // Отправьте уведомление для каждого просроченного лекарства
//            for (medicine in list) {
//                sendNotification(medicine.title)
//            }
//        }
//        Log.d("MyLog", "Finished doWork()")
//
//        // Возвращаем Result.SUCCESS, если работа выполнена успешно
//        return Result.success()
//    }
//
//    private fun sendNotification(title: String) {
//        Log.d("MyLog", "Sending notification for: $title")
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
//            .setContentTitle("Medicine Expiry Notification")
//            .setContentText("Лекарство $title просрочено! Проверьте срок годности.")
//            .setSmallIcon(R.drawable.ic_add)
//            .build()
//
//        notificationManager.notify(1, notification)
//    }
//}