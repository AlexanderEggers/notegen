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
        SQLiteDatabase db = getWritableDatabase();
        long insertedID;

        db.beginTransaction();
        switch (table) {
            case ToDoTable.TABLE_NAME:
                insertedID = ToDoTable.insert(db, values);
                break;
            case LabelTable.TABLE_NAME:
                insertedID = LabelTable.insert(db, values);
                break;
            case ToDoLabelTable.TABLE_NAME:
                insertedID = ToDoLabelTable.insert(db, values);
                break;
            default:
                db.endTransaction();
                throw new SQLException("SQL Table is not existing.");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return insertedID;
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
        SQLiteDatabase db = getWritableDatabase();
        int updatedID;

        db.beginTransaction();
        switch (table) {
            case ToDoTable.TABLE_NAME:
                updatedID = ToDoTable.update(db, primaryKey, values);
                break;
            case LabelTable.TABLE_NAME:
                updatedID = LabelTable.update(db, primaryKey, values);
                break;
            case ToDoLabelTable.TABLE_NAME:
                updatedID = ToDoLabelTable.update(db, primaryKey, values);
                break;
            default:
                db.endTransaction();
                throw new SQLException("SQL Table is not existing.");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return updatedID;
    }

    public int delete(String table, String primaryKey) {
        SQLiteDatabase db = getWritableDatabase();
        int deletedID;

        db.beginTransaction();
        switch (table) {
            case ToDoTable.TABLE_NAME:
                deletedID = ToDoTable.delete(db, primaryKey);
                break;
            case LabelTable.TABLE_NAME:
                deletedID = LabelTable.delete(db, primaryKey);
                break;
            case ToDoLabelTable.TABLE_NAME:
                deletedID = ToDoLabelTable.delete(db, primaryKey);
                break;
            default:
                db.endTransaction();
                throw new SQLException("SQL Table is not existing.");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return deletedID;
    }
}