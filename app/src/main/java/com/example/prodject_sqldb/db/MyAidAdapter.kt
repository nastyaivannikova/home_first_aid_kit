package com.example.prodject_sqldb.db

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
import com.example.prodject_sqldb.MainActivity
import com.example.prodject_sqldb.R

class MyAidAdapter(listMain:ArrayList<ListAidItem>, contextM: Context): RecyclerView.Adapter<MyAidAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM
    var backgroundColors = HashMap<Int, Int>()


    init {
        updateColors(0)
    }

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        val tvAidTitle = itemView.findViewById<TextView>(R.id.tvAidTitle)
        var context = contextV

        fun setData(item:ListAidItem) {
            tvAidTitle.text = item.title
            itemView.setOnClickListener{
                val intent = Intent(context, MainActivity::class.java).apply {

                    putExtra(MyIntentAidConstants.I_TITLE_AID_KEY, item.title)
                    putExtra(MyIntentAidConstants.I_ID_AID_KEY, item.id)

                }
                context.startActivity(intent)
                Log.d("MyLog", "item" + item.id.toString());
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_aid, parent, false), context)
    }

    override fun getItemCount(): Int {

        return listArray.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])

        // Определение цвета фона для элемента на основе его позиции
        val backgroundColor = if (position % 2 == 0) {
            ContextCompat.getColor(context, R.color.light_green)
        } else {
            ContextCompat.getColor(context, R.color.beige)
        }

        val colorStateList = ColorStateList.valueOf(backgroundColor)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f
            setColor(colorStateList)
        }

        holder.itemView.background = drawable
    }

    fun updateAdapter(listItems:List<ListAidItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun updateColors(startIndex: Int) {
        var colorIndex = startIndex % 2
        for (i in startIndex until listArray.size) {
            val backgroundColor = if (colorIndex % 2 == 0) {
                ContextCompat.getColor(context, R.color.beige)
            } else {
                ContextCompat.getColor(context, R.color.light_green)
            }

            backgroundColors[i] = backgroundColor // Сохраняем цвет фона для элемента
            colorIndex++
        }
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int, dbAidManager: DbAidManager) {
        val removedItemId = listArray[pos].id
        dbAidManager.deleteAllByAidId(removedItemId)
        dbAidManager.removeItemFromDb(removedItemId)
        listArray.removeAt(pos)
        updateColors(pos)
        notifyDataSetChanged()
    }
}