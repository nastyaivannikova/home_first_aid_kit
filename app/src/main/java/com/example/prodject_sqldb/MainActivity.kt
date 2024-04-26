package com.example.prodject_sqldb

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodject_sqldb.db.DbAidManager
import com.example.prodject_sqldb.db.DbManager
import com.example.prodject_sqldb.db.MyAdapter
import com.example.prodject_sqldb.db.MyIntentAidConstants
import com.example.prodject_sqldb.db.MyIntentConstans

class MainActivity : AppCompatActivity() {

    lateinit var rcView: RecyclerView
    lateinit var tvNoElements: TextView
    lateinit var tvAidOnMA: TextView
    lateinit var searchView: SearchView
    var idAid = 0
    var aidTitle = ""
    var tvAidOnMAVisible = true


    lateinit var dbManager: DbManager
    val myAdapter = MyAdapter(ArrayList(), this)
    var dbAidManager = DbAidManager(this)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idAid = intent.getIntExtra(MyIntentAidConstants.I_ID_AID_KEY, 0)
        aidTitle = intent.getStringExtra(MyIntentAidConstants.I_TITLE_AID_KEY)!!
        setContentView(R.layout.activity_main)
        dbManager = DbManager(this)

        rcView = findViewById<RecyclerView>(R.id.rcView)
        tvNoElements = findViewById<TextView>(R.id.tvNoElements)
        tvAidOnMA = findViewById<TextView>(R.id.tvAidOnMA)
        searchView = findViewById<SearchView>(R.id.searchView)
        init()
        tvAidOnMA.text = aidTitle
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        dbAidManager.openDb()
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
    }

    fun onClickMenu(view: View) {
        val i = Intent(this, AidKitActivity::class.java)
        startActivity(i)
    }

    fun init() {
        rcView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwagMg()
        swapHelper.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    tvAidOnMA.visibility = View.VISIBLE
                } else {
                    tvAidOnMA.visibility = View.GONE
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
                val position = viewHolder.adapterPosition
                val itemName = myAdapter.getItemName(position)
                showConfirmationDialog(itemName, position)
            }
        })
    }

    private fun showConfirmationDialog(itemName: String, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Удаление лекарства")
            .setMessage("Вы уверены, что хотите удалить лекарство $itemName?")
            .setPositiveButton("Yes") { dialog, _ ->
                myAdapter.removeItem(position, dbManager)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                myAdapter.restoreItem(position)
                dialog.dismiss()
            }
            .show()
    }
}