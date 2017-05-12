package com.acando.todohelper.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class LabelTable {

    // Database table
    public static final String TABLE_NAME = "label";
    public static final String COLUMN_INTERNAL_NAME = "_internal_name";
    public static final String COLUMN_NAME = "name";

    // Database creation SQL statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_INTERNAL_NAME + "   TEXT         PRIMARY KEY              NOT NULL, "
                    + COLUMN_NAME + "            TEXT                                  NOT NULL);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(LabelTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static long insert(SQLiteDatabase db, ContentValues values) {
        Cursor c = queue(db, values.getAsString(COLUMN_INTERNAL_NAME), null, null, null, null);
        if(!c.moveToFirst()) {
            long id = db.insert(TABLE_NAME, "", values);
            if (id > 0) {
                return id;
            } else {
                throw new SQLException("Error: SQL Syntax Error");
            }
        }
        throw new SQLException("Error: Label already existing");
    }

    public static int update(SQLiteDatabase db, String internalName, ContentValues values) {
        if (internalName != null) {
            return db.update(TABLE_NAME, values, COLUMN_INTERNAL_NAME + " = '" + internalName + "'", null);
        } else {
            throw new SQLException("Error: Uri or SQL Syntax Error -> id missing");
        }
    }

    public static Cursor queue(SQLiteDatabase db, String internalName, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        if (internalName != null) {
            queryBuilder.appendWhere(COLUMN_INTERNAL_NAME + " = '" + internalName + "'");
        }

        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = COLUMN_NAME;
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public static int delete(SQLiteDatabase db, String internalName) {
        if(internalName != null) {
            return db.delete(TABLE_NAME, COLUMN_INTERNAL_NAME + " = '" + internalName + "'", null);
        } else {
            throw new SQLException("Error: Failed to delete from table '" + TABLE_NAME + "' -> id missing");
        }
    }
}
