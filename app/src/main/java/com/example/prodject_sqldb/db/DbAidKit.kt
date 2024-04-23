package com.example.prodject_sqldb.db

import android.provider.BaseColumns

object DbAidKit: BaseColumns {
    const val TABLE_NAME = "first_aid_kits"
    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_TITLE = "title"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "FirstAidKit.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_NAME_TITLE TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}