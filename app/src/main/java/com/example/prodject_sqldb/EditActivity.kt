package com.example.prodject_sqldb

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.ExpCheckService
import com.example.prodject_sqldb.db.MyIntentConstans
import com.example.prodject_sqldb.db.TimeCheckService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class EditActivity : AppCompatActivity() {
    private lateinit var workManager: WorkManager

    var id = 0
    var isEditState = false
    lateinit var edTitle: TextView
    lateinit var edExpData: TextView
    lateinit var edTimeReceipt: TextView
    lateinit var edQuantity: TextView
    lateinit var edType: TextView
    lateinit var edFood: TextView
    lateinit var edDesc: TextView
    lateinit var fbEdit: FloatingActionButton
    val dbManager = DbManager(this)
    var idAid = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        workManager = WorkManager.getInstance(this)
        idAid = intent.getIntExtra(MyIntentConstans.I_ID_AID_KEY, -1)


        edTitle = findViewById<TextView>(R.id.edTitle)
        edExpData = findViewById<TextView>(R.id.edExpData)
        edTimeReceipt = findViewById<TextView>(R.id.edTimeReceipt)
        edQuantity = findViewById<TextView>(R.id.edQuantity)
        edType = findViewById<TextView>(R.id.edType)
        edFood = findViewById<TextView>(R.id.edFood)
        edDesc = findViewById<TextView>(R.id.edDesc)
        fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)

        edExpData.setOnClickListener {
            showDatePickerDialog()
        }

        edTimeReceipt.setOnClickListener {
            showTimePickerDialog()
        }

        getMyIntents()
    }

    fun scheduleMedicationCheckWork(context: Context) {
        val constraints = Constraints.Builder()
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        var initialDelay: Long
        if (scheduledTime.before(currentTime)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1)
            initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis
        } else {
            initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis
        }

        val periodicWorkRequest = PeriodicWorkRequestBuilder<ExpCheckService>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(periodicWorkRequest)
    }

    private fun checkAndScheduleNotifications() {
        val items = dbManager.readDbData("", idAid)
        for (item in items) {
            val timeReceipt = item.timeReceipt
            if (timeReceipt.isNotEmpty()) {
                val timeParts = timeReceipt.split(":")
                if (timeParts.size == 2 && timeParts[0].isNotEmpty() && timeParts[1].isNotEmpty()) {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    calendar.set(Calendar.MINUTE, timeParts[1].toInt())
                    val title = item.title
                    val message = "Время принять лекарство $title"
                    val uniqueId = UUID.randomUUID().toString()
                    scheduleNotification(title, message, calendar.timeInMillis, uniqueId)
                } else {
                    Log.e("TimeCheckService", "Invalid time format: $timeReceipt")
                }
            }
        }
    }

    private fun scheduleNotification(title: String, message: String, triggerTimeMillis: Long, uniqueId: String) {
        val data = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .putString("uniqueId", uniqueId)
            .build()

        val constraints = Constraints.Builder()
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<TimeCheckService>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(triggerTimeMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(notificationWork)
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    private fun showReminderDialog() {
        val message = "Пожалуйста, не забудьте указать название и дату истечения срока годности перед сохранением."
        AlertDialog.Builder(this)
            .setTitle("Напоминание")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun onClickSave(view: View) {
        val myTitle = edTitle.text.toString()
        val myDisc = edDesc.text.toString()
        val myFood = edFood.text.toString()
        val myExpData = edExpData.text.toString()
        val myTimeReceipt = edTimeReceipt.text.toString()
        val myType = edType.text.toString()
        val myQuantity = edQuantity.text.toString()

        if (myTitle != "" && myExpData != "") {
            if (isEditState) {
                dbManager.updateItem(myTitle, id, getCurrentTime(), myExpData, myTimeReceipt, myQuantity, myType, myFood, myDisc, idAid)
            } else {
                dbManager.insertToDb(myTitle, getCurrentTime(), myExpData, myTimeReceipt, myQuantity, myType, myFood, myDisc, idAid)
            }
            scheduleMedicationCheckWork(this)
            checkAndScheduleNotifications()
            finish()
        } else {
            showReminderDialog()
        }
    }

    fun onEditEnable(view: View) {
        edTitle.isEnabled = true
        edDesc.isEnabled = true
        edExpData.isEnabled = true
        edTimeReceipt.isEnabled = true
        edQuantity.isEnabled = true
        edType.isEnabled = true
        edFood.isEnabled = true
        fbEdit.visibility = View.GONE

        edExpData.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            edExpData.text = formatter.format(selectedDate.time)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val medicationTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                edTimeReceipt.text = medicationTime
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    fun getMyIntents() {
        fbEdit.visibility = View.GONE
        val i = intent
        if (i!=null) {
            if (i.getStringExtra(MyIntentConstans.I_TITLE_KEY) != null) {
                edTitle.setText(i.getStringExtra(MyIntentConstans.I_TITLE_KEY))
                edDesc.setText(i.getStringExtra(MyIntentConstans.I_DESC_KEY))
                edExpData.setText(i.getStringExtra(MyIntentConstans.I_EXP_DATE_KEY))
                edTimeReceipt.setText(i.getStringExtra(MyIntentConstans.I_TIME_RECEIPT_KEY))
                edQuantity.setText(i.getStringExtra(MyIntentConstans.I_QUANTITY_KEY))
                edType.setText(i.getStringExtra(MyIntentConstans.I_TYPE_KEY))
                edFood.setText(i.getStringExtra(MyIntentConstans.I_FOOD_KEY))

                isEditState = true
                edTitle.isEnabled = false
                edDesc.isEnabled = false
                edExpData.isEnabled = false
                edTimeReceipt.isEnabled = false
                edQuantity.isEnabled = false
                edType.isEnabled = false
                edFood.isEnabled = false
                fbEdit.visibility = View.VISIBLE
                id = i.getIntExtra(MyIntentConstans.I_ID_KEY, 0)
                idAid = intent.getIntExtra(MyIntentConstans.I_ID_AID_KEY, -1)
            }
        }
    }

    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(time)
    }

}