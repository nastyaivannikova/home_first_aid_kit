package com.example.prodject_sqldb.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class DbManager(context: Context) {
    val dbHelper = DbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = dbHelper.writableDatabase
    }

    fun insertToDb(title: String, time: String, expDate: String, timeReceipt: String, quantity: String, type: String, food: String, content: String, idAid: Int) {
        val values = ContentValues().apply {
            put(DbNameClass.COLUMN_NAME_TITLE, title)
            put(DbNameClass.COLUMN_NAME_TIME, time)
            put(DbNameClass.COLUMN_NAME_EXPIRATION_DATE, expDate)
            put(DbNameClass.COLUMN_NAME_TIME_RECEIPT, timeReceipt)
            put(DbNameClass.COLUMN_NAME_QUANTITY, quantity)
            put(DbNameClass.COLUMN_NAME_TYPE, type)
            put(DbNameClass.COLUMN_NAME_FOOD_RELATION, food)
            put(DbNameClass.COLUMN_NAME_CONTENT, content)
            put(DbNameClass.COLUMN_ID_AID, idAid)
        }
        db?.insert(DbNameClass.TABLE_NAME, null, values)
    }

    fun updateItem(title: String, id: Int, time: String, expDate: String, timeReceipt: String, quantity: String, type: String, food: String, content: String, idAid: Int) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(DbNameClass.COLUMN_NAME_TITLE, title)
            put(DbNameClass.COLUMN_NAME_TIME, time)
            put(DbNameClass.COLUMN_NAME_EXPIRATION_DATE, expDate)
            put(DbNameClass.COLUMN_NAME_TIME_RECEIPT, timeReceipt)
            put(DbNameClass.COLUMN_NAME_QUANTITY, quantity)
            put(DbNameClass.COLUMN_NAME_TYPE, type)
            put(DbNameClass.COLUMN_NAME_FOOD_RELATION, food)
            put(DbNameClass.COLUMN_NAME_CONTENT, content)
            put(DbNameClass.COLUMN_ID_AID, idAid)
        }
        db?.update(DbNameClass.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(DbNameClass.TABLE_NAME, selection, null)

    }

    fun readDbData(searchText: String, idAid: Int) : ArrayList<ListItem> {
        val dataList = ArrayList<ListItem>()
        val selection = "${DbNameClass.COLUMN_NAME_TITLE} like ? AND ${DbNameClass.COLUMN_ID_AID} = ?"
        val selectionArgs = arrayOf("%$searchText%", idAid.toString())
        val cursor = db?.query(DbNameClass.TABLE_NAME, null, selection, selectionArgs, null, null, null)
        while(cursor?.moveToNext()!!) { //not null
            val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_TITLE))
            val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_TIME))
            val dataExpData = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_EXPIRATION_DATE))
            val dataTimeReceipt = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_TIME_RECEIPT))
            val dataQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_QUANTITY))
            val dataType = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_TYPE))
            val dataFood = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_FOOD_RELATION))
            val dataContent = cursor.getString(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_NAME_CONTENT))
            val dataIdAid = cursor.getInt(cursor.getColumnIndexOrThrow(DbNameClass.COLUMN_ID_AID))
            val item = ListItem()
            item.title = dataTitle
            item.id = dataId
            item.time = dataTime
            item.expDate= dataExpData
            item.timeReceipt = dataTimeReceipt
            item.quantity = dataQuantity
            item.type = dataType
            item.food = dataFood
            item.desk = dataContent
            item.idAid = dataIdAid
            dataList.add(item)
        }
        cursor.close()
        return dataList
    }

    fun deleteAllByAidId(idAid: Int) {
        val selection = "${DbNameClass.COLUMN_ID_AID} = ?"
        val selectionArgs = arrayOf(idAid.toString())
        db?.delete(DbNameClass.TABLE_NAME, selection, selectionArgs)
    }

    fun closeDb() {
        dbHelper.close()
    }
}