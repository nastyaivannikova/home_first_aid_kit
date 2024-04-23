package com.example.prodject_sqldb.db

import android.provider.BaseColumns

object DbNameClass: BaseColumns {
    const val TABLE_NAME = "medicines"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_TIME = "time"
    const val COLUMN_NAME_EXPIRATION_DATE = "expiration_date"
    const val COLUMN_NAME_TIME_RECEIPT = "time_of_receipt"
    const val COLUMN_NAME_TYPE = "type"
    const val COLUMN_NAME_QUANTITY = "quantity"
    const val COLUMN_NAME_FOOD_RELATION = "food_relation"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_ID_AID = "id_aid"

    const val DATABASE_VERSION = 4
    const val DATABASE_NAME = "Medicines.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY,$COLUMN_NAME_TITLE TEXT,$COLUMN_NAME_TIME TEXT" +
            ",$COLUMN_NAME_EXPIRATION_DATE TEXT,$COLUMN_NAME_TIME_RECEIPT TEXT,$COLUMN_NAME_QUANTITY TEXT,$COLUMN_NAME_TYPE TEXT, $COLUMN_NAME_FOOD_RELATION TEXT," +
            "$COLUMN_NAME_CONTENT TEXT,$COLUMN_ID_AID INTEGER)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}