package com.acando.notegen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class NoteLabelTable {

    // Database table
    public static final String TABLE_NAME = "note_label";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_LABEL_ID = "label_id";

    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_TODO_ID_INDEX = 1;
    public static final int COLUMN_LABEL_INTERNAL_NAME_INDEX = 2;

    // Database creation SQL statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + "                     INTEGER         PRIMARY KEY           AUTOINCREMENT, "
                    + COLUMN_NOTE_ID + "                INTEGER                               NOT NULL, "
                    + COLUMN_LABEL_ID + "               INTEGER                               NOT NULL);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NoteLabelTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static long insert(SQLiteDatabase db, ContentValues values) {
        long id = db.insert(TABLE_NAME, "", values);
        if (id <= 0) {
            throw new SQLException("Error: SQL Syntax Error");
        }
        return id;
    }

    public static int update(SQLiteDatabase db, String contentType, ContentValues values) {
        if (contentType != null) {
            return db.update(TABLE_NAME, values, contentType, null);
        } else {
            throw new SQLException("Error: Uri or SQL Syntax Error -> id missing");
        }
    }

    public static Cursor queue(SQLiteDatabase db, String contentType, String[] projection,
                             String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        if (contentType != null) {
            queryBuilder.appendWhere(contentType);
        }

        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = COLUMN_ID;
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public static int delete(SQLiteDatabase db, String contentType, String selection) {
        if(contentType != null) {
            if(selection != null) {
                return db.delete(TABLE_NAME, selection, null);
            } else {
                return db.delete(TABLE_NAME, contentType, null);
            }
        } else {
            throw new SQLException("Error: Failed to delete from table '" + TABLE_NAME + "' -> id missing");
        }
    }
}
