package com.example.prodject_sqldb.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbAidHelper(context: Context) : SQLiteOpenHelper(context, DbAidKit.DATABASE_NAME,
    null, DbAidKit.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DbAidKit.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DbAidKit.SQL_DELETE_TABLE)
        onCreate(db)
    }
}