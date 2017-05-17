package com.acando.notegen.database;

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
        NoteTable.onCreate(db);
        LabelTable.onCreate(db);
        NoteLabelTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NoteTable.onUpgrade(db, oldVersion, newVersion);
        LabelTable.onUpgrade(db, oldVersion, newVersion);
        NoteLabelTable.onUpgrade(db, oldVersion, newVersion);
    }

    public long insert(String table, ContentValues values) throws SQLException {
        SQLiteDatabase db = getWritableDatabase();
        long insertedID;

        db.beginTransaction();
        switch (table) {
            case NoteTable.TABLE_NAME:
                insertedID = NoteTable.insert(db, values);
                break;
            case LabelTable.TABLE_NAME:
                insertedID = LabelTable.insert(db, values);
                break;
            case NoteLabelTable.TABLE_NAME:
                insertedID = NoteLabelTable.insert(db, values);
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
            case NoteTable.TABLE_NAME:
                return NoteTable.queue(getReadableDatabase(), primaryKey, projection, selection,
                    selectionArgs, sortOrder);
            case LabelTable.TABLE_NAME:
                return LabelTable.queue(getReadableDatabase(), null, primaryKey, projection, selection,
                        selectionArgs, sortOrder);
            case NoteLabelTable.TABLE_NAME:
                return NoteLabelTable.queue(getReadableDatabase(), primaryKey, projection, selection,
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
            case NoteTable.TABLE_NAME:
                updatedID = NoteTable.update(db, primaryKey, values);
                break;
            case LabelTable.TABLE_NAME:
                updatedID = LabelTable.update(db, primaryKey, values);
                break;
            case NoteLabelTable.TABLE_NAME:
                updatedID = NoteLabelTable.update(db, primaryKey, values);
                break;
            default:
                db.endTransaction();
                throw new SQLException("SQL Table is not existing.");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return updatedID;
    }

    public int delete(String table, String primaryKey, String selection) {
        SQLiteDatabase db = getWritableDatabase();
        int deletedID;

        db.beginTransaction();
        switch (table) {
            case NoteTable.TABLE_NAME:
                deletedID = NoteTable.delete(db, primaryKey);
                break;
            case LabelTable.TABLE_NAME:
                deletedID = LabelTable.delete(db, primaryKey);
                break;
            case NoteLabelTable.TABLE_NAME:
                deletedID = NoteLabelTable.delete(db, primaryKey, selection);
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