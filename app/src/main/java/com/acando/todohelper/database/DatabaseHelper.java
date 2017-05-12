package com.acando.todohelper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "content.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ToDoTable.onCreate(db);
        LabelTable.onCreate(db);
        ToDoLabelTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ToDoTable.onUpgrade(db, oldVersion, newVersion);
        LabelTable.onUpgrade(db, oldVersion, newVersion);
        ToDoLabelTable.onUpgrade(db, oldVersion, newVersion);
    }

    public long insert(String table, ContentValues values) throws SQLException {
        switch (table) {
            case ToDoTable.TABLE_NAME:
                return ToDoTable.insert(getWritableDatabase(), values);
            case LabelTable.TABLE_NAME:
                return LabelTable.insert(getWritableDatabase(), values);
            case ToDoLabelTable.TABLE_NAME:
                return ToDoLabelTable.insert(getWritableDatabase(), values);
            default:
                throw new SQLException("SQL Table is not existing.");
        }
    }

    public Cursor queue(String table, String primaryKey, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
        switch (table) {
            case ToDoTable.TABLE_NAME:
                return ToDoTable.queue(getReadableDatabase(), primaryKey, projection, selection,
                        selectionArgs, sortOrder);
            case LabelTable.TABLE_NAME:
                return LabelTable.queue(getReadableDatabase(), primaryKey, projection, selection,
                        selectionArgs, sortOrder);
            case ToDoLabelTable.TABLE_NAME:
                return ToDoLabelTable.queue(getReadableDatabase(), primaryKey, projection, selection,
                        selectionArgs, sortOrder);
            default:
                throw new SQLException("SQL Table is not existing.");
        }
    }

    public int update(String table, String primaryKey, ContentValues values) {
        switch (table) {
            case ToDoTable.TABLE_NAME:
                return ToDoTable.update(getWritableDatabase(), primaryKey, values);
            case LabelTable.TABLE_NAME:
                return LabelTable.update(getWritableDatabase(), primaryKey, values);
            case ToDoLabelTable.TABLE_NAME:
                return ToDoLabelTable.update(getWritableDatabase(), primaryKey, values);
            default:
                throw new SQLException("SQL Table is not existing.");
        }
    }

    public int delete(String table, String primaryKey) {
        switch (table) {
            case ToDoTable.TABLE_NAME:
                return ToDoTable.delete(getWritableDatabase(), primaryKey);
            case LabelTable.TABLE_NAME:
                return LabelTable.delete(getWritableDatabase(), primaryKey);
            case ToDoLabelTable.TABLE_NAME:
                return ToDoLabelTable.delete(getWritableDatabase(), primaryKey);
            default:
                throw new SQLException("SQL Table is not existing.");
        }
    }
}