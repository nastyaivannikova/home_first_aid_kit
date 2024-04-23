package com.example.prodject_sqldb

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.prodject_sqldb.db.AlarmReceiver
import com.example.prodject_sqldb.db.DbAidManager
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.DbNameClass
import com.example.prodject_sqldb.db.ExpirationWorkRequest
//import com.example.prodject_sqldb.db.ExpCheckService
import com.example.prodject_sqldb.db.ListItem
import com.example.prodject_sqldb.db.MyAdapter
import com.example.prodject_sqldb.db.MyIntentAidConstants
import com.example.prodject_sqldb.db.MyIntentConstans
import java.time.Duration

class MainActivity : AppCompatActivity() {

    lateinit var rcView: RecyclerView
    lateinit var tvNoElements: TextView
    lateinit var tvAidOnMA: TextView
    lateinit var searchView: SearchView
    var idAid = 0
    var aidTitle = ""
    var tvAidOnMAVisible = true


    val dbManager = DbManager(this)
    val dbAidManager = DbAidManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, 0)
        aidTitle = intent.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY)!!
        setContentView(R.layout.activity_main)

//        val serviceIntent = Intent(this, ExpCheckService::class.java)
//        startService(serviceIntent)

        rcView = findViewById<RecyclerView>(R.id.rcView)
        tvNoElements = findViewById<TextView>(R.id.tvNoElements)
        tvAidOnMA = findViewById<TextView>(R.id.tvAidOnMA)
        searchView = findViewById<SearchView>(R.id.searchView)
        init()
        tvAidOnMA.text = aidTitle
        initSearchView()
    }



   // @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        dbAidManager.openDb()
       // Log.d("MyLog", idAid.toString());
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
        dbAidManager.closeDb()
    }

    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java).apply {
            putExtra(MyIntentConstans.I_ID_AID_KEY, idAid)
        }
        startActivity(i)
//        //Log.d("MyLog", "Main idAid to Edit " + idAid.toString());
    }

    fun onClickMenu(view: View) {
        val i = Intent(this, AidKitActivity::class.java)
        startActivity(i)
    }

    fun init() {
        //if (idAid == idConst) {
        rcView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwagMg()
        swapHelper.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter
        //}
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    tvAidOnMA.visibility = View.VISIBLE // Показать TextView при пустом запросе
                } else {
                    tvAidOnMA.visibility = View.GONE // Скрыть TextView при активном поиске
                }
                val list = dbManager.readDbData(newText!!,idAid)
                myAdapter.updateAdapter(list)
                return true
            }
        })
    }

    fun fillAdapter() {
        val selectedAidId = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, -1)
        val list = dbManager.readDbData("", selectedAidId)
        myAdapter.updateAdapter(list)
        if (list.size > 0) {
            tvNoElements.visibility = View.GONE
        } else {
            tvNoElements.visibility = View.VISIBLE
        }
        tvAidOnMA.visibility = View.VISIBLE
    }

    private fun getSwagMg(): ItemTouchHelper{
        return ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, dbManager)

            }

        })
    }

    private fun createNotificationChannel(name: String, description: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(name, description, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String, id: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "my_channel_id")
            .setSmallIcon(R.drawable.ic_add)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}