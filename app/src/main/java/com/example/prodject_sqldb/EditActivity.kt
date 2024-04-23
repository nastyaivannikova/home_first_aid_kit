package com.example.prodject_sqldb

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prodject_sqldb.db.AlarmReceiver
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.MyIntentAidConstants
import com.example.prodject_sqldb.db.MyIntentConstans
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

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
    //val idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, -1)
    var idAid = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
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
        Log.d("MyLog", "intents: " + getMyIntents().toString())
        //Log.d("MyLog", "Edit idAid " + intent.getIntExtra(MyIntentConstans.I_ID_AID_KEY, -1).toString());
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

//    init {
//        idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, -1)
//    }

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
                //idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, -1)
                dbManager.updateItem(myTitle, id, getCurrentTime(), myExpData, myTimeReceipt, myQuantity, myType, myFood, myDisc, idAid)
                Log.d("MyLog", "update: " + idAid.toString())
            } else {
                //idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, -1)
                dbManager.insertToDb(myTitle, getCurrentTime(), myExpData, myTimeReceipt, myQuantity, myType, myFood, myDisc, idAid)
                Log.d("MyLog", "insert: " + idAid.toString())
            }
            finish()
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
            edExpData.setText(formatter.format(selectedDate.time))
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
                // Обработайте выбранное время здесь
                val medicationTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                // Назначьте напоминание о приеме лекарства
                //MedicationReminderUtils.scheduleOneTimeNotification(this, medicationTime)
                edTimeReceipt.setText(medicationTime)
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
                Log.d("MyLog", "getMyIntents: $idAid")
            }
        }
    }

    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(time)
    }
}