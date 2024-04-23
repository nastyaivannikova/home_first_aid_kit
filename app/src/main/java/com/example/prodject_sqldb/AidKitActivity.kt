package com.example.prodject_sqldb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodject_sqldb.db.DbAidManager
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.MyAdapter
import com.example.prodject_sqldb.db.MyAidAdapter
import com.example.prodject_sqldb.db.MyIntentAidConstants

class AidKitActivity : AppCompatActivity() {

    lateinit var rcAidView: RecyclerView
    lateinit var searchView2: SearchView

    val dbAidManager = DbAidManager(this)
    val myAidAdapter = MyAidAdapter(ArrayList(), this)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aid_kit_activity)

        rcAidView = findViewById<RecyclerView>(R.id.rcAidView)
        searchView2 = findViewById<SearchView>(R.id.searchView2)
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        dbAidManager.openDb()
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbAidManager.closeDb()
    }

    fun onClickAddAid(view: View) {
        val i = Intent(this, EditAidActivity::class.java)
        startActivity(i)
    }

    fun init() {
        rcAidView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwagMg()
        swapHelper.attachToRecyclerView(rcAidView)
        rcAidView.adapter = myAidAdapter
    }

    private fun initSearchView() {
        searchView2.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fillAdapter()
                } else {
                    val list = dbAidManager.readDbData(newText)
                    myAidAdapter.updateAdapter(list)
                }
                return true
            }
        })
    }

    fun fillAdapter() {
        val list = dbAidManager.readDbData("")
        myAidAdapter.updateAdapter(list)
    }

    private fun getSwagMg(): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAidAdapter.removeItem(viewHolder.adapterPosition, dbAidManager)
            }

        })
    }
}