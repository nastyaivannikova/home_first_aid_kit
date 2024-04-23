package com.example.prodject_sqldb.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prodject_sqldb.EditActivity
import com.example.prodject_sqldb.R

class MyAdapter(listMain:ArrayList<ListItem>, contextM: Context): RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        var context = contextV
        var backgroundColors = HashMap<Int, Int>()


        fun setData(item:ListItem) {
            tvTitle.text = item.title
            tvTime.text = item.time


            itemView.setOnClickListener{
                val intent = Intent(context, EditActivity::class.java).apply {

                    putExtra(MyIntentConstans.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstans.I_DESC_KEY, item.desk)
                    putExtra(MyIntentConstans.I_ID_KEY, item.id)
                    putExtra(MyIntentConstans.I_EXP_DATE_KEY, item.expDate)
                    putExtra(MyIntentConstans.I_TIME_RECEIPT_KEY, item.timeReceipt)
                    putExtra(MyIntentConstans.I_QUANTITY_KEY, item.quantity)
                    putExtra(MyIntentConstans.I_TYPE_KEY, item.type)
                    putExtra(MyIntentConstans.I_FOOD_KEY, item.food)
                    putExtra(MyIntentConstans.I_ID_AID_KEY, item.idAid)

                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_, parent, false), context)
    }

    override fun getItemCount(): Int {

        return listArray.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = listArray[position]
        holder.setData(item)

        // Определение цвета в зависимости от idAid
        Log.d("MyLog", item.idAid.toString())
        val backgroundColor = if (item.idAid % 2 == 0) {
            ContextCompat.getColor(context, R.color.beige)
        } else {
            ContextCompat.getColor(context, R.color.light_green)
        }

        val colorStateList = ColorStateList.valueOf(backgroundColor)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f
            setColor(colorStateList)
        }

        // Установка цвета фона
        holder.itemView.background = drawable
    }

    fun updateAdapter(listItems:List<ListItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int, dbManager: DbManager) {
        dbManager.removeItemFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}