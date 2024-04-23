package com.example.prodject_sqldb.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log

class DbAidManager(context: Context) {
    val dbManager = DbManager(context)
    val dbAidHelper = DbAidHelper(context)
    val dbHelper = DbHelper(context)
    var db: SQLiteDatabase? = null
    var db_medecines: SQLiteDatabase? = null

    fun openDb() {
        db = dbAidHelper.writableDatabase
        db_medecines = dbHelper.writableDatabase
    }

    fun deleteAllByAidId(idAid: Int) {
        val selection = "${DbNameClass.COLUMN_ID_AID} = ?"
        val selectionArgs = arrayOf(idAid.toString())
        db_medecines?.delete(DbNameClass.TABLE_NAME, selection, selectionArgs)
    }

    fun insertToDb(title: String) {
        val values = ContentValues().apply {
            put(DbAidKit.COLUMN_NAME_TITLE, title)
        }
        db?.insert(DbAidKit.TABLE_NAME, null, values)
    }

    fun updateItem(title: String, id: Int) {
        val selection = "${DbAidKit.COLUMN_NAME_ID}=$id"
        val values = ContentValues().apply {
            put(DbAidKit.COLUMN_NAME_TITLE, title)
            put(DbAidKit.COLUMN_NAME_ID, id)
        }
        db?.update(DbAidKit.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: Int) {
        val selection = "${DbAidKit.COLUMN_NAME_ID}=$id"
        db?.delete(DbAidKit.TABLE_NAME, selection, null)
        dbManager.deleteAllByAidId(id)
        updateIdsAfterDelete(id)
    }

    fun updateIdsAfterDelete(deletedId: Int) {
        val selection = "${DbAidKit.COLUMN_NAME_ID}>$deletedId"
        val cursor = db?.query(DbAidKit.TABLE_NAME, null, selection, null, null, null, "${DbAidKit.COLUMN_NAME_ID} ASC")
        var updatedId = deletedId
        while (cursor?.moveToNext() == true) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbAidKit.COLUMN_NAME_ID))
            val values = ContentValues().apply {
                put(DbAidKit.COLUMN_NAME_ID, updatedId)
            }
            val selection = "${DbAidKit.COLUMN_NAME_ID}=$id"
            db?.update(DbAidKit.TABLE_NAME, values, selection, null)
            updateIdAidForAllItems(id, updatedId)
            ++updatedId
        }
        cursor?.close()
    }

    fun updateIdAidForAllItems(oldIdAid: Int, newIdAid: Int) {
        val selection = "${DbNameClass.COLUMN_ID_AID} = ?"
        val selectionArgs = arrayOf(oldIdAid.toString())
        val cursor = db_medecines?.query(DbNameClass.TABLE_NAME, null, selection, selectionArgs, null, null, null)
        while (cursor?.moveToNext()!!) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            Log.d("id", "id : $id")
            val values = ContentValues().apply {
                put(DbNameClass.COLUMN_ID_AID, newIdAid)
            }
            db_medecines?.update(DbNameClass.TABLE_NAME, values, "${BaseColumns._ID} = ?", arrayOf(id.toString()))
        }
        cursor.close()
    }

    fun readDbData(searchText: String) : ArrayList<ListAidItem> {
        val dataList = ArrayList<ListAidItem>()
        val selection = "${DbAidKit.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(DbAidKit.TABLE_NAME, null, selection, arrayOf("%$searchText%"), null, null, null)
        while(cursor?.moveToNext()!!) { //not null
            val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(DbAidKit.COLUMN_NAME_TITLE))
            val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(DbAidKit.COLUMN_NAME_ID))
            val item = ListAidItem()
            item.title = dataTitle
            item.id = dataId
            dataList.add(item)
        }
        cursor.close()
        return dataList
    }

    fun closeDb() {
        dbAidHelper.close()
    }
}