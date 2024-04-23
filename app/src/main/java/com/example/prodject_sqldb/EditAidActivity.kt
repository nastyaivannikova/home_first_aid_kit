package com.example.prodject_sqldb

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prodject_sqldb.db.DbAidManager
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.MyIntentAidConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditAidActivity : AppCompatActivity() {
    var id = 0
    var isEditState = false
    lateinit var edAidName: TextView
    val dbAidManager = DbAidManager(this)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_aid_activity)

        edAidName = findViewById<TextView>(R.id.edAidName)

        //getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        dbAidManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbAidManager.closeDb()
    }

    fun onClickSaveAid(view: View) {
        val myTitle = edAidName.text.toString()

        if (myTitle != "") {
            if (isEditState) {
                dbAidManager.updateItem(myTitle, id)
            } else {
                dbAidManager.insertToDb(myTitle)
            }
            finish()
        }
    }

    fun onEditEnable(view: View) {
        edAidName.isEnabled = true

    }

    fun getMyIntents() {
        val i = intent
        if (i!=null) {
            if (i.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY) != null) {
                edAidName.setText(i.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY))

                isEditState = true
                edAidName.isEnabled = false
                id = i.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, 0)
            }
        }
    }
}