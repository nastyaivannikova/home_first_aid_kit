package com.example.prodject_sqldb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.prodject_sqldb.db.DbAidManager
import com.example.prodject_sqldb.db.MyIntentAidConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditAidActivity : AppCompatActivity() {
    var id = 0
    var isEditState = false
    lateinit var edAidName: TextView
    val dbAidManager = DbAidManager(this)
    lateinit var fbEditAid: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_aid_activity)

        edAidName = findViewById<TextView>(R.id.edAidName)
        fbEditAid = findViewById<FloatingActionButton>(R.id.fbEditAid)

        getMyIntents()
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
        fbEditAid.visibility = View.GONE
    }

    fun getMyIntents() {
        fbEditAid.visibility = View.GONE
        val i = intent
        if (i!=null) {
            if (i.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY) != null) {
                edAidName.setText(i.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY))

                isEditState = true
                edAidName.isEnabled = false
                fbEditAid.visibility = View.VISIBLE
                id = i.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, 0)
            }
        }
    }
}