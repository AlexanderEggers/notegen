package com.acando.notegen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class NoteTable {

    // Database table
    public static final String TABLE_NAME = "note";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_ARCHIVE = "archive";
    public static final String COLUMN_BIN = "bin";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAST_MODIFY = "last_modify";

    public static final int TRUE = 1, FALSE = 0;

    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_TITLE_INDEX = 1;
    public static final int COLUMN_TEXT_INDEX = 2;
    public static final int COLUMN_ARCHIVE_INDEX = 3;
    public static final int COLUMN_BIN_INDEX = 4;
    public static final int COLUMN_IMAGE_INDEX = 5;
    public static final int COLUMN_LAST_MODIFY_INDEX = 6;

    // Database creation SQL statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + "               INTEGER       PRIMARY KEY             AUTOINCREMENT, "
                    + COLUMN_TITLE + "            TEXT                                  , "
                    + COLUMN_TEXT + "             TEXT                                  , "
                    + COLUMN_ARCHIVE + "          TEXT                                  NOT NULL, "
                    + COLUMN_BIN + "              TEXT                                  NOT NULL, "
                    + COLUMN_IMAGE + "            BLOB                                  , "
                    + COLUMN_LAST_MODIFY + "      INTEGER                               NOT NULL);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NoteTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
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

    public static int update(SQLiteDatabase db, String id, ContentValues values) {
        if(id != null) {
            return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{id});
        } else {
            throw new SQLException("Error: Uri or SQL Syntax Error -> id missing");
        }
    }

    public static Cursor queue(SQLiteDatabase db, String id, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        if (id != null) {
            queryBuilder.appendWhere(COLUMN_ID + " = " + id);
        }

        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = COLUMN_LAST_MODIFY + " DESC";
        }

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public static int delete(SQLiteDatabase db, String id) {
        if(id != null) {
            return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{id});
        } else {
            throw new SQLException("Error: Failed to delete from table '" + TABLE_NAME + "' -> id missing");
        }
    }
}
