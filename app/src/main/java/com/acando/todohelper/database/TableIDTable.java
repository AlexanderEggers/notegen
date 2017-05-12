package com.acando.todohelper.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableIDTable {

    // Database table
    public static final String TABLE_NAME = "table_ids";
    public static final String COLUMN_TABLE_NAME = "_table_name";
    public static final String COLUMN_ID_NUM = "current_id";

    // Database creation SQL statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TABLE_NAME + "        TEXT         PRIMARY KEY              NOT NULL, "
                    + COLUMN_ID_NUM + "            INT                                   NOT NULL);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TableIDTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
